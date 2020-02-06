package com.like.banner

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import kotlinx.coroutines.*

/**
 * 通过 [BannerViewPager]、[BannerPagerAdapter] 控制 Banner 进行无限轮播
 */
class BannerController(private val mLifecycleOwner: LifecycleOwner) {
    /**
     * 真实的数据条数
     */
    private var mRealCount = 0
    /**
     * ViewPager的当前位置
     */
    private var mCurPosition = -1
    /**
     * 循环的时间间隔，毫秒。如果<=0，表示不循环播放。默认3000L
     */
    private var mCycleInterval: Long = 3000L

    private var mViewPager: BannerViewPager? = null
    private var mJob: Job? = null

    private val mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
        // position当前选择的是哪个页面。注意：如果mCount=1，那么默认会显示第0页，此时不会触发此方法，只会触发onPageScrolled方法。
        override fun onPageSelected(position: Int) {
            mCurPosition = position
            Log.d("tag", "BannerController onPageSelected mViewPager=${mViewPager.hashCode()} position=$position")
        }

        // position表示目标位置，positionOffset表示偏移的百分比，positionOffsetPixels表示偏移的像素
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageScrollStateChanged(state: Int) {
            when (state) {
                ViewPager.SCROLL_STATE_IDLE -> {// 页面停止在了某页，有可能是手指滑动一页结束，有可能是自动滑动一页结束，开始自动播放。
                    play()
                }
                ViewPager.SCROLL_STATE_DRAGGING -> {// 手指按下开始滑动，停止自动播放。
                    pause()
                }
                ViewPager.SCROLL_STATE_SETTLING -> {// 页面开始自动滑动
                }
            }
        }
    }

    fun setCycleInterval(interval: Long): BannerController {
        mCycleInterval = interval
        return this
    }

    fun setPosition(position: Int) {
        mCurPosition = position
    }

    fun getPosition(): Int = mCurPosition

    /**
     * @param viewPager [BannerViewPager] 类型，它必须已经设置了 [BannerPagerAdapter]。
     */
    @MainThread
    fun setViewPager(viewPager: BannerViewPager): BannerController {
        val adapter = viewPager.adapter
        require(adapter is BannerPagerAdapter) { "adapter of viewPager must be com.like.banner.BannerPagerAdapter" }
        mRealCount = adapter.getRealCount()
        viewPager.mBannerController = this
        mViewPager = viewPager
        when {
            mRealCount == 1 -> { // 如果只有一个页面，就限制 ViewPager 不能手动滑动
                viewPager.setScrollable(false)// 如果不设置，那么即使viewpager在只有一个页面时不能滑动，但是还是会触发onPageScrolled、onPageScrollStateChanged方法
            }
            mRealCount > 1 -> {
                viewPager.setScrollable(true)
                viewPager.addOnPageChangeListener(mOnPageChangeListener)
                if (mCurPosition == -1) {// 如果没有设置position，就初始化。
                    // 取余处理，避免默认值不能被 mDataCount 整除，从而不能让初始时在第0个位置。
                    mCurPosition = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2) % mRealCount
                }
                // 因为页面变化，所以setCurrentItem方法能触发onPageSelected、onPageScrolled方法，
                // 但是不能触发 onPageScrollStateChanged，所以不会启动自动播放，由使用者手动开启自动播放
                viewPager.currentItem = mCurPosition
            }
        }
        return this
    }

    fun play() {
        if (mCycleInterval <= 0) return
        if (mRealCount <= 1) return
        val viewPager = mViewPager ?: return
        if (mJob == null) {
            setViewPagerFirstLayoutFalse(viewPager)
            mJob = mLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                while (isActive) {
                    delay(mCycleInterval)
                    mCurPosition++
                    viewPager.setCurrentItem(mCurPosition, true)
                }
            }
        }
    }

    fun pause() {
        mJob?.cancel()
        mJob = null
    }

    /**
     * 设置 [ViewPager] 的 [ViewPager.mFirstLayout] 属性为 false。否则无法触发 [ViewPager.scrollToItem] 方法，从而进行平滑滚动。
     * 因为在 [ViewPager.onAttachedToWindow] 中把 [ViewPager.mFirstLayout] 属性设置为了 true。所以在重新显示 banner 时，没有动画效果。
     */
    private fun setViewPagerFirstLayoutFalse(viewPager: ViewPager) {
        try {
            val field = ViewPager::class.java.getDeclaredField("mFirstLayout")
            field.isAccessible = true
            field.set(viewPager, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

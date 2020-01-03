package com.like.banner

import androidx.viewpager.widget.ViewPager
import com.like.banner.utils.WeakHandler
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 通过 [BannerViewPager]、[BannerPagerAdapter] 控制 Banner 进行无限轮播
 */
class BannerController {
    /**
     * 是否正在自动循环播放
     */
    private val mIsAutoPlaying = AtomicBoolean(false)
    /**
     * 真实的数据条数
     */
    private var mRealCount = 0
    /**
     * ViewPager的当前位置
     */
    private var mCurPosition = Int.MAX_VALUE / 2
    /**
     * 循环的时间间隔，毫秒。如果<=0，表示不循环播放。默认3000L
     */
    private var mCycleInterval: Long = 3000L

    private var mViewPager: BannerViewPager? = null

    private val mCycleHandler: WeakHandler by lazy {
        WeakHandler {
            if (mIsAutoPlaying.get() && mCycleInterval > 0 && mRealCount > 1) {
                mViewPager?.let {
                    mCurPosition++
                    it.setCurrentItem(mCurPosition, true)
                    mCycleHandler.sendEmptyMessageDelayed(0, mCycleInterval)
                }
            }
            true
        }
    }

    private val mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
        // position当前选择的是哪个页面。注意：如果mCount=1，那么默认会显示第0页，此时不会触发此方法，只会触发onPageScrolled方法。
        override fun onPageSelected(position: Int) {
            mCurPosition = position
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

    /**
     * @param viewPager [BannerViewPager] 类型，它必须已经设置了 [BannerPagerAdapter]。
     */
    fun setViewPager(viewPager: BannerViewPager): BannerController {
        val adapter = viewPager.adapter
        require(adapter is BannerPagerAdapter) { "adapter of viewPager must be com.like.banner.BannerPagerAdapter" }
        mRealCount = adapter.getRealCount()
        mViewPager = viewPager
        when {
            mRealCount == 1 -> { // 如果只有一个页面，就限制 ViewPager 不能手动滑动
                viewPager.setScrollable(false)// 如果不设置，那么即使viewpager在只有一个页面时不能滑动，但是还是会触发onPageScrolled、onPageScrollStateChanged方法
            }
            mRealCount > 1 -> {
                viewPager.setScrollable(true)
                viewPager.addOnPageChangeListener(mOnPageChangeListener)
                // 因为页面变化，所以setCurrentItem方法能触发onPageSelected、onPageScrolled方法，
                // 但是不能触发 onPageScrollStateChanged，所以不会启动自动播放，由使用者手动开启自动播放
                mCurPosition -= mCurPosition % mRealCount// 取余处理，避免默认值不能被 mDataCount 整除
                viewPager.currentItem = mCurPosition
            }
        }
        return this
    }

    fun play() {
        if (mCycleInterval <= 0) return
        if (mRealCount <= 1) return
        if (mIsAutoPlaying.compareAndSet(false, true)) {
            mCycleHandler.removeCallbacksAndMessages(null)
            mCycleHandler.sendEmptyMessageDelayed(0, mCycleInterval)
        }
    }

    fun pause() {
        if (mIsAutoPlaying.compareAndSet(true, false)) {
            mCycleHandler.removeCallbacksAndMessages(null)
        }
    }

}

package com.like.banner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.like.banner.Banner.Companion.mAutoLoop
import com.like.banner.indicator.IBannerIndicator
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 对[ViewPager2]进行了封装，使其支持无限滚动轮播图，必须配合[BannerAdapter]使用。
 * 自动轮播功能是仿照[android.widget.ViewFlipper]来实现的。
 *
 * 自定义的属性包括：
 * @attr ref android.R.styleable#BannerViewPager_cycle_interval     [mCycleInterval]
 * 循环的时间间隔，毫秒。默认3000，如果小于等于0，也会设置为默认值3000
 * @attr ref android.R.styleable#BannerViewPager_auto_loop          [mAutoLoop]
 * 是否开启自动无限轮播
 */
open class Banner(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    companion object {
        private const val DEFAULT_CIRCLE_INTERVAL = 3000
        private const val DEFAULT_AUTO_LOOP = true
        internal const val MAX_COUNT = 10000// 注意：设置太大了会在 setCurrentItem 造成 ANR，但是不知道为什么，在 RecyclerView 中使用时又不会卡。
        internal var mAutoLoop: Boolean = DEFAULT_AUTO_LOOP
    }

    private lateinit var mViewPager2: ViewPager2
    private var mCycleInterval: Int = DEFAULT_CIRCLE_INTERVAL
    private var mRunning = AtomicBoolean(false)// 是否正在轮播
    private var mVisible = false// ViewPager是否可见
    private var mUserPresent = true// 手机屏幕对用户是否可见
    private var mScrollable = false// 是否可以滚动

    /**
     * 真实的数据条数
     */
    private var mRealCount = 0

    /**
     * ViewPager的当前位置
     */
    private var mCurPosition = -1

    /**
     * 指示器
     */
    private var mBannerIndicator: IBannerIndicator? = null

    private val mScrollRunnable: Runnable = object : Runnable {
        override fun run() {
            mViewPager2.setCurrentItem(++mCurPosition, true)
            postDelayed(this, mCycleInterval.toLong())
        }
    }

    /**
     * 设置指示器。
     * 库里默认实现了四种指示器：
     * [com.like.banner.indicator.ImageIndicator]、
     * [com.like.banner.indicator.StickyDotBezierCurveIndicator]、
     * [com.like.banner.indicator.StickyRoundRectIndicator]、
     * [com.like.banner.indicator.CircleTextIndicator]
     */
    fun setBannerIndicator(bannerIndicator: IBannerIndicator) {
        mBannerIndicator = bannerIndicator
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (Intent.ACTION_SCREEN_OFF == action) {
                mUserPresent = false
                stop()
            } else if (Intent.ACTION_USER_PRESENT == action) {
                mUserPresent = true
                play()
            }
        }
    }
    private val mOnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        fun getRealPosition(position: Int): Int = if (mRealCount == 0) 0 else position % mRealCount

        // position当前选择的是哪个页面。注意：如果mCount=1，那么默认会显示第0页，此时不会触发此方法，只会触发onPageScrolled方法。
        override fun onPageSelected(position: Int) {
            mCurPosition = position
            mBannerIndicator?.onPageSelected(getRealPosition(position))
        }

        // position表示目标位置，positionOffset表示偏移的百分比，positionOffsetPixels表示偏移的像素
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            mBannerIndicator?.onPageScrolled(getRealPosition(position), positionOffset, positionOffsetPixels)
        }

        override fun onPageScrollStateChanged(state: Int) {
            when (state) {
                ViewPager2.SCROLL_STATE_IDLE -> {// 页面停止在了某页，有可能是手指滑动一页结束，有可能是自动滑动一页结束，开始自动播放。
                    play()
                }
                ViewPager2.SCROLL_STATE_DRAGGING -> {// 手指按下开始滑动，停止自动播放。
                    stop()
                }
                ViewPager2.SCROLL_STATE_SETTLING -> {// 页面开始自动滑动
                }
            }
            mBannerIndicator?.onPageScrollStateChanged(state)
        }
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BannerViewPager)
        mCycleInterval = a.getInt(R.styleable.BannerViewPager_cycle_interval, DEFAULT_CIRCLE_INTERVAL)
        mAutoLoop = a.getBoolean(R.styleable.BannerViewPager_auto_loop, DEFAULT_AUTO_LOOP)
        if (mCycleInterval <= 0) {
            mCycleInterval = DEFAULT_CIRCLE_INTERVAL
        }
        a.recycle()
        mViewPager2 = ViewPager2(context, attrs).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            registerOnPageChangeCallback(mOnPageChangeCallback)
            // 必须设置这个，否则在使用setPageTransformer()并配合android:clipChildren="false"来使用时，
            // 会发现由于没有缓存，导致每次都要初始化下一页，从而使得下一页的页面每次都是初始状态，不能达到setPageTransformer()的效果。
            // 如果有缓存的话，那么setPageTransformer()的动画效果就会作用于缓存的页面，从而正确显示效果。
            offscreenPageLimit = 1
            this@Banner.addView(this)
        }

    }

    /**
     * 注意：此方法必须放在最后调用，否则刷新时会造成 Indicator 位置显示错乱。
     */
    fun setAdapter(adapter: BannerAdapter<*, *>) {
        require(adapter is BannerAdapter) { "adapter of viewPager must be com.like.banner.BannerAdapter" }
        val oldData = (mViewPager2.adapter as? BannerAdapter<*, *>)?.currentList
        mViewPager2.adapter = adapter
        stop()
        mRealCount = adapter.currentList.size
        when {
            mRealCount == 1 -> { // 如果只有一个页面，就限制 ViewPager 不能手动滑动
                mScrollable = false// 如果不设置，那么即使viewpager在只有一个页面时不能滑动，但是还是会触发onPageScrolled、onPageScrollStateChanged方法
                mViewPager2.setCurrentItem(0, false)
            }
            mRealCount > 1 -> {
                mScrollable = true
                if (adapter.currentList != oldData) {
                    // 取余处理，避免默认值不能被 mDataCount 整除，从而不能让初始时在第0个位置。
                    mCurPosition = if (mAutoLoop) {
                        MAX_COUNT / 2 - (MAX_COUNT / 2) % mRealCount
                    } else {
                        0
                    }
                }
                mViewPager2.setCurrentItem(mCurPosition, false)
                play()
            }
        }
    }

    // 注意：如果不是在 RecyclerView 中使用的话，那么调用此方法的时候还没有 setAdapter，那么就不会触发自动轮播，因为 mScrollable 为 false
    final override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Listen for broadcasts related to user-presence
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        context.registerReceiver(mReceiver, filter, null, handler)
        play()
    }

    final override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mVisible = false
        context.unregisterReceiver(mReceiver)
        stop()
    }

    final override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        mVisible = visibility == View.VISIBLE
        if (mVisible) {
            play()
        }
    }

    /**
     * 开始轮播
     */
    private fun play() {
        if (mVisible && mUserPresent && mScrollable && mAutoLoop) {
            if (mRunning.compareAndSet(false, true)) {
                // 因为页面变化，所以setCurrentItem方法能触发onPageSelected、onPageScrolled方法，
                // 但是不能触发 onPageScrollStateChanged，所以不会启动自动播放，由使用者手动开启自动播放
                postDelayed(mScrollRunnable, mCycleInterval.toLong())
            }
        }
    }

    private fun stop() {
        if (mRunning.compareAndSet(true, false)) {
            removeCallbacks(mScrollRunnable)
        }
    }

    final override fun onTouchEvent(ev: MotionEvent): Boolean {
        return mScrollable && super.onTouchEvent(ev)
    }

    final override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return mScrollable && super.onInterceptTouchEvent(ev)
    }

    /**
     * 设置ViewPager切换速度
     *
     * @param duration 默认300毫秒
     */
    fun setScrollSpeed(duration: Int = 300, interpolator: Interpolator = AccelerateInterpolator()) {
        try {
            val field = ViewPager::class.java.getDeclaredField("mScroller")
            field.isAccessible = true
            field.set(this, FixedSpeedScroller(context, interpolator, duration))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 加速滚动的Scroller
     */
    private class FixedSpeedScroller(context: Context, interpolator: Interpolator, private val mDuration: Int) :
        Scroller(context.applicationContext, interpolator) {

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, mDuration)
        }
    }
}

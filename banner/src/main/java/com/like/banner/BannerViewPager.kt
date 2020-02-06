package com.like.banner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.widget.Scroller
import android.widget.ViewFlipper
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.like.banner.indicator.IBannerIndicator

/**
 * 可以控制是否左右滑动的ViewPager[.setScrollable]，默认不能滑动
 */
open class BannerViewPager(context: Context, attrs: AttributeSet?) : androidx.viewpager.widget.ViewPager(context, attrs) {
    companion object {
        private const val DEFAULT_INTERVAL = 3000
    }

    // 高宽比例
    private var mHeightWidthRatio = 0f
    /**
     * 循环的时间间隔，毫秒。如果<=0，表示不循环播放。默认3000L
     */
    private var mCycleInterval: Int = DEFAULT_INTERVAL
    private var mAutoStart = false

    private var mRunning = false
    private var mStarted = false
    private var mVisible = false
    private var mUserPresent = true

    private var mScrollable = false
    /**
     * 真实的数据条数
     */
    private var mRealCount = 0
    /**
     * ViewPager的当前位置
     */
    private var mCurPosition = -1
    private var mBannerIndicator: IBannerIndicator? = null

    fun setBannerIndicator(bannerIndicator: IBannerIndicator) {
        mBannerIndicator = bannerIndicator
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (Intent.ACTION_SCREEN_OFF == action) {
                mUserPresent = false
                updateRunning()
            } else if (Intent.ACTION_USER_PRESENT == action) {
                mUserPresent = true
                updateRunning()
            }
        }
    }
    private val mOnPageChangeListener = object : OnPageChangeListener {
        fun getRealPosition(position: Int): Int = position % mRealCount
        // position当前选择的是哪个页面。注意：如果mCount=1，那么默认会显示第0页，此时不会触发此方法，只会触发onPageScrolled方法。
        override fun onPageSelected(position: Int) {
            mCurPosition = position
            mBannerIndicator?.onPageSelected(getRealPosition(position))
            Log.i("BannerViewPager", "onPageSelected position=$position")
        }

        // position表示目标位置，positionOffset表示偏移的百分比，positionOffsetPixels表示偏移的像素
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            mBannerIndicator?.onPageScrolled(getRealPosition(position), positionOffset, positionOffsetPixels)
        }

        override fun onPageScrollStateChanged(state: Int) {
            Log.w("BannerViewPager", "onPageScrollStateChanged state=$state")
            when (state) {
                SCROLL_STATE_IDLE -> {// 页面停止在了某页，有可能是手指滑动一页结束，有可能是自动滑动一页结束，开始自动播放。
                    play()
                }
                SCROLL_STATE_DRAGGING -> {// 手指按下开始滑动，停止自动播放。
                    stop()
                }
                SCROLL_STATE_SETTLING -> {// 页面开始自动滑动
                }
            }
            mBannerIndicator?.onPageScrollStateChanged(state)
        }
    }

    init {
        // 获取高宽比例
        val a = context.obtainStyledAttributes(attrs, R.styleable.BannerViewPager)
        mHeightWidthRatio = a.getFloat(R.styleable.BannerViewPager_height_width_ratio, 0f)
        mCycleInterval = a.getInt(R.styleable.BannerViewPager_cycle_interval, DEFAULT_INTERVAL)
        mAutoStart = a.getBoolean(R.styleable.BannerViewPager_auto_start, false)
        a.recycle()
        // 必须设置这个，否则在使用setPageTransformer()并配合android:clipChildren="false"来使用时，
        // 会发现由于没有缓存，导致每次都要初始化下一页，从而使得下一页的页面每次都是初始状态，不能达到setPageTransformer()的效果。
        // 如果有缓存的话，那么setPageTransformer()的动画效果就会作用于缓存的页面，从而正确显示效果。
        offscreenPageLimit = 3
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        Log.e("BannerViewPager", "setAdapter")
        val oldData = (getAdapter() as? BannerPagerAdapter)?.getData()
        super.setAdapter(adapter)
        require(adapter is BannerPagerAdapter) { "adapter of viewPager must be com.like.banner.BannerPagerAdapter" }
        mRealCount = adapter.getRealCount()
        removeOnPageChangeListener(mOnPageChangeListener)
        when {
            mRealCount == 1 -> { // 如果只有一个页面，就限制 ViewPager 不能手动滑动
                setScrollable(false)// 如果不设置，那么即使viewpager在只有一个页面时不能滑动，但是还是会触发onPageScrolled、onPageScrollStateChanged方法
            }
            mRealCount > 1 -> {
                setScrollable(true)
                addOnPageChangeListener(mOnPageChangeListener)
                if (!adapter.isSameData(oldData)) {// 如果不是相同的数据，证明是刷新操作，而不是RecyclerView的复用操作。
                    // 取余处理，避免默认值不能被 mDataCount 整除，从而不能让初始时在第0个位置。
                    mCurPosition = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2) % mRealCount
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        Log.e("BannerViewPager", "onAttachedToWindow")
        super.onAttachedToWindow()
        // Listen for broadcasts related to user-presence
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        context.registerReceiver(mReceiver, filter, null, handler)
        if (mAutoStart) { // Automatically start when requested
            play()
        }
    }

    override fun onDetachedFromWindow() {
        Log.e("BannerViewPager", "onDetachedFromWindow")
        super.onDetachedFromWindow()
        mVisible = false
        context.unregisterReceiver(mReceiver)
        updateRunning()
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        Log.e("BannerViewPager", "onWindowVisibilityChanged visibility=$visibility")
        super.onWindowVisibilityChanged(visibility)
        mVisible = visibility == View.VISIBLE
        updateRunning()
    }

    fun play() {
        Log.e("BannerViewPager", "play")
        mStarted = true
        updateRunning()
    }

    fun stop() {
        Log.e("BannerViewPager", "stop")
        mStarted = false
        updateRunning()
    }

    private fun updateRunning() {
        Log.w("BannerViewPager", "updateRunning")
        val running = mVisible && mStarted && mUserPresent && mScrollable
        if (running != mRunning) {
            if (running) {
                // 因为页面变化，所以setCurrentItem方法能触发onPageSelected、onPageScrolled方法，
                // 但是不能触发 onPageScrollStateChanged，所以不会启动自动播放，由使用者手动开启自动播放
                currentItem = mCurPosition
                postDelayed(mFlipRunnable, mCycleInterval.toLong())
            } else {
                removeCallbacks(mFlipRunnable)
            }
            mRunning = running
        }
    }

    private val mFlipRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mRunning) {
                Log.v("BannerViewPager", "mFlipRunnable")
                mCurPosition++
                setCurrentItem(mCurPosition, true)
                postDelayed(this, mCycleInterval.toLong())
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var hms = heightMeasureSpec
        if (mHeightWidthRatio > 0f) {
            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            if (widthMode == MeasureSpec.EXACTLY) {
                val heightSize = (widthSize * mHeightWidthRatio).toInt()
                hms = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
            }
        }
        super.onMeasure(widthMeasureSpec, hms)
    }

    fun setScrollable(isScrollable: Boolean) {
        this.mScrollable = isScrollable
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return mScrollable && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return mScrollable && super.onInterceptTouchEvent(ev)
    }

    /**
     * 设置ViewPager切换速度
     *
     * @param duration 默认300毫秒
     */
    fun setScrollSpeed(duration: Int = 300) {
        try {
            val field = ViewPager::class.java.getDeclaredField("mScroller")
            field.isAccessible = true
            field.set(this, FixedSpeedScroller(context, AccelerateInterpolator(), duration))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 加速滚动的Scroller
     */
    class FixedSpeedScroller(context: Context, interpolator: Interpolator, private val mDuration: Int) :
        Scroller(context.applicationContext, interpolator) {

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, mDuration)
        }
    }
}

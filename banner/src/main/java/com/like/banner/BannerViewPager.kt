package com.like.banner

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager

/**
 * 可以控制是否左右滑动的ViewPager[.setScrollable]，默认不能滑动
 */
open class BannerViewPager(context: Context, attrs: AttributeSet?) :
    androidx.viewpager.widget.ViewPager(context, attrs) {
    private var isScrollable = false
    // 高宽比例
    private var mHeightWidthRatio = 0f

    init {
        // 获取高宽比例
        val a = context.obtainStyledAttributes(attrs, R.styleable.BannerViewPager)
        mHeightWidthRatio = a.getFloat(R.styleable.BannerViewPager_height_width_ratio, 0f)
        a.recycle()
        // 必须设置这个，否则在使用setPageTransformer()并配合android:clipChildren="false"来使用时，
        // 会发现由于没有缓存，导致每次都要初始化下一页，从而使得下一页的页面每次都是初始状态，不能达到setPageTransformer()的效果。
        // 如果有缓存的话，那么setPageTransformer()的动画效果就会作用于缓存的页面，从而正确显示效果。
        offscreenPageLimit = 3
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
        this.isScrollable = isScrollable
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return isScrollable && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return isScrollable && super.onInterceptTouchEvent(ev)
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

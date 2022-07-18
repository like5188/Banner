package com.like.banner.indicator

/**
 * Banner 的指示器基类。
 *
 * 库里默认实现了四种指示器：
 * [com.like.banner.indicator.ImageIndicator]、
 * [com.like.banner.indicator.StickyDotBezierCurveIndicator]、
 * [com.like.banner.indicator.StickyRoundRectIndicator]、
 * [com.like.banner.indicator.CircleTextIndicator]
 */
interface IBannerIndicator {

    fun onPageSelected(position: Int) {
    }

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    fun onPageScrollStateChanged(state: Int) {
    }
}
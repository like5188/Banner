package com.like.banner.indicator

/**
 * Banner 的指示器基类。
 */
interface IBannerIndicator {

    fun onPageSelected(position: Int) {
    }

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    fun onPageScrollStateChanged(state: Int) {
    }
}
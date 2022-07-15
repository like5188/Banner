package com.like.banner.indicator

import androidx.viewpager.widget.ViewPager

/**
 * Banner 的指示器基类。
 */
interface IBannerIndicator : ViewPager.OnPageChangeListener {

    override fun onPageSelected(position: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }
}
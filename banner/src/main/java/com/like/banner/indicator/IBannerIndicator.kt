package com.like.banner.indicator

import androidx.viewpager.widget.ViewPager
import com.like.banner.BannerPagerAdapter
import com.like.banner.BannerViewPager

/**
 * Banner 的指示器基类。
 */
interface IBannerIndicator : ViewPager.OnPageChangeListener {

    /**
     * 设置指示器的高度。dp
     */
    fun setIndicatorHeight(height: Float)

    override fun onPageSelected(position: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }
}
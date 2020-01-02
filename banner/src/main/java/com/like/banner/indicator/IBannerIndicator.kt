package com.like.banner.indicator

import androidx.viewpager.widget.ViewPager
import com.like.banner.BannerPagerAdapter
import com.like.banner.BannerViewPager

/**
 * Banner 的指示器基类。
 * 使用方式：调用 [setViewPager] 方法，和 [com.like.banner.BannerController] 设置同一个 [com.like.banner.BannerViewPager] 即可。
 */
interface IBannerIndicator {

    /**
     * @param viewPager [BannerViewPager] 类型，它必须已经设置了 [BannerPagerAdapter]。
     */
    fun setViewPager(viewPager: BannerViewPager) {
        val adapter = viewPager.adapter
        require(adapter is BannerPagerAdapter) { "adapter of viewPager must be com.like.banner.BannerPagerAdapter" }
        val realCount = adapter.getRealCount()
        if (realCount > 0) {
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                fun getRealPosition(position: Int): Int = position % realCount

                override fun onPageScrollStateChanged(state: Int) {
                    this@IBannerIndicator.onPageScrollStateChanged(state)
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    if (realCount == 1) {
                        // 如果只有一个页面，那么setCurrentItem(0)无法触发onPageSelected方法，因为页面没有变化，只能触发onPageScrolled()方法。
                        // 所以要单独处理来触发指示器的显示效果，因为一般指示器的操作都是在onPageSelected方法中的。
                        this@IBannerIndicator.onPageSelected(getRealPosition(position))
                    } else {
                        this@IBannerIndicator.onPageScrolled(getRealPosition(position), positionOffset, positionOffsetPixels)
                    }
                }

                override fun onPageSelected(position: Int) {
                    this@IBannerIndicator.onPageSelected(getRealPosition(position))
                }
            })
        }
    }

    /**
     * 设置指示器的高度。dp
     */
    fun setIndicatorHeight(height: Float)

    fun onPageScrollStateChanged(state: Int) {}

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    fun onPageSelected(position: Int) {}

}
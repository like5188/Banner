package com.like.banner

import android.view.View
import android.view.ViewGroup

/**
 * 无限滚动轮播图[BannerViewPager]的适配器。
 */
abstract class BannerPagerAdapter(private val mList: List<*>) : androidx.viewpager.widget.PagerAdapter() {
    internal fun getRealCount(): Int = mList.size

    /**
     * 是否是相同的数据
     * 如果不是，则说明是刷新操作。
     * 如果是，则说明是RecyclerView的复用操作。
     */
    internal fun isSameData(list: List<*>?): Boolean {
        return list == mList
    }

    internal fun getData() = mList

    final override fun getCount(): Int = if (BannerViewPager.mAutoLoop) BannerViewPager.MAX_COUNT else mList.size

    final override fun isViewFromObject(p0: View, p1: Any): Boolean = p0 == p1

    final override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val realPosition = if (mList.isEmpty()) 0 else position % mList.size
        val view = onInstantiateItem(realPosition)
        container.addView(view)
        return view
    }

    final override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    abstract fun onInstantiateItem(position: Int): View

}
package com.like.banner

import android.view.View
import android.view.ViewGroup

abstract class BannerPagerAdapter(private val mList: List<*>) : androidx.viewpager.widget.PagerAdapter() {
    fun getRealCount(): Int = mList.size

    fun getRealPosition(position: Int): Int = position % getRealCount()

    final override fun getCount(): Int = Int.MAX_VALUE

    final override fun isViewFromObject(p0: View, p1: Any): Boolean = p0 == p1

    final override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = onInstantiateItem(getRealPosition(position))
        container.addView(view)
        return view
    }

    final override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    abstract fun onInstantiateItem(position: Int): View

}
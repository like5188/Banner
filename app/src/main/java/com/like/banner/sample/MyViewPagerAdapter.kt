package com.like.banner.sample

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.like.common.util.GlideUtils

class MyViewPagerAdapter(context: Context, private val list: List<BannerInfo>) :
    androidx.viewpager.widget.PagerAdapter() {
    private val layoutInflater = LayoutInflater.from(context)
    private val mGlideUtils = GlideUtils(context)

    override fun getCount(): Int = list.size

    override fun isViewFromObject(p0: View, p1: Any): Boolean = p0 == p1

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return layoutInflater.inflate(R.layout.item_banner, null).apply {
            val iv = findViewById<ImageView>(R.id.iv)
            val info = list[position]
            mGlideUtils.display(info.imageUrl, iv)
            iv.setOnClickListener {
                Log.d("BannerPagerAdapter", info.toString())
            }
            container.addView(this)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}
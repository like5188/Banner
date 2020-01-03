package com.like.banner.sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.like.banner.BannerPagerAdapter
import com.like.banner.sample.databinding.ItemBannerBinding
import com.like.common.util.GlideUtils

class MyBannerPagerAdapter(context: Context, private val list: List<BannerInfo.Banner>) : BannerPagerAdapter(list) {
    private val mLayoutInflater by lazy { LayoutInflater.from(context) }
    private val mGlideUtils by lazy { GlideUtils(context) }

    override fun onInstantiateItem(position: Int): View {
        val binding: ItemBannerBinding = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_banner, null, false)
        val info = list[position]
        mGlideUtils.display(info.imagePath, binding.iv)
        return binding.root
    }

}
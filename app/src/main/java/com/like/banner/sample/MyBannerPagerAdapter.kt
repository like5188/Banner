package com.like.banner.sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import coil.load
import com.like.banner.BannerPagerAdapter
import com.like.banner.sample.databinding.ItemBannerBinding

class MyBannerPagerAdapter(context: Context, private val list: List<BannerInfo.Banner>) : BannerPagerAdapter(list) {
    private val mLayoutInflater by lazy { LayoutInflater.from(context) }

    override fun onInstantiateItem(position: Int): View {
        val binding: ItemBannerBinding = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_banner, null, false)
        val info = list[position]
        binding.iv.load(info.imagePath)
        return binding.root
    }

}
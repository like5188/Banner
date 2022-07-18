package com.like.banner.sample

import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.like.banner.BannerAdapter
import com.like.banner.BindingViewHolder
import com.like.banner.sample.databinding.ItemBannerBinding

class MyBannerAdapter : BannerAdapter<ItemBannerBinding, BannerInfo.Banner>(
    object : DiffUtil.ItemCallback<BannerInfo.Banner>() {
        override fun areItemsTheSame(oldItem: BannerInfo.Banner, newItem: BannerInfo.Banner): Boolean {
            return oldItem.imagePath == newItem.imagePath
        }

        override fun areContentsTheSame(oldItem: BannerInfo.Banner, newItem: BannerInfo.Banner): Boolean {
            return true
        }
    }
) {

    override fun onBindViewHolder(holder: BindingViewHolder<ItemBannerBinding>, item: BannerInfo.Banner) {
        holder.binding.iv.load(item.imagePath)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_banner
    }

}
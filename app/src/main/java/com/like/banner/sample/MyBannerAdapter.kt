package com.like.banner.sample

import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.like.banner.sample.databinding.ItemBannerBinding
import com.like.recyclerview.adapter.BaseListAdapter
import com.like.recyclerview.viewholder.BindingViewHolder

class MyBannerAdapter : BaseListAdapter<ItemBannerBinding, BannerInfo.Banner>(
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

}
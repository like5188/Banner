package com.like.banner.sample

import com.like.recyclerview.model.IRecyclerViewItem

class BannerInfo : IRecyclerViewItem {
    val bannerList = mutableListOf<Banner>()
    override val layoutId: Int = R.layout.view_banner

    data class Banner(val imagePath: String) : IRecyclerViewItem {
        override val layoutId: Int = R.layout.item_banner
    }
}
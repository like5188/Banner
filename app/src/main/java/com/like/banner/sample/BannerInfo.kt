package com.like.banner.sample

import com.like.recyclerview.model.IHeader

class BannerInfo : IHeader {
    val bannerList = mutableListOf<Banner>()
    override val layoutId: Int = R.layout.view_banner

    data class Banner(val imagePath: String)
}
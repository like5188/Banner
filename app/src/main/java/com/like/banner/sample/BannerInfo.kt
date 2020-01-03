package com.like.banner.sample

import com.like.livedatarecyclerview.model.IHeader

class BannerInfo : IHeader {
    val bannerList = mutableListOf<Banner>()
    override val layoutId: Int = R.layout.view_banner
    override val variableId: Int = -1

    data class Banner(val imagePath: String)
}
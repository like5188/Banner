package com.like.banner.sample

import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    private val bannerDataSource = BannerDataSource()
    val myLoadAfterResult = MyDataSource().pagingResult()
    suspend fun getBannerData() = bannerDataSource.load()
}
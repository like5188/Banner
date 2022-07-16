package com.like.banner.sample

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.flow

class MyViewModel : ViewModel() {
    private val bannerDataSource = BannerDataSource()
    val myLoadAfterResult = MyDataSource().pagingResult()
    fun getBannerInfoFlow() = flow {
        emit(bannerDataSource.load())
    }
}
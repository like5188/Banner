package com.like.banner.sample

import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    private val myLoadAfterDataSource = MyLoadAfterDataSource()
    val myLoadAfterResult = myLoadAfterDataSource.result()

}
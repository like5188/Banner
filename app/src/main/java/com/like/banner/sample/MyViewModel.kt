package com.like.banner.sample

import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    private val myLoadAfterDataSource = MyDataSource()
    val myLoadAfterResult = myLoadAfterDataSource.result()

}
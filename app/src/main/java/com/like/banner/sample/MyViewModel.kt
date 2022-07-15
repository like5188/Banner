package com.like.banner.sample

import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    val myLoadAfterResult = MyDataSource().pagingResult()
}
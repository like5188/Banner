package com.like.banner.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class MyViewModel : ViewModel() {
    private val myLoadAfterDataSource = MyLoadAfterDataSource(viewModelScope)
    val myLoadAfterResult = myLoadAfterDataSource.result()

}
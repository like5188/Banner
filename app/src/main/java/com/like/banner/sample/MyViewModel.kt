package com.like.banner.sample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.like.repository.ResultFactory

class MyViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 10
    }

    private val customPagingLoadAfterResult = ResultFactory.getCustomPagingMemoryResult(
        MyLoadAfterDataSourceImpl(viewModelScope, MutableLiveData(), PAGE_SIZE)
    )

    fun getResult() = customPagingLoadAfterResult

    fun loadInitial() {
        getResult().loadInitial?.invoke()
    }

    fun refresh() {
        getResult().refresh.invoke()
    }

    fun loadAfter() {
        getResult().loadAfter?.invoke()
    }

    fun loadBefore() {
        getResult().loadBefore?.invoke()
    }

    fun retry() {
        getResult().retry.invoke()
    }

    /**
     * 如果获取ViewModel需要参数，就自定义一个Factory类。或者通过定义公共方法传参。
     */
    class Factory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
                return MyViewModel() as T
            }
            throw IllegalArgumentException("Unknown MainViewModel class")
        }
    }

}
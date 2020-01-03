package com.like.banner.sample

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.like.common.util.NETWORK_EXECUTOR
import com.like.livedatarecyclerview.model.IFooter
import com.like.livedatarecyclerview.model.IHeader
import com.like.livedatarecyclerview.model.IRecyclerViewItem
import com.like.repository.custompaging.memory.CustomLoadAfterDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

class MyLoadAfterDataSourceImpl(
    coroutineScope: CoroutineScope,
    liveValue: MutableLiveData<List<IRecyclerViewItem>>,
    pageSize: Int
) : CustomLoadAfterDataSource<IRecyclerViewItem>(
    coroutineScope,
    liveValue,
    pageSize,
    NETWORK_EXECUTOR
) {
    override suspend fun onLoadInitial(isRefresh: Boolean, pageSize: Int): List<IRecyclerViewItem> {
        delay(2000)
        return getInitialData(0, 10)
    }

    override suspend fun getInitialPage(): Int {
        return 0
    }

    override suspend fun onLoadAfter(page: Int, pageSize: Int): List<IRecyclerViewItem> {
        delay(2000)
        return getAfter(page, pageSize)
    }

    private fun getInitialData(page: Int, pageSize: Int): List<IRecyclerViewItem> {
        val start = page * pageSize + 1
        val end = start + pageSize
        val result = mutableListOf<IRecyclerViewItem>()
        val items = (start until end).map {
            Book(
                id = it,
                name = "Book name $it",
                des = "des $it"
            )
        }

        val headers = mutableListOf<IHeader>()
        val bannerInfo = BannerInfo()
        bannerInfo.bannerList.add(BannerInfo.Banner("https://mall02.sogoucdn.com/image/2019/03/18/20190318094408_4590.png"))
        bannerInfo.bannerList.add(BannerInfo.Banner("https://mall03.sogoucdn.com/image/2019/05/13/20190513191053_4977.png"))
        bannerInfo.bannerList.add(BannerInfo.Banner("https://mall03.sogoucdn.com/image/2018/12/21/20181221191646_4221.png"))
        headers.add(bannerInfo)

        val footers = mutableListOf<IFooter>()
        footers.add(Footer(ObservableInt(0)))

        result.addAll(headers)
        result.addAll(items)
        result.addAll(footers)
        return result
    }

    private fun getAfter(page: Int, pageSize: Int): List<IRecyclerViewItem> {
        val start = page * pageSize + 1
        val end = start + pageSize
        return (start until end).map {
            Book(
                id = it,
                name = "name $it",
                des = "des $it"
            )
        }
    }

}
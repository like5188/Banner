package com.like.banner.sample

import com.like.recyclerview.model.IHeader
import com.like.recyclerview.model.IItem
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.repository.RequestType
import com.like.repository.paging.byPageNo.PageNoKeyedPagingDataSource
import com.like.repository.util.MultiDataSourceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

class MyLoadAfterDataSource(coroutineScope: CoroutineScope) :
    PageNoKeyedPagingDataSource<List<IRecyclerViewItem>?>(coroutineScope, 10) {
    private var i = 0

    override suspend fun load(requestType: RequestType, pageNo: Int, pageSize: Int): List<IRecyclerViewItem>? {
        return MultiDataSourceHelper.successIfAllSuccess(
            requestType,
            { getHeaders() },
            pagingBlock = { getItems(pageNo, pageSize) }
        )
    }

    override fun getInitialPage(): Int {
        return 0
    }

    private suspend fun getItems(page: Int, pageSize: Int): List<IItem> {
        delay(1000)
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

    private fun getHeaders(): List<IHeader> {
        val headers = mutableListOf<IHeader>()
        val bannerInfo = BannerInfo()
        when (i++) {
            0 -> {
            }
            1 -> {
                bannerInfo.bannerList.add(BannerInfo.Banner("https://mall02.sogoucdn.com/image/2019/03/18/20190318094408_4590.png"))
            }
            2 -> {
                bannerInfo.bannerList.add(BannerInfo.Banner("https://mall03.sogoucdn.com/image/2019/05/13/20190513191053_4977.png"))
                bannerInfo.bannerList.add(BannerInfo.Banner("https://mall03.sogoucdn.com/image/2018/12/21/20181221191646_4221.png"))
            }
            else -> {
                bannerInfo.bannerList.add(BannerInfo.Banner("https://www.wanandroid.com/blogimgs/c7a55d24-6fc1-4eb5-97c4-ee1ae694d175.png"))
                bannerInfo.bannerList.add(BannerInfo.Banner("https://www.wanandroid.com/blogimgs/90c6cc12-742e-4c9f-b318-b912f163b8d0.png"))
                bannerInfo.bannerList.add(BannerInfo.Banner("https://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png"))
            }
        }
        headers.add(bannerInfo)
        return headers
    }
}
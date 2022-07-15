package com.like.banner.sample

import com.like.common.util.successIfAllSuccess
import com.like.paging.RequestType
import com.like.paging.dataSource.byPageNoKeyed.PageNoKeyedPagingDataSource
import com.like.recyclerview.model.IRecyclerViewItem
import kotlinx.coroutines.delay

class MyDataSource : PageNoKeyedPagingDataSource<List<IRecyclerViewItem>?>(0, 10) {
    private var i = 0
    private var j = 0

    override suspend fun load(requestType: RequestType, pageNo: Int, pageSize: Int): List<IRecyclerViewItem>? {
        if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
            i = 0
        }
        return if (i++ == 0) {
            val result = mutableListOf<IRecyclerViewItem>()
            successIfAllSuccess(::getHeaders, { getItems(pageNo, pageSize) }).forEach {
                if (!it.isNullOrEmpty()) {
                    result.addAll(it)
                }
            }
            result
        } else {
            getItems(pageNo, pageSize)
        }
    }

    private suspend fun getItems(page: Int, pageSize: Int): List<IRecyclerViewItem>? {
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

    private fun getHeaders(): List<IRecyclerViewItem>? {
        val headers = mutableListOf<IRecyclerViewItem>()
        val bannerInfo = BannerInfo()
        when (j++) {
            0 -> {
            }
            1 -> {
                bannerInfo.bannerList.add(BannerInfo.Banner("https://mall02.sogoucdn.com/image/2019/03/18/20190318094408_4590.png"))
            }
            2 -> {
                bannerInfo.bannerList.add(BannerInfo.Banner("https://mall03.sogoucdn.com/image/2019/05/13/20190513191053_4977.png"))
                bannerInfo.bannerList.add(BannerInfo.Banner("https://mall03.sogoucdn.com/image/2018/12/21/20181221191646_4221.png"))
            }
            3 -> {
                bannerInfo.bannerList.add(BannerInfo.Banner("https://www.wanandroid.com/blogimgs/c7a55d24-6fc1-4eb5-97c4-ee1ae694d175.png"))
                bannerInfo.bannerList.add(BannerInfo.Banner("https://www.wanandroid.com/blogimgs/90c6cc12-742e-4c9f-b318-b912f163b8d0.png"))
                bannerInfo.bannerList.add(BannerInfo.Banner("https://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png"))
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
package com.like.banner.sample

import com.like.common.util.successIfAllSuccess
import com.like.paging.RequestType
import com.like.paging.dataSource.byPageNoKeyed.PageNoKeyedPagingDataSource
import com.like.recyclerview.model.IRecyclerViewItem
import kotlinx.coroutines.delay

class MyDataSource : PageNoKeyedPagingDataSource<List<IRecyclerViewItem>?>(0, 10) {
    private var i = 0
    private val bannerDataSource = BannerDataSource()

    override suspend fun load(requestType: RequestType, pageNo: Int, pageSize: Int): List<IRecyclerViewItem>? {
        if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
            i = 0
        }
        return if (i++ == 0) {
            val result = mutableListOf<IRecyclerViewItem>()
            successIfAllSuccess({
                val bannerInfo = bannerDataSource.load()
                if (bannerInfo != null) {
                    listOf(bannerInfo)
                } else {
                    emptyList()
                }
            }, { getItems(pageNo, pageSize) }).forEach {
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

}
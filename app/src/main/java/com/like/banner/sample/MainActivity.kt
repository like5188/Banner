package com.like.banner.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.like.banner.indicator.ImageIndicator
import com.like.banner.sample.databinding.ActivityMainBinding
import com.like.common.util.Logger
import com.like.common.util.dp
import com.like.recyclerview.adapter.CombineAdapter
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.ui.loadstate.LoadStateAdapter
import com.like.recyclerview.ui.loadstate.LoadStateItem
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val mBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }
    private val mViewModel: MyViewModel by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    private val mAdapter by lazy {
        object : CombineAdapter<IRecyclerViewItem>() {
            override fun hasMore(list: List<IRecyclerViewItem>?): Boolean {
                val items = list?.filterIsInstance<Book>()
                return !items.isNullOrEmpty()
            }
        }.apply {
            attachedToRecyclerView(mBinding.rv)
            show = { mBinding.swipeRefreshLayout.isRefreshing = true }
            hide = { mBinding.swipeRefreshLayout.isRefreshing = false }
            onError = { requestType, throwable ->
                Logger.e(throwable.message)
            }
            withPagingListAdapter(
                MyItemAdapter(),
                mViewModel.myLoadAfterResult
            )
            withLoadStateFooter(LoadStateAdapter(LoadStateItem()))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.adapter = mAdapter.concatAdapter

        mBinding.swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                mAdapter.refresh()
                getBannerData()
            }
        }

        lifecycleScope.launchWhenResumed {
            mAdapter.initial()
            getBannerData()
        }
    }

    private suspend fun getBannerData() {
        val bannerInfo = mViewModel.getBannerData()
        val data = bannerInfo?.bannerList
        if (!data.isNullOrEmpty()) {
            mBinding.viewBanner.vp.stop()
            mBinding.viewBanner.vp.setScrollSpeed()
            mBinding.viewBanner.vp.adapter = MyBannerPagerAdapter(this, data)

            val indicator = ImageIndicator(
                this,
                data.size,
                mBinding.viewBanner.indicatorContainer,
                10.dp,
                10.dp,
                listOf(R.drawable.store_point2),
                listOf(R.drawable.store_point1)
            )
            mBinding.viewBanner.vp.setBannerIndicator(indicator)
        }
    }

}

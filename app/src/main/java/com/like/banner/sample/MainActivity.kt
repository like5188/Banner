package com.like.banner.sample

import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val mBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }
    private val mViewModel: MyViewModel by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSingleBannerView()
    }

    private fun initSingleBannerView() {
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                getBannerData()
            }
        }

        lifecycleScope.launch {
            getBannerData()
        }
    }

    private fun initList() {
        val adapter = object : CombineAdapter<IRecyclerViewItem>() {
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
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.adapter = adapter.concatAdapter

        mBinding.swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                adapter.refresh()
            }
        }

        lifecycleScope.launchWhenResumed {
            adapter.initial()
        }
    }

    private suspend fun getBannerData() {
        mViewModel.getBannerInfoFlow()
            .flowOn(Dispatchers.IO)
            .onStart {
                mBinding.swipeRefreshLayout.isRefreshing = true
            }
            .onCompletion {
                mBinding.swipeRefreshLayout.isRefreshing = false
            }
            .catch { throwable ->
                Log.e("TAG", throwable.message ?: "unknown error")
            }
            .flowOn(Dispatchers.Main)
            .collect {
                val bannerList = it?.bannerList
                if (!bannerList.isNullOrEmpty()) {
                    mBinding.viewBanner.vp.adapter = MyBannerPagerAdapter(this, bannerList)

                    val indicator = ImageIndicator(
                        this,
                        bannerList.size,
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

}

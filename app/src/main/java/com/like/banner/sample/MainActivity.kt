package com.like.banner.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
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
        initList()
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
                val bannerList = it?.bannerList ?: emptyList()
                if (mBinding.viewBanner.banner.needBindViewHolder(bannerList, MyBannerAdapter.DIFF)) {
                    mBinding.viewBanner.banner.setAdapter(MyBannerAdapter())
                    val indicator = ImageIndicator(
                        this,
                        bannerList.size,
                        mBinding.viewBanner.indicatorContainer,
                        10.dp,
                        listOf(R.drawable.dot_unselected),
                        listOf(R.drawable.dot_selected)
                    )
                    mBinding.viewBanner.banner.setOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            Logger.d("onPageSelected position=$position")
                            indicator.onPageSelected(position)
                        }

                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                            Logger.v("onPageScrolled position=$position positionOffset=$positionOffset positionOffsetPixels=$positionOffsetPixels")
                            indicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        }

                        override fun onPageScrollStateChanged(state: Int) {
                            Logger.i("onPageScrollStateChanged state=$state")
                            indicator.onPageScrollStateChanged(state)
                        }
                    })
                    mBinding.viewBanner.banner.submitList(bannerList)
                }
            }
    }

}

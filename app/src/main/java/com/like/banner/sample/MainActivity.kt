package com.like.banner.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.like.banner.sample.databinding.ActivityMainBinding
import com.like.recyclerview.adapter.bindLoadAfter
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.ui.util.AdapterFactory
import com.like.recyclerview.utils.add
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val mBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }
    private val mViewModel: MyViewModel by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    private val mAdapter by lazy {
        ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.adapter = mAdapter

        mBinding.swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                mViewModel.myLoadAfterResult.refresh()
            }
        }

        val headerAdapter = MyHeaderAdapter(this)
        val itemAdapter = MyItemAdapter()
        val contentAdapter = ConcatAdapter()
        val emptyAdapter = AdapterFactory.createEmptyAdapter()
        val errorAdapter = AdapterFactory.createErrorAdapter()
        val loadMoreAdapter = AdapterFactory.createLoadMoreAdapter {
            lifecycleScope.launch {
                mViewModel.myLoadAfterResult.loadAfter?.invoke()
            }
        }

        lifecycleScope.launch {
            mAdapter.bindLoadAfter(
                recyclerView = mBinding.rv,
                result = mViewModel.myLoadAfterResult,
                onInitialOrRefreshSuccess = {
                    if (it.isNullOrEmpty()) {
                        0
                    } else {
                        val headers = it.getOrNull(0)
                        val items = it.getOrNull(1)
                        if (!headers.isNullOrEmpty()) {
                            contentAdapter.add(headerAdapter)
                            headerAdapter.clear()
                            headers.forEach {
                                if (it is BannerInfo) {
                                    headerAdapter.addToEnd(it)
                                }
                            }
                        }
                        if (!items.isNullOrEmpty()) {
                            contentAdapter.add(itemAdapter)
                            itemAdapter.clear()
                            items.forEach {
                                if (it is Book) {
                                    itemAdapter.addToEnd(it)
                                }
                            }
                        }
                        if (headers.isNullOrEmpty() && items.isNullOrEmpty()) {
                            0
                        } else if (!items.isNullOrEmpty()) {
                            2
                        } else {
                            1
                        }
                    }
                },
                onLoadMoreSuccess = {
                    val items = it.getOrNull(1)
                    if (!items.isNullOrEmpty()) {
                        items.forEach {
                            if (it is Book) {
                                itemAdapter.addToEnd(it)
                            }
                        }
                    }
                    items.isNullOrEmpty()
                },
                contentAdapter = contentAdapter,
                loadMoreAdapter = loadMoreAdapter,
                emptyAdapter = emptyAdapter,
                errorAdapter = errorAdapter,
                show = { mBinding.swipeRefreshLayout.isRefreshing = true },
                hide = { mBinding.swipeRefreshLayout.isRefreshing = false },
            ).collect()
        }
        lifecycleScope.launch {
            mViewModel.myLoadAfterResult.initial()
        }
    }

}

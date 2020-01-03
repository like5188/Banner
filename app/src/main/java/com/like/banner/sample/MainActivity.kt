package com.like.banner.sample

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.banner.sample.databinding.ActivityMainBinding
import com.like.common.util.map
import com.like.livedatarecyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.repository.requesthelper.*

class MainActivity : AppCompatActivity() {
    private val mBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }
    private val viewModel: MyViewModel by lazy {
        ViewModelProviders.of(this, MyViewModel.Factory()).get(MyViewModel::class.java)
    }
    private var mAdapter: MyLoadAfterAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter(mBinding.rv)
        initSwipeToRefresh(mBinding.swipeRefreshLayout)
        viewModel.loadInitial()
    }

    private fun initAdapter(recyclerView: RecyclerView) {
        recyclerView.layoutManager = WrapLinearLayoutManager(this)

        viewModel.getResult().liveValue.observe(this, Observer {
            val statusReport = viewModel.getResult().liveStatus.value
            when {
                (statusReport?.type is Initial || statusReport?.type is Refresh) && statusReport.status is Success -> {
                    mAdapter = MyLoadAfterAdapter(this) { viewModel.loadAfter() }
                    recyclerView.adapter = mAdapter
                    mAdapter?.mAdapterDataManager?.clearAndAdd(it)
                }
                statusReport?.type is After && statusReport.status is Success -> {
                    mAdapter?.mAdapterDataManager?.getFooters()?.forEach { iFooter ->
                        if (iFooter is Footer) {
                            iFooter.status.set(0)
                        }
                    }
                    mAdapter?.mAdapterDataManager?.addItemsToEnd(it.map())
                }
            }
        })
    }

    private fun initSwipeToRefresh(swipeRefreshLayout: SwipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.getResult().liveStatus.observe(this, Observer {
            when {
                it.type is Initial && it.status is Running -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                it.type is Initial && it.status is Success -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                it.type is Initial && it.status is Failed -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                it.type is Refresh && it.status is Running -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                it.type is Refresh && it.status is Success -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                it.type is Refresh && it.status is Failed -> {
                    Toast.makeText(this@MainActivity, "刷新失败", Toast.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })
    }

}

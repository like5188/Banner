package com.like.banner.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.like.banner.sample.databinding.ActivityMainBinding
import com.like.common.util.repository.RecyclerViewLoadType
import com.like.common.util.repository.bindListResultToRecyclerViewWithProgress
import com.like.common.util.shortToastCenter
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager

class MainActivity : AppCompatActivity() {
    private val mBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }
    private val mViewModel: MyViewModel by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    private val mAdapter: MyLoadAfterAdapter by lazy {
        MyLoadAfterAdapter(this) { mViewModel.myLoadAfterResult.loadAfter?.invoke() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.adapter = mAdapter

        mViewModel.myLoadAfterResult.bindListResultToRecyclerViewWithProgress(
            this, mAdapter, RecyclerViewLoadType.LoadAfter, mBinding.swipeRefreshLayout,
            {
                it
            },
            { requestType, throwable ->
                shortToastCenter(throwable.message)
            }
        )
        mViewModel.myLoadAfterResult.initial()
    }

}

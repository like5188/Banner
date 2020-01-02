package com.like.banner.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.banner.sample.databinding.ActivityBanner1Binding

class BannerActivity1 : AppCompatActivity() {
    private val mBinding: ActivityBanner1Binding by lazy {
        DataBindingUtil.setContentView<ActivityBanner1Binding>(this, R.layout.activity_banner_1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

}
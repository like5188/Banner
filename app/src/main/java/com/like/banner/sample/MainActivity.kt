package com.like.banner.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.banner.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val mBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    fun common(view: View) {
        startActivity(Intent(this@MainActivity, BannerActivity0::class.java))
    }

    fun recyclerViewBanner(view: View) {
        startActivity(Intent(this@MainActivity, BannerActivity1::class.java))
    }
}

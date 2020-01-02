package com.like.banner.sample

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.banner.BannerController
import com.like.banner.BannerPagerAdapter
import com.like.banner.indicator.IBannerIndicator
import com.like.banner.indicator.StickyDotBezierCurveIndicator
import com.like.banner.sample.databinding.ActivityMainBinding
import com.like.banner.utils.DimensionUtils
import com.ocnyang.pagetransformerhelp.cardtransformer.AlphaPageTransformer
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }
    private val mBannerController: BannerController by lazy { BannerController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding

        val bannerInfoList = ArrayList<BannerInfo>()
        bannerInfoList.add(BannerInfo("https://mall02.sogoucdn.com/image/2019/03/18/20190318094408_4590.png"))
        bannerInfoList.add(BannerInfo("https://mall03.sogoucdn.com/image/2019/05/13/20190513191053_4977.png"))
        bannerInfoList.add(BannerInfo("https://mall03.sogoucdn.com/image/2018/12/21/20181221191646_4221.png"))
        initBanner(bannerInfoList)
    }

    private fun initBanner(data: List<BannerInfo>) {
        mBinding.vp.setScrollSpeed()
        val adapter: BannerPagerAdapter = MyBannerPagerAdapter(this, data)
        mBinding.vp.adapter = adapter

        mBinding.vp.pageMargin = DimensionUtils.dp2px(this, 10f)
        mBinding.vp.setPageTransformer(true, AlphaPageTransformer())

        // 设置指示器
//        val indicator: IBannerIndicator = TextIndicator(this, data.size, mBinding.indicatorContainer).apply {
//            setTextSize(12f)
//            setTextColor(Color.WHITE)
//            setBackgroundColor(Color.GRAY)
//        }
//        val indicator: IBannerIndicator = ImageIndicator(
//            this,
//            data.size,
//            mBinding.indicatorContainer,
//            10f,
//            listOf(R.drawable.store_point2),
//            listOf(R.drawable.store_point1)
//        )
        val indicator: IBannerIndicator = StickyDotBezierCurveIndicator(
            this,
            data.size,
            mBinding.indicatorContainer,
            20f,
            Color.GRAY,
            listOf(Color.parseColor("#ff4a42"), Color.parseColor("#fcde64"), Color.parseColor("#73e8f4"))
        )
//        val indicator: IBannerIndicator = StickyRoundRectIndicator(
//            this, data.size, mBinding.indicatorContainer, 20f, 10f, Color.GRAY, listOf(
//                Color.parseColor("#ff4a42"),
//                Color.parseColor("#fcde64"),
//                Color.parseColor("#73e8f4")
//            )
//        )
        indicator.setIndicatorHeight(20f)
        indicator.setViewPager(mBinding.vp)

        // 设置轮播控制器
        mBannerController.setViewPager(mBinding.vp)
            .setCycleInterval(3000L)
            .play()
    }

    override fun onResume() {
        super.onResume()
        mBannerController.play()
    }

    override fun onPause() {
        super.onPause()
        mBannerController.pause()
    }
}

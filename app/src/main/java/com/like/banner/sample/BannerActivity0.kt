package com.like.banner.sample

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.palette.graphics.Target
import androidx.viewpager.widget.ViewPager
import com.like.banner.BannerController
import com.like.banner.BannerPagerAdapter
import com.like.banner.indicator.IBannerIndicator
import com.like.banner.indicator.StickyRoundRectIndicator
import com.like.banner.sample.databinding.ActivityBanner0Binding
import com.like.common.util.ImageUtils
import com.like.common.util.onPreDrawListener
import com.like.common.view.viewPagerTransformer.RotateYTransformer
import java.util.*

class BannerActivity0 : AppCompatActivity() {
    private val mBinding: ActivityBanner0Binding by lazy {
        DataBindingUtil.setContentView<ActivityBanner0Binding>(this, R.layout.activity_banner_0)
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

        mBinding.vp.setPageTransformer(true, object : RotateYTransformer() {
            override fun getRotate(context: Context): Float {
                var rotate = 0.5f
                (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.apply {
                    val metric = DisplayMetrics()
                    defaultDisplay.getMetrics(metric)
                    val densityDpi = metric.densityDpi
                    if (densityDpi <= 240) {
                        rotate = 3f
                    } else if (densityDpi <= 320) {
                        rotate = 2f
                    }
                }
                return rotate
            }
        })

        mBinding.vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            private val argbEvaluator = ArgbEvaluator()

            override fun onPageScrollStateChanged(state: Int) {
            }

            // position表示目标位置，positionOffset表示偏移的百分比，positionOffsetPixels表示偏移的像素
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val iv0 = mBinding.vp.getChildAt(adapter.getRealPosition(position))
                    .findViewById<ImageView>(R.id.iv)
                val iv1 = mBinding.vp.getChildAt(adapter.getRealPosition(position + 1))
                    .findViewById<ImageView>(R.id.iv)
                if (iv0 != null && iv0.drawable != null && iv1 != null && iv1.drawable != null) {
                    val color0 = ImageUtils.getColor(iv0.drawable, Target.DARK_MUTED, 0x000000)
                    val color1 = ImageUtils.getColor(iv1.drawable, Target.DARK_MUTED, 0x000000)
                    Log.e(
                        "tag",
                        "drawable0=${iv0.drawable} color0=$color0 drawable1=${iv1.drawable} color1=$color1"
                    )
                    mBinding.root.setBackgroundColor(
                        argbEvaluator.evaluate(positionOffset, color0, color1).toString().toInt()
                    )
                }
            }

            override fun onPageSelected(position: Int) {
            }

        })

        mBinding.vp.onPreDrawListener {
            it.layoutParams.height = (it.width * 0.4f).toInt()// vp 的高度是宽度的 0.4

//            val indicator: IBannerIndicator = TextIndicator(this, data.size, indicatorContainer).apply {
//                setTextSize(12f)
//                setTextColor(Color.WHITE)
//                setBackgroundColor(Color.GRAY)
//            }
//            val indicator: IBannerIndicator = ImageIndicator(this, data.size, indicatorContainer, 10f, listOf(R.drawable.store_point2), listOf(R.drawable.store_point1))
//            val indicator: IBannerIndicator = StickyDotBezierCurveIndicator(this, data.size, indicatorContainer, 20f, Color.GRAY, listOf(Color.parseColor("#ff4a42"), Color.parseColor("#fcde64"), Color.parseColor("#73e8f4")))
            val indicator: IBannerIndicator = StickyRoundRectIndicator(
                this, data.size, mBinding.indicatorContainer, 20f, 10f, Color.GRAY, listOf(
                    Color.parseColor("#ff4a42"),
                    Color.parseColor("#fcde64"),
                    Color.parseColor("#73e8f4")
                )
            )
            indicator.setViewPager(mBinding.vp)

            mBannerController.setViewPager(mBinding.vp)
                .setCycleInterval(3000L)
                .play()
        }

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
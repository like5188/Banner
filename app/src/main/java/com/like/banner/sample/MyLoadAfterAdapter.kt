package com.like.banner.sample

import android.content.Context
import android.graphics.Color
import com.like.banner.BannerController
import com.like.banner.indicator.IBannerIndicator
import com.like.banner.indicator.StickyRoundRectIndicator
import com.like.banner.sample.databinding.ViewBannerBinding
import com.like.banner.utils.DimensionUtils
import com.like.livedatarecyclerview.adapter.BaseLoadAfterAdapter
import com.like.livedatarecyclerview.model.IRecyclerViewItem
import com.like.livedatarecyclerview.viewholder.CommonViewHolder
import com.ocnyang.pagetransformerhelp.cardtransformer.CascadingPageTransformer

class MyLoadAfterAdapter(private val context: Context, onLoadAfter: () -> Unit) : BaseLoadAfterAdapter(onLoadAfter) {

    override fun bindOtherVariable(holder: CommonViewHolder, position: Int, item: IRecyclerViewItem?) {
        when (item) {
            is BannerInfo -> {
                if (holder.binding is ViewBannerBinding && item.bannerList.isNotEmpty()) {
                    val binding = holder.binding as ViewBannerBinding
                    if (binding.vp.adapter != null) return

                    binding.vp.setScrollSpeed()
                    binding.vp.adapter = MyBannerPagerAdapter(context, item.bannerList)
                    binding.vp.pageMargin = DimensionUtils.dp2px(context, 10f)
                    binding.vp.setPageTransformer(true, CascadingPageTransformer())

                    // 设置指示器
//                    val indicator: IBannerIndicator = TextIndicator(context, item.bannerList.size, binding.indicatorContainer).apply {
//                        setTextSize(12f)
//                        setTextColor(Color.WHITE)
//                        setBackgroundColor(Color.GRAY)
//                    }
//                    val indicator: IBannerIndicator = ImageIndicator(
//                        context,
//                        item.bannerList.size,
//                        binding.indicatorContainer,
//                        10f,
//                        listOf(R.drawable.store_point2),
//                        listOf(R.drawable.store_point1)
//                    )
//                    val indicator: IBannerIndicator = StickyDotBezierCurveIndicator(
//                        context,
//                        item.bannerList.size,
//                        binding.indicatorContainer,
//                        20f,
//                        Color.GRAY,
//                        listOf(
//                            Color.parseColor("#ff4a42"),
//                            Color.parseColor("#fcde64"),
//                            Color.parseColor("#73e8f4")
//                        )
//                    )
                    val indicator: IBannerIndicator = StickyRoundRectIndicator(
                        context,
                        item.bannerList.size,
                        binding.indicatorContainer,
                        20f,
                        10f,
                        Color.GRAY,
                        listOf(
                            Color.parseColor("#ff4a42"),
                            Color.parseColor("#fcde64"),
                            Color.parseColor("#73e8f4")
                        )
                    )

                    indicator.setIndicatorHeight(6f)
                    indicator.setViewPager(binding.vp)

                    // 设置轮播控制器
                    BannerController().setViewPager(binding.vp)
                        .setCycleInterval(3000L)
                        .play()
                }
            }
        }
    }
}
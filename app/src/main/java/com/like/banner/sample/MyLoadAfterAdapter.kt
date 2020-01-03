package com.like.banner.sample

import android.graphics.Color
import android.view.ViewGroup
import com.like.banner.BannerController
import com.like.banner.indicator.*
import com.like.banner.sample.databinding.ViewBannerBinding
import com.like.banner.utils.DimensionUtils
import com.like.livedatarecyclerview.adapter.BaseLoadAfterAdapter
import com.like.livedatarecyclerview.model.IRecyclerViewItem
import com.like.livedatarecyclerview.viewholder.CommonViewHolder
import com.ocnyang.pagetransformerhelp.cardtransformer.CascadingPageTransformer

class MyLoadAfterAdapter(private val context: MainActivity, onLoadAfter: () -> Unit) : BaseLoadAfterAdapter(onLoadAfter) {
    private val mBannerControllers = mutableMapOf<CommonViewHolder, BannerController>()

    override fun onViewAttachedToWindow(holder: CommonViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (mBannerControllers.containsKey(holder)) {
            mBannerControllers[holder]?.play()
        }
    }

    override fun onViewDetachedFromWindow(holder: CommonViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (mBannerControllers.containsKey(holder)) {
            mBannerControllers[holder]?.pause()
        }
    }

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

                    val indicator: StickyRoundRectIndicator = createBannerIndicator(item.bannerList.size, binding.indicatorContainer)
                    indicator.setIndicatorHeight(6f)
                    indicator.setViewPager(binding.vp)

                    // 设置轮播控制器
                    val bannerController = BannerController(context)
                    bannerController.setViewPager(binding.vp)
                        .setCycleInterval(3000L)
                        .play()

                    mBannerControllers[holder] = bannerController
                }
            }
        }
    }

    private inline fun <reified T : IBannerIndicator> createBannerIndicator(
        mDataCount: Int,
        mContainer: ViewGroup
    ): T = when (T::class.java) {
        TextIndicator::class.java -> {
            TextIndicator(context, mDataCount, mContainer).apply {
                setTextSize(12f)
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.GRAY)
            }
        }
        StickyDotBezierCurveIndicator::class.java -> {
            StickyDotBezierCurveIndicator(
                context, mDataCount, mContainer, 20f, Color.GRAY,
                listOf(Color.parseColor("#ff4a42"), Color.parseColor("#fcde64"), Color.parseColor("#73e8f4"))
            )
        }
        StickyRoundRectIndicator::class.java -> {
            StickyRoundRectIndicator(
                context, mDataCount, mContainer, 20f, 10f, Color.GRAY,
                listOf(Color.parseColor("#ff4a42"), Color.parseColor("#fcde64"), Color.parseColor("#73e8f4"))
            )
        }
        ImageIndicator::class.java -> {
            ImageIndicator(
                context, mDataCount, mContainer, 10f,
                listOf(R.drawable.store_point2), listOf(R.drawable.store_point1)
            )
        }
        else -> throw IllegalArgumentException("不支持的类型")
    } as T
}
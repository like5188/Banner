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
    private val mBannerControllerCaches = mutableMapOf<CommonViewHolder, BannerController>()
    private val mDataCaches = mutableMapOf<CommonViewHolder, IRecyclerViewItem?>()

    override fun onViewAttachedToWindow(holder: CommonViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (mBannerControllerCaches.containsKey(holder)) {
            mBannerControllerCaches[holder]?.play()
        }
    }

    override fun onViewDetachedFromWindow(holder: CommonViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (mBannerControllerCaches.containsKey(holder)) {
            mBannerControllerCaches[holder]?.pause()
        }
    }

    override fun bindOtherVariable(holder: CommonViewHolder, position: Int, item: IRecyclerViewItem?) {
        when (item) {
            is BannerInfo -> {
                val binding = holder.binding
                if (binding is ViewBannerBinding && item.bannerList.isNotEmpty()) {
                    binding.vp.clearOnPageChangeListeners()// 重新绑定数据时，需要清空ViewPager原来的监听，因为indicator和bannerController都要设置监听。

                    binding.vp.setScrollSpeed()
                    binding.vp.adapter = MyBannerPagerAdapter(context, item.bannerList)
                    binding.vp.pageMargin = DimensionUtils.dp2px(context, 10f)
                    binding.vp.setPageTransformer(true, CascadingPageTransformer())

                    val indicator: StickyRoundRectIndicator = createBannerIndicator(item.bannerList.size, binding.indicatorContainer)
                    indicator.setIndicatorHeight(6f)
                    indicator.setViewPager(binding.vp)

                    // 设置轮播控制器
                    val bannerController = BannerController(context)
                    if (!isRefresh(holder, item)) {// 如果不是刷新操作，就证明是滑出再滑进操作，那么就需要保留原来的position
                        bannerController.setPosition(getPositionFromBannerControllerCache(holder))
                    }
                    bannerController.setViewPager(binding.vp)
                        .setCycleInterval(3000L)
                        .play()

                    mBannerControllerCaches[holder] = bannerController
                    mDataCaches[holder] = item
                }
            }
        }
    }

    /**
     * 获取BannerController当前位置
     */
    private fun getPositionFromBannerControllerCache(holder: CommonViewHolder): Int =
        mBannerControllerCaches[holder]?.getPosition() ?: -1

    /**
     * 是否是刷新操作
     */
    private fun isRefresh(holder: CommonViewHolder, item: IRecyclerViewItem?): Boolean {
        if (mDataCaches.containsKey(holder)) {
            val data = mDataCaches[holder]
            if (data === item) {
                return false
            }
        }
        return true
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
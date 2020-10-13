package com.like.banner.sample

import android.graphics.Color
import android.view.ViewGroup
import com.like.banner.indicator.*
import com.like.banner.sample.databinding.ViewBannerBinding
import com.like.banner.utils.DimensionUtils
import com.like.recyclerview.adapter.BaseLoadAfterAdapter
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.viewholder.CommonViewHolder
import com.ocnyang.pagetransformerhelp.cardtransformer.CascadingPageTransformer

class MyLoadAfterAdapter(private val context: MainActivity, onLoadAfter: () -> Unit) : BaseLoadAfterAdapter(onLoadAfter) {

    override fun bindOtherVariable(holder: CommonViewHolder, position: Int, item: IRecyclerViewItem?) {
        when (item) {
            is BannerInfo -> {
                val binding = holder.binding
                if (binding is ViewBannerBinding && item.bannerList.isNotEmpty()) {

                    binding.vp.setScrollSpeed()
                    binding.vp.adapter = MyBannerPagerAdapter(context, item.bannerList)
                    binding.vp.pageMargin = DimensionUtils.dp2px(context, 10f)
                    binding.vp.setPageTransformer(true, CascadingPageTransformer())

                    val indicator: ImageIndicator = createBannerIndicator(item.bannerList.size, binding.indicatorContainer)
                    indicator.init(6f)
                    binding.vp.setBannerIndicator(indicator)

                    binding.vp.play()
                }
            }
        }
    }

    private inline fun <reified T : IBannerIndicator> createBannerIndicator(mDataCount: Int, mContainer: ViewGroup): T =
        when (T::class.java) {
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
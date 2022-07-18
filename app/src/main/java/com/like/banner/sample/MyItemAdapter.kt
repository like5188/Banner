package com.like.banner.sample

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.like.banner.indicator.*
import com.like.banner.sample.databinding.ViewBannerBinding
import com.like.common.util.Logger
import com.like.common.util.dp
import com.like.recyclerview.adapter.BaseListAdapter
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.viewholder.BindingViewHolder

class MyItemAdapter : BaseListAdapter<ViewDataBinding, IRecyclerViewItem>(
    object : DiffUtil.ItemCallback<IRecyclerViewItem>() {
        override fun areItemsTheSame(oldItem: IRecyclerViewItem, newItem: IRecyclerViewItem): Boolean {
            return if (oldItem is Book && newItem is Book) {
                oldItem.id == newItem.id
            } else if (oldItem is BannerInfo && newItem is BannerInfo) {
                true
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: IRecyclerViewItem, newItem: IRecyclerViewItem): Boolean {
            return if (oldItem is Book && newItem is Book) {
                oldItem.name == newItem.name && oldItem.des == newItem.des
            } else if (oldItem is BannerInfo && newItem is BannerInfo) {
                oldItem.bannerList == newItem.bannerList
            } else {
                false
            }
        }

    }
) {
    override fun onBindViewHolder(holder: BindingViewHolder<ViewDataBinding>, item: IRecyclerViewItem) {
        val binding = holder.binding
        if (binding !is ViewBannerBinding) {
            return
        }
        if (item !is BannerInfo) {
            return
        }
        if (binding.banner.needBindViewHolder(item.bannerList)) {
            val context = holder.itemView.context
            binding.banner.setAdapter(MyBannerAdapter())
            binding.banner.setScrollSpeed()
//        binding.banner.pageMargin = 10.dp
//        binding.banner.setPageTransformer(true, CascadingPageTransformer())
            val indicator: StickyRoundRectIndicator = createBannerIndicator(context, item.bannerList.size, binding.indicatorContainer)
            binding.banner.setOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    Logger.d("onPageSelected position=$position")
                    indicator.onPageSelected(position)
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    Logger.v("onPageScrolled position=$position positionOffset=$positionOffset positionOffsetPixels=$positionOffsetPixels")
                    indicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    Logger.i("onPageScrollStateChanged state=$state")
                    indicator.onPageScrollStateChanged(state)
                }
            })
            binding.banner.submitList(item.bannerList)
        }
    }

    private inline fun <reified T : IBannerIndicator> createBannerIndicator(context: Context, mDataCount: Int, mContainer: ViewGroup): T =
        when (T::class.java) {
            CircleTextIndicator::class.java -> {
                CircleTextIndicator(context, mDataCount, mContainer, 24.dp).apply {
                    setTextSize(12f)
                    setTextColor(Color.WHITE)
                    setBackgroundColor(Color.GRAY)
                }
            }
            StickyDotBezierCurveIndicator::class.java -> {
                StickyDotBezierCurveIndicator(
                    context, mDataCount, mContainer, 10.dp, 20.dp, Color.GRAY,
                    listOf(Color.parseColor("#ff4a42"), Color.parseColor("#fcde64"), Color.parseColor("#73e8f4"))
                )
            }
            StickyRoundRectIndicator::class.java -> {
                StickyRoundRectIndicator(
                    context, mDataCount, mContainer, 20.dp, 10.dp, 10.dp, Color.GRAY,
                    listOf(Color.parseColor("#ff4a42"), Color.parseColor("#fcde64"), Color.parseColor("#73e8f4"))
                )
            }
            ImageIndicator::class.java -> {
                ImageIndicator(
                    context, mDataCount, mContainer, 10.dp,
                    listOf(R.drawable.store_point2), listOf(R.drawable.store_point1)
                )
            }
            else -> throw IllegalArgumentException("不支持的类型")
        } as T

}

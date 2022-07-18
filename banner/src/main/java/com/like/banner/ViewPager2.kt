package com.like.banner

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewpager2.widget.ViewPager2

/**
 * 是否需要 bindViewHolder
 * 1、没有设置adapter。
 * 2、新旧数据不相同。
 *
 * 注意：如果不判断直接 bindViewHolder 进行相关设置的话，会导致在复用时，每次都会重新设置，然后显示第一个页面。
 */
fun <T> ViewPager2.needBindViewHolder(newData: List<T>, diffCallback: DiffUtil.ItemCallback<T>): Boolean {
    if (adapter == null) {
        return true
    }
    val oldData = (adapter as? ListAdapter<T, *>)?.currentList?.toMutableList()
    if (oldData?.size != newData.size) {
        return true
    }
    oldData.forEachIndexed { index, t ->
        if (!diffCallback.areItemsTheSame(t, newData[index])) {
            return true
        }
        if (!diffCallback.areContentsTheSame(t, newData[index])) {
            return true
        }
    }
    return false
}
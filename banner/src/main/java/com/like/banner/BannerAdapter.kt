package com.like.banner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BannerAdapter<VB : ViewDataBinding, ValueInList>(diffCallback: DiffUtil.ItemCallback<ValueInList>) :
    ListAdapter<ValueInList, BindingViewHolder<VB>>(diffCallback) {

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<VB> {
        return BindingViewHolder(
            DataBindingUtil.inflate<VB>(
                LayoutInflater.from(parent.context),
                getLayoutId(viewType),
                parent,
                false
            )
        ).apply {
            // 为list添加Item的点击事件监听
            itemView.setOnClickListener {
                onItemClick(this)
            }
        }
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int) {
        val realPosition = if (currentList.isEmpty()) 0 else holder.adapterPosition % currentList.size
        val item = try {
            getItem(realPosition)
        } catch (e: Exception) {
            null
        } ?: return
        // 这里不能直接把 holder.bindingAdapterPosition 的值传递下去，因为有添加删除前面的 item 都会造成后面 item 的位置改变，
        // 所以在使用的时候，需要随时使用 holder.bindingAdapterPosition 重新获取。
        onBindViewHolder(holder, item)
    }

    override fun getItemCount(): Int {
        return if (Banner.mAutoLoop) Banner.MAX_COUNT else currentList.size
    }

    open fun onItemClick(holder: BindingViewHolder<VB>) {}

    abstract fun onBindViewHolder(holder: BindingViewHolder<VB>, item: ValueInList)
    abstract fun getLayoutId(viewType: Int): Int
}

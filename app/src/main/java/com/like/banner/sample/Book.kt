package com.like.banner.sample

import com.like.recyclerview.model.IRecyclerViewItem

data class Book(val id: Int, val name: String, val des: String) : IRecyclerViewItem {
    override val layoutId: Int = R.layout.book_item
    override val variableId: Int = BR.book
}
package com.like.banner.sample

import com.like.livedatarecyclerview.model.IItem

data class Book(val id: Int, val name: String, val des: String) : IItem {
    override val variableId: Int = BR.book
    override val layoutId: Int = R.layout.book_item
}
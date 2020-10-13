package com.like.banner.sample

import com.like.recyclerview.model.IItem

data class Book(val id: Int, val name: String, val des: String) : IItem {
    override val layoutId: Int = R.layout.book_item
    override fun variableId(): Int = BR.book
}
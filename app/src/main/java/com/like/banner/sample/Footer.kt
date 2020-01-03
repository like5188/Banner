package com.like.banner.sample

import androidx.databinding.ObservableInt
import com.like.livedatarecyclerview.model.IFooter

data class Footer(val status: ObservableInt) : IFooter {
    override val variableId: Int = BR.footer
    override val layoutId: Int = R.layout.footer_item
}
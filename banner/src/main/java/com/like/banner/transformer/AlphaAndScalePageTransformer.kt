package com.like.banner.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class AlphaAndScalePageTransformer(private val alpha: Float = 0.5f, private val scale: Float = 0.8f) : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val scale = if (position < 0) (1 - scale) * position + 1 else (scale - 1) * position + 1
        val alpha = if (position < 0) (1 - alpha) * position + 1 else (alpha - 1) * position + 1
        if (position < 0) {
            page.pivotX = page.width.toFloat()
            page.pivotY = (page.height / 2).toFloat()
        } else {
            page.setPivotX(0f)
            page.pivotY = (page.height / 2).toFloat()
        }
        page.scaleX = scale
        page.scaleY = scale
        page.alpha = Math.abs(alpha)
    }
}
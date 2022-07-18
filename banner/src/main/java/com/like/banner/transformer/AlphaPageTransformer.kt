package com.like.banner.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class AlphaPageTransformer(private val alpha: Float = 0.5f) : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        if (position < -1) {
            page.setAlpha(alpha)
        } else if (position <= 1) { // [-1,1]
            if (position < 0) //[0，-1]
            {
                val factor = alpha + (1 - alpha) * (1 + position)
                page.setAlpha(factor)
            } else  //[1，0]
            {
                val factor = alpha + (1 - alpha) * (1 - position)
                page.setAlpha(factor)
            }
        } else { // (1,+Infinity]
            page.setAlpha(alpha)
        }
    }
}
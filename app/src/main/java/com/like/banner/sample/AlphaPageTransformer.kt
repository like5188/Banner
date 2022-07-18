package com.like.banner.sample

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class AlphaPageTransformer : ViewPager2.PageTransformer {
    private val DEFAULT_MIN_ALPHA = 0.5f
    private val mMinAlpha = DEFAULT_MIN_ALPHA

    override fun transformPage(page: View, position: Float) {
        if (position < -1) {
            page.setAlpha(mMinAlpha)
        } else if (position <= 1) { // [-1,1]
            if (position < 0) //[0，-1]
            {
                val factor = mMinAlpha + (1 - mMinAlpha) * (1 + position)
                page.setAlpha(factor)
            } else  //[1，0]
            {
                val factor = mMinAlpha + (1 - mMinAlpha) * (1 - position)
                page.setAlpha(factor)
            }
        } else { // (1,+Infinity]
            page.setAlpha(mMinAlpha)
        }
    }
}
package com.like.banner.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.view.Gravity
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView

/**
 * 文本指示器
 * 一个圆形的 TextView，显示内容为 1/3
 *
 * @param mContext
 * @param mDataCount    指示器的数量
 * @param mContainer    指示器的容器
 */
class TextIndicator(
    private val mContext: Context,
    private val mDataCount: Int,
    private val mContainer: ViewGroup
) : IBannerIndicator {
    private val mCircleTextView = CircleTextView(mContext).apply {
        gravity = Gravity.CENTER
    }

    init {
        if (mDataCount > 0) {
            // 设置CircleTextView的宽高
            val containerHeight =
                mContainer.height - mContainer.paddingTop - mContainer.paddingBottom
            mCircleTextView.layoutParams = ViewGroup.LayoutParams(containerHeight, containerHeight)

            mContainer.addView(mCircleTextView)
        }
    }

    fun setTextColor(@ColorInt color: Int) {
        mCircleTextView.setTextColor(color)
    }

    fun setBackgroundColor(@ColorInt color: Int) {
        mCircleTextView.setBackgroundColor(color)
    }

    fun setTextSize(textSize: Float) {
        mCircleTextView.textSize = textSize
    }

    override fun onPageSelected(position: Int) {
        if (mDataCount <= 0) return
        mCircleTextView.text = "${position + 1}/$mDataCount"
    }

    /**
     * 圆形TextView
     */
    class CircleTextView(context: Context?) : AppCompatTextView(context, null, 0) {
        private val mBgPaint = Paint().apply {
            color = Color.RED
            isAntiAlias = true
        }
        private val pfd = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            val max = Math.max(measuredWidth, measuredHeight)
            setMeasuredDimension(max, max)
        }

        override fun setBackgroundColor(color: Int) {
            mBgPaint.color = color
        }

        /**
         * 设置通知个数显示
         *
         * @param number
         */
        fun setTextNumber(number: Int) {
            if (number <= 99) {
                val text = number.toString() + ""
                setText(text)
            } else { // 变成小圆点
                val text = ""
                textSize = 5f
                setText(text)
            }
        }

        override fun draw(canvas: Canvas) {
            canvas.drawFilter = pfd
            canvas.drawCircle(
                width / 2.toFloat(),
                height / 2.toFloat(),
                Math.max(width, height) / 2.toFloat(),
                mBgPaint
            )
            super.draw(canvas)
        }

    }

}
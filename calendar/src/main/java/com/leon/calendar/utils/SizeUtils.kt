package com.leon.calendar.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup

/**
 * @time:2020/3/19 14:54
 * @author:Leon
 * @description:
 */
object SizeUtils {
    fun measureView(view: View): IntArray {
        var lp = view.layoutParams
        if (lp == null) {
            lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width)
        val lpHeight = lp.height
        val heightSpec: Int
        heightSpec = if (lpHeight > 0) {
            View.MeasureSpec.makeMeasureSpec(
                lpHeight,
                View.MeasureSpec.EXACTLY
            )
        } else {
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        }
        view.measure(widthSpec, heightSpec)
        return intArrayOf(view.measuredWidth, view.measuredHeight)
    }

    /**
     * Return the width of view.
     *
     * @param view The view.
     * @return the width of view
     */
    fun getMeasuredWidth(view: View): Int {
        return measureView(view)[0]
    }

    fun dipToPx(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * Return the height of view.
     *
     * @param view The view.
     * @return the height of view
     */
    fun getMeasuredHeight(view: View): Int {
        return measureView(view)[1]
    }

}
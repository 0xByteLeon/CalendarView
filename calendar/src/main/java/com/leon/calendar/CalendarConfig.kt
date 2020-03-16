package com.leon.calendar

import android.graphics.Color

/**
 * @time:2020/3/13 16:43
 * @author:Leon
 * @description:
 */
class CalendarConfig {
    var from = System.currentTimeMillis()
    var to = System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000L
    var enabledStart = System.currentTimeMillis()
    var enabledEnd = System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000L
    var startTime = -1L
    var endTime = -1L
    var enableTextColor = Color.parseColor("#FF282729")
    var disableTextColor = Color.parseColor("#FFD5D5D5")
    var rangeTextColor = Color.parseColor("#FF282729")
    var selectedTextColor = Color.parseColor("#FFFFFFFF")

    var outRangeBg = Color.WHITE

    var rangeMode = false
    var rangeBgColor = Color.parseColor("#1AFF3973")
    var rangeSelectBgColor = Color.parseColor("#FFFF3973")

    fun inEnabelRange(timestamp: Long): Boolean {
        return timestamp >= enabledStart && timestamp <= enabledEnd
    }

    fun isInRange(timestamp: Long): Boolean {
        return timestamp >= startTime && timestamp <= endTime
    }

    fun innerRange(timestamp: Long): Boolean {
        return timestamp > startTime && timestamp < endTime
    }

    fun isSelectedHead(timestamp: Long): Boolean {
        return isStartRange(timestamp) || isEndRange(timestamp)
    }

    fun isStartRange(timestamp: Long): Boolean {
        return timestamp == startTime
    }

    fun isEndRange(timestamp: Long): Boolean {
        return timestamp == endTime
    }

    fun resetRange() {
        startTime = -1
        endTime = -1
    }

    fun selectedRange(): Boolean {
        return startTime >= 0 && endTime >= 0
    }
}
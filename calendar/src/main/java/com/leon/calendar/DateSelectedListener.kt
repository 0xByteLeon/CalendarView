package com.leon.calendar

/**
 * @time:2020/3/16 13:59
 * @author:Leon
 * @description:
 */
interface DateSelectedListener {
    fun onDateSelected(timeStamap: Long)
}

interface DateRangeSelectedListener {
    fun onRangeSelected(startTime: Long, endTime: Long)
}

interface DateClickListener {
    fun onDateClick(timeStamap: Long)
}
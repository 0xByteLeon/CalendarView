package com.leon.calendar

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.util.*

/**
 * @time:2020/3/13 11:27
 * @author:Leon
 * @description:
 */
class DayEntity(
    override val itemType: Int,
    val timeStamp: Long = System.currentTimeMillis(),
    val dayOfWeek: Int = Calendar.getInstance().apply {
        timeInMillis = timeStamp
    }.get(Calendar.DAY_OF_WEEK)
) : MultiItemEntity {

    val displayStr: String
        get() {
            if (timeStamp <= 0)
                return ""

            if (itemType == 1) {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = timeStamp
                }
                return "${calendar[Calendar.YEAR]}年  ${calendar[Calendar.MONTH] + 1}月"
            }

            return Calendar.getInstance().apply {
                timeInMillis = timeStamp
            }.get(Calendar.DAY_OF_MONTH).toString()
        }

}
package com.leon.calendar

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apkfuns.logutils.LogUtils
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.android.synthetic.main.item_date_label.view.*
import kotlinx.android.synthetic.main.item_day.view.*
import kotlinx.android.synthetic.main.rv_calendar_view.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @time:2020/3/13 16:53
 * @author:Leon
 * @description:
 */
open class CalendarView : FrameLayout {

    private val sdf = SimpleDateFormat("yyyy-MM-dd")

    var dateSelectedListener: DateSelectedListener? = null

    var dateRangeSelectedListener: DateRangeSelectedListener? = null

    var dateClickListener: DateClickListener? = null

    private val config = CalendarConfig()

    constructor(context: Context) : super(context)

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView(context, attrs)
    }


    constructor(
        context: Context, attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    val circleBg = ShapeDrawable().apply {
        shape = OvalShape()
        intrinsicWidth = 240
        intrinsicHeight = 240
        getPaint().setColor(config.rangeSelectBgColor);
        getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
    }

    val adapter = object : BaseMultiItemQuickAdapter<DayEntity, BaseViewHolder>() {
        init {
            addItemType(
                0,
                R.layout.item_day
            )
            addItemType(
                1,
                R.layout.item_date_label
            )
        }


        override fun convert(helper: BaseViewHolder, item: DayEntity) {
            when (item.itemType) {
                1 -> {
                    helper.itemView.monthLabelTv.text = item.displayStr
                }
                else -> {
                    setText(helper, item)
                    setItemBg(helper, item)
                    helper.itemView.dayTv.setOnClickListener {
                        if (config.inEnabelRange(item.timeStamp)) {
                            if (config.selectedRange()) {
                                config.resetRange()
                                config.startTime = item.timeStamp
                                notifyDataSetChanged()
                            }
                            if (config.startTime < 0) {
                                config.startTime = item.timeStamp
                                notifyItemChanged(helper.adapterPosition)
                            } else {
                                if (config.startTime < item.timeStamp) {
                                    config.endTime = item.timeStamp
                                    notifyDataSetChanged()
                                    dateRangeSelectedListener?.onRangeSelected(
                                        config.startTime,
                                        config.endTime
                                    )
                                }
                                if (config.startTime > item.timeStamp) {
                                    config.endTime = config.startTime
                                    config.startTime = item.timeStamp
                                    notifyDataSetChanged()
                                    dateRangeSelectedListener?.onRangeSelected(
                                        config.startTime,
                                        config.endTime
                                    )
                                }
                            }
                            dateSelectedListener?.onDateSelected(item.timeStamp)
                        }
                        dateClickListener?.onDateClick(item.timeStamp)
                    }
                }
            }
        }

        fun setText(helper: BaseViewHolder, item: DayEntity) {
            helper.itemView.dayTv.text = item.displayStr
            if (config.innerRange(item.timeStamp)) {
                helper.setTextColor(R.id.dayTv, config.rangeTextColor)
            } else {
                if (config.inEnabelRange(item.timeStamp)) {
                    helper.setTextColor(R.id.dayTv, config.enableTextColor)
                } else {
                    helper.setTextColor(R.id.dayTv, config.disableTextColor)
                }
                if (config.isStartRange(item.timeStamp) || config.isEndRange(item.timeStamp)) {
                    helper.setTextColor(R.id.dayTv, config.selectedTextColor)
                }
            }
        }

        fun setItemBg(helper: BaseViewHolder, item: DayEntity) {
            helper.setVisible(
                    R.id.selectBgLL,
                    config.isInRange(item.timeStamp) && config.selectedRange()
                )
                .setBackgroundColor(
                    R.id.dayBg, if (config.innerRange(item.timeStamp)) {
                        config.rangeBgColor
                    } else {
                        config.outRangeBg
                    }
                )
                .setBackgroundColor(
                    R.id.dayLeftRectBg,
                    if (config.isEndRange(item.timeStamp) && config.selectedRange()) config.rangeBgColor
                    else Color.TRANSPARENT
                )
                .setBackgroundColor(
                    R.id.dayRightBg,
                    if (config.isStartRange(item.timeStamp) && config.selectedRange())
                        config.rangeBgColor
                    else
                        Color.TRANSPARENT
                )
                .setBackgroundColor(R.id.dayBg, getItemBg(item))
            helper.itemView.dayTv.background =
                if (config.isStartRange(item.timeStamp) || config.isEndRange(item.timeStamp)) circleBg else null
        }

        fun getItemBg(item: DayEntity): Int {

            if (item.timeStamp < 0) {
                return Color.TRANSPARENT
            }

            if (config.isInRange(item.timeStamp)) {
                if (config.isStartRange(item.timeStamp) || config.isEndRange(item.timeStamp)) {
                    return Color.TRANSPARENT
                }
                return config.rangeBgColor
            } else {
                return config.outRangeBg
            }
        }
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        initConfig(context, attrs)
        val view = View.inflate(
            context,
            R.layout.rv_calendar_view, null
        )
        initWeekView(view)
        val layoutManager = GridLayoutManager(context, 7)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (adapter.data[position].itemType == 1)
                    return 7
                else
                    return 1
            }

        }
        adapter.setNewData(getData(config.from, config.to))
        view.calendarRv.layoutManager = layoutManager
        view.calendarRv.adapter = adapter
        addView(view)
    }

    private fun initConfig(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.CalendarView, 0, 0
        )
        a.getString(R.styleable.CalendarView_from)?.let {
            config.from = sdf.parse(it).time
        }
        a.getString(R.styleable.CalendarView_to)?.let {
            config.to = sdf.parse(it).time
        }
        a.getColor(R.styleable.CalendarView_dateDisableTextColor, config.disableTextColor)?.let {
            config.disableTextColor = it
        }
        a.getColor(R.styleable.CalendarView_dateEnableTextColor, config.enableTextColor)?.let {
            config.enableTextColor = it
        }
        a.getString(R.styleable.CalendarView_enabledFrom)?.let {
            config.enabledStart = sdf.parse(it).time
            it
        } ?: kotlin.run {
            config.enabledStart = config.from
        }
        a.getString(R.styleable.CalendarView_enabledTo)?.let {
            config.enabledEnd = sdf.parse(it).time
            it
        } ?: kotlin.run {
            config.enabledEnd = config.to
        }
        a.getColor(R.styleable.CalendarView_rangeBackgroundColor, config.rangeBgColor)?.let {
            config.rangeBgColor = it
        }
        a.getBoolean(R.styleable.CalendarView_rangeMode, config.rangeMode)?.let {
            config.rangeMode = it
        }

        a.getColor(R.styleable.CalendarView_rangeSelectBackgroundColor, config.rangeSelectBgColor)
            ?.let {
                config.rangeSelectBgColor = it
            }
        a.getColor(R.styleable.CalendarView_rangeTextColor, config.rangeTextColor)?.let {
            config.rangeTextColor = it
        }
        a.getColor(R.styleable.CalendarView_selectedTextColor, config.selectedTextColor)?.let {
            config.selectedTextColor = it
        }
        a.recycle()
    }

    private fun initWeekView(view: View) {
        view.findViewById<RecyclerView>(R.id.calendarWeekRv).adapter =
            object : BaseQuickAdapter<String, BaseViewHolder>(
                R.layout.item_week,
                mutableListOf(
                    "日",
                    "一",
                    "二",
                    "三",
                    "四",
                    "五",
                    "六"
                )
            ) {
                override fun convert(helper: BaseViewHolder, item: String) {
                    helper.setText(R.id.weekDayTv, item)
                }

            }
    }

    private fun getData(from: Long, to: Long): MutableList<DayEntity> {

        val data = mutableListOf<DayEntity>()
        check((to - from) >= 24 * 60 * 60 * 1000)

        val fromCalendar = Calendar.getInstance().apply {
            timeInMillis = from
            set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
        }

        LogUtils.d(fromCalendar)

        LogUtils.d("from ${fromCalendar.get(Calendar.DAY_OF_WEEK)}")


        val toCalendar = Calendar.getInstance().apply {
            timeInMillis = to
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }

        LogUtils.d("to  ${toCalendar[Calendar.DAY_OF_WEEK]}")


        val realFrom = fromCalendar.timeInMillis
        val realTo = toCalendar.timeInMillis

        for (timeStamp in (realFrom..realTo step 24 * 60 * 60 * 1000)) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = timeStamp
            }

            val currentDay =
                DayEntity(0, timeStamp)
            LogUtils.d(currentDay)

            //当月第一天
            if (calendar[Calendar.DAY_OF_MONTH] == calendar.getActualMinimum(Calendar.DAY_OF_MONTH)) {

                if (data.isNotEmpty()) {
                    //需要填补上一个月份末尾的空白
                    //上个月最后一天
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                    val previousDay =
                        DayEntity(
                            0,
                            calendar.timeInMillis
                        )
                    if (previousDay.dayOfWeek < 7) {
                        for (i in previousDay.dayOfWeek + 1..Calendar.SATURDAY) {
                            val blockDay =
                                DayEntity(
                                    0,
                                    0
                                )
                            data.add(blockDay)
                        }
                    }
                }

                val monthLabel =
                    DayEntity(
                        1,
                        timeStamp
                    )
                data.add(monthLabel)

                LogUtils.d(currentDay)
                if (currentDay.dayOfWeek != 1) {
                    //当月第一天根据星期排布，需要填补空白
                    for (i in 1 until currentDay.dayOfWeek) {
                        val blockDay =
                            DayEntity(
                                0,
                                0
                            )
                        data.add(blockDay)
                    }
                }
                data.add(currentDay)
            } else {
                data.add(currentDay)
            }
        }
        return data
    }

    fun setDateRange(from: Long, to: Long) {
        check(from > 0 && to > 0 && from < to)
        config.from = from
        config.to = to
    }

    fun setEnableRange(start: Long, end: Long) {
        check(start > 0 && end > 0 && end > start && start > config.from && end < config.to)
        config.enabledStart = start
        config.enabledEnd = end
    }

    fun resetSelectedRange() {
        config.startTime = -1
        config.endTime = -1
    }

    fun setSelectedRang(start: Long, end: Long) {
        check(start > 0 && end > 0 && end > start && start >= config.enabledStart && end <= config.enabledEnd) {
            "参数非法"
        }
        config.startTime = start
        config.endTime = end
    }

    /*
    * 设置完参数后需要调用此方法刷新日历
    * */
    fun refreshCalendar() {
        if (!config.isInRange(config.startTime) || !config.isInRange(config.endTime)) {
            resetSelectedRange()
        }
        adapter.setNewData(getData(config.from, config.to))
    }
}
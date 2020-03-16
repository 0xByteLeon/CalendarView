package com.leon.calendar.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.leon.calendar.DateClickListener
import com.leon.calendar.DateRangeSelectedListener
import com.leon.calendar.DateSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        calendarView.dateRangeSelectedListener = object : DateRangeSelectedListener {
            override fun onRangeSelected(startTime: Long, endTime: Long) {
                Log.d("MainActivity", "Start : ${sdf.format(startTime)}")
                Log.d("MainActivity", "End : ${sdf.format(endTime)}")
            }
        }
        calendarView.dateSelectedListener = object : DateSelectedListener {
            override fun onDateSelected(timeStamap: Long) {
                Log.d("MainActivity", "Selected : ${sdf.format(timeStamap)}")
            }
        }
        calendarView.dateClickListener = object : DateClickListener {
            override fun onDateClick(timeStamap: Long) {
                Log.d("MainActivity", "Click : ${sdf.format(timeStamap)}")

            }
        }
    }
}

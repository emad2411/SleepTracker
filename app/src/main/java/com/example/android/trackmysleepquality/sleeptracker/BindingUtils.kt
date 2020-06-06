package com.example.android.trackmysleepquality.sleeptracker

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.R

@BindingAdapter ("sleepDurationFormatted")
fun TextView.setFormattedDuration(item:SleepNight?){
    item?.let {
        text= convertDurationToFormatted(it.startTimeMilli,it.endTimeMilli,context.resources)
    }
}

@BindingAdapter("sleepQualityFormatted")
fun TextView.setFormattedSleepQuality(item: SleepNight?){
    item?.let {
        text= convertNumericQualityToString(it.sleepQuality,context.resources)
    }
}
@BindingAdapter("sleepImage")
fun ImageView.setQualityImage(item:SleepNight?){
    item?.let {
        setImageResource(when (item.sleepQuality) {
            0 -> R.drawable.ic_sleep_0
            1 -> R.drawable.ic_sleep_1
            2 -> R.drawable.ic_sleep_2
            3 -> R.drawable.ic_sleep_3
            4 -> R.drawable.ic_sleep_4
            5 -> R.drawable.ic_sleep_5
            else -> R.drawable.ic_sleep_active
        })
    }
}

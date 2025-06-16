package com.pw.codeset.utils

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity


fun startCalendar(context: Context,time: Long) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val builder = CalendarContract.CONTENT_URI.buildUpon()
        builder.appendPath("time")
        ContentUris.appendId(builder,time)
        intent.setData(builder.build())
    context.startActivity(intent)
}

fun startCalendar2(context: Context) {

    // 尝试通用方法
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(CalendarContract.Events.CONTENT_URI)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // 如果通用方法失败，尝试厂商特定方法
        try {
            // 华为日历
            val intent = Intent(Intent.ACTION_MAIN)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setComponent(
                ComponentName(
                    "com.android.calendar",
                    "com.android.calendar.AllInOneActivity"
                )
            )
            intent.putExtra("viewType", 1) // 1通常表示列表视图
            context.startActivity(intent)
        } catch (e2: ActivityNotFoundException) {
            // 处理没有日历应用的情况
            Toast.makeText(context, "没有找到日历应用", Toast.LENGTH_SHORT).show()
        }
    }
}
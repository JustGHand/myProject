package com.pw.codeset.utils

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields


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

fun getNextTime(timestamp: Long,value:Long,unit: ChronoUnit): Long {
    val zoneId = ZoneId.systemDefault()

    val instant = Instant.ofEpochMilli(timestamp)
    val zonedDateTime = instant.atZone(zoneId)

    val after = zonedDateTime.plus(value, unit)

    return after.toInstant().toEpochMilli()
}

object DateJudge {

    private val zoneId: ZoneId = ZoneId.systemDefault()
//    private val weekFields: WeekFields = WeekFields.of(java.util.Locale.getDefault())
    private val weekFields: WeekFields = WeekFields.ISO

    /** 是否是今天 */
    fun isToday(timestamp: Long): Boolean {
        val target = toLocalDate(timestamp)
        return target == LocalDate.now(zoneId)
    }

    /** 是否早于今天（在今天 00:00 之前） */
    fun isBeforeToday(timestamp: Long): Boolean {
        val target = toLocalDate(timestamp)
        return target.isBefore(LocalDate.now(zoneId))
    }
    private fun toLocalDate(timestamp: Long): LocalDate =
        Instant.ofEpochMilli(timestamp)
            .atZone(zoneId)
            .toLocalDate()



    /** 是否是本周（按系统地区周起始日） */
    fun isThisWeek(timestamp: Long): Boolean {
        val target = toLocalDate(timestamp)
        val today = LocalDate.now(zoneId)

        return target.get(weekFields.weekOfWeekBasedYear()) ==
                today.get(weekFields.weekOfWeekBasedYear()) &&
                target.get(weekFields.weekBasedYear()) ==
                today.get(weekFields.weekBasedYear())
    }

    /** 是否是本月 */
    fun isThisMonth(timestamp: Long): Boolean {
        val target = toLocalDate(timestamp)
        val today = LocalDate.now(zoneId)

        return target.year == today.year &&
                target.month == today.month
    }

    /** 是否是本年 */
    fun isThisYear(timestamp: Long): Boolean {
        val target = toLocalDate(timestamp)
        val today = LocalDate.now(zoneId)

        return target.year == today.year
    }
}
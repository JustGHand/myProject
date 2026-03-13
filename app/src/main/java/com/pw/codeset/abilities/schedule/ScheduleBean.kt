package com.pw.codeset.abilities.schedule

import android.content.Context
import androidx.compose.ui.res.stringResource
import com.pw.codeset.R
import com.pw.codeset.utils.Constant


data class ScheduleFilterBean(
    val tag: Any,
    val text:String,
    val type:Int,
)


fun createScheduleStateFilterList(context: Context): List<ScheduleFilterBean>{
    val stateList = listOf(
        Constant.SCHEDULE_STATE_ALL,
        Constant.SCHEDULE_STATE_UNDONE,
        Constant.SCHEDULE_STATE_FINISHED,
        Constant.SCHEDULE_STATE_DELETED,
    )
    return stateList.map { ScheduleFilterBean(it, getStateFilterText(it, context), Constant.SCHEDULE_FILTER_TYPE_STATE) }
}

fun createScheduleTimeFilterList(context: Context): List<ScheduleFilterBean> {
    val timeList = listOf(
        Constant.SCHEDULE_DATE_TYPE_TODAY,
        Constant.SCHEDULE_DATE_TYPE_WEEK,
        Constant.SCHEDULE_DATE_TYPE_MONTH,
        Constant.SCHEDULE_DATE_TYPE_YEAR,
        Constant.SCHEDULE_DATE_TYPE_EARLIER,
        Constant.SCHEDULE_DATE_TYPE_ALL,
    )
    return timeList.map { ScheduleFilterBean(it,getTimeFilterText(it,context),Constant.SCHEDULE_FILTER_TYPE_TIME) }
}

fun getTimeFilterText(time:Int,context: Context):String {
    return when (time) {
        Constant.SCHEDULE_DATE_TYPE_EARLIER -> {
           context.getString(R.string.schedule_list_title_earlier)
        }

        Constant.SCHEDULE_DATE_TYPE_TODAY ->{
            context.getString(R.string.schedule_list_title_today)
        }
        Constant.SCHEDULE_DATE_TYPE_WEEK -> {
            context.getString(R.string.schedule_list_title_week)
        }
        Constant.SCHEDULE_DATE_TYPE_MONTH ->{
            context.getString(R.string.schedule_list_title_month)
        }
        Constant.SCHEDULE_DATE_TYPE_YEAR ->{
            context.getString(R.string.schedule_list_title_year)
        }
        else -> {
            context.getString(R.string.schedule_filter_all)
        }
    }
}
fun getStateFilterText(time:Int,context: Context):String {
    return when (time) {
        Constant.SCHEDULE_STATE_UNDONE -> {
           context.getString(R.string.schedule_filter_undone)
        }
        Constant.SCHEDULE_STATE_FINISHED -> {
           context.getString(R.string.schedule_filter_finished)
        }
        Constant.SCHEDULE_STATE_DELETED -> {
           context.getString(R.string.schedule_filter_deleted)
        }
        else -> {
            context.getString(R.string.schedule_filter_all)
        }
    }
}
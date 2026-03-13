package com.pw.codeset.abilities.schedule

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pw.codeset.databean.ScheduleBean
import com.pw.codeset.manager.ScheduleManager
import com.pw.codeset.utils.Constant
import com.pw.codeset.utils.DateJudge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(): ViewModel() {

    val filterMap= mutableMapOf<Int, ScheduleFilterBean>()
    private var _showingScheduleList = MutableStateFlow<List<ScheduleBean>>(emptyList())
    val showingScheduleList = _showingScheduleList.asStateFlow()

    private var _isEditMode = MutableStateFlow<Boolean>(false)
    val isEditMode = _isEditMode.asStateFlow()

    private var _mEditSelectedList = MutableStateFlow<MutableList<ScheduleBean>>(mutableListOf<ScheduleBean>())
    val mEditSelectedList = _mEditSelectedList.asStateFlow()

    fun refreshData() {
        onFilterChange()
    }

    fun changeFilter(filter: ScheduleFilterBean) {
        filterMap.put(filter.type,filter)
        onFilterChange()
    }

    fun onFilterChange() {
        val totalList = ScheduleManager.getInstance().scheduleList

        val timeFilter = filterMap.get(Constant.SCHEDULE_FILTER_TYPE_TIME)

        val filterByTime = totalList.filter {
            timeFilter?.let {filter->
                val isToday = DateJudge.isToday(it.getTarTime())
                val isThisWeek = DateJudge.isThisWeek(it.getTarTime())
                val isThisMonth = DateJudge.isThisMonth(it.getTarTime())
                val isThisYear = DateJudge.isThisYear(it.getTarTime())
                val isBeforeToday = DateJudge.isBeforeToday(it.getTarTime())
                when (filter.tag) {
                    Constant.SCHEDULE_DATE_TYPE_TODAY-> isToday
                    Constant.SCHEDULE_DATE_TYPE_WEEK-> !isToday && isThisWeek
                    Constant.SCHEDULE_DATE_TYPE_MONTH->!isToday && !isThisWeek && isThisMonth
                    Constant.SCHEDULE_DATE_TYPE_YEAR-> !isToday && !isThisWeek && !isThisMonth && isThisYear
                    Constant.SCHEDULE_DATE_TYPE_EARLIER-> isBeforeToday
                    else -> true
                }
            }?:run { true }
        }
        val stateFilter = filterMap.get(Constant.SCHEDULE_FILTER_TYPE_STATE)

        val filterByState = filterByTime.filter {
            stateFilter?.let { filter->
                filter.tag== Constant.SCHEDULE_STATE_ALL||it.status==filter.tag
            }?:run { true }
        }

        _showingScheduleList.value=filterByState
    }

    fun completeSchedule(scheduleBean: ScheduleBean) {
        ScheduleManager.getInstance().completeSchedule(scheduleBean)
        refreshData()
    }
    fun deleteSchedule(scheduleBean: ScheduleBean) {
        ScheduleManager.getInstance().deleteSchedule(scheduleBean,scheduleBean.status== Constant.SCHEDULE_STATE_DELETED)
        refreshData()
    }
    fun restoreSchedule(scheduleBean: ScheduleBean) {
        ScheduleManager.getInstance().restoreSchedule(scheduleBean)
        refreshData()
    }

    fun startEditMode() {
        _isEditMode.value = true
        _mEditSelectedList.value = mutableListOf<ScheduleBean>()
    }
    fun endEditMode(delete: Boolean) {
        if (delete) {
            ScheduleManager.getInstance().deleteSchedule(_mEditSelectedList.value)
        }
        _isEditMode.value = false
        _mEditSelectedList.value = mutableListOf<ScheduleBean>()
        refreshData()
    }
    fun selectItemOnEdit(item: ScheduleBean) {
        var result = mutableListOf<ScheduleBean>()
        result.addAll(_mEditSelectedList.value)
        if (result.contains(item)) {
            result.remove(item)
        }else{
            result.add(item)
        }
        _mEditSelectedList.value = result
    }
}
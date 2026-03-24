package com.pw.codeset.abilities.schedule

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.pw.baseutils.utils.NStringUtils
import com.pw.codeset.base.BaseBindingActivity
import com.pw.codeset.databean.ScheduleBean
import com.pw.codeset.databinding.ActSheduleEditBinding
import com.pw.codeset.manager.NotesManager
import com.pw.codeset.manager.ScheduleManager
import com.pw.codeset.utils.CommenUseViewUtils
import com.pw.codeset.utils.Constant
import com.pw.codeset.weidgt.InputDialog
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ScheduleEdit : BaseBindingActivity<ActSheduleEditBinding>() {

    var scheduleBean: ScheduleBean? = null
    var haveCheckWidget = false
    val mUnitList = arrayListOf<String>("天", "周", "月", "年")

    override fun initViewBinding(): ActSheduleEditBinding {
        return ActSheduleEditBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.scheduleTitleGroup.isVisible = false
        binding.scheduleEditDateRepeatSwitch.setOnCheckedChangeListener({ it, check ->
            binding.scheduleRepeatSetGroup.isVisible = check
            scheduleBean?.isRepeat = check
        })
        binding.scheduleEditDateEdit.setOnClickListener {
            showCalendarDialog()
        }
        binding.scheduleEditTitleEdit.doOnTextChanged { text,start,before,count->
            scheduleBean?.title = text.toString()
        }
        binding.scheduleEditDescEdit.doOnTextChanged { text, start, before, count ->
            scheduleBean?.desc=text.toString()
        }
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mUnitList).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.scheduleRepeatUnit.adapter = arrayAdapter
        binding.scheduleRepeatUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                adaptCountItems(selectedItem)
                scheduleBean?.repeatUnit = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        adaptCountItems("天")
        binding.scheduleEditLabelContainer.apply {
            NotesManager.getInstance().labelList.forEach({ label ->
                val labelView = CommenUseViewUtils.getNoteLabelView(this@ScheduleEdit,label,false,object :
                    CommenUseViewUtils.onLabelCheckListener{
                    override fun onCheckedChange(label: String?, isChecked: Boolean) {
                        if (isChecked) {
                            scheduleBean?.addLabel(label)
                        }else{
                            scheduleBean?.removeLabel(label)
                        }
                    }
                    override fun onLongClick(label: String?,view: View): Boolean {
                        removeView(view)
                        NotesManager.getInstance().deleteLabel(label)
                        return true
                    }
                })
                addView(labelView,0)
            })
        }
        binding.labelAddBtn.setOnClickListener {
            addLabel(it)
        }
    }

    fun addLabel(view: View?) {
        val inputDialog = InputDialog(this, object : InputDialog.DialogListener {
            override fun cancel() {
            }

            override fun confirm(content: String?) {
                if (NStringUtils.isNotBlank(content)) {
                    NotesManager.getInstance().addLabel(content)
                    binding.scheduleEditLabelContainer.apply {
                        val labelView = CommenUseViewUtils.getNoteLabelView(this@ScheduleEdit,content,false,object :
                            CommenUseViewUtils.onLabelCheckListener{
                            override fun onCheckedChange(label: String?, isChecked: Boolean) {
                                if (isChecked) {
                                    scheduleBean?.addLabel(label)
                                }else{
                                    scheduleBean?.removeLabel(label)
                                }
                            }

                            override fun onLongClick(label: String?,view: View): Boolean {
                                removeView(view)
                                NotesManager.getInstance().deleteLabel(label)
                                return true
                            }
                        })
                        addView(labelView,0)
                    }
                }
            }

            override fun editChange(content: String?) {
            }
        }, "输入标签", "创建标签")
        if (!this.isFinishing()) {
            inputDialog.show()
        }
    }


    override fun dealWithData() {
        val tarId = intent.getStringExtra(Constant.SCHEDULE_ID)
        if (!tarId.isNullOrBlank()) {
            scheduleBean = ScheduleManager.getInstance().getSchedule(tarId)
        }else{
            scheduleBean = ScheduleBean()
            (System.currentTimeMillis()).let {
                scheduleBean?.tarTime =it
            }
        }
        syncData()
    }

    fun syncData() {
        scheduleBean?.let {
            binding.scheduleEditDateEdit.text = NStringUtils.dateConvert(it.tarTime, Constant.DATA_PARTNER_WITH_CHAR_WITHOUT_TIME)
            binding.scheduleEditDescEdit.setText(it.desc)
            binding.scheduleEditDateRepeatSwitch.isChecked = it.isRepeat
            binding.scheduleRepeatSetGroup.isVisible = it.isRepeat
            if (it.isRepeat) {
                binding.scheduleRepeatUnit.setSelection(mUnitList.indexOf(it.repeatUnit))
                adaptCountItems(it.repeatUnit)
            }
        }
    }

    fun adaptCountItems(unit: String) {
        var countItems = mutableListOf<Int>()
        var countMax = 0
        when (unit) {
            "天" -> countMax = 7
            "周" -> countMax = 4
            "月" -> countMax = 12
            "年" -> countMax = 10
        }
        if (countMax > 0) {
            for (i in 1 until countMax) {
                countItems.add(i)
            }
        }
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, countItems).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        binding.scheduleRepeatCount.setAdapter(adapter)
        binding.scheduleRepeatCount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                scheduleBean?.repeatValue = selectedItem.toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.scheduleRepeatCount.post {
            scheduleBean?.repeatValue?.let {
                binding.scheduleRepeatCount.setSelection(it-1)
            }
        }
    }

    override fun onMenuClick(view: View?) {
        scheduleBean?.let {
            it.id = System.currentTimeMillis().toString()
            ScheduleManager.getInstance().addSchedule(it)
        }
        exit()
    }

    private fun showCalendarDialog() {

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("选择日期")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setStart(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            )
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            if (selection == null) return@addOnPositiveButtonClickListener

            // selection 是 UTC 的当天 00:00，需要转成本地日期
            val localDate = Instant.ofEpochMilli(selection)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val localDateTime = LocalDateTime.of(
                localDate.year,
                localDate.month,
                localDate.dayOfMonth,
                10,
                0
            )
        // ✅ 转成时间戳（毫秒）
            scheduleBean?.tarTime = localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            scheduleBean?.tarTime?.let {
                binding.scheduleEditDateEdit.text = NStringUtils.dateConvert(it, Constant.DATA_PARTNER_WITH_CHAR_WITHOUT_TIME)
            }
//            val timePicker = MaterialTimePicker.Builder()
//                .setTimeFormat(TimeFormat.CLOCK_24H)
//                .setHour(9)
//                .setMinute(0)
//                .setTitleText("选择时间")
//                .build()
//
//            timePicker.addOnPositiveButtonClickListener {
//
//                val hour = timePicker.hour
//                val minute = timePicker.minute
//
//                // ✅ 正确合成 LocalDateTime（无任何偏移）
//                val localDateTime = LocalDateTime.of(
//                    localDate.year,
//                    localDate.month,
//                    localDate.dayOfMonth,
//                    hour,
//                    minute
//                )
//
//                // ✅ 转成时间戳（毫秒）
//                scheduleBean?.tarTime = localDateTime
//                    .atZone(ZoneId.systemDefault())
//                    .toInstant()
//                    .toEpochMilli()
//                scheduleBean?.tarTime?.let {
//                    binding.scheduleEditDateEdit.text = NStringUtils.dateConvert(it, Constant.DATA_PARTNER_WITH_CHAR)
//                }
//            }
//
//            timePicker.show(supportFragmentManager, "TIME_PICKER_TAG")
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
    }



    fun exit() {
//        if (!haveCheckWidget) {
//            checkWidget()
//            return
//        }
        finish()
    }

    fun checkWidget() {
//        val appWidgetManager = this.getSystemService(AppWidgetManager::class.java)
//        val myProvider = ComponentName(this, ScheduleGlanceWidget::class.java)
//
//        if (appWidgetManager.isRequestPinAppWidgetSupported) {
//            // 成功添加后的回调（可选）
////            val successCallback = PendingIntent.getBroadcast(...)
//            appWidgetManager.requestPinAppWidget(myProvider, null, null)
//        }
        haveCheckWidget = true
    }

}
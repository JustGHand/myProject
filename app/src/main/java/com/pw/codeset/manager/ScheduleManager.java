package com.pw.codeset.manager;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pw.baseutils.utils.NStringUtils;
import com.pw.baseutils.utils.SPUtils;
import com.pw.codeset.R;
import com.pw.codeset.abilities.schedule.ScheduleAppWidgetProvider;
import com.pw.codeset.application.MyApp;
import com.pw.codeset.databean.NotesBean;
import com.pw.codeset.databean.ScheduleBean;
import com.pw.codeset.utils.CalendarUtilKt;
import com.pw.codeset.utils.Constant;
import com.pw.codeset.utils.DateJudge;
import com.pw.codeset.utils.GlanceUtils;
import com.pw.codeset.utils.SaveFileUtils;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ScheduleManager {

    public static ScheduleManager mInstance;
    private List<ScheduleBean> mScheduleList;

    public MutableLiveData<List<ScheduleBean>> earlierScheduleList = new MutableLiveData<>();
    public MutableLiveData<List<ScheduleBean>> todayScheduleList = new MutableLiveData<>();
    public MutableLiveData<List<ScheduleBean>> weekScheduleList = new MutableLiveData<>();
    public MutableLiveData<List<ScheduleBean>> monthScheduleList = new MutableLiveData<>();
    public MutableLiveData<List<ScheduleBean>> yearScheduleList = new MutableLiveData<>();

    public static ScheduleManager getInstance() {
        if (mInstance == null) {
            synchronized (ScheduleManager.class) {
                if (mInstance == null) {
                    mInstance = new ScheduleManager();
                }
            }
        }
        return mInstance;
    }

    ScheduleManager() {
        init();
    }

    public void init() {
        readScheduleListFromFile();
    }


    public void addSchedule(ScheduleBean scheduleBean) {
        if (mScheduleList == null) {
            mScheduleList = new ArrayList<>();
        }
        if (needAutoComplete(scheduleBean)) {
            completeSchedule(scheduleBean);
        }
        mScheduleList.add(scheduleBean);
        updateList();
        saveScheduleListToFile();
    }

    public void deleteSchedule(ScheduleBean scheduleBean,Boolean realDelete) {
        deleteSingleSchedule(scheduleBean, realDelete);
        updateList();
        saveScheduleListToFile();
    }

    private void deleteSingleSchedule(ScheduleBean scheduleBean,Boolean realDelete) {
        ScheduleBean tarScheduleBean = scheduleBean;
        for (int i = 0; i < mScheduleList.size(); i++) {
            ScheduleBean item = mScheduleList.get(i);
            if (item.getId().equals(scheduleBean.getId())) {
                tarScheduleBean = item;
            }
        }
        if (realDelete) {
            if (mScheduleList == null) {
                mScheduleList = new ArrayList<>();
            }
            if (mScheduleList.contains(tarScheduleBean)) {
                mScheduleList.remove(tarScheduleBean);
            }
        }else {
            tarScheduleBean.setStatus(Constant.SCHEDULE_STATE_DELETED);
        }
    }

    public void deleteSchedule(List<ScheduleBean> scheduleBeanList) {
        for (ScheduleBean scheduleBean : scheduleBeanList) {
            deleteSingleSchedule(scheduleBean,scheduleBean.getStatus()==Constant.SCHEDULE_STATE_DELETED);
        }
    }

    public void restoreSchedule(ScheduleBean scheduleBean) {
        scheduleBean.setStatus(Constant.SCHEDULE_STATE_DELETED);
        updateList();
        saveScheduleListToFile();
    }

    public List<ScheduleBean> getScheduleList() {
        return mScheduleList;
    }

    public List<ScheduleBean> getEarlierScheduleList() {
        List<ScheduleBean> result = new ArrayList<>();
        for (int i = 0; i < mScheduleList.size(); i++) {
            ScheduleBean scheduleBean = mScheduleList.get(i);
            if (DateJudge.INSTANCE.isBeforeToday(scheduleBean.getTarTime())) {
                result.add(scheduleBean);
            }
        }
        return result;
    }
    public List<ScheduleBean> getTodayScheduleList() {
        List<ScheduleBean> result = new ArrayList<>();
        for (int i = 0; i < mScheduleList.size(); i++) {
            ScheduleBean scheduleBean = mScheduleList.get(i);
            if (DateJudge.INSTANCE.isToday(scheduleBean.getTarTime())) {
                result.add(scheduleBean);
            }
        }
        return result;
    }
    public List<ScheduleBean> getWeekScheduleList() {
        List<ScheduleBean> result = new ArrayList<>();
        for (int i = 0; i < mScheduleList.size(); i++) {
            ScheduleBean scheduleBean = mScheduleList.get(i);
            if (DateJudge.INSTANCE.isThisWeek(scheduleBean.getTarTime())) {
                result.add(scheduleBean);
            }
        }
        return result;
    }
    public List<ScheduleBean> getMonthScheduleList() {
        List<ScheduleBean> result = new ArrayList<>();
        for (int i = 0; i < mScheduleList.size(); i++) {
            ScheduleBean scheduleBean = mScheduleList.get(i);
            if (DateJudge.INSTANCE.isThisMonth(scheduleBean.getTarTime())) {
                result.add(scheduleBean);
            }
        }
        return result;
    }
    public List<ScheduleBean> getYearScheduleList() {
        List<ScheduleBean> result = new ArrayList<>();
        for (int i = 0; i < mScheduleList.size(); i++) {
            ScheduleBean scheduleBean = mScheduleList.get(i);
            if (DateJudge.INSTANCE.isThisYear(scheduleBean.getTarTime())) {
                result.add(scheduleBean);
            }
        }
        return result;
    }

    public ScheduleBean getSchedule(String scheduleId) {
        if (mScheduleList == null) {
            mScheduleList = new ArrayList<>();
        }
        if (mScheduleList.isEmpty()) {
            return null;
        }
        for (int i = 0; i < mScheduleList.size(); i++) {
            ScheduleBean scheduleBean = mScheduleList.get(i);
            if (scheduleBean != null) {
                if (scheduleBean.getId().equals(scheduleId)) {
                    return scheduleBean;
                }
            }
        }
        return null;
    }

    /**
     * 完成日程任务
     * 🈲禁止在遍历任务列表时调用
     * @param scheduleBean
     */
    public void completeSchedule(ScheduleBean scheduleBean) {
        if (scheduleBean.isRepeat()) {
            ScheduleBean repeatBean =
                    mScheduleList.stream()
                            .filter(bean -> bean.getId().equals(scheduleBean.getId()+Constant.SCHEDULE_REPEAT_TOKEN))
                            .findAny()
                            .orElse(null);
            if (repeatBean != null) {
                ScheduleBean copy = scheduleBean.copy();
                addSchedule(copy);
            }

            String repeatUnit = scheduleBean.getRepeatUnit();
            ChronoUnit unit =ChronoUnit.DAYS;
            switch (repeatUnit) {
                case "天":
                    unit = ChronoUnit.DAYS;
                    break;
                case "周":
                    unit =  ChronoUnit.WEEKS;
                    break;
                case "月":
                    unit =  ChronoUnit.MONTHS;
                    break;
                case "年":
                    unit =  ChronoUnit.YEARS;
                    break;
                default:
                    unit = ChronoUnit.DAYS;
                    break;
            }
            long nextTime = CalendarUtilKt.getNextTime(scheduleBean.getTarTime(), scheduleBean.getRepeatValue(), unit);
            scheduleBean.setTarTime(nextTime);
        }else {
            scheduleBean.setStatus(1);
        }
        updateList();
        saveScheduleListToFile();
    }

    private void readScheduleListFromFile() {
        String scheduleListStr = SaveFileUtils.getScheduleListStr();
        if (NStringUtils.isNotBlank(scheduleListStr)) {
            mScheduleList = new Gson().fromJson(scheduleListStr,new TypeToken<List<ScheduleBean>>(){}.getType());
        }else {
            mScheduleList = new ArrayList<>();
        }
        verifyScheduleList();
        updateList();
    }

    private void verifyScheduleList() {
        List<ScheduleBean> expiredSchedules = new ArrayList<>();
        for (int i = 0; i < mScheduleList.size(); i++) {
            ScheduleBean scheduleBean = mScheduleList.get(i);
            if (needAutoComplete(scheduleBean)) {
                if (scheduleBean.isRepeat()) {
                    expiredSchedules.add(scheduleBean);
                }
            }
        }
        for (int i = 0; i < expiredSchedules.size(); i++) {
            ScheduleBean scheduleBean = expiredSchedules.get(i);
            completeSchedule(scheduleBean);
        }
    }

    private void updateList() {
        earlierScheduleList.postValue(getEarlierScheduleList());
        todayScheduleList.postValue(getTodayScheduleList());
        weekScheduleList.postValue(getWeekScheduleList());
        monthScheduleList.postValue(getMonthScheduleList());
        yearScheduleList.postValue(getYearScheduleList());
    }

    private boolean needAutoComplete(ScheduleBean scheduleBean) {
        return !isRepeatSchedule(scheduleBean) && DateJudge.INSTANCE.isBeforeToday(scheduleBean.getTarTime());
    }

    private boolean isRepeatSchedule(ScheduleBean scheduleBean) {
        return scheduleBean.getId().contains(Constant.SCHEDULE_REPEAT_TOKEN);
    }


    private void saveScheduleListToFile() {
        String scheduleStr = "";
        if (mScheduleList != null && mScheduleList.size() > 0) {
            Collections.sort(mScheduleList, new Comparator<ScheduleBean>() {
                @Override
                public int compare(ScheduleBean o1, ScheduleBean o2) {
                    if (o1 == null) {
                        return 1;
                    }
                    return o1.compareTo(o2);
                }
            });
            scheduleStr = new Gson().toJson(mScheduleList);
        }
        SaveFileUtils.saveScheduleList(scheduleStr);
        updateWidget();
    }

    private void updateWidget() {
        Context context = MyApp.getInstance();
        if (context != null) {
            // 直接传入具体的类名
            GlanceUtils.INSTANCE.updateAllWidgets(
                    context,
                    ScheduleAppWidgetProvider.class,
                    R.id.lv_schedules
            );
        }
    }

}

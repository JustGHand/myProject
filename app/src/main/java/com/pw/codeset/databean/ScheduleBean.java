package com.pw.codeset.databean;

import com.google.gson.Gson;
import com.pw.codeset.utils.Constant;

import java.util.Date;

public class ScheduleBean {
    String id;
    long time;//创建时间戳
    Date tarDate;//指定日期
    long tarTime;//指定时间戳
    String title;//标题
    String desc;//备注
    boolean repeat;//是否重复
    int repeatValue;//重复值
    String repeatUnit;//重复单位 1:天   2: 星期  3：月  4：年
    int status;//状态 0：待完成 1：已完成 2:已删除

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Date getTarDate() {
        return tarDate;
    }

    public void setTarDate(Date tarDate) {
        this.tarDate = tarDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public int getRepeatValue() {
        return repeatValue;
    }

    public void setRepeatValue(int repeatValue) {
        this.repeatValue = repeatValue;
    }

    public String getRepeatUnit() {
        return repeatUnit;
    }

    public void setRepeatUnit(String repeatUnit) {
        this.repeatUnit = repeatUnit;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTarTime() {
        return tarTime;
    }

    public void setTarTime(long tarTime) {
        this.tarTime = tarTime;
        this.tarDate = new Date(tarTime);
    }

    public ScheduleBean copy(){

        ScheduleBean copyResult = new Gson().fromJson(new Gson().toJson(this),ScheduleBean.class);
        copyResult.setId(this.id + Constant.SCHEDULE_REPEAT_TOKEN);

        return copyResult;
    }
    public int compareTo(ScheduleBean scheduleBean) {
        if (scheduleBean == null) {
            return -1;
        }if (this.status < scheduleBean.status) {
            return -1;
        } else if (this.status > scheduleBean.status) {
            return 1;
        }
        if (this.tarTime < scheduleBean.tarTime) {
            return -1;
        } else if (this.time > scheduleBean.time) {
            return 1;
        }else {
            return 0;
        }
    }
}

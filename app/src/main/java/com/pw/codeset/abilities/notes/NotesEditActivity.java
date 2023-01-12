package com.pw.codeset.abilities.notes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.constraintlayout.widget.Group;

import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.databean.NotesBean;
import com.pw.codeset.manager.NotesManager;
import com.pw.codeset.utils.CalendarReminderUtils;
import com.pw.codeset.utils.Constant;
import com.pw.codeset.utils.LogToastUtils;
import com.xd.baseutils.utils.NStringUtils;

import java.util.Calendar;

public class NotesEditActivity extends BaseActivity {

    NotesBean mNoteBean;

    EditText mTitleEdit;
    TextView mDateEdit;
    EditText mContentEdit;

    Button mAddCalendarBtn;
    Group mCalendarGroup;

    int mCalendarId = -1;

    @Override
    protected int getContentId() {
        return R.layout.activity_notes_edit;
    }

    @Override
    protected void initView() {


        mTitleEdit = findViewById(R.id.notes_edit_title_edit);
        mDateEdit = findViewById(R.id.notes_edit_date_edit);
        mContentEdit = findViewById(R.id.notes_edit_content_edit);
        mAddCalendarBtn = findViewById(R.id.notes_add_calendar);
        mCalendarGroup = findViewById(R.id.notes_calendar_group);

    }

    @Override
    protected void dealWithData() {

        String noteId = getIntent().getStringExtra(Constant.NOTE_ID);
        if (NStringUtils.isNotBlank(noteId)) {
            mNoteBean = NotesManager.getInstance().getNote(noteId);
        }
        if (mNoteBean == null) {
            mNoteBean = new NotesBean();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        saveNote();
    }

    @Override
    protected void onMenuClick(View view) {
        saveNote();
        finish();
    }

    @Override
    protected void finishData() {
        mCalendarGroup.setVisibility(View.GONE);
        if (mNoteBean != null) {
            String noteTitle = mNoteBean.getTitle();
            if (NStringUtils.isNotBlank(noteTitle)) {
                mTitleEdit.setText(noteTitle);
            }
            Long noteTime = mNoteBean.getDate();
            String noteDate = "";
            if (noteTime != null && noteTime > 0) {
                noteDate = NStringUtils.dateConvert(noteTime, Constant.DATA_PARTNER_WITH_LINE);
            }
            if (NStringUtils.isBlank(noteDate)) {
                noteDate = getResources().getString(R.string.notes_edit_date);
            }
            mDateEdit.setText(noteDate);

            mCalendarId = mNoteBean.getPwCalendarId();

            mContentEdit.setText(mNoteBean.getContent());

            varifyCalendarView();

        }
    }

    private void varifyCalendarView() {
        if (mCalendarId >= 0) {
            long calendarTime = CalendarReminderUtils.getCalendarTime(this, mCalendarId);
            if (calendarTime > 0) {
                mCalendarGroup.setVisibility(View.VISIBLE);
                mAddCalendarBtn.setText("删除日历事件");
                mDateEdit.setText(NStringUtils.dateConvert(calendarTime, Constant.DATA_PARTNER_WITH_LINE));
                return;
            }
        }
        mCalendarGroup.setVisibility(View.GONE);
        mAddCalendarBtn.setText("添加日历事件");
    }

    public void addCalendar(View view) {
        if (view.getId() == R.id.notes_add_calendar) {
            if (mCalendarId >= 0) {
                CalendarReminderUtils.deleteCalendarEvent(this,mCalendarId);
                mCalendarId = -1;
                varifyCalendarView();
                return;
            }
        }
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                new TimePickerDialog(NotesEditActivity.this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String title = mTitleEdit.getText().toString();
                        String content = mContentEdit.getText().toString();
                        LogToastUtils.printLog(year+'-'+(month+1)+"-"+dayOfMonth+" "+hourOfDay+":"+minute);
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(year, month, dayOfMonth, hourOfDay, minute);
                        long time = calendar1.getTimeInMillis();
                        if (mCalendarId >= 0) {
                            CalendarReminderUtils.deleteCalendarEvent(NotesEditActivity.this,mCalendarId);
                        }
                        mCalendarId = CalendarReminderUtils.addCalendarEvent(NotesEditActivity.this,title,content,time,0);
                        varifyCalendarView();
                    }
                },calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),true).show();
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveNote() {
        if (mNoteBean == null) {
            mNoteBean = new NotesBean();
        }
        String title = mTitleEdit.getText().toString();
        String content = mContentEdit.getText().toString();
        if (NStringUtils.isBlank(title)) {
            title = getResources().getString(R.string.notes_edit_title_hint);
        }
        if (NStringUtils.isBlank(content)) {
            content = getResources().getString(R.string.notes_edit_content_hint);
        }
        mNoteBean.setTitle(title);
        mNoteBean.setContent(content);
        mNoteBean.setPwCalendarId(mCalendarId);
        NotesManager.getInstance().updateNotes(mNoteBean);
    }
}

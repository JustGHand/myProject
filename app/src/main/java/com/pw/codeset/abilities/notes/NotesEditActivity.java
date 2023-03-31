package com.pw.codeset.abilities.notes;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.databean.NotesBean;
import com.pw.codeset.manager.NotesManager;
import com.pw.codeset.utils.CalendarReminderUtils;
import com.pw.codeset.utils.CommenUseViewUtils;
import com.pw.codeset.utils.Constant;
import com.pw.codeset.utils.GlideEngine;
import com.pw.codeset.utils.LogToastUtils;
import com.pw.codeset.utils.SaveFileUtils;
import com.pw.codeset.weidgt.IconImageView;
import com.pw.codeset.weidgt.ImagesContainer;
import com.pw.codeset.weidgt.InputDialog;
import com.pw.codeset.weidgt.WarpLinearLayout;
import com.pw.baseutils.utils.ArrayUtils;
import com.pw.baseutils.utils.FileUtil;
import com.pw.baseutils.utils.NStringUtils;
import com.pw.baseutils.utils.PermissionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotesEditActivity extends BaseActivity {

    public static final int REUQEST_CODE_SPLASH_PERMISSION = 1001;
    public static final int REUQEST_CODE_INSTALLAPK_PERMISSION = 1002;


    NotesBean mNoteBean;

    EditText mTitleEdit;
    TextView mDateEdit;
    EditText mContentEdit;

    TextView mAddCalendarBtn;
    Group mCalendarGroup;

    List<String> mAllLabels;
    List<String> mSelectedLabels;
    IconImageView mLabelAddBtn;
    WarpLinearLayout mLabelContainer;

    ImagesContainer mImagesContainer;

    private List<String> mDeleteImages;

    int mCalendarId = -1;

    @Override
    protected int getContentId() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_notes_edit;
    }

    @Override
    protected void initView() {


        mTitleEdit = findViewById(R.id.notes_edit_title_edit);
        mDateEdit = findViewById(R.id.notes_edit_date_edit);
        mContentEdit = findViewById(R.id.notes_edit_content_edit);
        mAddCalendarBtn = findViewById(R.id.notes_add_calendar);
        mCalendarGroup = findViewById(R.id.notes_calendar_group);

        mLabelAddBtn = findViewById(R.id.notes_edit_label_add);
        mLabelContainer = findViewById(R.id.notes_edit_label_container);


        mImagesContainer = findViewById(R.id.notes_edit_content_images);
        mImagesContainer.setListener(new ImagesContainer.ImagesListener() {
            @Override
            public void onDelete(String url) {
                if (mDeleteImages == null) {
                    mDeleteImages = new ArrayList<>();
                }
                mDeleteImages.add(url);
                if (mNoteBean!=null) {
                    List<String> imageList = mNoteBean.getImageList();
                    if (ArrayUtils.isArrayEnable(imageList)) {
                        if (NStringUtils.isNotBlank(url)) {
                            mNoteBean.removeImage(url);
                            mImagesContainer.removeImage(url);
                        }
                    }
                }
            }

            @Override
            public boolean onImageClick( String url) {
                return false;
            }

            @Override
            public void onAddClick() {
                addImageToContent(null);
            }
        });


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

        mAllLabels = NotesManager.getInstance().getLabelList();

    }

    @Override
    protected void onPause() {
        super.onPause();
        saveNote();
    }

    @Override
    protected void onMenuClick(View view) {
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
        generateLabelViews();
        generateImages();
    }

    public void addLabel(View view) {
        InputDialog inputDialog = new InputDialog(this, new InputDialog.DialogListener() {
            @Override
            public void cancel() {

            }

            @Override
            public void confirm(String content) {
                if (NStringUtils.isNotBlank(content)) {
                    NotesManager.getInstance().addLabel(content);
                    mAllLabels = NotesManager.getInstance().getLabelList();
                    generateLabelViews();
                }
            }

            @Override
            public void editChange(String content) {

            }
        }, "输入标签", "创建标签");
        if (!this.isFinishing()) {
            inputDialog.show();
        }
    }

    private void varifyCalendarView() {
        if (mCalendarId >= 0) {
            long calendarTime = CalendarReminderUtils.getCalendarTime(this, mCalendarId);
            if (calendarTime > 0) {
                mCalendarGroup.setVisibility(View.VISIBLE);
                mAddCalendarBtn.setTextColor(getResources().getColor(R.color.normal_import_color));
//                mAddCalendarBtn.setText("删除日历事件");
                mDateEdit.setText(NStringUtils.dateConvert(calendarTime, Constant.DATA_PARTNER_WITH_LINE));
                return;
            }
        }
        mCalendarGroup.setVisibility(View.GONE);
        mAddCalendarBtn.setTextColor(getResources().getColor(R.color.normal_text_color));
//        mAddCalendarBtn.setText("添加日历事件");
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
                        saveNote();
                    }
                },calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),true).show();
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void generateLabelViews() {
        mLabelContainer.removeAllViews();
        if (ArrayUtils.isArrayEnable(mAllLabels)) {
            for (int i = 0; i < mAllLabels.size(); i++) {
                String label = mAllLabels.get(i);
                if (NStringUtils.isNotBlank(label)) {
                    CheckBox labelView = CommenUseViewUtils.getNoteLabelView(this, label, isLabelSelected(label), new CommenUseViewUtils.onLabelCheckListener() {
                        @Override
                        public void onCheckedChange(String label, boolean isChecked) {
                            setLabelStatus(label, isChecked);
                        }
                    });
                    mLabelContainer.addView(labelView);
                }
            }
        }
    }

    private void setLabelStatus(String label, boolean isChecked) {
        if (isChecked && !isLabelSelected(label)) {
            mNoteBean.addLabel(label);
        } else if(!isChecked && isLabelSelected(label)){
            mNoteBean.removeLabel(label);
        }
    }

    private boolean isLabelSelected(String label) {
        if (mNoteBean != null) {
            if (mNoteBean.haveLabel(label)) {
                return true;
            }
        }
        return false;
    }

    public void addImageToContent(View view) {
        checkPermission();
    }

    private void generateImages() {
        if (mNoteBean != null) {
            List<String> imageList = mNoteBean.getImageList();
            if (ArrayUtils.isArrayEnable(imageList)) {
                for (int i = 0; i < imageList.size(); i++) {
                    String imagePath = imageList.get(i);
                    if (NStringUtils.isNotBlank(imagePath)) {
                        mImagesContainer.addImage(imagePath);
                    }
                }
            }
        }
    }

    private void checkPermission() {

        if (PermissionUtils.isWritePermissionGranted(this)) {
            openFileManager();
        }else {
            String[] permission = new String[1];
            permission[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permission,REUQEST_CODE_SPLASH_PERMISSION);
            }else {
                ActivityCompat.requestPermissions(this,permission,REUQEST_CODE_SPLASH_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.hasAllPermissionsGranted(grantResults)) {
            openFileManager();
        }else {
            LogToastUtils.show("缺少必要权限，无法进行文件选择");
        }
    }

    // 打开文件管理器选择文件
    private void openFileManager() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine((ImageEngine) GlideEngine.createGlideEngine())
                .selectionMode(PictureConfig.MULTIPLE)//单选
                .isPreviewImage(true)//预览图片
                .setOfAllCameraType(PictureMimeType.ofImage())//拍照，不允许录制
                .isEditorImage(true)//编辑图片
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        // onResult Callback
                        LogToastUtils.printLog("picture selector result");
                        if (result != null && result.size() > 0) {
                            for (int i = 0; i < result.size(); i++) {
                                LocalMedia media = result.get(i);
                                if (media != null) {
                                    String filePath = media.getRealPath();
                                    String fileSuffix = NStringUtils.getFileSuffix(filePath);
                                    String tarPath = SaveFileUtils.getNoteImageFileFolder() + File.separator + mNoteBean.getId() + System.currentTimeMillis() + i+"."+fileSuffix;
                                    FileUtil.copyFile(filePath, tarPath);
                                    mImagesContainer.addImage(tarPath);
                                    mNoteBean.addImage(tarPath);
                                }
                            }
                            saveNote();
                        }
                    }

                    @Override
                    public void onCancel() {
                        // onCancel Callback
                        LogToastUtils.printLog("picture selector cancel");
                    }
                });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // onResult Callback
                    List<LocalMedia> result = PictureSelector.obtainMultipleResult(data);
                    break;
                default:
                    break;
            }
        }
    }


    private void saveNote() {
        if (ArrayUtils.isArrayEnable(mDeleteImages)) {
            for (int i = 0; i < mDeleteImages.size(); i++) {
                String deletedFile = mDeleteImages.get(i);
                if (NStringUtils.isNotBlank(deletedFile)) {
                    FileUtil.deleteFile(deletedFile);
                }
            }
        }
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

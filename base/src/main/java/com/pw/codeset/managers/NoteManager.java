package com.pw.codeset.managers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pw.codeset.activity.model.NoteSaveModel;
import com.pw.codeset.base.BaseManagerWithFile;
import com.pw.codeset.utils.Constant;
import com.xd.base.file.FileUtils;
import com.xd.base.utils.NStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteManager extends BaseManagerWithFile<NoteSaveModel> {

    public static NoteManager mInstance;

    public static NoteManager getInstance() {
        if (mInstance == null) {
            synchronized (NoteManager.class) {
                if (mInstance == null) {
                    mInstance = new NoteManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void varifyData() {
        super.varifyData();
        if (mData != null) {
            mNoteMap = mData.getModels();
        }
        if (mNoteMap == null) {
            mNoteMap = new HashMap<>();
        }
        if (mTags == null) {
            mTags = new ArrayList<>();
        }
        for (String key : mNoteMap.keySet()) {
            mTags.add(key);
        }
    }

    Map<String , NoteSaveModel.NoteModel> mNoteMap;
    List<String> mTags;

    private void saveToFile() {
        mData.setModels(mNoteMap);
        saveDataToFile(mData);
    }

    NoteManager() {
        varifyData();
    }

    public void saveNewNote(String content,String tag) {
        varifyData();
        NoteSaveModel.NoteModel noteModel = new NoteSaveModel.NoteModel();
        mNoteMap.put(tag, noteModel);
        saveToFile();
    }

    public List<String> getTags() {
        varifyData();
        return mTags;
    }

    public List<NoteSaveModel.NoteModel> getNotes(String tag) {

        List<NoteSaveModel.NoteModel> resultList = new ArrayList<>();

        if (NStringUtils.isNotBlank(tag)) {
            for (String key : mNoteMap.keySet()) {
                if (NStringUtils.isNotBlank(key) && key.equals(tag)) {
                    resultList.add(mNoteMap.get(key));
                }
            }
        }


        return resultList;
    }

    @Override
    public String getDataFileName() {
        return Constant.FILE_NAME_NOTE;
    }
}

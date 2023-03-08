package com.pw.codeset.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pw.codeset.databean.RecordBean;
import com.pw.codeset.utils.SaveFileUtils;
import com.pw.baseutils.utils.NStringUtils;

import java.util.ArrayList;
import java.util.List;

public class BookRecordManager {
    public static BookRecordManager mInstance;

    public static BookRecordManager getInstance() {
        if (mInstance == null) {
            synchronized (BookRecordManager.class) {
                if (mInstance == null) {
                    mInstance = new BookRecordManager();
                }
            }
        }
        return mInstance;
    }

    BookRecordManager() {
        init();
    }

    List<RecordBean> mReadRecordList;

    public void init() {
        readRecordFromFile();
    }

    public RecordBean getBookRecord(String bookId) {
        if (NStringUtils.isNotBlank(bookId)) {
            if (mReadRecordList != null && mReadRecordList.size() > 0) {
                for (int i = 0; i < mReadRecordList.size(); i++) {
                    RecordBean recordBean = mReadRecordList.get(i);
                    if (recordBean != null && bookId.equals(recordBean.getBookId())) {
                        return recordBean;
                    }
                }
            }
        }
        return null;
    }

    public void saveBookRecord(String bookId, int chapterPos, int charPos) {
        RecordBean bookRecord = getBookRecord(bookId);
        if (bookRecord == null) {
            bookRecord = new RecordBean();
            bookRecord.setBookId(bookId);
            mReadRecordList.add(bookRecord);
        }
        bookRecord.setChapterPos(chapterPos);
        bookRecord.setStartCharPos(charPos);
        saveReadRecordToFile();
    }

    private void saveReadRecordToFile() {
        String recordStr = "";
        if (mReadRecordList != null && mReadRecordList.size() > 0) {
            recordStr = new Gson().toJson(mReadRecordList);
        }
        SaveFileUtils.saveReadRecord(recordStr);
    }

    private void readRecordFromFile() {
        String recordStr = SaveFileUtils.getReadRecordStr();
        if (NStringUtils.isNotBlank(recordStr)) {
            mReadRecordList = new Gson().fromJson(recordStr,new TypeToken<List<RecordBean>>(){}.getType());
        }else {
            mReadRecordList = new ArrayList<>();
        }
    }
}

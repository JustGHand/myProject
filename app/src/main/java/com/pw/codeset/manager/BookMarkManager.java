package com.pw.codeset.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pw.codeset.databean.BookMarkBean;
import com.pw.codeset.databean.RecordBean;
import com.pw.codeset.utils.SaveFileUtils;
import com.xd.baseutils.utils.NStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookMarkManager {
    public static BookMarkManager mInstance;

    public static BookMarkManager getInstance() {
        if (mInstance == null) {
            synchronized (BookMarkManager.class) {
                if (mInstance == null) {
                    mInstance = new BookMarkManager();
                }
            }
        }
        return mInstance;
    }

    BookMarkManager() {
        init();
    }

    Map<String,List<BookMarkBean>> mMarkMap;


    private void init() {
        readRecordFromFile();
    }

    public void saveBookMark(String bookId,String chapterTitle,String lineContent, int chapterPos, int charPos) {
        if (mMarkMap == null) {
            mMarkMap = new HashMap<>();
        }
        if (NStringUtils.isNotBlank(bookId)) {
            List<BookMarkBean> curBookMarkList = mMarkMap.get(bookId);
            if (curBookMarkList == null) {
                curBookMarkList = new ArrayList<>();
            }
            BookMarkBean bookMarkBean = new BookMarkBean(bookId, chapterPos, charPos);
            bookMarkBean.setChapterTitle(chapterTitle);
            bookMarkBean.setLineContent(lineContent);
            curBookMarkList.add(bookMarkBean);
            mMarkMap.put(bookId, curBookMarkList);
        }
        saveReadRecordToFile();
    }

    public void deleteBookMark(BookMarkBean bookMarkBean) {
        if (bookMarkBean != null) {
            String bookId = bookMarkBean.getBookId();
            if (NStringUtils.isNotBlank(bookId)) {
                if (mMarkMap != null) {
                    List<BookMarkBean> bookMarkBeanList = mMarkMap.get(bookId);
                    if (bookMarkBeanList != null && bookMarkBeanList.contains(bookMarkBean)) {
                        bookMarkBeanList.remove(bookMarkBean);
                    }
                    mMarkMap.put(bookId, bookMarkBeanList);
                }
            }
        }
        saveReadRecordToFile();
    }

    public void deleteBookMark(String bookId, int chapterPos, int charPos, int charCount) {
        if (NStringUtils.isNotBlank(bookId)) {
            BookMarkBean bookMarkBean = getPageBookMark(bookId, chapterPos, charPos, charCount);
            if (bookMarkBean != null) {
                if (mMarkMap != null) {
                    List<BookMarkBean> bookMarkBeanList = mMarkMap.get(bookId);
                    if (bookMarkBeanList != null && bookMarkBeanList.contains(bookMarkBean)) {
                        bookMarkBeanList.remove(bookMarkBean);
                    }
                    mMarkMap.put(bookId, bookMarkBeanList);
                }
            }
        }
        saveReadRecordToFile();
    }

    public List<BookMarkBean> getBookMarkList(String bookId) {
        if (NStringUtils.isNotBlank(bookId) && mMarkMap != null) {
            List<BookMarkBean> result = mMarkMap.get(bookId);
            if (result != null) {
                return result;
            }
        }
        return new ArrayList<>();
    }

    public boolean isCurPageHasMark(String bookId, int chapterPos, int charPos, int charCount) {
        if (NStringUtils.isNotBlank(bookId)) {
            return getPageBookMark(bookId, chapterPos, charPos, charCount) != null;
        }
        return false;
    }

    private BookMarkBean getPageBookMark(String bookId, int chapterPos, int charPos, int charCount) {
        if (NStringUtils.isNotBlank(bookId)) {
            if (mMarkMap != null) {
                List<BookMarkBean> bookMarkList = mMarkMap.get(bookId);
                if (bookMarkList != null && !bookMarkList.isEmpty()) {
                    for (int i = 0; i < bookMarkList.size(); i++) {
                        BookMarkBean bookMarkBean = bookMarkList.get(i);
                        if (bookMarkBean != null) {
                            int bookMarkChapterPos = bookMarkBean.getChapterPos();
                            int bookMarkCharPos = bookMarkBean.getCharPos();
                            if (bookMarkChapterPos == chapterPos) {
                                if (bookMarkCharPos >= charPos && bookMarkCharPos < charPos + charCount) {
                                    return bookMarkBean;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void saveReadRecordToFile() {
        String recordStr = "";
        if (mMarkMap != null && mMarkMap.size() > 0) {
            recordStr = new Gson().toJson(mMarkMap);
        }
        SaveFileUtils.saveBookMark(recordStr);
    }

    private void readRecordFromFile() {
        String recordStr = SaveFileUtils.getBookMarkStr();
        if (NStringUtils.isNotBlank(recordStr)) {
            mMarkMap = new Gson().fromJson(recordStr,new TypeToken<Map<String,List<BookMarkBean>>>(){}.getType());
        }else {
            mMarkMap = new HashMap<>();
        }
    }
}

package com.pw.codeset.manager;

public class ChapterManager {
    public static ChapterManager mInstance;

    public static ChapterManager getInstance() {
        if (mInstance == null) {
            synchronized (ChapterManager.class) {
                if (mInstance == null) {
                    mInstance = new ChapterManager();
                }
            }
        }
        return mInstance;
    }

    ChapterManager() {

    }

    private void saveChapterToFile(String bookId, String chapterList) {

    }
}

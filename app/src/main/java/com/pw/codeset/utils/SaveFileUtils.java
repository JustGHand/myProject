package com.pw.codeset.utils;

import com.pw.codeset.application.MyApp;
import com.pw.baseutils.utils.FileUtil;
import com.pw.baseutils.utils.NStringUtils;

import java.io.File;

public class SaveFileUtils {

    public static final String SAVE_FILE_JSON_SUFFIX = ".json";

    public static final String SAVE_FILE_BASE_FOLDER = "pw_code";
    public static final String SAVE_FILE_NOTES_FILE = "notes";
    public static final String SAVE_FILE_NOTES_IMAGE_FILE = "notesImage";
    public static final String SAVE_FILE_NOTES_LABEL_FILE = "notes_label";
    public static final String SAVE_FILE_BOOKSHELF_FILE = "bookshelf";
    public static final String SAVE_FILE_BOOKS_FILE = "books";
    public static final String SAVE_FILE_CHAPTERS_FILE = "chapters";
    public static final String SAVE_FILE_READCONFIG_FILE = "readConfig";
    public static final String SAVE_FILE_READRECORD_FILE = "readRecord";
    public static final String SAVE_FILE_BOOKMARK_FILE = "bookMark";
    public static final String SAVE_FILE_FILE_TRANSFER_FOLDER = "transfer";


    /**
     * 备忘录
     */

    public static String getNotesListStr() {
        File notesFile = getNotesFile();
        if (notesFile != null && notesFile.exists()) {
            return FileUtil.getFileContent(notesFile);
        }
        return null;
    }

    public static void saveNotesList(String notesListStr) {
        if (NStringUtils.isBlank(notesListStr)) {
            notesListStr = "";
        }
        File notesFile = getNotesFile();
        FileUtil.saveFile(notesListStr, notesFile);
    }

    public static File getNotesFile() {
        File notesFile = FileUtil.getFile(getNotesFilePath());
        return notesFile;
    }

    private static String getNotesFilePath() {
        return getBaseFileFolder() + SAVE_FILE_NOTES_FILE + SAVE_FILE_JSON_SUFFIX;
    }

    /**
     * 备忘录图片
     */
    public static String getNoteImageFileFolder() {
        return getBaseFileFolder() + SAVE_FILE_NOTES_IMAGE_FILE;
    }

    /**
     * 备忘录标签
     */

    public static String getNotesLabelListStr() {
        File notesLabelFile = getNotesLabelFile();
        if (notesLabelFile != null && notesLabelFile.exists()) {
            return FileUtil.getFileContent(notesLabelFile);
        }
        return null;
    }

    public static void saveNotesLabelList(String notesLabelListStr) {
        if (NStringUtils.isBlank(notesLabelListStr)) {
            notesLabelListStr = "";
        }
        File notesLabelFile = getNotesLabelFile();
        FileUtil.saveFile(notesLabelListStr, notesLabelFile);
    }

    public static File getNotesLabelFile() {
        File notesLabelFile = FileUtil.getFile(getNotesLabelFilePath());
        return notesLabelFile;
    }

    private static String getNotesLabelFilePath() {
        return getBaseFileFolder() + SAVE_FILE_NOTES_LABEL_FILE + SAVE_FILE_JSON_SUFFIX;
    }

    /**
     * 文件中转
     */
    public static String getFileTransferFolder() {
        return getBaseFileFolder() + SAVE_FILE_FILE_TRANSFER_FOLDER;
    }

    /*
    books
     */

    public static void saveChapterList(String bookId, String chapterListStr) {
        if (NStringUtils.isBlank(chapterListStr)) {
            chapterListStr = "";
        }
        File chapterFile = getChapterFile(bookId);
        FileUtil.saveFile(chapterListStr, chapterFile);
    }

    public static String getChapterListStr(String bookId) {
        if (NStringUtils.isNotBlank(bookId)) {
            File chapterFile = getChapterFile(bookId);
            return FileUtil.getFileContent(chapterFile);
        }
        return null;
    }

    private static File getChapterFile(String bookId) {
        String chapterFilePath = getChapterFilePath(bookId);
        return FileUtil.getFile(chapterFilePath);
    }

    private static String getChapterFilePath(String bookId) {
        String folderPath = getBookFolderPath(bookId);
        return folderPath + SAVE_FILE_CHAPTERS_FILE + SAVE_FILE_JSON_SUFFIX;
    }

    public static String getBookFile(String bookId,String bookName) {
        return getBookFolderPath(bookId) + bookName;
    }

    public static String getBookFolderPath(String bookId) {
        return getBooksFolderPath() + bookId + File.separator;
    }

    public static String getBooksFolderPath() {
        return getBaseFileFolder() + SAVE_FILE_BOOKS_FILE + File.separator;
    }


    public static void saveBooksList(String bookListStr) {
        if (NStringUtils.isBlank(bookListStr)) {
            bookListStr = "";
        }
        File bookShelfFile = getBookShelfFile();
        FileUtil.saveFile(bookListStr, bookShelfFile);
    }

    public static String getBookShelfStr() {
        File bookShelfFile = getBookShelfFile();
        if (bookShelfFile != null && bookShelfFile.exists()) {
            return FileUtil.getFileContent(bookShelfFile);
        }
        return null;
    }

    public static File getBookShelfFile() {
        File bookShelfFile = FileUtil.getFile(getBookShelfFilePath());
        return bookShelfFile;
    }

    private static String getBookShelfFilePath() {
        return getBaseFileFolder() + SAVE_FILE_BOOKSHELF_FILE + SAVE_FILE_JSON_SUFFIX;
    }

    /*
    read record
     */

    public static void saveReadRecord(String recordStr) {
        if (NStringUtils.isBlank(recordStr)) {
            recordStr = "";
        }
        File recordFile = getRecordFile();
        FileUtil.saveFile(recordStr, recordFile);
    }

    public static String getReadRecordStr() {
        File readRecordFile = getRecordFile();
        if (readRecordFile != null && readRecordFile.exists()) {
            return FileUtil.getFileContent(readRecordFile);
        }
        return null;
    }

    public static File getRecordFile() {
        File recordFile = FileUtil.getFile(getRecordFilePath());
        return recordFile;
    }

    private static String getRecordFilePath() {
        return getBaseFileFolder() + SAVE_FILE_READRECORD_FILE + SAVE_FILE_JSON_SUFFIX;
    }

    /*
    book mark
     */

    public static void saveBookMark(String bookMarkStr) {
        if (NStringUtils.isBlank(bookMarkStr)) {
            bookMarkStr = "";
        }
        File bookMarkFile = getBookMarkFile();
        FileUtil.saveFile(bookMarkStr, bookMarkFile);
    }

    public static String getBookMarkStr() {
        File bookMarkFile = getBookMarkFile();
        if (bookMarkFile != null && bookMarkFile.exists()) {
            return FileUtil.getFileContent(bookMarkFile);
        }
        return null;
    }

    public static File getBookMarkFile() {
        File bookMarkFile = FileUtil.getFile(getBookMarkFilePath());
        return bookMarkFile;
    }

    private static String getBookMarkFilePath() {
        return getBaseFileFolder() + SAVE_FILE_BOOKMARK_FILE + SAVE_FILE_JSON_SUFFIX;
    }

    /*
    read config
     */

    public static void saveReadConfigStr(String config) {
        if (NStringUtils.isBlank(config)) {
            config = "";
        }
        File configFile = getReadConfigFile();
        FileUtil.saveFile(config, configFile);
    }

    public static String getReadConfigStr() {
        File configFile = getReadConfigFile();
        if (configFile != null && configFile.exists()) {
            return FileUtil.getFileContent(configFile);
        }
        return null;
    }

    private static File getReadConfigFile() {
        return FileUtil.getFile(getReadConfigFilePath());
    }
    private static String getReadConfigFilePath() {
        return getBaseFileFolder() + SAVE_FILE_READCONFIG_FILE + SAVE_FILE_JSON_SUFFIX;
    }



    public static String getBaseFileFolder() {
        String folder = MyApp.getInstance().getFilesDir().getAbsolutePath();
        String name = SAVE_FILE_BASE_FOLDER;
        return folder + File.separator + name + File.separator;
    }
}

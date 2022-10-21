package com.pw.codeset.utils;

import com.pw.codeset.application.MyApp;
import com.xd.baseutils.utils.NStringUtils;

import java.io.File;

public class SaveFileUtils {

    public static final String SAVE_FILE_JSON_SUFFIX = ".json";

    public static final String SAVE_FILE_BASE_FOLDER = "pw_code";
    public static final String SAVE_FILE_NOTES_FILE = "notes";
    public static final String SAVE_FILE_BOOKSHELF_FILE = "bookshelf";
    public static final String SAVE_FILE_BOOKS_FILE = "books";
    public static final String SAVE_FILE_CHAPTERS_FILE = "chapters";
    public static final String SAVE_FILE_READCONFIG_FILE = "readConfig";


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



    private static String getBaseFileFolder() {
        String folder = MyApp.getInstance().getFilesDir().getAbsolutePath();
        String name = SAVE_FILE_BASE_FOLDER;
        return folder + File.separator + name + File.separator;
    }
}

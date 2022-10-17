package com.pw.codeset.utils;

import com.pw.codeset.application.MyApp;
import com.xd.baseutils.utils.NStringUtils;

import java.io.File;

public class SaveFileUtils {

    public static final String SAVE_FILE_JSON_SUFFIX = ".json";

    public static final String SAVE_FILE_BASE_FOLDER = "pw_code";
    public static final String SAVE_FILE_NOTES_FILE = "notes";

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





    private static String getBaseFileFolder() {
        String folder = MyApp.getInstance().getFilesDir().getAbsolutePath();
        String name = SAVE_FILE_BASE_FOLDER;
        return folder + File.separator + name + File.separator;
    }
}

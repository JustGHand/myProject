package com.pw.read.utils;

import android.content.Context;

import com.pw.baseutils.utils.FileUtil;
import com.pw.baseutils.utils.NStringUtils;

import java.io.File;

public class FileSaveUtils {
    public static final String SAVE_FILE_BASE_FOLDER = "pw_read";
    public static final String SAVE_FILE_JSON_SUFFIX = ".json";
    public static final String SAVE_FILE_READCONFIG_FILE = "readConfig";

    /*
    read config
     */

    public static void saveReadConfigStr(Context context, String config) {
        if (NStringUtils.isBlank(config)) {
            config = "";
        }
        File configFile = getReadConfigFile(context);
        FileUtil.saveFile(config, configFile);
    }

    public static String getReadConfigStr(Context context) {
        File configFile = getReadConfigFile(context);
        if (configFile != null && configFile.exists()) {
            return FileUtil.getFileContent(configFile);
        }
        return null;
    }

    private static File getReadConfigFile(Context context) {
        return FileUtil.getFile(getReadConfigFilePath(context));
    }
    private static String getReadConfigFilePath(Context context) {
        return getBaseFileFolder(context) + SAVE_FILE_READCONFIG_FILE + SAVE_FILE_JSON_SUFFIX;
    }



    private static String getBaseFileFolder(Context context) {
        String folder = context.getFilesDir().getAbsolutePath();
        String name = SAVE_FILE_BASE_FOLDER;
        return folder + File.separator + name + File.separator;
    }

}

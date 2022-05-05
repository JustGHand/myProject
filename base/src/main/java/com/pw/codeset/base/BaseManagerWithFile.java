package com.pw.codeset.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pw.codeset.application.BaseApplication;
import com.xd.base.file.FileUtils;
import com.xd.base.utils.NStringUtils;

import java.io.File;

public abstract class BaseManagerWithFile<T> {

    public abstract String getDataFileName();

    public T mData;

    public void varifyData() {
        if (mData == null) {
            mData = readDataFromFile();
        }
    }

    /**
     * @param data
     */
    public boolean saveDataToFile(@NonNull T data) {
        File dataFile = getDataFile();
        //FileUtils.getFile()方法默认返回存在的文件，如果不存在则会先创建文件
        //如果返回的文件不存在表示创建失败，当做错误处理
        if (!dataFile.exists()) {
            return false;
        }
        String dataContent;
        try {
            dataContent = new Gson().toJson(dataFile);
        } catch (Exception e) {
            return false;
        }
        FileUtils.saveFile(dataContent, dataFile);
        return true;
    }

    /**
     * 获取用户信息存储路径
     * @return
     */
    public String getDataFilePath() {
        return FileUtils.getDownloadFilePath(BaseApplication.getContext()) + File.separator + getDataFileName();
    }

    /**
     * 获取用户信息文件
     * @return
     */
    public File getDataFile() {
        return FileUtils.getFile(getDataFilePath());
    }

    /**
     * 从文件读取用户信息
     * @return
     */
    public @Nullable
    T readDataFromFile() {
        File dataFile = getDataFile();
        if (!dataFile.exists()) {
            return null;
        }
        String dataContent = FileUtils.getFileContent(dataFile);
        if (NStringUtils.isNotBlank(dataContent)) {
            try {
                T userInfoBean = new Gson().fromJson(dataContent, new TypeToken<T>(){}.getType());
                return userInfoBean;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}

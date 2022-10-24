package com.pw.codeset.activity.read;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pw.codeset.databean.BookBean;
import com.pw.read.bean.ChaptersBean;
import com.pw.codeset.utils.FileUtil;
import com.pw.codeset.utils.MD5Utils;
import com.pw.codeset.utils.SaveFileUtils;
import com.xd.baseutils.utils.NStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookManager {

    public static BookManager mInstance;

    public static BookManager getInstance() {
        if (mInstance == null) {
            synchronized (BookManager.class) {
                if (mInstance == null) {
                    mInstance = new BookManager();
                }
            }
        }
        return mInstance;
    }

    BookManager() {
        readBookListFromFile();
    }

    private List<BookBean> mBooks;

    public List<BookBean> getBookList() {
        if (mBooks == null) {
            readBookListFromFile();
        }
        return mBooks;
    }


    public BookBean addBook(String filePath) {
        BookBean newBookBean = null;
        if (NStringUtils.isFileReadable(filePath)) {
            File file = FileUtil.getFile(filePath);
            if (file != null && file.exists()) {

                int randomNum = (int) (Math.random() * 1000);
                String bookIdBef = System.currentTimeMillis() + file.getName() + randomNum;

                String bookId = MD5Utils.strToMd5By16(bookIdBef);

                String newFilePath = SaveFileUtils.getBookFolderPath(bookId) + file.getName();

                FileUtil.copyFile(filePath, newFilePath);

                File newBookFile = FileUtil.getFile(newFilePath);

                if (newBookFile != null && newBookFile.exists()) {
                    String bookName = newBookFile.getName();
                    newBookBean = new BookBean(bookId,newFilePath,BookBean.BOOKFILETYPE_TXT,bookName);
                    mBooks.add(newBookBean);
                    saveBookListToFile();
                }
            }
        }
        return newBookBean;
    }

    public static void saveChapterListToFile(String bookId, List<ChaptersBean> chaptersBeanList) {
        if (NStringUtils.isNotBlank(bookId)) {
            if (chaptersBeanList != null && chaptersBeanList.size() > 0) {
                String chapterListStr = new Gson().toJson(chaptersBeanList);
                SaveFileUtils.saveChapterList(bookId,chapterListStr);
            }
        }
    }

    public void deleteBook(String bookId) {
        BookBean bookBean = getBook(bookId);
        if (bookBean != null) {
            mBooks.remove(bookBean);
            saveBookListToFile();
            String bookFolder = SaveFileUtils.getBookFolderPath(bookId);
            FileUtil.deleteFile(bookFolder);
        }
    }

    public BookBean getBook(String bookId) {
        if (mBooks != null && NStringUtils.isNotBlank(bookId)) {
            for (int i = 0; i < mBooks.size(); i++) {
                BookBean bookBean = mBooks.get(i);
                if (bookBean != null) {
                    if (bookBean.getBookId().equals(bookId)) {
                        return bookBean;
                    }
                }
            }
        }
        return null;
    }

    public static List<ChaptersBean> getBookChapters(String bookId) {
        if (NStringUtils.isNotBlank(bookId)) {
            String chapterListStr = SaveFileUtils.getChapterListStr(bookId);
            if (NStringUtils.isNotBlank(chapterListStr)) {
                List<ChaptersBean> chaptersBeanList = new Gson().fromJson(chapterListStr, new TypeToken<List<ChaptersBean>>() {
                }.getType());
                return chaptersBeanList;
            }
        }
        return null;
    }

    private void readBookListFromFile() {
        String fileContent = SaveFileUtils.getBookShelfStr();
        if (NStringUtils.isNotBlank(fileContent)) {
            mBooks = new Gson().fromJson(fileContent,new TypeToken<List<BookBean>>() {}.getType());
        }else {
            mBooks = new ArrayList<>();
        }
    }

    private void saveBookListToFile() {

        String booksStr = "";
        if (mBooks != null && mBooks.size() > 0) {
            booksStr = new Gson().toJson(mBooks);
        }
        SaveFileUtils.saveBooksList(booksStr);
    }

}

package com.pw.codeset.utils.fileParase;

import android.app.Activity;

import com.pw.codeset.activity.read.BookManager;
import com.pw.codeset.databean.BookBean;
import com.pw.codeset.databean.ChaptersBean;
import com.pw.codeset.utils.FileUtil;
import com.pw.codeset.utils.IOUtils;
import com.pw.codeset.utils.MD5Utils;
import com.xd.baseutils.utils.NStringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class TxtParser {

    private static String chapterTitleParse = "^(.{0,20}第[\\s]*([0-9○一二三四五六七八九十零壹贰叁肆伍陆柒捌玖拾百佰千仟０１２３４５６７８９Ｏ]+)[\\s]*[章节卷回集](.+))$";
    private static String chapterTitleParse2 = "^[(第)(Chapter)(chapter)(CHAPTER)][\\s]*([0-9一二三四五六七八九十零壹贰叁肆伍陆柒捌玖拾百佰千仟０１２３４５６７８９Ｏ]+)[\\s]*[章节卷回篇](.+)$";

    public interface FileLisenter{
        void onStart();
        void onProgress(float progress);

        void onFinish();
    }

    public static List<ChaptersBean> parserFile(Activity activity, BookBean bookBean, FileLisenter lisenter) {
        List<ChaptersBean> chapterList = new ArrayList<>();
        int totalWordcount = 0;
        if (bookBean!=null && NStringUtils.isFileReadable(bookBean.getBookPath())) {
            File file = FileUtil.getFile(bookBean.getBookPath());
            if (file != null && !file.exists()) {
                return chapterList;
            }
            if (lisenter != null) {
                lisenter.onStart();
            }
            long fileLength = file.length();
            Reader reader = null;
            StringBuilder sb = new StringBuilder();
            int progress = 0;
            try {
                Charset charset = FileUtil.getCharset(file.getAbsolutePath());
                InputStream inputStream = new FileInputStream(file);
                if (inputStream != null) {
                    InputStreamReader reader2 = new InputStreamReader(inputStream,charset.getName());
                    BufferedReader bufferedReader = new BufferedReader(reader2);
                    StringBuilder paraStrBuilder = new StringBuilder();
                    ChaptersBean chaptersBean = new ChaptersBean();
                    chaptersBean.setStart(0);
                    chaptersBean.setIndex(0);
                    int value = 0;
                    int charStart = 0;
                    long byteCount = 0;
                    while ((value = bufferedReader.read()) != -1) {
                        char c = (char) value;
                        if ((c == '\n') || (c == '\r')) {
                            String paraStr = paraStrBuilder.toString();
                            byteCount += paraStr.getBytes(charset.getName()).length;
                            if (paraStr!="") {
                                if (lisenter != null) {
                                    int tmpProgress = (int) ((byteCount * 100 / fileLength));
                                    if (tmpProgress != progress) {
                                        progress = tmpProgress;
                                        lisenter.onProgress(progress);
                                    }
                                }
                                if (NStringUtils.isBlank(chaptersBean.getChapterName())) {
                                    chaptersBean.setChapterName(paraStr);
                                    chaptersBean.setTitle(paraStr);
                                    chaptersBean.setFilename(bookBean.getBookPath());
                                    chaptersBean.setChapterId(MD5Utils.strToMd5By16(paraStr));
                                }
                                if (paraStr.matches(chapterTitleParse) || paraStr.matches(chapterTitleParse2)) {
                                    String chapterContent = sb.toString();
                                    chaptersBean.setEnd(charStart - paraStrBuilder.length());
                                    chaptersBean.setTextCount(chapterContent.length());
                                    chapterList.add(chaptersBean);

                                    sb = new StringBuilder();
                                    chaptersBean = new ChaptersBean();
                                    chaptersBean.setChapterName(paraStr);
                                    chaptersBean.setTitle(paraStr);
                                    chaptersBean.setFilename(bookBean.getBookPath());
                                    chaptersBean.setChapterId(MD5Utils.strToMd5By16(paraStr));
                                    chaptersBean.setIndex(chapterList.size());
                                    chaptersBean.setStart(charStart);
                                } else {
                                    sb.append(paraStrBuilder + "\n");
                                }
                                paraStrBuilder = new StringBuilder();
                            }else {
                                sb.append(paraStrBuilder + "\n");
                                paraStrBuilder = new StringBuilder();
                            }
                        }else {
                            paraStrBuilder.append(c);
                        }
                        charStart++;
                    }
                    if (chaptersBean != null && chaptersBean.getStart() < charStart) {
                        String tmpStr = paraStrBuilder.toString();
                        byteCount += tmpStr.getBytes(charset.getName()).length;
                        if (lisenter != null) {
                            lisenter.onProgress((byteCount/fileLength)*100);
                        }
                        sb.append(paraStrBuilder + "\n");
                        String chapterContent = sb.toString();
                        chaptersBean.setEnd(charStart);
                        chaptersBean.setTextCount(chapterContent.length());
                        chapterList.add(chaptersBean);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                IOUtils.close(reader);
            }
            if (chapterList!=null&&chapterList.size()>0) {
                BookManager.saveChapterListToFile(bookBean.getBookId(),chapterList);
            }
            if (lisenter != null) {
                lisenter.onFinish();
            }
        }
        return chapterList;
    }

    public static BufferedReader getChapterBuffer(ChaptersBean chaptersBean) {
        if (chaptersBean == null) {
            return null;
        }
        String fileName = chaptersBean.getFilename();
        int startChar = (int) chaptersBean.getStart();
        int endChar = (int) chaptersBean.getEnd();
        if (NStringUtils.isBlank(fileName) || startChar < 0 || endChar < 0 || endChar < startChar) {
            return null;
        }
        File file = FileUtil.getFile(fileName);
        if (file == null || !file.exists()) {
            return null;
        }
        Reader reader = null;
        try {
            Charset charset = FileUtil.getCharset(file.getAbsolutePath());
            InputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader reader2 = new InputStreamReader(inputStream, charset.getName());
                BufferedReader bufferedReader = new BufferedReader(reader2);
                bufferedReader.skip(startChar);
                return bufferedReader;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(reader);
        }
        return null;
    }

    public static String readChapterContent(ChaptersBean chaptersBean) {
        if (chaptersBean == null) {
            return null;
        }
        String fileName = chaptersBean.getFilename();
        int textCount = chaptersBean.getTextCount();
        int startChar = (int) chaptersBean.getStart();
        int endChar = (int) chaptersBean.getEnd();
        if (NStringUtils.isBlank(fileName) || startChar < 0 || endChar < 0 || endChar < startChar) {
            return null;
        }
        File file = FileUtil.getFile(fileName);
        if (file == null || !file.exists()) {
            return null;
        }
        Reader reader = null;
        String str = null;
        StringBuilder sb = new StringBuilder();
        try {
            Charset charset = FileUtil.getCharset(file.getAbsolutePath());
            InputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader reader2 = new InputStreamReader(inputStream, charset.getName());
                BufferedReader bufferedReader = new BufferedReader(reader2);
                bufferedReader.skip(startChar);
                while (sb.length()<textCount&&(str = bufferedReader.readLine()) != null){
                    if (!str.equals("")) {
                        //由于sb会自动过滤\n,所以需要加上去
                        sb.append("    " + str + "\n");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(reader);
        }
        return sb.toString();
    }

}

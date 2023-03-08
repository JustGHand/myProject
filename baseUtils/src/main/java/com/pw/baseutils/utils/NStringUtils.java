package com.pw.baseutils.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//import com.zqc.opencc.android.lib.ChineseConverter;
//import com.zqc.opencc.android.lib.ConversionType;

/**
 * Created by newbiechen on 17-4-22.
 * 对文字操作的工具类
 */

public class NStringUtils {
    private static final String TAG = "NStringUtils";
    private static final int HOUR_OF_DAY = 24;
    private static final int DAY_OF_YESTERDAY = 2;
    private static final int TIME_UNIT = 60;


    public static String getSiteDomainByUrl(String url) {
        if (isBlank(url)) {
            return "";
        }
        String[] splits = url.split("/");
        if (splits.length >= 2) {
            return getDomainBySite(splits[2]);
        }
        return "";
    }

    public static String getDomainBySite(String site) {
        if (isBlank(site)) {
            return "";
        }
        return site.substring(site.indexOf(".")+1, site.length());
    }


    public static String getDate(int dateShift) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.DATE,dateShift);
        int year = calendar.get(Calendar.YEAR);
//月
        int month = calendar.get(Calendar.MONTH)+1;
//日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dateString = String.valueOf(year) + String.valueOf(month<10?"0"+month:month) + String.valueOf(day);
        return dateString;
    }

    //将时间转换成日期，time为毫秒
    public static String dateConvert(Long time,String pattern){
//        Date date = new Date(time*1000);
//        SimpleDateFormat format = new SimpleDateFormat(pattern);
//        return format.format(date);

        if (time==null||time<0||isBlank(pattern)){
            return "error";
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String dateString = format.format(date);
        return dateString;
    }

    public static String dateConvertFromTimeStamp(long timeStamp) {
        String date = dateConvert(timeStamp, "yyyy-MM-dd HH:mm");
        String finalDate = dateConvert(date, "yyyy-MM-dd HH:mm");
        return finalDate;
    }

    //将日期转换成昨天、今天、明天
    public static String dateConvert(String source,String pattern){
        if (isBlank(source)) {
            return "很久以前";
        }
        DateFormat format = new SimpleDateFormat(pattern);
        try {
            Date date = format.parse(source);
            if (date == null || date.getTime() <= 0) {
                return "很久以前";
            }
            long curTime = System.currentTimeMillis();
            //将MISC 转换成 sec
            long difSec = Math.abs((curTime - date.getTime())/1000);

            int defNum = 0;
            String defUnit = null;
            if (difSec < 60) {
                return "刚刚";
            } else if (difSec < 60 * 60) {
                defNum = (int) (difSec / 60);
                defUnit = "分钟";
            } else if (difSec < 60 * 60 * 24) {
                defNum = (int) (difSec / (60 * 60));
                defUnit = "小时";
            } else if (difSec < 60 * 60 * 24 * 30) {
                defNum = (int) (difSec / (60 * 60 * 24));
                defUnit = "天";
            } else if (difSec < 60 * 60 * 24 * 30*12) {
                defNum = (int) (difSec / (60 * 60 * 24 * 30));
                defUnit = "月";
            } else {
                defNum = (int) (difSec / (60 * 60 * 24 * 30*12));
                defUnit = "年";
            }
            return defNum + defUnit+"前";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return source;
    }



    public static String toFirstCapital(String str){
        return str.substring(0,1).toUpperCase()+str.substring(1);
    }

    /**
     * 将文本中的半角字符，转换成全角字符
     * @param input
     * @return
     */
    public static String halfToFull(String input)
    {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++)
        {
            if (c[i] == 32) //半角空格
            {
                c[i] = (char) 12288;
                continue;
            }
            //根据实际情况，过滤不需要转换的符号
            //if (c[i] == 46) //半角点号，不转换
            // continue;

            if (c[i]> 32 && c[i]< 127)    //其他符号都转换为全角
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    //功能：字符串全角转换为半角
    public static String fullToHalf(String input)
    {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++)
        {
            if (c[i] == 12288) //全角空格
            {
                c[i] = (char) 32;
                continue;
            }

            if (c[i]> 65280&& c[i]< 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    //繁簡轉換
    public static String convertCC(String input, Context context)
    {
        return input;
//        ConversionType currentConversionType = ConversionType.S2TWP;
//        int convertType = SharedPreUtils.getInstance().getInt(SHARED_READ_CONVERT_TYPE, 0);
//
//        if (input.length() == 0)
//            return "";
//
//        switch (convertType) {
//            case 1:
//                currentConversionType = ConversionType.TW2SP;
//                break;
//            case 2:
//                currentConversionType = ConversionType.S2HK;
//                break;
//            case 3:
//                currentConversionType = ConversionType.S2T;
//                break;
//            case 4:
//                currentConversionType = ConversionType.S2TW;
//                break;
//            case 5:
//                currentConversionType = ConversionType.S2TWP;
//                break;
//            case 6:
//                currentConversionType = ConversionType.T2HK;
//                break;
//            case 7:
//                currentConversionType = ConversionType.T2S;
//                break;
//            case 8:
//                currentConversionType = ConversionType.T2TW;
//                break;
//            case 9:
//                currentConversionType = ConversionType.TW2S;
//                break;
//            case 10:
//                currentConversionType = ConversionType.HK2S;
//                break;
//        }
//
//        return (convertType != 0)?ChineseConverter.convert(input, currentConversionType, context):input;
    }


    public static String txfloat(int a,int b) {

        DecimalFormat df=new DecimalFormat("0.0");//设置保留位数

        return df.format((float)a/b);

    }

    public static boolean isBlank(CharSequence cs) {

        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;

    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static String LongTimeStampToDateString(Long time, String parttner) {
        if (time == null || time == 0) {
            return null;
        }
        if (NStringUtils.isBlank(parttner)) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(parttner);
        return simpleDateFormat.format(new Date(time*1000));
    }

    /**
     * 手机号正则判断
     * @param mobileNums
     * @return
     */
    /**
     * 判断字符串是否符合手机号码格式
     * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
     * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
     * 电信号段: 133,149,153,170,173,177,180,181,189
     * @return 待检测的字符串
     */
    public static boolean isMobileNO(String mobileNums) {

        if (isBlank(mobileNums)||mobileNums.length() != 11) {
            return false;
        }else {
            return true;
        }

        /**
         * 判断字符串是否符合手机号码格式
         * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
         * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
         * 电信号段: 133,149,153,170,173,177,180,181,189
         * @param str
         * @return 待检测的字符串
         */
//        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
//        if (TextUtils.isEmpty(mobileNums))
//            return false;
//        else
//            return mobileNums.matches(telRegex);
    }

    /**
     * 该函数判断一个字符串是否包含标点符号（中文英文标点符号）。
     * 原理是原字符串做一次清洗，清洗掉所有标点符号。
     * 此时，如果原字符串包含标点符号，那么清洗后的长度和原字符串长度不同。返回true。
     * 如果原字符串未包含标点符号，则清洗后长度不变。返回false。
     * @param s
     * @return
     */
    public static boolean isOnlyPunctuation(String s) {
        boolean b = false;

        String tmp = s;
        tmp = tmp.replaceAll("\\p{P}", "");
        if (tmp.length()==0) {
            b = true;
        }

        return b;
    }

    //复制字符串到剪贴板
    public static void copyString(Context context, CharSequence content) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
//        ToastUtils.show("已复制");
    }


    public static String getFileNameFromPath(String path){
        if (isBlank(path)){
            return null;
        }
        return path.substring(path.lastIndexOf("/")+1,path.length());
    }

    public static String getFileSuffix(String path) {
        if (isBlank(path)) {
            return null;
        }
        return path.substring(path.lastIndexOf(".") + 1, path.length());
    }

    public static boolean isFileReadable(String filePath) {
        if (isBlank(filePath)) {
            return false;
        }
        String suffix = getFileSuffix(filePath);
        if (NStringUtils.isNotBlank(suffix) && suffix.equalsIgnoreCase("txt")) {
            return true;
        }
        return false;
    }


    public static String intToIp(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append(ipInt >> 8 & 0xFF).append(".");
        sb.append(ipInt >> 16 & 0xFF).append(".");
        sb.append(ipInt >> 24 & 0xFF);
        return sb.toString();
    }

}

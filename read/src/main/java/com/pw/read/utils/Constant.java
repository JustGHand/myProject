package com.pw.read.utils;

public class Constant {

    //智能断行需要的字符集标记
    public static int KAsciiCharCount = 256;
    public static final int KChsSpecialSymbol = 23;
    public static final char KSpecialChsBegin = 0x2010;
    public static final char KSpecialChsEnd = 0x2026;

    public static final char KFullWidthMarkBegin = 0xff00;
    public static final char KFullWidthMarkEnd = 0xffef;
    public static final char KCjkMarkBegin = 0x3001;
    public static final char KCjkMarkEnd = 0x303F;
    public static final char KAsciiMarkBegin = 0x21;
    public static final char KAsciiMarkEnd = 0x2F;
    // 默认的显示参数配置
    public static final int DEFAULT_MARGIN_HEIGHT = 28;
    public static final int DEFAULT_MARGIN_WIDTH = 0;
    public static final int DEFAULT_TIP_SIZE = 12;
    public static final int EXTRA_TITLE_SIZE = 4;
    public static final String FORMAT_TIME = "HH:mm";



    public static final int DEFAULT_FONT_SIZE = 16;
    public static final int DEFAULT_TEXT_INTERVAL = 10;
    public static final int DEFAULT_PAGE_PADDING = 15;
}

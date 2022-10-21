package com.xd.readcore.utils;

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
    public static final int DEFAULT_MARGIN_WIDTH = 15;
    public static final int DEFAULT_TIP_SIZE = 12;
    public static final int EXTRA_TITLE_SIZE = 4;

    //for native ad
    public static final int YYNativeAd_TopBottom_Height = 22;
    //for ReadCore
    public static final int YYReadCore_JinCou_LeftRightMargin = 0;
    public static final int YYReadCore_Shushi_LeftRightMargin = 10;
    public static final int YYReadCore_Songsan_LeftRightMargin = 15;
    public static final int YYReadCore_Default_LeftRightMargin = 10;

    // 当前页面的状态
    public static final int STATUS_LOADING = 1;         // 正在加载
    public static final int STATUS_FINISH = 2;          // 加载完成
    public static final int STATUS_ERROR = 3;           // 加载错误 (一般是网络加载情况)
    public static final int STATUS_EMPTY = 4;           // 空数据
    public static final int STATUS_PARING = 5;          // 正在解析 (装载本地数据)
    public static final int STATUS_PARSE_ERROR = 6;     // 本地文件解析错误(暂未被使用)
    public static final int STATUS_CATEGORY_EMPTY = 7;  // 获取到的目录为空

    public enum InsertViewType{
        VIEWTYPE_INSERT, VIEWTYPE_TAIL_PAGEAD, VIEWTYPE_FULLPAGE, NONE
    }

    public static final String FORMAT_TIME = "HH:mm";


    public static final int DEFAULTCONFIG_AD_PAGEADENTER_Y = 20;//大页广告距离去广告按钮距离，单位dp
}

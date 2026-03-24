package com.pw.codeset.utils;

public class Constant {
    public static final String TAG = "PW_CODE";

    public static int PCConnectPort = 54321;
    public static String PCConnectStart = "/";
    public static String PCConnectUpload = "/files";


    public static final String NOTE_ID = "note_id";
    public static final String BOOK_ID = "bookId";
    public static final String SCHEDULE_ID = "scheduleId";
    public static final String SCHEDULE_REPEAT_TOKEN = "repeat";

    public static final String DATA_PARTNER_WITH_CHAR = "yyyy年MM月dd日 HH:mm";
    public static final String DATA_PARTNER_WITH_LINE = "yyyy-MM-dd HH:mm";
    public static final String DATA_PARTNER_WITH_CHAR_WITHOUT_TIME = "yyyy-MM-dd";
    public static final String DATA_PARTNER_WITH_LINE_WITHOUT_TIME = "yyyy-MM-dd";
    public static final String DATA_PARTNER_WITH_LINE_TILE_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String DATA_PARTNER_WITH_LINE_TILE_TIME_ONLY = "HH:mm:ss";
    public static final String DATA_PARTNER_WITH_LINE_TILE_WITHOUT_YEAR = "MM-dd HH:mm:ss";
    public static final String DATA_PARTNER_WITH_LINE_TILE_WITH_DAY_TIME = "dd HH:mm:ss";

    public static final String INTENT_KEY_IMAGE_PREVIEW_URL = "image_preview_url";

    public static final Integer SCHEDULE_DATE_TYPE_ALL = -1;
    public static final Integer SCHEDULE_DATE_TYPE_TODAY = 0;
    public static final Integer SCHEDULE_DATE_TYPE_WEEK = 1;
    public static final Integer SCHEDULE_DATE_TYPE_MONTH = 2;
    public static final Integer SCHEDULE_DATE_TYPE_YEAR = 3;
    public static final Integer SCHEDULE_DATE_TYPE_EARLIER = 4;


    //状态 0：待完成 1：已完成 -1:已删除
    public static final Integer SCHEDULE_STATE_ALL = -1;
    public static final Integer SCHEDULE_STATE_UNDONE = 0;
    public static final Integer SCHEDULE_STATE_FINISHED = 1;
    public static final Integer SCHEDULE_STATE_DELETED= 2;

    public static final Integer SCHEDULE_ACTION_UNDONE = 0;
    public static final Integer SCHEDULE_ACTION_FINISH = 1;
    public static final Integer SCHEDULE_ACTION_DELETE= 2;
    public static final Integer SCHEDULE_ACTION_PUT_OFF= 3;
    public static final Integer SCHEDULE_ACTION_EDIT= 4;

    public static final Integer SCHEDULE_FILTER_TYPE_TIME = 1;
    public static final Integer SCHEDULE_FILTER_TYPE_STATE = 2;

    public static final String TODAY_SCHEDULE_SP_KEY = "TODAY_SCHEDULE_SP_KEY";

}

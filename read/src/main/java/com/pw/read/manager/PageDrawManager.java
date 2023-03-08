package com.pw.read.manager;

import static com.pw.read.utils.Constant.DEFAULT_MARGIN_HEIGHT;
import static com.pw.read.utils.Constant.DEFAULT_MARGIN_WIDTH;
import static com.pw.read.utils.Constant.DEFAULT_TIP_SIZE;
import static com.pw.read.utils.Constant.EXTRA_TITLE_SIZE;
import static com.pw.read.utils.Constant.KAsciiCharCount;
import static com.pw.read.utils.Constant.KAsciiMarkBegin;
import static com.pw.read.utils.Constant.KAsciiMarkEnd;
import static com.pw.read.utils.Constant.KChsSpecialSymbol;
import static com.pw.read.utils.Constant.KCjkMarkBegin;
import static com.pw.read.utils.Constant.KCjkMarkEnd;
import static com.pw.read.utils.Constant.KFullWidthMarkBegin;
import static com.pw.read.utils.Constant.KFullWidthMarkEnd;
import static com.pw.read.utils.Constant.KSpecialChsBegin;
import static com.pw.read.utils.Constant.KSpecialChsEnd;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.DisplayMetrics;

import androidx.core.content.ContextCompat;

import com.pw.read.bean.ChaptersBean;
import com.pw.read.bean.LayoutMode;
import com.pw.read.bean.LineInfo;
import com.pw.read.bean.PageStyle;
import com.pw.read.bean.TxtPage;
import com.pw.read.utils.Constant;
import com.pw.read.utils.Utils;
import com.pw.baseutils.utils.IOUtils;
import com.pw.baseutils.utils.NStringUtils;
import com.pw.baseutils.utils.ScreenUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PageDrawManager {

    public static PageDrawManager mInstance;

    public static PageDrawManager getInstance() {
        if (mInstance == null) {
            synchronized (PageDrawManager.class) {
                if (mInstance == null) {
                    mInstance = new PageDrawManager();
                }
            }
        }
        return mInstance;
    }

    public PageDrawManager( ) {

    }


    // 绘制电池的画笔
    private Paint mBatteryPaint;
    private int mBatteryColor;
    // 绘制提示的画笔
    private Paint mTipPaint;
    // 绘制标题的画笔
    private Paint mTitlePaint;
    // 绘制背景颜色的画笔(用来擦除需要重绘的部分)
    private Paint mBgPaint;
    // 绘制小说内容的画笔
    private TextPaint mTextPaint;
    //绘制听书时播放内容的背景的画笔
    private Paint mSpeechBgPaint;
    //绘制听书时播放内容的画笔
    private TextPaint mSpeechTextPaint;

    private TextPaint mAdTextPaint;

    //书籍绘制区域的宽高
    private int mVisibleWidth;
    private int mVisibleHeight;
    //排版模式相关的绘制区域周边留白
    private int mDrawTopBottomMargin;
    private int mDrawLeftRightMargin;
    //应用的宽高
    private int mDisplayWidth;
    private int mDisplayHeight;
    //间距
    private int mMarginWidth;
    private int mMarginHeight;
    //字体的颜色
    private int mTextColor;
    //标题的大小
    private int mTitleSize;
    //字体的大小
    private int mTextSize;
    //字间距
    private int mTextMargin;
    //行间距
    private int mTextInterval;
    //标题的行间距
    private int mTitleInterval;
    //段落距离(基于行间距的额外距离)
    private int mTextPara;
    private int mTitlePara;
    //电池的百分比
    private int mBatteryLevel;
    //当前页面的背景
    private int mBgColor;
    //当前听书文字背景颜色
    private int mSpeechBgColor;
    private int mSpeechTextColor;

    private int mBgResource;
    private int mScreenWidth;
    private int mScreenHeight;

    private boolean haveDisplayCutout = false;
    private int displaycutoutRight = 0;

    private String mCurFontPath;

    private Context mContext;

    private Context getContext() {
        if (mContext != null) {
            return mContext;
        }
        return null;
    }

    /**
     * 获取状态
     * @return
     */
    private int getStatus() {
        return 1;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context) {
        mContext = context;

        mMarginWidth = ScreenUtils.dpToPx(getContext(),DEFAULT_MARGIN_WIDTH);
        mMarginHeight = ScreenUtils.dpToPx(getContext(),DEFAULT_MARGIN_HEIGHT);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        mScreenHeight = Utils.getRealHeight(getContext());
    }

    /**
     * 设置参数
     * @param textSize
     * @param layoutMode
     * @param fontPath
     * @param haveCutout
     * @param cutoutRight
     */
    public void setParams(int textSize, LayoutMode layoutMode, String fontPath, boolean haveCutout, int cutoutRight) {
        mCurFontPath = fontPath;
        initPaint();
        setUpTextParams(textSize);
        setLayoutModeParm(layoutMode);
        haveDisplayCutout = haveCutout;
        displaycutoutRight = cutoutRight;
    }

    /**
     * 更新页面尺寸
     * @param w
     * @param h
     */
    public void updateWH(int w, int h) {
        //回调排版文字宽度
        // 获取PageView的宽高
        mDisplayWidth = w;
        mDisplayHeight = h;

        // 获取内容显示位置的大小
        mVisibleWidth = mDisplayWidth - mMarginWidth * 2;
        mVisibleHeight = mDisplayHeight - mMarginHeight * 2;
    }

    /**
     * 设置样式
     * @param pageStyle
     */
    public void setPageStyle(PageStyle pageStyle) {

        // 设置当前颜色样式
        mTextColor = ContextCompat.getColor(getContext(), pageStyle.getFontColor());
        mSpeechTextColor = ContextCompat.getColor(getContext(), pageStyle.getHighlightTextColor());
        mBatteryColor=mTextColor;
        mBgColor = ContextCompat.getColor(getContext(), pageStyle.getBgColor());
        mBgResource = pageStyle.getBgDrawable();
        mSpeechBgColor =  ContextCompat.getColor(getContext(), pageStyle.getHighlightColor());
        mBatteryColor = mTextColor;
        mTipPaint.setColor(mTextColor);
        mTitlePaint.setColor(mTextColor);
        mBatteryPaint.setColor(mBatteryColor);
        mTextPaint.setColor(mTextColor);
        mSpeechTextPaint.setColor(mSpeechTextColor);
        mSpeechBgPaint.setColor(mSpeechBgColor);
        mBgPaint.setColor(mBgColor);
    }

    /**
     * 设置布局模式
     * @param layoutMode
     */
    public void setLayoutModeParm(LayoutMode layoutMode){
        //行间距
        mTextInterval = ScreenUtils.dpToPx(getContext(),layoutMode.getmTextInterval());
        //段间距
        mTextPara = ScreenUtils.dpToPx(getContext(),layoutMode.getmTextPara());
        //标题行间距
        mTitleInterval = ScreenUtils.dpToPx(getContext(),layoutMode.getmTitleInterval());
        //字间距
        mTextMargin = ScreenUtils.dpToPx(getContext(),layoutMode.getmTextMargin());
        //标题到第一段距离
        mTitlePara = ScreenUtils.dpToPx(getContext(),layoutMode.getmTitlePara());
        //top & bottom margin
        mDrawTopBottomMargin = ScreenUtils.dpToPx(getContext(),layoutMode.getmDrawTopBottomMargin());
        //left & right margin
        mDrawLeftRightMargin = ScreenUtils.dpToPx(getContext(),layoutMode.getmDrawLeftRightMargin());
    }

    /**
     * 设置边距
     * @param marginWidth
     * @param marginHeight
     */
    public void setMargin(int marginWidth,int marginHeight) {
        mMarginWidth = marginWidth;
        mMarginHeight = marginHeight;
    }

    /**
     * 设置字体
     * @param fontPath
     */
    public void setReadFont(String fontPath) {
        String curFontPath = fontPath;
        if (NStringUtils.isNotBlank(curFontPath) && curFontPath.equals(mCurFontPath)) {
            //目标字体文件路径相同则不做处理
            return;
        }
        // 设置文字相关参数
        mCurFontPath = curFontPath;
        Typeface font;
        if (NStringUtils.isBlank(mCurFontPath) || !new File(mCurFontPath).exists()) {
            font = null;
        }else {
            try {
                font = Typeface.createFromFile(mCurFontPath);
            } catch (Exception e) {
                font = null;
            }
        }

        mTipPaint.setTypeface(font);

        mTextPaint.setTypeface(font);
        mSpeechTextPaint.setTypeface(font);

        mTitlePaint.setTypeface(font);

        initFontSizeTable();

    }

    /**
     * 更新电池
     * @param batteryLevel
     */
    public void setmBatteryLevel(int batteryLevel) {
        mBatteryLevel = batteryLevel;
    }

    /**
     * 设置字体大小
     * @param textSize
     */
    public void setUpTextParams(int textSize) {
        // 文字大小
        mTextSize = textSize;
        mTitleSize = mTextSize + ScreenUtils.spToPx(getContext(),EXTRA_TITLE_SIZE);

        // 设置画笔的字体大小
        mTextPaint.setTextSize(mTextSize);
        mSpeechTextPaint.setTextSize(mTextSize);
        // 设置标题的字体大小
        mTitlePaint.setTextSize(mTitleSize);
        initFontSizeTable();
    }

    /**
     * 初始化画笔对象
     */
    private void initPaint() {
        Typeface font;
        if (NStringUtils.isBlank(mCurFontPath)||!new File(mCurFontPath).exists()) {
            font = null;
        }else {
            try {
                font = Typeface.createFromFile(mCurFontPath);
            } catch (Exception e) {
                font = null;
            }
        }

        // 绘制提示的画笔
        mTipPaint = new Paint();
        mTipPaint.setColor(mTextColor);
        mTipPaint.setTextAlign(Paint.Align.LEFT); // 绘制的起始点
        mTipPaint.setTextSize(ScreenUtils.spToPx(getContext(),DEFAULT_TIP_SIZE)); // Tip默认的字体大小
        mTipPaint.setAntiAlias(true);
        mTipPaint.setSubpixelText(true);
        mTipPaint.setTypeface(font);

        // 绘制页面内容的画笔
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTypeface(font);
        // 绘制页面内容的画笔
        mSpeechTextPaint = new TextPaint();
        mSpeechTextPaint.setColor(mSpeechTextColor);
        mSpeechTextPaint.setTextSize(mTextSize);
        mSpeechTextPaint.setAntiAlias(true);
        mSpeechTextPaint.setTypeface(font);

        mSpeechBgPaint = new Paint();
        //设置画笔颜色
        mSpeechBgPaint.setColor(mSpeechBgColor);
        //设置它的填充方法，用的多的是FILL 和 STORKE
        mSpeechBgPaint.setStyle(Paint.Style.FILL);

        // 绘制标题的画笔
        mTitlePaint = new TextPaint();
        mTitlePaint.setColor(mTextColor);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTitlePaint.setAntiAlias(true);
        mTitlePaint.setTypeface(font);

        //绘制广告内容的画笔
        mAdTextPaint = new TextPaint();
        mAdTextPaint.setColor(mTextColor);
        mAdTextPaint.setTextSize(ScreenUtils.spToPx(getContext(),20));
        mAdTextPaint.setAntiAlias(true);

        // 绘制背景的画笔
        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);

        // 绘制电池的画笔
        mBatteryPaint = new Paint();
        mBatteryPaint.setColor(mBatteryColor);
        mBatteryPaint.setAntiAlias(true);
        mBatteryPaint.setDither(true);

    }

    /**
     * 绘制页面
     * @param isUpdate
     */
    public void drawPage(Canvas canvas,TxtPage mCurPage, boolean isUpdate) {
        drawBackground(canvas,mCurPage, isUpdate);
        if (!isUpdate) {
            drawContent(canvas,mCurPage, isUpdate);
        }
    }



    /**
     * 绘制背景
     * @param isUpdate
     */
    private void drawBackground(Canvas canvas,TxtPage curPage, boolean isUpdate) {

        if (!isUpdate) {
            /****绘制背景****/
            if (mBgResource == 0) {
                canvas.drawColor(mBgColor);
            }else {
                drawBackgroundImg(canvas, false);
            }
            if (isCategoryReady()) {
                /*****初始化标题的参数********/
                //需要注意的是:绘制text的y的起始点是text的基准线的位置，而不是从text的头部的位置
                float tipTop = getTipMargin() + getTipTextHeight();

                if (curPage != null) {
                    String title = curPage.getTitle();
                    int tiptitleStart = mMarginWidth+mDrawLeftRightMargin;
                    if (haveDisplayCutout) {
                        tiptitleStart = displaycutoutRight+mMarginWidth+mDrawLeftRightMargin;
                    }
                    canvas.drawText(title, tiptitleStart, tipTop, mTipPaint);
                }

                /******绘制页码********/
                // 底部的字显示的位置Y
                float y = mDisplayHeight - getTipMargin() - ScreenUtils.dpToPx(getContext(),2);
                // 只有finish的时候采用页码
                canvas.drawText(String.valueOf(curPage.position), mMarginWidth+mDrawLeftRightMargin, y, mTipPaint);

            }
        } else {
            //擦除区域
            if (mBgResource != 0) {
                drawBackgroundImg(canvas, true);
            }else {
                mBgPaint.setColor(mBgColor);
                canvas.drawRect(getBGUpdateRect(), mBgPaint);
            }
        }

        /******绘制电池********/

        int visibleRight = mDisplayWidth - mMarginWidth-mDrawLeftRightMargin;
        int visibleBottom = mDisplayHeight - getTipMargin();

        int outFrameWidth = (int) mTipPaint.measureText("xxx");
        int outFrameHeight = (int) mTipPaint.getTextSize();

        int polarHeight = ScreenUtils.dpToPx(getContext(),6);
        int polarWidth = ScreenUtils.dpToPx(getContext(),2);
        int border = 1;
        int innerMargin = 1;

        //电极的制作
        int polarLeft = visibleRight - polarWidth;
        int polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2;
        Rect polar = new Rect(polarLeft, polarTop, visibleRight,
                polarTop + polarHeight - ScreenUtils.dpToPx(getContext(),2));

        mBatteryPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(polar, mBatteryPaint);

        //外框的制作
        int outFrameLeft = polarLeft - outFrameWidth;
        int outFrameTop = visibleBottom - outFrameHeight;
        int outFrameBottom = visibleBottom - ScreenUtils.dpToPx(getContext(),2);
        Rect outFrame = new Rect(outFrameLeft, outFrameTop, polarLeft, outFrameBottom);

        mBatteryPaint.setStyle(Paint.Style.STROKE);
        mBatteryPaint.setStrokeWidth(border);
        canvas.drawRect(outFrame, mBatteryPaint);

        //内框的制作
        float innerWidth = (outFrame.width() - innerMargin * 2 - border) * (mBatteryLevel / 100.0f);
        RectF innerFrame = new RectF(outFrameLeft + border + innerMargin, outFrameTop + border + innerMargin,
                outFrameLeft + border + innerMargin + innerWidth, outFrameBottom - border - innerMargin);

        mBatteryPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(innerFrame, mBatteryPaint);

        /******绘制当前时间********/
        //底部的字显示的位置Y
        float y = mDisplayHeight - getTipMargin() - ScreenUtils.dpToPx(getContext(),2);
        String time = NStringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_TIME);
        float x = outFrameLeft - mTipPaint.measureText(time) - ScreenUtils.dpToPx(getContext(), 4);
        canvas.drawText(time, x, y, mTipPaint);
    }

    /**
     * 获取页面标题
     * @return
     */
    private String getPageTitle() {
        // TODO: 2022/10/24  get title
        String title = "";
        if (NStringUtils.isBlank(title)) {
            title = "未知章节";
        }
        return title;
    }

    /**
     * 获取更新时擦除范围
     * @return
     */
    private Rect getBGUpdateRect() {
        Rect rect = new Rect(
                mDisplayWidth / 2,
                mDisplayHeight - getTipMargin() - ((int) getTipTextHeight()) - 2,
                mDisplayWidth,
                mDisplayHeight
        );
        return rect;
    }

    /**
     * 获取进度文字
     * @return
     */
    private String getPagePercentStr() {
        // TODO: 2022/10/24 get percent
        int pageIndex = -1;
        int pageCount = -1;
        String result = "N/A";
        if (pageIndex != -1 && pageCount >= 0) {
            result = (pageIndex + 1) + "/" + pageCount;
        }
        return result;
    }


    private Bitmap bgBitmap;
    private int mBgBitmapHeight = 0;

    /**
     * 绘制背景图片
     * @param canvas
     * @param isUpdate
     */
    private void drawBackgroundImg(Canvas canvas, Boolean isUpdate){
        if (canvas == null){
            return;
        }
        try {

            if (bgBitmap == null || mBgBitmapHeight != mDisplayHeight) {

                if (bgBitmap != null && !bgBitmap.isRecycled()) {
                    bgBitmap.recycle();
                    bgBitmap = null;
                }

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inSampleSize = 4;

                Bitmap resourceBitmap = BitmapFactory.decodeResource(getContext().getResources(), mBgResource,options);
                bgBitmap = Bitmap.createScaledBitmap(resourceBitmap, mDisplayWidth, mDisplayHeight, true);
                mBgBitmapHeight = mDisplayHeight;
                if (resourceBitmap!=null && !resourceBitmap.isRecycled()) {
                    resourceBitmap.recycle();
                    resourceBitmap = null;
                }
            }
            if (isUpdate){
                canvas.save();
                canvas.clipRect(mDisplayWidth / 2, mDisplayHeight - mMarginHeight + ScreenUtils.dpToPx(getContext(),2), mDisplayWidth, mDisplayHeight);
                canvas.drawBitmap(bgBitmap, 0,0, null);
                canvas.restore();
            }else {
                canvas.drawBitmap(bgBitmap, 0, 0, null);
            }
        }catch (Exception e){
            //非正常情况, 画白色背景
            canvas.drawColor(Color.WHITE);
        }
    }

    /**
     * 绘制内容
     * @param isUpdate
     */
    private void drawContent(Canvas canvas,TxtPage mCurPage, boolean isUpdate) {

        if (isScrollMode()) {

            if (mBgResource == 0) {
                canvas.drawColor(mBgColor);
            }else {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
//                drawBackgroundImg(canvas, false);
            }
        }
        /******绘制内容****/
        float top;
        int paraIndex = 0;

        if (isScrollMode()) {
            top = getTextTextHeight();
        } else {
            top = mMarginHeight + getTextTextHeight() + mDrawTopBottomMargin;
        }

        if (mCurPage == null) {
            return;
        }
        //对标题进行绘制
        for (int i = 0; i < mCurPage.titleLines; ++i) {
            LineInfo aLine = mCurPage.lineInfos.get(i);
            drawTitleLine(aLine,canvas,top);
            //设置尾部间距
            if (i == mCurPage.titleLines - 1) {
                top += getTitleParaSpacing();
            } else {
                //行间距
                top += getTitleLineSpacing();
            }
        }

        clearViews();

        //对内容进行绘制
        for (int i = mCurPage.titleLines; i < mCurPage.lineInfos.size(); ++i) {
            LineInfo aLine = mCurPage.lineInfos.get(i);
            drawTextLine(aLine, canvas, needDrawTextBack(paraIndex), top);
            aLine.setmParaIndex(paraIndex);

            if (i == mCurPage.titleLines || aLine.getmLineType() == LineInfo.LineType.LineTypeFirstLine) {
                mCurPage.getPara(paraIndex).setStartY(top - mTextSize);
            }
            if (i+1 < mCurPage.lineInfos.size()){
                LineInfo nextLine = mCurPage.lineInfos.get(i+1);
                if (nextLine.getmLineType() == LineInfo.LineType.LineTypeFirstLine){
                    top += getTextParaSpacing();
                    mCurPage.getPara(paraIndex).setEndY(top-mTextSize);
                    paraIndex++;
                } else {
                    top += getTextLineSpacing();
                    mCurPage.getPara(paraIndex).setEndY(top-mTextSize);
                }
            }else {
                top += getTextLineSpacing();
                mCurPage.getPara(paraIndex).setEndY(top-mTextSize);
            }

        }

    }

    /**
     * 绘制标题行
     * @param aLine
     * @param canvas
     * @param top
     */
    private void drawTitleLine(LineInfo aLine,Canvas canvas,float top) {
        String str = aLine.getmLineText();
        //计算文字显示的起始点
        float strWidth = getStrWidth(aLine);
        float start = mMarginWidth + (mVisibleWidth - strWidth - 2 * mDrawLeftRightMargin) / 2;
        //进行绘制
        for (int j = 0; j < str.length(); j++) {
            String a = str.substring(j, j + 1);
            canvas.drawText(a, start, top, mTitlePaint);
            char aa = a.charAt(0);
            start += aLine.getmAdjustOffset() + getCharWidth(aa,aLine.getmLineType());
        }
    }

    /**
     * 绘制文字行
     * @param aLine
     * @param canvas
     * @param needBack
     * @param top
     */
    private void drawTextLine(LineInfo aLine,Canvas canvas,boolean needBack,float top) {
        String str = aLine.getmLineText();
        float startX = mMarginWidth+mDrawLeftRightMargin;
        float speechBackStartX = startX;
        if (aLine.getmLineType() == LineInfo.LineType.LineTypeFirstLine){
            startX += 2*mTextSize;
            speechBackStartX += 2*mTextSize;
        }
        for (int j = 0; j < str.length(); j++) {
            String a = str.substring(j, j + 1);
            if (needBack) {
                char aa = a.charAt(0);
                speechBackStartX += aLine.getmAdjustOffset() + getCharWidth(aa, aLine.getmLineType());
                canvas.drawRect(speechBackStartX, top - mTextSize, startX, top + mTextInterval / 2, mSpeechBgPaint);
                canvas.drawText(a, startX, top, mSpeechTextPaint);
            } else {
                canvas.drawText(a, startX, top, mTextPaint);
            }
            aLine.setmStartY(top-mTextSize);
            char aa = a.charAt(0);
            startX += aLine.getmAdjustOffset() + getCharWidth(aa,aLine.getmLineType());
        }

    }

    /**
     * 将章节数据，解析成页面列表
     *
     * @param chapter：章节信息
     * @param br：章节的文本流
     * @return
     */
    public List<TxtPage> loadPages(ChaptersBean chapter, int chapterPos, BufferedReader br, Context context) {

        //生成的页面
        List<TxtPage> pages = new ArrayList<>();
        //使用流的方式加载
//        List<String> lines = new ArrayList<>();
        int rHeight = mVisibleHeight;
        int titleLinesCount = 0;
        boolean showTitle = true; // 是否展示标题
        String paragraph = chapter.getTitle();//默认展示标题

        StringBuilder vModParaTextSb = new StringBuilder();

        int startLength = 0;
        try {
            vModParaTextSb.append(paragraph + "\n");
            startLength = vModParaTextSb.length();

            int value = 0;
            int charCount = 0;
            StringBuilder contentSb = new StringBuilder();
            while (((chapter.start <= 0 && chapter.end <= 0) || charCount < (chapter.end - chapter.start)) && (value = br.read()) != -1) {
                char c = (char) value;
                contentSb.append(c);
                charCount++;
            }
//            while ((chapter.textCount <= 0 || vModParaTextSb.length()-startLength < chapter.textCount) && (paragraph = br.readLine()) != null) {
//                vModParaTextSb.append(paragraph + "\n");
//            }
            if (contentSb.toString().startsWith("<")) {
                Spanned spanned = Html.fromHtml(contentSb.toString());//htmlContent是String类型的html文本
                SpannableString spannableString = new SpannableString(spanned);
                vModParaTextSb.append(spannableString.toString());
            }else {
                vModParaTextSb.append(contentSb);
            }
            if (vModParaTextSb.length() <= 0) {
                return pages;
            }
            //智能断行
            ArrayList<LineInfo> mLines = layoutWithText(vModParaTextSb.toString());
            if (mLines.size() <= 0) {
                return pages;
            }
            int wordcount = 0;
            for (LineInfo lineInfo : mLines) {
                wordcount+=lineInfo.getmCharCount();
            }
            chapter.setTextCount(wordcount);
            int startY = 0;
            int displayHeight = mVisibleHeight - mDrawTopBottomMargin;
            float offSetY = 0;
            float lineHeight = 0;

            ArrayList<LineInfo> lines = new ArrayList<>();

            for (int i = 0; i < mLines.size(); i++) {
                LineInfo line = mLines.get(i);
                if (i + 1 < mLines.size()) {

                        LineInfo nextInfo = mLines.get(i + 1);
                        if (line.getmLineType() == LineInfo.LineType.LineTypeTitle) {
                            lineHeight = getTitleTextHeight();
                            if (nextInfo.getmLineType() == LineInfo.LineType.LineTypeTitle) {
                                offSetY = getTitleTextHeight() + mTitleInterval;
                            } else {
                                offSetY = getTitleTextHeight() + mTitlePara;
                            }
                        } else if (line.getmLineType() == LineInfo.LineType.LineTypeFirstLine) {
                            lineHeight = getTextTextHeight();
                            if (nextInfo.getmLineType() == LineInfo.LineType.LineTypeFirstLine) {
                                offSetY = getTextTextHeight() + mTextPara;
                            } else {
                                offSetY = getTextTextHeight() + mTextInterval;
                            }
                        } else {
                            lineHeight = getTextTextHeight();
                            if (nextInfo.getmLineType() == LineInfo.LineType.LineTypeFirstLine) {
                                offSetY = getTextTextHeight() + mTextPara;
                            } else {
                                offSetY = getTextTextHeight() + mTextInterval;
                            }
                        }

                } else {
                    //最后一行
                    lineHeight = getTextTextHeight();
                    offSetY = getTextTextHeight();
                }

                if (startY + lineHeight > displayHeight) {
                    //创建Page
                    TxtPage page = new TxtPage();
                    page.position = pages.size();
                    page.title = NStringUtils.convertCC(chapter.getTitle(), context);
//                    page.lines = new ArrayList<>(lines);
                    boolean haveSetTitleLine = false;
                    for (int j = 0; j < lines.size(); j++) {
                        LineInfo aLine = lines.get(j);
                        if (aLine.getmLineType() == LineInfo.LineType.LineTypeTitle) {
                            continue;
                        } else {
                            if (!haveSetTitleLine) {
                                page.titleLines = j;
                                haveSetTitleLine = true;
                            }
//                            break;
                            if (j == page.titleLines) {
                                page.addLineToLastPara(aLine, true);
                            } else {
                                page.addLineToLastPara(aLine, false);
                            }
                        }
                    }
                    page.lineInfos = new ArrayList<>(lines);
                    pages.add(page);
                    // 重置Lines
                    lines.clear();

                    startY = 0;
                    i--;

                } else {
                    lines.add(line);
                    startY += offSetY;
                }
            }
            if (lines.size() > 0) {
                int iStartY = 0;
                TxtPage page = new TxtPage();
                page.position = pages.size();
                page.title = NStringUtils.convertCC(chapter.getTitle(), context);
//                    page.lines = new ArrayList<>(lines);

                boolean bHaveSetTitleLines = false;
                for (int j = 0; j < lines.size(); j++) {
                    LineInfo aLine = lines.get(j);
                    if (aLine.getmLineType() == LineInfo.LineType.LineTypeTitle) {
                        iStartY += getTitleTextHeight() + mTitleInterval;
                        continue;
                    } else {
                        if (aLine.getmLineType() == LineInfo.LineType.LineTypeFirstLine) {
                            iStartY += getTextTextHeight() + mTitlePara;
                        } else {
                            iStartY += getTextTextHeight() + mTextInterval;
                        }
                        if (bHaveSetTitleLines == false) {
                            page.titleLines = j;
                            bHaveSetTitleLines = true;
                        }
                    }

                    if (j == page.titleLines) {
                        page.addLineToLastPara(aLine,true);
                    }else {
                        page.addLineToLastPara(aLine, false);
                    }
                }

                page.lineInfos = new ArrayList<>(lines);
                pages.add(page);
                // 重置Lines
                lines.clear();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br);
        }
        return pages;
    }


    /**
     * 是否是滚动模式
     * @return
     */
    private boolean isScrollMode() {
        // TODO: 2022/10/24 is scroll mode
        return false;
    }

    /**
     * 是否需要绘制听书背景
     * @param paraIndex 段落index
     * @return
     */
    private boolean needDrawTextBack(int paraIndex) {
        // TODO: 2022/10/24  need draw text back book listener
        return false;
    }

    /**
     * 章节目录是否已准备好
     * @return
     */
    private boolean isCategoryReady() {
        // TODO: 2022/10/24 is category ready
        return true;
    }


    /**
     * 清空非绘制内容 adViews
     */
    private void clearViews() {
    }


    //章节断行总入口
    public ArrayList<LineInfo> layoutWithText(String vTextPtr){

        ArrayList<LineInfo> mlines = new ArrayList<>();
        int baseCharPos = 0;  //线程中用于对char pos进行计数
        char vBeginChar;
        int vBreakPos = 0;
        int vLength = 0;
        String vParaTextPtr = null;

        StringBuilder vModParaTextSb = new StringBuilder();
        //去掉段首非法字符
        while(vBreakPos < vTextPtr.length())
        {
            vBeginChar = vTextPtr.charAt(vBreakPos);
            if (vBeginChar == '\n') {
                vBreakPos++;
                continue;
            }else if(vBeginChar>0x20)// char码值在33之前的字符为非正常可见字符
            {
                break;//找到第一个正常字符退出循环
            }
            vBreakPos++;
        }
        baseCharPos += vBreakPos;
        vTextPtr = vTextPtr.substring(vBreakPos);//截取掉段首非法字符

        int location = vTextPtr.indexOf("\n");//找到第一个换行符的位置
        boolean bTitle = true;
        while (location > 0 && location < vTextPtr.length()){
            vBreakPos = location;
            vTextPtr.charAt(vBreakPos-1);
            if(vBreakPos>0 && vTextPtr.charAt(vBreakPos-1) == '\r')
            {
                vParaTextPtr = vTextPtr.substring(0,vBreakPos-1);
            }else{
                vParaTextPtr = vTextPtr.substring(0,vBreakPos);
            }
            mlines.addAll(SmartBreakText(vParaTextPtr,vModParaTextSb,baseCharPos,bTitle));
            if (bTitle){
                bTitle = false;
            }

            //去除段落前非法控制字符及多个‘\n’
            vLength = vTextPtr.length();
            while (++vBreakPos < vLength){
                vBeginChar = vTextPtr.charAt(vBreakPos);
                if (vBeginChar == '\n'){
                    continue;
                }else if(vBeginChar > 0x20){
                    break;
                }
            }

            vTextPtr = vTextPtr.substring(vBreakPos);
            baseCharPos += vBreakPos;

            vModParaTextSb.setLength(0);

            location = vTextPtr.indexOf("\n");
        }

        //处理剩下的string
        if (vTextPtr.length() > 0){
            mlines.addAll(SmartBreakText(vTextPtr,vModParaTextSb,baseCharPos,bTitle));
        }
        return mlines;
    }



    //智能断行
    public ArrayList<LineInfo> SmartBreakText(String aParaText, StringBuilder aModSb, int baseCharPos, boolean bTitle) {

        ArrayList<LineInfo> mlines = new ArrayList<>();
        char vBeginChar;
        int vHeadSpCount = 0, vEndSpCount = 0;
        int vLength = aParaText.length();

        //检测段首是否有空格
        while (vHeadSpCount < vLength) {
            vBeginChar = aParaText.charAt(vHeadSpCount);
            if (vBeginChar == 0x3000 //汉语 空格
                    ||vBeginChar == 0xfffc
                    || vBeginChar <= 0x20 // 换行
                    || (vBeginChar <= 0xff //
                        && vBeginChar >= 0x81)) {
                vHeadSpCount++;
                continue;
            }
            break;
        }

        if (vHeadSpCount >= vLength){
            return mlines;
        }

        //计算段尾空白字符数量
        for (int i = vLength - 1; i >= 0; i--) {
            vBeginChar = aParaText.charAt(i);
            if (vBeginChar == 0x3000
                    || vBeginChar <= 0x20
                    || (vBeginChar <= 0xff
                        && vBeginChar >= 0x81)) {
                vEndSpCount++;
                continue;
            }
            break;
        }

        aModSb.setLength(0);
//        aModSb.append(aParaText, vHeadSpCount, aParaText.length()-vHeadSpCount-vEndSpCount);
        aModSb.append(aParaText, vHeadSpCount, aParaText.length()-vEndSpCount);
        mlines.addAll(
                breakText(
                        aModSb.toString(),
                        baseCharPos,
                        bTitle
                )
        );
        return mlines;
    }

    public float getStrWidth(LineInfo info){
        String str = info.getmLineText();
        float width = 0;
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            width += getCharWidth(a, info.getmLineType())+info.getmAdjustOffset();
        }
        return width;
    }

    public float getCharWidth(char aChar, LineInfo.LineType lineType) {
        if (lineType == LineInfo.LineType.LineTypeTitle) {
            if (aChar >= 0 && aChar < KAsciiCharCount) {
                return getiHAsciiFontWidth()[aChar];
            } else if (aChar >= KSpecialChsBegin && aChar <= KSpecialChsEnd) {
                return getiHSpecialChsFontWidth()[aChar - KSpecialChsBegin];
            } else {
                return getmTitleChsCharWidth() + getmTextMargin();
            }
        } else {
            if (aChar >= 0 && aChar < KAsciiCharCount) {
                return getiAsciiFontWidth()[aChar];
            } else if (aChar >= KSpecialChsBegin && aChar <= KSpecialChsEnd) {
//                return iSpecialChsFontWidth[aChar];
                return getiSpecialChsFontWidth()[aChar - KSpecialChsBegin];
            } else {
                return getmChsCharWidth() + getmTextMargin();
            }
        }
    }

    public void getSpaceNumWithStr(String text, int[] vHeadSpCount, int[] vTailSpCount) {
        vHeadSpCount[0] = 0;
        vTailSpCount[0] = 0;
        //检测行首是否有空格
        char vBeginChar;
        int vLength = text.length();

        while (vHeadSpCount[0] < vLength) {
            vBeginChar = text.charAt(vHeadSpCount[0]);
            if (vBeginChar == 0x0020) {
                vHeadSpCount[0]++;
                continue;
            }
            break;
        }
        //检测行尾是否有空格
        int tailSPCount = text.length() - 1;
        char tailChar;
        while (tailSPCount >= 0) {
            tailChar = text.charAt(tailSPCount);
            if (tailChar == 0x3000 || tailChar == 0x0020) {
                tailSPCount--;
                vTailSpCount[0]++;
                continue;
            }
            break;
        }
        return;
    }


    public int GetWord(String str, int offset, float[] aWidth, LineInfo.LineType lineType) {
        aWidth[0] = 0;
        int len = 0;
        float width = 0;
        int strLen = str.length();
        char c;
        while (offset + len <= strLen - 1) {
            c = str.charAt(offset + len);
            if ((c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')) {
                len++;
                if (lineType == LineInfo.LineType.LineTypeTitle) {
                    if (c >= 0 && c < KAsciiCharCount) {
                        width += getiHAsciiFontWidth()[c];
                    } else if (c >= KSpecialChsBegin && c <= KSpecialChsEnd) {
                        width += getiHSpecialChsFontWidth()[c - KSpecialChsBegin];
                    } else {
                        width += getmTitleChsCharWidth();
                    }
                } else {
                    if (c >= 0 && c < KAsciiCharCount) {
                        width += getiAsciiFontWidth()[c];
                    } else if (c >= KSpecialChsBegin && c <= KSpecialChsEnd) {
                        width += getiSpecialChsFontWidth()[c - KSpecialChsBegin];
                    } else {
                        width += getmChsCharWidth();
                    }
                }
            } else {
                break;
            }
        }
        if (aWidth.length > 0) {
            aWidth[0] = width;
        }
        return len;
    }

//    private ArrayList<LineInfo> mLines = new ArrayList<LineInfo>();

    // aLineWidth 行宽
    public ArrayList<LineInfo> breakText(String aText, int baseCharPos, boolean bTitle) {

        int aLineWidth = getmDisplayWidth()-2*getmDrawLeftRightMargin();//单行宽度 = 可展示宽度- 2*左右留白

        int marginWidth = getmMarginWidth();//字间距

        float cnCharWidth = getmChsCharWidth();//单字宽度

        ArrayList<LineInfo> mLines = new ArrayList<LineInfo>();
        int vTextCount = aText.length();
        if (vTextCount <= 0) {
            return mLines;
        }

        LineInfo.LineType lineType = LineInfo.LineType.LineTypeFirstLine;
        if (bTitle) {
            lineType = LineInfo.LineType.LineTypeTitle;
        }

        int KRepeatCount = 0;
        String tmpTitleStr = null;


        float[] vWordWidth = new float[1];
        int vWordSize;
        //普通的文本行(非第一行)的宽度[因为每次累加的是文字宽度+字间距，为了计算方便行宽需要加上一个字间距]
        int lineWidth = aLineWidth - 2 * marginWidth;
        //每一行的字符数。每次计算新行时，重置为0。
        int vLineCharCount = 0;
        //遍历字符时，累加每一行字符的宽度。每次计算新行时，重置为0。
        int totalCharWidth = 0;
        char c;

        if (lineType != LineInfo.LineType.LineTypeTitle) {
            //段落的第一行缩进两个中文字符，所以还要再减去两个字符及两个字间距的宽度
            lineWidth -= 2 * cnCharWidth;

        }
        int i = 0;
        int spaceNum = 0;//统计用于断行的空格的数量:(加上该空格使字符总宽度大于行宽)
        int wordNum = 0; //一行中单词(welcome. welcome wel- ),以及其他字符的总数量

        int lineCount = mLines.size();  //断行前已有的行数，新增的第一行为FistLine，其他为MainLine

        for (; i < vTextCount; i++) {
            c = aText.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                //英文字母
                vWordSize = GetWord(aText, i, vWordWidth, lineType);
                wordNum++;
                //单词宽度小于行宽的情况
                if (vWordWidth[0] < lineWidth) {
                    totalCharWidth += vWordWidth[0];

                    //目前行中字符加上英文单词后的宽度超过行宽
                    if (totalCharWidth > lineWidth) {
                        String word = aText.substring(i, i+vWordSize);
                        //先减去最后一个单词
                        totalCharWidth -= vWordWidth[0];

                        String text = aText.substring(i - vLineCharCount, i);
                        LineInfo info = new LineInfo(text, baseCharPos + i - vLineCharCount, vLineCharCount);
                        info.setmLineType(lineType);

                        if (lineType != LineInfo.LineType.LineTypeTitle) {
                            if (lineCount == mLines.size() && aText.startsWith(info.getmLineText())) {
                                info.setmLineType(LineInfo.LineType.LineTypeFirstLine);
                            } else {
                                info.setmLineType(LineInfo.LineType.LineTypeMainText);
                            }
                        }

                        //检测行首尾空格
                        //检测行首尾空格的数量
                        int[] vHeadSpCount = new int[1];
                        int[] vTailSpCount = new int[1];

                        //去除行首尾空格
                        getSpaceNumWithStr(text, vHeadSpCount, vTailSpCount);
                        text = text.substring(vHeadSpCount[0], text.length() - vTailSpCount[0]);
                        totalCharWidth -= getCharWidth(' ',lineType) * (vHeadSpCount[0] + vTailSpCount[0]);
                        wordNum -= (vHeadSpCount[0] + vTailSpCount[0] + 1);

                        info.setmLineText(text);
                        info.setmStartPos(info.getmStartPos() + vHeadSpCount[0]);
                        info.setmCharCount(text.length());

                        mLines.add(info);
                        info = null;

                        totalCharWidth = 0;
                        vLineCharCount = 0;
                        wordNum = 0;
                        spaceNum = 0;

                        lineWidth = aLineWidth - 2 * marginWidth;
                        i--;
                    } else {
                        i += (vWordSize - 1);
                        vLineCharCount += (vWordSize);
                    }
                } else {
                    //单词宽度大于行宽 TODO ...
                    totalCharWidth += getCharWidth(c, lineType);
                    if (totalCharWidth > lineWidth) {
                        String text = aText.substring(i - vLineCharCount, vLineCharCount);
                        LineInfo info = new LineInfo(text, baseCharPos + i - vLineCharCount, vLineCharCount);
                        info.setmLineType(lineType);
                        if (lineType != LineInfo.LineType.LineTypeTitle) {
                            if (lineCount == mLines.size() && aText.startsWith(text)) {
                                info.setmLineType(LineInfo.LineType.LineTypeFirstLine);
                            } else {
                                info.setmLineType(LineInfo.LineType.LineTypeMainText);

                            }
                        }

                        float adjust = (getCharWidth(c, lineType) - (float) (lineWidth - totalCharWidth)) / (float) (info.getmLineText().length());
                        info.setmAdjustOffset(adjust);
                        mLines.add(info);
                        info = null;

                        totalCharWidth = 0;
                        vLineCharCount = 0;

                        lineWidth = aLineWidth - 2 * marginWidth;
                        i--;
                    } else {
                        vLineCharCount++;
                    }
                }
            } else {
                //非英文字母的文字断行，一个个累加，判断字符累计长度是否超过行长，如果超过，则将前面没超过的字符生成行
                //charWidth:即为字符的实际宽度+字间距
                float charWidth = getCharWidth(c, lineType);
                wordNum++;
                totalCharWidth += charWidth;

                //此时的总宽度大于行的宽度的时候
                if (totalCharWidth > lineWidth) {
                    if (c == 0x0020) {//总宽度大于行宽度时，如果此时字符为' ',那么跳过这个字符，不进行断行
                        spaceNum++;
                        vLineCharCount++;
                        continue;
                    }
                    totalCharWidth -= charWidth;
                    //检查上一行的最后一个字符是否为上引号
                    char lastChar = aText.charAt(i - 1);
                    if (lastChar == 0x201c) {
                        i--;
                        vLineCharCount--;
                        totalCharWidth -= getCharWidth(lastChar, lineType);
                    } else {
                        //检查当前字符
                        if (c == 0x201d) {
                            //全角下引号,与上一行合并
                            i++;
                            vLineCharCount++;
                            totalCharWidth += charWidth;
                        } else if (c == 0x201c) {
                            //全角上引号，donothing
                            //                        continue;
                        } else if ((c >= KFullWidthMarkBegin && c <= KFullWidthMarkEnd)
                                || (c >= KCjkMarkBegin && c <= KCjkMarkEnd)
                                || (c >= KSpecialChsBegin && c <= KSpecialChsEnd)
                                || (c >= KAsciiMarkBegin && c <= KAsciiMarkEnd)) {
                            //如果是 。 并且前面一个是 ”，都并到上一行
                            if (c == 0x3002 && aText.charAt(i - 1) == 0x201d) {
                                i++;
                                vLineCharCount++;
                                totalCharWidth += charWidth;
                            } else {
                                //独立标点符号，取上行末尾字符到下行行首
                                //去除对最后一个字符的累加
                                i--;
                                vLineCharCount--;
                                totalCharWidth -= getCharWidth(aText.charAt(i), lineType);
                            }
                        }
                    }

                    KRepeatCount++;
                    //去除对最后一个字符的累加
                    //i:即为此时遍历的字符总数量
                    //vLineCharCount:此时字符累加的宽度不大于一行时，该行字符的总数量
                    //baseCharPos:该段落的第一个字符在整个章节中的位置
                    String text = aText.substring(i - vLineCharCount, i);
                    LineInfo info = new LineInfo(text, baseCharPos + i - vLineCharCount, vLineCharCount);

                    //检测行首尾空格的数量
                    int[] vHeadSpCount = new int[1];
                    int[] vTailSpCount = new int[1];

                    //去除行首尾空格
                    getSpaceNumWithStr(text, vHeadSpCount, vTailSpCount);
                    text = text.substring(vHeadSpCount[0], text.length() - vTailSpCount[0]);
                    totalCharWidth -= getCharWidth(' ',lineType) * (vHeadSpCount[0] + vTailSpCount[0]);
                    wordNum -= (vHeadSpCount[0] + vTailSpCount[0] + 1);

                    info.setmLineText(text);
                    info.setmStartPos(info.getmStartPos() + vHeadSpCount[0]);
                    info.setmCharCount(text.length());


                    if (KRepeatCount == 1) {
                        tmpTitleStr = new String(info.getmLineText());
                    } else if (KRepeatCount == 2) {
                        tmpTitleStr = null;
                    }
                    info.setmLineType(lineType);
                    if (lineType != LineInfo.LineType.LineTypeTitle) {
                        if (lineCount == mLines.size() && aText.startsWith(info.getmLineText())) {
                            //判断是否为第一行
                            info.setmLineType(LineInfo.LineType.LineTypeFirstLine);
                        } else {
                            info.setmLineType(LineInfo.LineType.LineTypeMainText);
                        }
                    }
                    //判断字符间距的偏移量
                    //adjustOffset:整个行减去字符以及间距后的剩余宽度，平均分配到每个字符间距中去,从而使每个完整的行都被完全填充。
                    info.setmAdjustOffset((float) (lineWidth - totalCharWidth) / (float) (vLineCharCount - 1));
//                    if (info.getmLineType() == LineInfo.LineType.LineTypeFirstLine){
//                        info.setmAdjustOffset((float) (lineWidth - totalCharWidth-2*mTextSize) / (float) (wordNum - 1));
//                    }
                    mLines.add(info);
                    info = null;

                    //断行后初始化参数
                    totalCharWidth = 0;
                    vLineCharCount = 0;
                    spaceNum = 0;
                    wordNum = 0;
                    //非首行时，用于计算的行宽
                    lineWidth = aLineWidth - 2 * marginWidth;
                    i--;
                } else {
                    vLineCharCount++;
                }
            }
        }

        if (vLineCharCount > 0){
            String text = aText.substring(i-vLineCharCount,i);
            LineInfo info = new LineInfo(text,baseCharPos+i-vLineCharCount,vLineCharCount);
            info.setmLineType(lineType);

            //检测行首尾空格的数量
            int[] vHeadSpCount = new int[1];
            int[] vTailSpCount = new int[1];

            //去除行首尾空格
            getSpaceNumWithStr(text, vHeadSpCount, vTailSpCount);
            text = text.substring(vHeadSpCount[0], text.length() - vTailSpCount[0]);
            if (text.length() > 0){
                info.setmLineText(text);
                info.setmCharCount(text.length());
                if (mLines.size() > 0 && bTitle == false){
                    info.setmLineType(LineInfo.LineType.LineTypeMainText);
                }
                mLines.add(info);
            }
        }
        return mLines;
    }


    private float[] iAsciiFontWidth;
    private float[] iSpecialChsFontWidth;
    private float[] iHAsciiFontWidth;
    private float[] iHSpecialChsFontWidth;
    private String iAsciiCharTable;
    private String iSpecialChsCharTable;

    private float mChsCharWidth;  //普通汉字大小
    private float mTitleChsCharWidth;  //标题汉字大小

    private void initFontSizeTable() {
        if (mTextSize <= 0) return;
        int i;
        if (iAsciiCharTable == null) {
            byte[] vAsciiBytes = new byte[KAsciiCharCount];
            for (i = 0; i < KAsciiCharCount; i++) {
                vAsciiBytes[i] = (byte) i;
            }
            iAsciiCharTable = new String(vAsciiBytes);
            KAsciiCharCount = iAsciiCharTable.length();
        }
        if (iSpecialChsCharTable == null) {
            byte[] vSpecialChsBytes = new byte[KChsSpecialSymbol * 2];
            char vSpecialChsBegin = KSpecialChsBegin;
            for (i = 0; i < KChsSpecialSymbol; i++) {
                vSpecialChsBytes[2 * i] = (byte) vSpecialChsBegin;
                vSpecialChsBytes[2 * i + 1] = (byte) (vSpecialChsBegin >> 8);
                vSpecialChsBegin++;
            }
            try {
                iSpecialChsCharTable = new String(vSpecialChsBytes, "UTF-16LE");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (iAsciiFontWidth == null) {
            iAsciiFontWidth = new float[KAsciiCharCount];
        }
        if (iHAsciiFontWidth == null) {
            iHAsciiFontWidth = new float[KAsciiCharCount];
        }
        for (i = 0; i < KAsciiCharCount; i++) {
            iAsciiFontWidth[i] = mTextPaint.measureText(iAsciiCharTable, i, i + 1);
            iHAsciiFontWidth[i] = mTitlePaint.measureText(iAsciiCharTable, i, i + 1);
        }

        if (iSpecialChsFontWidth == null) {
            iSpecialChsFontWidth = new float[KChsSpecialSymbol];
        }
        if (iHSpecialChsFontWidth == null) {
            iHSpecialChsFontWidth = new float[KChsSpecialSymbol];
        }
        for (i = 0; i < KChsSpecialSymbol; i++) {
            iSpecialChsFontWidth[i] = mTextPaint.measureText(iSpecialChsCharTable, i, i + 1);
            iHSpecialChsFontWidth[i] = mTitlePaint.measureText(iSpecialChsCharTable, i, i + 1);
        }
        mChsCharWidth = mTextPaint.measureText("国");
        mTitleChsCharWidth = mTitlePaint.measureText("国");
    }

    public float getTextHeight() {
        return mTextPaint.getTextSize();
    }


    public int getmDrawTopBottomMargin() {
        return mDrawTopBottomMargin;
    }

    public int getmDrawLeftRightMargin() {
        return mDrawLeftRightMargin;
    }

    public int getmDisplayWidth() {
        return mDisplayWidth;
    }

    public int getmDisplayHeight() {
        return mDisplayHeight;
    }

    public int getmMarginWidth() {
        return mMarginWidth;
    }

    public int getmMarginHeight() {
        return mMarginHeight;
    }

    private boolean needPageAdNotice() {
        return true;
    }

    public int getAdNoticeHeight() {
        return getAdNoticeTextSize()+100;
    }

    private int getAdNoticeTextSize() {
        return ScreenUtils.spToPx(getContext(), 18);
    }

    private String getAdNoticeText() {
        return "广告是为了更好的支持正版";
    }

    /**
     * 获取tip内容边距
     * @return
     */
    private int getTipMargin() {
        return ScreenUtils.dpToPx(getContext(), 3);
    }


    public int getmTextMargin() {
        return mTextMargin;
    }

    public int getmTextInterval() {
        return mTextInterval;
    }

    private float getTipTextHeight() {
        return mTipPaint.getFontSpacing();
    }

    private float getTitleTextHeight() {
        return mTitlePaint.getFontSpacing();
    }

    private float getTextTextHeight() {
        return mTextPaint.getFontSpacing();
    }

    public float[] getiAsciiFontWidth() {
        return iAsciiFontWidth;
    }

    public float[] getiSpecialChsFontWidth() {
        return iSpecialChsFontWidth;
    }

    public float[] getiHAsciiFontWidth() {
        return iHAsciiFontWidth;
    }

    public float[] getiHSpecialChsFontWidth() {
        return iHSpecialChsFontWidth;
    }

    public float getmChsCharWidth() {
        return mChsCharWidth;
    }

    public float getmTitleChsCharWidth() {
        return mTitleChsCharWidth;
    }

    private int getTextLineSpacing() {
        return mTextInterval + (int) getTextTextHeight();
    }

    private int getTextParaSpacing() {
        return mTextPara + (int) getTextTextHeight();
    }

    private int getTitleLineSpacing() {
        return mTitleInterval + (int) getTitleTextHeight();
    }

    private int getTitleParaSpacing() {
        return mTitlePara + (int) getTitleTextHeight();
    }

    public void destroy() {

        if (bgBitmap != null && !bgBitmap.isRecycled()){
            bgBitmap.recycle();
            bgBitmap = null;
        }
        mContext = null;
        mInstance = null;
    }
}

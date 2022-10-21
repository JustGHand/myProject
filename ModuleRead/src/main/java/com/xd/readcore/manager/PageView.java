package com.xd.readcore.manager;


import static com.xd.readcore.bean.PageMode.SCROLL;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.xd.base.ad.YYInsertView;
import com.xd.base.utils.ScreenUtils;
import com.xd.readcore.R;
import com.xd.readcore.animation.AutoReadPageAnim;
import com.xd.readcore.animation.CoverPageAnim;
import com.xd.readcore.animation.HorizonPageAnim;
import com.xd.readcore.animation.NonePageAnim;
import com.xd.readcore.animation.PageAnimation;
import com.xd.readcore.animation.ScrollPageAnim;
import com.xd.readcore.animation.SimulationPageAnim;
import com.xd.readcore.animation.SlidePageAnim;

import com.xd.readcore.bean.LineInfo;
import com.xd.readcore.bean.PageMode;
import com.xd.base.ad.YYFrame;
import com.xd.readcore.utils.Constant;
import com.xd.readcore.utils.Constant.InsertViewType;

import java.util.List;

/**
 * Created by Administrator on 2016/8/29 0029.
 * 原作者的GitHub Project Path:(https://github.com/PeachBlossom/treader)
 * 绘制页面显示内容的类， 单View获取2个Bitmap进行动画策略
 */
public class PageView extends FrameLayout {

    private enum TURN_PAGE_TYPE{
        TURN_PAGE_TYPE_CUR, TURN_PAGE_TYPE_PREV, TURN_PAGE_TYPE_NEXT
    }

    private final static String TAG = "BookPageWidget";

    private int mViewWidth = 0; // 当前View的宽
    private int mViewHeight = 0; // 当前View的高

    public int getmViewWidth() {
        return mViewWidth;
    }

    public int getmViewHeight() {
        return mViewHeight;
    }

    private int mStartX = 0;
    private int mStartY = 0;
    private boolean isMove = false;
    // 初始化参数
    private int mBgColor = 0xFFCEC29C;
    private PageMode mPageMode = PageMode.SIMULATION;
    // 是否允许点击
    private boolean canTouch = true;
    // 唤醒菜单的区域
    private RectF mCenterRect = null;
    private boolean isPrepare;

    private Bitmap mBitmap;
    private Canvas mCanvas;

    private YYInsertView mAdView = null; //当前页的广告View
    private YYFrame mFrame = null;    //当前页的广告Frame
    private InsertViewType mAdType = InsertViewType.NONE;   //当前页的广告类型

    private YYInsertView mPrevAdView = null;   //前一页的广告View
    private YYFrame mPrevFrame = null;
    private InsertViewType mPrevAdType = InsertViewType.NONE;

    private YYInsertView mNextAdView = null;   //后一页的广告View
    private YYFrame mNextFrame = null;
    private InsertViewType mNextAdType = InsertViewType.NONE;

    private TURN_PAGE_TYPE mTurnPageType = TURN_PAGE_TYPE.TURN_PAGE_TYPE_CUR;

    private boolean shouldDraw = true;
    private int drawCount = 0;

    private boolean mTouchReloadBtn = false;


    // 动画类
    private PageAnimation mPageAnim;
    // 动画监听类
    private PageAnimation.OnPageChangeListener mPageAnimListener = new PageAnimation.OnPageChangeListener() {
        @Override
        public boolean hasPrev() {
            mTurnPageType = TURN_PAGE_TYPE.TURN_PAGE_TYPE_PREV;
            return PageView.this.hasPrevPage();
        }

        @Override
        public boolean hasNext() {
            mTurnPageType = TURN_PAGE_TYPE.TURN_PAGE_TYPE_NEXT;
            return PageView.this.hasNextPage();
        }

        @Override
        public void pageCancel() {
            PageView.this.pageCancel();
        }

        @Override
        public void moveTurnPageBegin(){
            if (mTurnPageListener != null){
                mTurnPageListener.moveTurnPageBegin();
            }
        }

        @Override
        public void moveTurnPageFinished(boolean bFinished){
            if (mTurnPageListener != null){
                mTurnPageListener.moveTurnPageFinished(bFinished);
            }
            PageView.this.turnPageFinished();
        }

        @Override
        public void tapTurnPage(){
            if (mTurnPageListener != null){
                mTurnPageListener.tapTurnPage();
            }
//            PageView.this.turnPageFinished();
        }

        @Override
        public void autoReadPageFinish() {
            if (mAutoReadListener != null){
                mAutoReadListener.autoReadPageFinished();
            }else {
//                ToastUtils.show("自动阅读已结束或出错请退出阅读重试");
            }
        }

        @Override
        public void onScroll() {
        }
    };

    private boolean snycAdViewLocation() {

        boolean haveChangeLayout = false;
        if (mPageAnim instanceof ScrollPageAnim) {
            List<ScrollPageAnim.BitmapView> actBitMapViews = ((ScrollPageAnim) mPageAnim).getActViews();
            if (actBitMapViews != null && actBitMapViews.size() >0) {
                ScrollPageAnim.BitmapView firstView = actBitMapViews.get(0);
                ScrollPageAnim.BitmapView secondView = null;
                if (actBitMapViews.size() > 1) {
                    secondView = actBitMapViews.get(1);
                }
                if (firstView != null && secondView != null) {
                    if (firstView.getDestRect().top > secondView.getDestRect().top) {
                        ScrollPageAnim.BitmapView tmpView = firstView;
                        firstView = secondView;
                        secondView = tmpView;
                    }
                }
                int mScrollShift = mViewHeight / 2 - ((ScrollPageAnim) mPageAnim).getScrollViewHeight() / 2;
                if (firstView!=null) {
                    int firstTop = firstView.getDestRect().top + mScrollShift;
                    if (mAdView != null) {
                        int tarTop = mFrame.getY() + firstTop;
                        mAdView.layout(mFrame.getX(), tarTop, mFrame.getX() + mFrame.getWidth(), tarTop + mFrame.getHeight());
                        if (mAdType == InsertViewType.VIEWTYPE_FULLPAGE || mAdType == InsertViewType.VIEWTYPE_TAIL_PAGEAD) {
                            watchVideoBtn.layout(mBtnFram.getX(), mBtnFram.getY() + firstTop, mBtnFram.getX() + mBtnFram.getWidth(), mBtnFram.getY() + firstTop + mBtnFram.getHeight());
                        }
                        haveChangeLayout = true;
                    }
                }

                if (secondView!=null) {
                    if (mNextAdView != null) {
                        int secondTop = secondView.getDestRect().top + mScrollShift;
                        int tarTop = mNextFrame.getY() + secondTop;
                        mNextAdView.layout(mNextFrame.getX(), tarTop, mNextFrame.getX() + mNextFrame.getWidth(), tarTop + mNextFrame.getHeight());
                        if (mNextAdType == InsertViewType.VIEWTYPE_FULLPAGE || mAdType == InsertViewType.VIEWTYPE_TAIL_PAGEAD) {
                            watchVideoBtn.layout(mBtnFram.getX(), mBtnFram.getY() + secondTop, mBtnFram.getX() + mBtnFram.getWidth(), mBtnFram.getY() + secondTop + mBtnFram.getHeight());
                        }
                        haveChangeLayout = true;
                    }

                }
            }

        }
        return haveChangeLayout;
    }

    public interface AutoReadListener{
        void autoReadStart();

        void autoReadPageFinished();

        void autoReadFinished();
    }

    //用于自动翻页回调
    private AutoReadListener mAutoReadListener = null;

    public PageMode getmPageMode() {
        return mPageMode;
    }

    public void setmAutoReadListener(@Nullable AutoReadListener autoReadListener) {
        this.mAutoReadListener = autoReadListener;
    }

    //信息流广告翻页监听
    private TurnPageListener mTurnPageListener = null;
    public void setmTurnPageListener(@Nullable TurnPageListener mTurnPageListener) {
        this.mTurnPageListener = mTurnPageListener;
    }


    //点击监听
    private TouchListener mTouchListener;

    public PageView(Context context) {
        this(context, null);
        setClipChildren(false);
    }

    public PageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        //千万不要关闭硬件加速，否则页面渲染会很卡
//        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    private ReadDispatch mReadDispatch;

    public void setReadDispatch(ReadDispatch readDispatch) {
        mReadDispatch = readDispatch;
    }

    protected void init(ReadDispatch readDispatch) {
        mReadDispatch = readDispatch;
        setPageMode(mReadDispatch.getPageMode());
        setBgColor(mBgColor);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

        isPrepare = true;

        if (mReadDispatch != null) {
            mReadDispatch.prepareDisplay(w, h);
        }
    }

    //设置翻页的模式
    void setPageMode(PageMode pageMode) {
        //视图未初始化的时候，禁止调用
        if (mViewWidth == 0 || mViewHeight == 0) return;


        Bitmap curBitmap = null;
        Bitmap nextBitmap = null;

        if (mPageMode == SCROLL){
            //scroll 切换到其他, 什么都不做
        }else if(pageMode == SCROLL){
            //其他切换到scroll，释放内存，特别是图片内存
            if (mPageAnim != null){
                mPageAnim.clear();
                mPageAnim = null;
            }
        }else {
            //非scroll的动画互相切换，共用bitmap,节省图片内存，防oom
            if (mPageAnim != null){
                curBitmap = mPageAnim.getmCurBitmap();
                nextBitmap = mPageAnim.getNextBitmap();
            }
        }
        mPageMode = pageMode;
        switch (mPageMode) {
            case SIMULATION:
                mPageAnim = new SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                ((SimulationPageAnim) mPageAnim).setBitmaps(curBitmap, nextBitmap);
                break;
            case COVER:
                mPageAnim = new CoverPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                ((CoverPageAnim) mPageAnim).setBitmaps(curBitmap, nextBitmap);
                break;
            case SLIDE:
                mPageAnim = new SlidePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                ((SlidePageAnim) mPageAnim).setBitmaps(curBitmap, nextBitmap);
                break;
            case NONE:
                mPageAnim = new NonePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                ((NonePageAnim) mPageAnim).setBitmaps(curBitmap, nextBitmap);
                break;
            case SCROLL:
                mPageAnim = new ScrollPageAnim(mViewWidth, mViewHeight, 0,
                        mReadDispatch.getMarginHeight(), this, mPageAnimListener);
                break;
            case AUTO:
                mPageAnim = new AutoReadPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                ((AutoReadPageAnim) mPageAnim).setBitmaps(curBitmap, nextBitmap);
                break;
            default:
                mPageAnim = new SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                ((SimulationPageAnim) mPageAnim).setBitmaps(curBitmap, nextBitmap);
        }
    }

    public Bitmap getNextBitmap() {
        if (mPageAnim == null) return null;
        return mPageAnim.getNextBitmap();
    }

    public Bitmap getBgBitmap() {
        if (mPageAnim == null) return null;
        return mPageAnim.getBgBitmap();
    }

    public void setBgBitmap(Bitmap bitmap) {
        if (mPageAnim == null) {
            return;
        }
        mPageAnim.setBgBitmap(bitmap);
    }

    public Bitmap getCurBitmap() {
        if (mPageAnim == null) {
            return null;
        }
        return mPageAnim.getmCurBitmap();
    }

    public boolean autoPrevPage() {
        //滚动暂时不支持自动翻页
        if (mPageAnim instanceof ScrollPageAnim) {
            return false;
        } else {
            startPageAnim(PageAnimation.Direction.PRE);
            return true;
        }
    }

    public boolean autoNextPage() {
        if (mPageAnim instanceof ScrollPageAnim) {
            return false;
        } else {
            startPageAnim(PageAnimation.Direction.NEXT);
            return true;
        }
    }

    public boolean drawInsertView(InsertViewType viewType, Bitmap bitmap, YYInsertView insertView, YYFrame frame) {
        if (!isPrepare) {
            return false;
        }
        if (insertView == null) {
            return false;
        }
        mBitmap = bitmap;
        shouldDraw = true;
        switch (mTurnPageType){
            case TURN_PAGE_TYPE_CUR:
                if (mAdView != null) {
                    removeAdView(mAdView, mAdType);
                    mAdView.destory();
                    mAdView = null;
                    mAdType = null;
                    mFrame = null;
                }
                mAdView = insertView;
                mFrame = frame;
                mAdType = viewType;
                break;
            case TURN_PAGE_TYPE_PREV:
                if (mPrevAdView != null) {
                    removeAdView(mPrevAdView, mPrevAdType);
                    mPrevAdView.destory();
                    mPrevAdView = null;
                    mPrevAdType = null;
                    mPrevFrame = null;
                }
                mPrevAdView = insertView;
                mPrevFrame = frame;
                mPrevAdType = viewType;
                break;
            case TURN_PAGE_TYPE_NEXT:
                if (mNextAdView != null) {
                    removeAdView(mNextAdView, mNextAdType);
                    mNextAdView.destory();
                    mNextAdView = null;
                    mNextAdType = null;
                    mNextFrame = null;
                }
                mNextAdView = insertView;
                mNextFrame = frame;
                mNextAdType = viewType;
                break;
            default:
                break;
        }

        addAdLayout();
        return true;
    }


    public boolean drawInterAdWithCloseMode(Bitmap bitmap){
        if (!isPrepare) return false;
        if (mAdView == null){
            return false;
        }
        mBitmap = bitmap;

        mTurnPageType = TURN_PAGE_TYPE.TURN_PAGE_TYPE_CUR;

        addAdLayout();
        return true;
    }

    private void addVideoContainnerTemply(ViewGroup containner, View adView){
        if (containner == null || adView == null){
            return;
        }

        addView(containner);

        return;
    }

    //根据TurnPageType来Add Ad
    private void addAdLayout(){
        View tmpAdView = null;
        YYFrame tmpFrame = null;
        YYFrame tmpContentFram = null;
        InsertViewType tmpAdType = InsertViewType.NONE;
        switch (mTurnPageType){
            case TURN_PAGE_TYPE_CUR:
                tmpAdView = mAdView;
                tmpFrame = mFrame;
                tmpAdType = mAdType;
                break;
            case TURN_PAGE_TYPE_PREV:
                tmpAdView = mPrevAdView;
                tmpFrame = mPrevFrame;
                tmpAdType = mPrevAdType;
                break;
            case TURN_PAGE_TYPE_NEXT:
                tmpAdView = mNextAdView;
                tmpFrame = mNextFrame;
                tmpAdType = mNextAdType;
                break;
            default:
                break;
        }

        //无广告
        if (tmpAdView == null || tmpFrame == null){
            return;
        }

        if (tmpAdView instanceof YYInsertView) {
            tmpContentFram = ((YYInsertView) tmpAdView).getmContentFrame();
        }else {
            tmpContentFram = tmpFrame;
        }

        LayoutParams lp = new LayoutParams(tmpFrame.getWidth(),tmpFrame.getHeight());
        if (mPageAnim instanceof ScrollPageAnim) {
            lp.setMargins(tmpFrame.getX(), mViewHeight + tmpFrame.getY(), 0, 0);
        }else {
            lp.setMargins(tmpFrame.getX(), tmpFrame.getY(), 0, 0);
        }
        tmpAdView.setLayoutParams(lp);
        addView(tmpAdView);


        if (tmpAdType == InsertViewType.VIEWTYPE_FULLPAGE && tmpContentFram != null) {
            addRewordBtn(tmpContentFram);
        }
    }

    View watchVideoBtn;
    YYFrame mBtnFram;
    private float mBtnExtraWidthPercent = 0f;
    private float maxBtnWitdhPercent = 0.7f;

    private boolean needPageRemoveAdBtnAnim = false;

    protected void setNeedPageRemoveAdBtnAnim(boolean need) {
        needPageRemoveAdBtnAnim = need;
    }

    private boolean isNeedPageRemoveAdBtnAnim() {
        return needPageRemoveAdBtnAnim;
    }

    protected void setPageBtnExtraPercent(int percent) {
        mBtnExtraWidthPercent = percent / 100f;
    }

    private float getBtnWidthPercent() {
        return 1 + mBtnExtraWidthPercent;
    }

    private int getBtnMaxWidth() {
        return (int) (getWidth() * maxBtnWitdhPercent);
    }

    public void addRewordBtn(YYFrame tmpFrame){
        if (watchVideoBtn != null) {
            watchVideoBtn.getAnimation().cancel();
            watchVideoBtn.clearAnimation();
            removeView(watchVideoBtn);
            watchVideoBtn = null;
        }
        watchVideoBtn = LayoutInflater.from(getContext()).inflate(R.layout.view_removebtn, this, false);

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        watchVideoBtn.setLayoutParams(lp);
        watchVideoBtn.setMinimumHeight(ScreenUtils.dpToPx(getContext(),48));
        watchVideoBtn.setPadding(ScreenUtils.dpToPx(getContext(),10),0,ScreenUtils.dpToPx(getContext(),10),0);
        String btnContent = "看视频去广告>>";
        if (mReadDispatch != null) {
            btnContent = mReadDispatch.getAdRemoveBtnContent();
        }
        ((TextView)watchVideoBtn.findViewById(R.id.remove_text)).setText(btnContent);
        watchVideoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTouchListener.onWatchVideoBtnClick();
            }
        });
        watchVideoBtn.measure(0,0);
        int watchVideoBtnWidth = (int) (watchVideoBtn.getMeasuredWidth() * getBtnWidthPercent());
        if (watchVideoBtnWidth > getBtnMaxWidth()) {
            watchVideoBtnWidth = getBtnMaxWidth();
        }

        int watchVideoBtnHeight = watchVideoBtn.getMeasuredHeight();
        if (watchVideoBtnHeight < ScreenUtils.dpToPx(getContext(), 48)) {
            lp.height = ScreenUtils.dpToPx(getContext(), 48);
        }
        lp.width = watchVideoBtnWidth;
        Integer btnX = tmpFrame.getX() + (tmpFrame.getWidth()-watchVideoBtnWidth)/2;
        int btnFramHeight = 0;
        int adViewEndY = tmpFrame.getY() + tmpFrame.getHeight();
        if (mPageAnim instanceof ScrollPageAnim) {
            btnFramHeight = mViewHeight-mReadDispatch.getMarginHeight()*2-(tmpFrame.getY()+tmpFrame.getHeight());
            lp.setMargins(btnX,mViewHeight+(adViewEndY + (btnFramHeight-lp.height)/2),0,0);
        }else {
            btnFramHeight = getHeight() - (tmpFrame.getY() + tmpFrame.getHeight());
            lp.setMargins(btnX,adViewEndY + (btnFramHeight-lp.height)/2,0,0);
        }
        watchVideoBtn.setLayoutParams(lp);

        addView(watchVideoBtn);
        mBtnFram = new YYFrame();
        mBtnFram.setHeight(btnFramHeight);
        mBtnFram.setWidth(watchVideoBtnWidth);
        mBtnFram.setX(btnX);
        mBtnFram.setY(adViewEndY + (btnFramHeight-lp.height)/2);
        if (isNeedPageRemoveAdBtnAnim()) {
            startAnim();
        }
    }

    private void startAnim() {
        if (watchVideoBtn == null) {
            return;
        }

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.btn_anim);
        watchVideoBtn.startAnimation(anim);

    }

    /**
     * 清除添加的所有view
     */
    public void cleanAdView() {
        if (mReadDispatch != null) {
            mReadDispatch.pauseAd();
        }
        if (mPageAnim instanceof ScrollPageAnim){
            return;
        }
        if (watchVideoBtn != null) {
            if (watchVideoBtn.getAnimation()!=null) {
                watchVideoBtn.getAnimation().cancel();
            }
            watchVideoBtn.clearAnimation();
            removeView(watchVideoBtn);
            watchVideoBtn = null;
        }
        removeAllViews();
        mCanvas = null;
        mBitmap = null;
    }

    private boolean isAutoRead = false;


    public void startAutoRead() {
        isAutoRead = true;
        startPageAnim(PageAnimation.Direction.NEXT);
    }

    public void endAutoRead() {
        isAutoRead = false;
    }

    public boolean isAutoRead() {
        return isAutoRead;
    }

    private void startPageAnim(PageAnimation.Direction direction) {
        if (mTouchListener == null) return;
        //是否正在执行动画
        abortAnimation();

        if (isAutoRead) {
            int x = mViewWidth;
            int y = mViewHeight;
            //初始化动画
            mPageAnim.setStartPoint(x, y);
            //设置点击点
            mPageAnim.setTouchPoint(x, y);
//            //设置方向
//            Boolean hasNext = hasNextPage();
//
            mPageAnim.setDirection(direction);
//            if (!hasNext) {
//                return;
//            }
            mPageAnim.btnChangePage(true);
            return;
        }

        if (direction == PageAnimation.Direction.NEXT) {
            int x = mViewWidth;
            int y = mViewHeight;
            //初始化动画
            mPageAnim.setStartPoint(x, y);
            //设置点击点
            mPageAnim.setTouchPoint(x, y);
//            //设置方向
//            Boolean hasNext = hasNextPage();
//
//            mPageAnim.setDirection(direction);
//            if (!hasNext) {
//                return;
//            }
            mPageAnim.btnChangePage(true);
        } else {
            int x = 0;
            int y = mViewHeight;
//            //初始化动画
            mPageAnim.setStartPoint(x, y);
//            //设置点击点
            mPageAnim.setTouchPoint(x, y);
//            mPageAnim.setDirection(direction);
//            //设置方向方向
//            Boolean hashPrev = hasPrevPage();
//            if (!hashPrev) {
//                return;
//            }
            mPageAnim.btnChangePage(false);
        }
//        mPageAnim.startAnim();
//        this.postInvalidate();
//        if (mTurnPageListener != null){
//            mTurnPageListener.moveTurnPageFinished(true);
//        }
    }

    public void setBgColor(int color) {
        mBgColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制背景
//        canvas.drawColor(mBgColor);
//        canvas.drawBitmap(getBgBitmap(),0,0,null);
        //绘制动画
        if (mPageAnim == null || mPageAnim.getNextBitmap() == null){
            //当mPageAnim为null或者内存溢出时会导致bitmap为null，此时直接return
            mReadDispatch.readError();
            return;
        }
        snycAdViewLocation();
        mPageAnim.draw(canvas);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (mPageAnim != null && mPageAnim instanceof ScrollPageAnim) {
            return super.drawChild(canvas, child, drawingTime);
        }
        Rect rect;
        if (child instanceof YYInsertView) {
            rect = ((YYInsertView) child).getmContentFrame().getRect();
        }else {
            rect = new Rect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        }

        if (mReadDispatch != null) {
            mReadDispatch.drawBackInPage(getBgBitmap(),rect);
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mPageAnim instanceof ScrollPageAnim) {
            canvas.save();
            int scrollContentHeight = ((ScrollPageAnim) mPageAnim).getScrollViewHeight();
            canvas.clipRect(0, (mViewHeight-scrollContentHeight)/2, mViewWidth, mViewHeight-(mViewHeight-scrollContentHeight)/2, Region.Op.REPLACE);
            super.dispatchDraw(canvas);
            canvas.restore();
            return;
        }
        if (mPageAnim.isRunning()) {
            if (mBitmap != null) {
                if (mCanvas == null) {
                    mCanvas = new Canvas(mBitmap);
                }
                super.dispatchDraw(mCanvas);
            }
            return;
        }
        super.dispatchDraw(canvas);
    }

    private boolean isPointInFrame(int x, int y, YYFrame frame){
        return x>=frame.getX() && (x<=frame.getX()+frame.getWidth()) && y>=frame.getY() && (y <= frame.getY()+frame.getHeight());
    }


    private boolean touchInReloadBtn(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        YYFrame btnFrame = YYFrame.YYFrameZero();
        btnFrame = mReadDispatch.getReloadBtnFram();
        if (isPointInFrame(x,y,btnFrame)){
            return true;
        }
        return false;
    }

    private boolean touchInAdArea(MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();
        YYFrame adFrame = YYFrame.YYFrameZero();
        if (mReadDispatch.bCurPageHaveAd()){
            adFrame = mReadDispatch.getCurPageAdFrame();
        }
        if (isPointInFrame(x,y,adFrame)){
            return true;
        }
        return false;
    }

    private boolean mNeedInterceptTouch = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mNeedInterceptTouch;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            mStartX = x;
            mStartY = y;
            mMoveY = y;
            isMove = false;
            canTouch = mTouchListener.onTouch();
            mPageAnim.onTouchEvent(ev);
            if (mReadDispatch.curPageShowReloadBtn()) {
                mTouchReloadBtn = touchInReloadBtn(ev);
            }
            mNeedInterceptTouch = false;
        }
        if (mAdView != null) {
            if (mAdType == InsertViewType.VIEWTYPE_FULLPAGE && mAdView.isExtraClick()) {
                return super.dispatchTouchEvent(ev);
            }
        }
        if (ev.getAction() == MotionEvent.ACTION_MOVE && canTouch) {
            int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            if (!isMove) {
                isMove = Math.abs(mStartX - ev.getX()) > slop || Math.abs(mStartY - ev.getY()) > slop;
            }
            // 如果滑动了，则进行事件拦截。
            if (isMove) {
                mNeedInterceptTouch = true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (!canTouch && event.getAction() != MotionEvent.ACTION_DOWN) return true;

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            isMove = false;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                mStartX = x;
//                mStartY = y;
//                mMoveY = y;
//                isMove = false;
//                canTouch = mTouchListener.onTouch();
//                mPageAnim.onTouchEvent(event);
//
//                if (mReadDispatch.curPageShowReloadBtn()) {
//                    mTouchReloadBtn = touchInReloadBtn(event);
//                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchReloadBtn) {
                    Log.e("speech_action", "mTouchReloadBtn");
                    return true;
                }
                // 判断是否大于最小滑动值。
                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (!isMove) {
                    isMove = Math.abs(mStartX - event.getX()) > slop || Math.abs(mStartY - event.getY()) > slop;
                }



                // 如果滑动了，则进行翻页。
                if (isMove) {
                    if (isBookSpeeching) {
                        mTouchListener.move(event.getX(),event.getY() - mMoveY);
                        mMoveY = event.getY();
                        break;
                    }
                    mPageAnim.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchReloadBtn) {
                    boolean lastTouchInReload = touchInReloadBtn(event);
                    if (lastTouchInReload){
                        //最后离开位置还在广告区域内，响应为点击
                        mTouchListener.onReloadClick();
                    }
                    mTouchReloadBtn = false;
                    return true;
                }

                if (!isMove) {
                    //设置中间区域范围
                    if (mCenterRect == null) {
                        mCenterRect = new RectF(mViewWidth / 4, mViewHeight / 5,
                                mViewWidth * 3 / 4, mViewHeight * 4 / 5);
                    }

                    //是否点击了中间
                    if (mCenterRect.contains(x, y)) {
                        if (mTouchListener != null) {
                            mTouchListener.center();
                        }
                        return true;
                    }
                }

                if (isBookSpeeching) {
                    mTouchListener.up();
                    return true;
                }

                mPageAnim.onTouchEvent(event);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 判断是否存在上一页
     *
     * @return
     */
    private boolean hasPrevPage() {
        mTouchListener.prePage();
        shouldDraw=true;
        if (mReadDispatch == null) {
            return false;
        }
        boolean curPagehasAd = mReadDispatch.isCurPageHasAd();
        boolean hasPrev = mReadDispatch.prev();
        boolean prePageHasAd = mReadDispatch.isCurPageHasAd();
        if (mPageAnim instanceof ScrollPageAnim) {
            if (mNextAdView != null) {
                removeAdView(mNextAdView,mNextAdType);
                mNextAdView.destory();
                mNextAdView = null;
                mNextAdType = null;
                mNextFrame = null;
            }
            if (curPagehasAd && mAdView != null) {
                mNextAdView = mAdView;
                mNextFrame = mFrame;
                mNextAdType = mAdType;
            } else if (mAdView != null) {
                removeAdView(mAdView,mAdType);
                mAdView.destory();
                mAdView = null;
                mAdType = null;
                mFrame = null;
            }
            if (prePageHasAd && mPrevAdView != null) {
                mAdView = mPrevAdView;
                mAdType = mPrevAdType;
                mFrame = mPrevFrame;
                mPrevAdView = null;
                mPrevFrame = null;
                mPrevAdType = null;
            } else if (mPrevAdView != null) {
                removeAdView(mPrevAdView,mPrevAdType);
                mPrevAdView.destory();
                mPrevAdView = null;
                mPrevFrame = null;
                mPrevAdType = null;
            }
        }
        return hasPrev;
    }

    private void removeAdView(YYInsertView adview,InsertViewType type) {
        if (adview != null) {
            if (type == InsertViewType.VIEWTYPE_TAIL_PAGEAD || type == InsertViewType.VIEWTYPE_TAIL_PAGEAD) {
                removeView(watchVideoBtn);
            }
            removeView(adview);
        }
    }

    /**
     * 判断是否下一页存在
     *
     * @return
     */
    private boolean hasNextPage() {
        mTouchListener.nextPage();
        shouldDraw=true;
        if (mReadDispatch == null) {
            return false;
        }
        boolean curPagehasAd = mReadDispatch.isCurPageHasAd();
        if (mPageAnim instanceof ScrollPageAnim) {
            if (mNextAdView != null) {
                mAdView = mNextAdView;
                mFrame = mNextFrame;
                mAdType = mNextAdType;
                mNextAdView = null;
                mNextFrame = null;
                mNextAdType = null;
            }
        }
        boolean hasNext = mReadDispatch.next();
        if (hasNext) {
            boolean nextPageHasAd = mReadDispatch.isCurPageHasAd();
            if (mPageAnim instanceof ScrollPageAnim) {
                if (mPrevAdView != null) {
                    removeAdView(mPrevAdView, mPrevAdType);
                    mPrevAdView.destory();
                    mPrevAdView = null;
                    mPrevAdType = null;
                    mPrevFrame = null;
                }
                if (!curPagehasAd && mAdView != null) {
                    removeAdView(mAdView, mAdType);
                    mAdView.destory();
                    mAdView = null;
                    mAdType = null;
                    mFrame = null;
                }
                if (!nextPageHasAd && mNextAdView != null) {
                    removeAdView(mNextAdView, mNextAdType);
                    mNextAdView.destory();
                    mNextAdView = null;
                    mNextFrame = null;
                    mNextAdType = null;
                }
            }
        }
        return hasNext;
    }

    private void pageCancel() {
        mTouchListener.cancel();
        mReadDispatch.pageCancel();

        cleanAdView();

        //重置TurnPageType
        mTurnPageType = TURN_PAGE_TYPE.TURN_PAGE_TYPE_CUR;
        if (mPageAnim instanceof ScrollPageAnim){
            return;
        }
        addAdLayout();


//        if (mPageLoader.mCurPage != null && mPageLoader.mCurPage.bHaveAd()) {
//            //翻页取消的时候，如果当前页是广告页，那么就重新添加
//            addAdLayout(mIsInterAd, mFrame);
//        }else {
//            //不是广告页，则移除广告
//            cleanAdView();
//        }
    }

    private void cleanDumpedAdView() {
        if (mTurnPageType != TURN_PAGE_TYPE.TURN_PAGE_TYPE_CUR) {
            if (mAdView != null){
                mAdView.destory();
                mAdView = null;
            }

        }else {
            if (mPrevAdView != null) {
                mPrevAdView.destory();
                mPrevAdView = null;
            }
            if (mNextAdView != null) {
                mNextAdView.destory();
                mNextAdView = null;
            }
        }

    }

    private void turnPageFinished(){

        cleanDumpedAdView();

        switch (mTurnPageType){
            case TURN_PAGE_TYPE_CUR:
                //理论上不会进
                break;
            case TURN_PAGE_TYPE_PREV:
                mNextAdView = mAdView;
                mNextFrame = mFrame;
                mNextAdType = mAdType;

                mAdView = mPrevAdView;
                mFrame = mPrevFrame;
                mAdType = mPrevAdType;

                mPrevAdView = null;
                mPrevFrame = null;
                mPrevAdType = InsertViewType.NONE;
                break;
            case TURN_PAGE_TYPE_NEXT:
                mPrevAdView = mAdView;
                mPrevFrame = mFrame;
                mPrevAdType = mAdType;

                mAdView = mNextAdView;
                mFrame = mNextFrame;
                mAdType = mNextAdType;

                mNextAdView = null;
                mNextFrame = null;
                mNextAdType = InsertViewType.NONE;
                break;
            default:
                break;
        }

        mTurnPageType = TURN_PAGE_TYPE.TURN_PAGE_TYPE_CUR;
    }

    @Override
    public void computeScroll() {
        //进行滑动
        if (mPageAnim != null) {
            mPageAnim.scrollAnim();
        }
        super.computeScroll();
    }

    public void continueAutoRead() {
        if (mPageAnim instanceof AutoReadPageAnim) {
            ((AutoReadPageAnim) mPageAnim).restartAnim();
        }
    }

    public void pauseAutoRead() {
        if (mPageAnim instanceof AutoReadPageAnim) {
            ((AutoReadPageAnim) mPageAnim).pauseAnim();
        }
    }

    //如果滑动状态没有停止就取消状态，重新设置Anim的触碰点
    public void abortAnimation() {
        mPageAnim.abortAnim();
    }

    public boolean isRunning() {
        if (mPageAnim == null) {
            return false;
        }
        return mPageAnim.isRunning();
    }

    public boolean isAutoReadRunning() {
        if (mPageAnim == null) {
            return false;
        }
        return mPageAnim.isAutoReadRunning();
    }

    public boolean isPrepare() {
        return isPrepare;
    }

    public void setTouchListener(TouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
    }


    public void drawNextPage() {
        if (!isPrepare) return;

        if (mPageAnim instanceof HorizonPageAnim) {
            ((HorizonPageAnim) mPageAnim).changePage();
        }
        if (mPageAnim instanceof ScrollPageAnim) {
            if (mReadDispatch.getmStatus() != Constant.STATUS_FINISH) {
                return;
            }
        }
        mReadDispatch.drawPage(getNextBitmap(), getBgBitmap(), false);
    }

    /**
     * 绘制当前页。
     *
     * @param isUpdate
     */
    public void drawCurPage(boolean isUpdate) {
        if (!isPrepare) return;

        if (!isUpdate) {
            if (mAdView != null) {
                removeAdView(mAdView,mAdType);
                mAdView.destory();
                mAdView = null;
                mAdType = null;
                mFrame = null;
            }
            if (mPrevAdView != null) {
                removeAdView(mPrevAdView,mPrevAdType);
                mPrevAdView.destory();
                mPrevAdView = null;
                mPrevFrame = null;
                mPrevAdType = null;
            }
            if (mNextAdView != null) {
                removeAdView(mNextAdView,mNextAdType);
                mNextAdView.destory();
                mNextAdView = null;
                mNextFrame = null;
                mNextAdType = null;
            }
            if (mPageAnim instanceof ScrollPageAnim) {
                ((ScrollPageAnim) mPageAnim).resetBitmap();
            }
        }

        mReadDispatch.drawPage(getNextBitmap(), getBgBitmap(), isUpdate);
        if (mPageAnimListener != null) {
            mPageAnimListener.onScroll();
        }
    }

    private FrameLayout curView;
    private FrameLayout nextView;


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPageAnim!=null) {
            mPageAnim.abortAnim();
            mPageAnim.clear();
        }

        mPageAnim = null;
    }


    public interface TouchListener {
        boolean onTouch();

        void center();

        void prePage();

        void nextPage();

        void cancel();

        void move(float x, float y);

        void up();

        void onReloadClick();

        void onWatchVideoBtnClick();
    }


    //用于信息流广告处理
    public interface TurnPageListener{
        void moveTurnPageBegin();   //开始翻页，包括开始翻页动画
        void moveTurnPageFinished(boolean bFinished);  //true:翻页成功  false:翻页回退
        void tapTurnPage();
    }

    public YYInsertView getAdView() {
        return mAdView;
    }


    private float mMoveY = 0f;

    private boolean isBookSpeeching = false;

    public void setIsBookSpeeching(boolean isSpeeching) {
        isBookSpeeching = isSpeeching;
    }

    public boolean getIsBookSpeeching() {
        return isBookSpeeching;
    }
}

package com.pw.base.widget;

/**
 * Created by developer on 2019/11/21.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.OverScroller;
import android.widget.RelativeLayout;

import com.pw.base.R;
import com.pw.base.utils.ScreenUtils;

public class CustomScrollView extends RelativeLayout
{
    public  int OVERSCROLL_Y_DISTANCE;
    public int OVERSCROLL_X_DISTANCE;
    public int THRESHOLD_Y_DISTANCE;
    protected static final int  INVALID_POINTER_ID  = -1;

    private OverScroller        fScroller;
    // The ‘active pointer’ is the one currently moving our object.
    private int                 fTranslatePointerId = INVALID_POINTER_ID;
    private PointF              fTranslateLastTouch = new PointF( );

    private float firstX;
    private float firstY;

    private boolean supportOverScroll = false;

    private OverScrollListener mOverScrollListener;

    public interface OverScrollListener {
        public void onScrollingY(int maxOverScrollX, int overScrolledX, int maxOverScrollY, int overScrolledY, int thresholdY);

        public void onTouchEnd(int direction,boolean overThreshold);//direction=0,无位移；direction=-1，向下滑动；direction=1，向上滑动；
    }

    public void setmOverScrollListener(OverScrollListener overScrollListener) {
        mOverScrollListener = overScrollListener;
    }

    public void setSupportOverScroll(boolean support) {
        supportOverScroll = support;
    }

    public boolean isSupportOverScroll() {
        return supportOverScroll;
    }

    public CustomScrollView(Context context, AttributeSet attrs)
    {
        super( context, attrs );
        initAttrs(attrs, context);
        this.initView( context, attrs );
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super( context, attrs, defStyle );
        initAttrs(attrs, context);
        this.initView( context, attrs );
    }

    protected void initView(Context context, AttributeSet attrs)
    {
        fScroller = new OverScroller( this.getContext( ) );
        initAttrs(attrs, context);
        this.setOverScrollMode( OVER_SCROLL_ALWAYS );
    }

    private void initAttrs(AttributeSet attributeSet, Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomScrollView);
        OVERSCROLL_Y_DISTANCE = typedArray.getInt(R.styleable.CustomScrollView_maxY, 200);
        OVERSCROLL_X_DISTANCE = typedArray.getInt(R.styleable.CustomScrollView_maxX, 200);
        THRESHOLD_Y_DISTANCE = typedArray.getInt(R.styleable.CustomScrollView_validDistance, 100);

    }

    private float pointXdistance = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        int action = ev.getAction();
        switch ( action & MotionEvent.ACTION_MASK )
        {


            case MotionEvent.ACTION_MOVE:
            {
                if (!supportOverScroll) {
                    return false;
                }
                if (this.getScrollY()!=0) {
                    return true;
                }
                final float translateX = ev.getX( );
                final float translateY = ev.getY( );

                if ((firstY - translateY < -5 || firstY - translateY > 5) && (firstX - translateX < 5 && firstX - translateX > -5)&&pointXdistance<5) {
                    return true;
                } else {
                    if (Math.abs((firstX - translateX)) > pointXdistance) {
                        pointXdistance = Math.abs(firstX - translateX);
                    }
                    return false;
                }

//                //距离小于5认为是单击事件，传递给子控件
//                if((firstX - translateX < -5) || (firstX - translateX > 5) ||
//                        (firstY - translateY < -5) || (firstY - translateY > 5))
//                {
//                    return true;
//                }
//                else
//                {
//                    return false;
//                }
            }
            case MotionEvent.ACTION_DOWN:
            {
                pointXdistance = 0;
                if ( !fScroller.isFinished( ) )
                    fScroller.abortAnimation( );


                final float x = ev.getX( );
                final float y = ev.getY( );
                firstX = x;
                firstY = y;
                fTranslateLastTouch.set( x, y );

                //记录第一个手指按下时的ID
                fTranslatePointerId = ev.getPointerId( 0 );

                if (this.getScrollY()!=0) {
                    return true;
                }
                return false;
            }
            default:
            {
                if (this.getScrollY()!=0) {
                    return true;
                }
                return false;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        final int action = event.getAction( );
        switch ( action & MotionEvent.ACTION_MASK )
        {
            case MotionEvent.ACTION_DOWN:
            {
                if ( !fScroller.isFinished( ) )
                    fScroller.abortAnimation( );

                final float x = event.getX( );
                final float y = event.getY( );

                fTranslateLastTouch.set( x, y );

                //记录第一个手指按下时的ID
                fTranslatePointerId = event.getPointerId( 0 );
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                /**
                 * 取第一个触摸点的位置
                 */
                final int pointerIndexTranslate = event.findPointerIndex( fTranslatePointerId );
                if ( pointerIndexTranslate >= 0 )
                {
                    float translateX = event.getX( pointerIndexTranslate );
                    float translateY = event.getY( pointerIndexTranslate );

                    //Log.i("com.zte.allowance", "fTranslatePointerId = " + fTranslatePointerId);
                    /**
                     * deltaX 将要在X轴方向上移动距离
                     * scrollX 滚动deltaX之前，x轴方向上的偏移
                     * scrollRangeX 在X轴方向上最多能滚动的距离
                     * maxOverScrollX 在x轴方向上，滚动到边界时，还能超出的滚动距离
                     */
                    //Log.i("com.zte.allowance", "delta y = " + (fTranslateLastTouch.y - translateY));
                    this.overScrollBy(
                            (int) (fTranslateLastTouch.x - translateX),
                            (int) (fTranslateLastTouch.y - translateY)/2,
                            this.getScrollX( ),
                            this.getScrollY( ),
                            0,
                            0,
                            0,
                            OVERSCROLL_Y_DISTANCE,
                            true );

                    fTranslateLastTouch.set( translateX, translateY );
                    if (mOverScrollListener != null) {
                        mOverScrollListener.onScrollingY(OVERSCROLL_X_DISTANCE,this.getScrollX( ),OVERSCROLL_Y_DISTANCE,this.getScrollY(), THRESHOLD_Y_DISTANCE);
                    }
                    this.invalidate( );
                }

                break;
            }

            case MotionEvent.ACTION_UP:
            {
                /**
                 * startX 回滚开始时x轴上的偏移
                 * minX 和maxX 当前位置startX在minX和manX之 间时就不再回滚
                 *
                 * 此配置表示X和Y上的偏移都必须复位到0
                 */
                boolean overThreshold = false;
                int direction = 0;
                if (this.getScrollY() > 0) {
                    direction = 1;
                } else if (this.getScrollY() < 0) {
                    direction = -1;
                }else {
                    direction = 0;
                }
                if (Math.abs(this.getScrollY()) >= THRESHOLD_Y_DISTANCE) {
                    overThreshold = true;
                }
                if (overThreshold && direction>0) {
                    this.scrollTo(0,0);
                }else {
                    if (fScroller.springBack(this.getScrollX(), this.getScrollY(), 0, 0, 0, 0)) {
                        this.invalidate();
                    }
                }
                if (mOverScrollListener != null) {
                    mOverScrollListener.onTouchEnd(direction,overThreshold);
                }
                fTranslatePointerId = INVALID_POINTER_ID;
                break;
            }
        }

        return true;
    }

    @Override
    public void computeScroll()
    {
        if ( fScroller != null && fScroller.computeScrollOffset( ) )
        {
            int oldX = this.getScrollX( );
            int oldY = this.getScrollY( );

            /**
             * 根据动画开始及持续时间计算出当前时间下，view的X.Y方向上的偏移量
             * 参见OverScroller computeScrollOffset 的SCROLL_MODE
             */
            int x = fScroller.getCurrX( );
            int y = fScroller.getCurrY( );

            if ( oldX != x || oldY != y )
            {
                Log.i("bookmark_action", oldY + "  " + y);
                this.overScrollBy(
                        x - oldX,
                        (y - oldY),
                        oldX,
                        oldY,
                        0,
                        0,
                        0,
                        OVERSCROLL_Y_DISTANCE,
                        false );
            }

            this.postInvalidate( );
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY)
    {
        // Treat animating scrolls differently; see #computeScroll() for why.
        if ( !fScroller.isFinished( ) )
        {
            super.scrollTo( scrollX, scrollY );

            if ( clampedX || clampedY )
            {
                fScroller.springBack( this.getScrollX( ), this.getScrollY( ), 0, 0, 0, 0);
            }
        }
        else
        {
            super.scrollTo( scrollX, scrollY );
        }
        awakenScrollBars( );
    }

    @Override
    protected int computeHorizontalScrollExtent()
    {
        return this.getWidth( );
    }

    @Override
    protected int computeHorizontalScrollOffset()
    {
        return this.getScrollX( );
    }

    @Override
    protected int computeVerticalScrollExtent()
    {
        return this.getHeight( );
    }


    @Override
    protected int computeVerticalScrollOffset()
    {
        return this.getScrollY( );
    }



}
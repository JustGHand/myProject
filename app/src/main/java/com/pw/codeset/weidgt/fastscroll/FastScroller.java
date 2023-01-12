
package com.pw.codeset.weidgt.fastscroll;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.IntDef;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.recyclerview.widget.RecyclerView;


import com.pw.codeset.R;

import java.lang.annotation.Retention;

public class FastScroller {

    private static final int DEFAULT_AUTO_HIDE_DELAY = 2000;

    private FastScrollRecyclerView mRecyclerView;
    private int mThumbHeight;
    private int mWidth;

    private Paint mThumb;

    private Rect mTmpRect = new Rect();
    private Rect mInvalidateRect = new Rect();
    private Rect mInvalidateTmpRect = new Rect();

    private int mTouchOffset;

    public Point mThumbPosition = new Point(-1, -1);
    public Point mOffset = new Point(0, 0);
    private Bitmap bitmap;

    private boolean mIsDragging;

    private Animator mAutoHideAnimator;
    boolean mAnimatingShow;
    private int mAutoHideDelay = DEFAULT_AUTO_HIDE_DELAY;
    private boolean mAutoHideEnabled = true;
    private final Runnable mHideRunnable;
    private final Resources resources;
    private String sectionName;


    @Retention(SOURCE)
    @IntDef({FastScrollerPopupPosition.ADJACENT, FastScrollerPopupPosition.CENTER})
    public @interface FastScrollerPopupPosition {
        int ADJACENT = 0;
        int CENTER = 1;
    }

    public FastScroller(Context context, FastScrollRecyclerView recyclerView, AttributeSet attrs) {

        resources = context.getResources();
        mRecyclerView = recyclerView;
        mThumb = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.FastScrollRecyclerView, 0, 0);
        try {
            mAutoHideEnabled = typedArray.getBoolean(R.styleable.FastScrollRecyclerView_fastScrollAutoHide, true);
            mAutoHideDelay = typedArray.getInteger(R.styleable.FastScrollRecyclerView_fastScrollAutoHideDelay, DEFAULT_AUTO_HIDE_DELAY);

            bitmap= BitmapFactory.decodeResource(resources, typedArray.getResourceId(R.styleable.FastScrollRecyclerView_fastScrollThumbDrawable,0));
            mThumbHeight = bitmap.getHeight();
            mWidth = bitmap.getWidth();
        } finally {
            typedArray.recycle();
        }
        mHideRunnable = new Runnable() {
            @SuppressLint("ObjectAnimatorBinding")
            @Override
            public void run() {
                if (!mIsDragging) {
                    if (mAutoHideAnimator != null) {
                        mAutoHideAnimator.cancel();
                    }
                    mAutoHideAnimator = ObjectAnimator.ofInt(FastScroller.this, "offsetX", (Utils.isRtl(mRecyclerView.getResources()) ? -1 : 1) * mWidth);
                    mAutoHideAnimator.setInterpolator(new FastOutLinearInInterpolator());
                    mAutoHideAnimator.setDuration(200);
                    mAutoHideAnimator.start();
                }
            }
        };

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mRecyclerView.isInEditMode()) {
                    show();
                }
            }
        });

        if (mAutoHideEnabled) {
            postAutoHideDelayed();
        }
    }

    public int getThumbHeight() {
        return mThumbHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public boolean isDragging() {
        return mIsDragging;
    }

    /**
     * Handles the touch event and determines whether to show the fast scroller (or updates it if
     * it is already showing).
     */
    public void handleTouchEvent(MotionEvent ev, int downX, int downY, int lastY,
                                 OnFastScrollStateChangeListener stateChangeListener) {
        ViewConfiguration config = ViewConfiguration.get(mRecyclerView.getContext());

        int action = ev.getAction();
        int y = (int) ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isNearPoint(downX, downY)) {
                    mTouchOffset = downY - mThumbPosition.y;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // Check if we should start scrolling
                if (!mIsDragging && isNearPoint(downX, downY) &&
                        Math.abs(y - downY) > config.getScaledTouchSlop()) {
                    mRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                    mIsDragging = true;
                    mTouchOffset += (lastY - downY);
                    if (stateChangeListener != null) {
                        stateChangeListener.onFastScrollStart();
                    }
                }
                if (mIsDragging) {
                    // Update the fastscroller section name at this touch position
                    int top = 0;
                    int bottom = mRecyclerView.getHeight() - mThumbHeight;
                    float boundedY = (float) Math.max(top, Math.min(bottom, y - mTouchOffset));
                    sectionName = mRecyclerView.scrollToPositionAtProgress((boundedY - top) / (bottom - top));
                    Log.e("sectionName", sectionName);
                }
                break;
            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_CANCEL:
                mTouchOffset = 0;
                if (mIsDragging) {
                    mIsDragging = false;
                    if (stateChangeListener != null) {
                        stateChangeListener.onFastScrollStop();
                    }
                }
                break;
        }
    }

    public void draw(Canvas canvas) {
        if (mThumbPosition.x < 0 || mThumbPosition.y < 0) {
            return;
        }

        //Handle
        Rect src=new Rect(0,0,mWidth,mThumbHeight);
        Rect dsc=new Rect(mThumbPosition.x + mOffset.x, mThumbPosition.y + mOffset.y, mThumbPosition.x + mOffset.x + mWidth, mThumbPosition.y + mOffset.y + mThumbHeight);
        canvas.drawBitmap(bitmap,src,dsc,mThumb);
    }
    private boolean isNearPoint(int x, int y) {
        mTmpRect.set(mThumbPosition.x, mThumbPosition.y, mThumbPosition.x + mWidth,
                mThumbPosition.y + mThumbHeight);
        return mTmpRect.contains(x, y);
    }

    public void setThumbPosition(int x, int y) {
        if (mThumbPosition.x == x && mThumbPosition.y == y) {
            return;
        }
        // do not create new objects here, this is called quite often
        mInvalidateRect.set(mThumbPosition.x + mOffset.x, mOffset.y, mThumbPosition.x + mOffset.x + mWidth, mRecyclerView.getHeight() + mOffset.y);
        mThumbPosition.set(x, y);
        mInvalidateTmpRect.set(mThumbPosition.x + mOffset.x, mOffset.y, mThumbPosition.x + mOffset.x + mWidth, mRecyclerView.getHeight() + mOffset.y);
        mInvalidateRect.union(mInvalidateTmpRect);
        mRecyclerView.invalidate(mInvalidateRect);
    }

    @SuppressLint("ObjectAnimatorBinding")
    public void show() {
        if (!mAnimatingShow) {
            if (mAutoHideAnimator != null) {
                mAutoHideAnimator.cancel();
            }
            mAutoHideAnimator = ObjectAnimator.ofInt(this, "offsetX", 0);
            mAutoHideAnimator.setInterpolator(new LinearOutSlowInInterpolator());
            mAutoHideAnimator.setDuration(150);
            mAutoHideAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    mAnimatingShow = false;
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mAnimatingShow = false;
                }
            });
            mAnimatingShow = true;
            mAutoHideAnimator.start();
        }
        if (mAutoHideEnabled) {
            postAutoHideDelayed();
        } else {
            cancelAutoHide();
        }
    }
    protected void postAutoHideDelayed() {
        if (mRecyclerView != null) {
            cancelAutoHide();
            mRecyclerView.postDelayed(mHideRunnable, mAutoHideDelay);
        }
    }
    protected void cancelAutoHide() {
        if (mRecyclerView != null) {
            mRecyclerView.removeCallbacks(mHideRunnable);
        }
    }


    public void setAutoHideDelay(int hideDelay) {
        mAutoHideDelay = hideDelay;
        if (mAutoHideEnabled) {
            postAutoHideDelayed();
        }
    }

    public void setAutoHideEnabled(boolean autoHideEnabled) {
        mAutoHideEnabled = autoHideEnabled;
        if (autoHideEnabled) {
            postAutoHideDelayed();
        } else {
            cancelAutoHide();
        }
    }

    public void setThumbDrawable(int drawableId) {
        bitmap = BitmapFactory.decodeResource(resources,drawableId);
    }
}

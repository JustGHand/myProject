package com.pw.read.transformer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PageTurnContainer extends FrameLayout {
    public PageTurnContainer(@NonNull Context context) {
        super(context);
    }

    public PageTurnContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PageTurnContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    View curPage;
    View nextPage;
    View prevPage;

    boolean isMove;
    boolean directionToNext;
    float startX;
    float startY;
    boolean isCancel = false;

    boolean isAnimating = false;

    public View getCurPage() {
        return curPage;
    }

    public View getNextPage() {
        return nextPage;
    }

    public View getPrevPage() {
        return prevPage;
    }

    public void addPages(List<View> views) {
        if (views == null) {
            return;
        }
        for (int i = 0; i < views.size(); i++) {
            View view = views.get(i);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (curPage == null) {
                curPage = view;
            } else if (nextPage == null) {
                nextPage = view;
            } else if (prevPage == null) {
                prevPage = view;
            }
            view.setLayoutParams(layoutParams);
            addView(view);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (curPage!=null) {
            curPage.setX(0);
        }
        if (prevPage != null) {
            prevPage.setX(-getWidth());
        }
        if (nextPage != null) {
            nextPage.setX(getWidth());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isAnimating) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                isMove = false;
                directionToNext = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (!isMove) {
                    isMove = Math.abs(startX - event.getX()) > slop || Math.abs(startY - event.getY()) > slop;
                    if (isMove) {
                        directionToNext = startX > event.getX();
//                        if (directionToNext) {
//
//                        }
                    }
                }
                if (isMove) {
                    dealWithTouchMove(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    dealWithTouchUp(event);
                }
                break;
        }
//        return super.onTouchEvent(event);
        return true;
    }

    private void dealWithTouchMove(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (directionToNext) {
            getCurPage().setX(touchX - startX);
        }else {
            getPrevPage().setX(touchX - startX - getWidth());
        }
    }

    private void dealWithTouchUp(MotionEvent event) {

        autoAnim();
    }

    private void autoAnim() {
        isAnimating = true;
        float fromX = 0;
        float tarX = 0;
        View tarView = null;
        if (directionToNext) {
            tarView = getCurPage();
            float curX = tarView.getX();
            isCancel = curX < getWidth() / 2;
            fromX = tarView.getX();
            tarX = isCancel ? getWidth() : 0;
        }else {
            tarView = getPrevPage();
            float curX = getPrevPage().getX();
            isCancel = curX > getWidth() / 2;
            fromX = tarView.getX();
            tarX = isCancel ? -getWidth() : 0;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(tarView, "translationX", fromX, tarX);
        animator.setDuration(500);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                autoAnimFinish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    private void autoAnimFinish() {

        if (isMove) {
            if (directionToNext) {
                if (!isCancel) {
                    View tmpPage = prevPage;
                    prevPage = curPage;
                    curPage = nextPage;
                    nextPage = tmpPage;
                }else {
                }
            }else {
                if (!isCancel) {
                    View tmpPage = nextPage;
                    nextPage = curPage;
                    curPage = prevPage;
                    prevPage = tmpPage;
                }
            }
        }
        updatePageLoc();
        isAnimating = false;
    }

    private void updatePageLoc() {
        if (curPage!=null) {
            curPage.setX(0);
        }
        if (prevPage != null) {
            prevPage.setX(-getWidth());
        }
        if (nextPage != null) {
            nextPage.setX(getWidth());
        }
    }

}

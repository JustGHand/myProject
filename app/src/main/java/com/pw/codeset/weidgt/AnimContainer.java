package com.pw.codeset.weidgt;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pw.codeset.R;
import com.xd.baseutils.utils.ColorUtil;
import com.xd.baseutils.utils.DeviceUtils;
import com.xd.baseutils.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class AnimContainer extends FrameLayout {
    public AnimContainer(@NonNull Context context) {
        super(context);
    }

    public AnimContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean isAnimating = false;
    private float stepDistance = 10;
    ValueAnimator animator;
    private List<View> mBubbls;

    public void startAnim() {
        if (isAnimating) {
            return;
        }

        addBubble(20);

        isAnimating = true;
        animator = ValueAnimator.ofFloat(0, 1);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(500).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                for (int i = 0; i < getChildCount(); i++) {
                    View childView = getChildAt(i);
                    if (mBubbls != null && mBubbls.contains(childView)) {
                        dealWithBubble(childView,value);
                    }else {
                        dealWithNormalChild(childView, value);
                    }
                }
            }
        });
    }

    public void endAnim() {
        if (!isAnimating) {
            return;
        }
        isAnimating = false;
        if (animator!=null) {
            animator.cancel();
            animator = null;
        }
        clearBuubles();
    }

    private void dealWithNormalChild(View childView, float value) {
        Integer angle = (Integer) childView.getTag(R.id.tag_move_angle);
        if (angle == null) {
            double random = Math.random();
            angle = (int) (random * 360);
            childView.setTag(R.id.tag_move_angle, angle);
        }
        if (mBubbls.contains(childView)) {
            return;
        }
        Boolean xTarget = (Boolean) childView.getTag(R.id.tag_move_x);
        Boolean yTarget = (Boolean) childView.getTag(R.id.tag_move_y);
        if (xTarget == null) {
            double randomTarget = Math.random();
            if (randomTarget<0.5) {
                xTarget = true;
            } else {
                xTarget = false;
            }
        }
        if (yTarget == null) {
            double randomTarget = Math.random();
            if (randomTarget<0.5) {
                yTarget = true;
            } else {
                yTarget = false;
            }
        }
        double sinValue = Math.sin(Math.PI * angle / 360);
        double cosValue = Math.cos(Math.PI * angle / 360);
        double xMove = stepDistance * sinValue * (xTarget ? 1 : -1);
        double yMove = stepDistance * cosValue * (yTarget ? 1 : -1);
        if (!xMoveAble(childView, (int) (xMove), 0)) {
            xMove = -xMove;
            xTarget = !xTarget;
            if (!xMoveAble(childView, (int) xMove, 0)) {
                xMove = 0;
                xTarget = null;
            }
        }
        childView.setTag(R.id.tag_move_x, xTarget);
        int[] curLoc = getViewLoc(childView);
        if (!xMoveAble(childView, 0, (int) (yMove))) {
            yMove = -yMove;
            yTarget = !yTarget;
            if (!xMoveAble(childView, 0, (int) yMove)) {
                yMove = 0;
                yTarget = null;
            }
        }
        childView.setTag(R.id.tag_move_y, yTarget);
        if (xMove > 0 && xMove < 1) {
            xMove = 1;
        } else if (xMove < 0 && xMove > -1) {
            xMove = -1;
        }
        if (yMove > 0 && yMove < 1) {
            yMove = 1;
        }else if (yMove < 0 && yMove > -1) {
            yMove = -1;
        }
        setViewLoc(childView, curLoc[0] + (int) xMove, curLoc[1] + (int) yMove);
    }

    private void dealWithBubble(View childView , float value) {
        if (mBubbls == null || !mBubbls.contains(childView)) {
            return;
        }

        Integer angle = (Integer) childView.getTag(R.id.tag_move_angle);
        if (angle == null) {
            double random = Math.random();
            angle = (int) (random * 180);
        }
        int targetRandom = (int) (Math.random()*10);
        if (targetRandom < 1) {
            if (angle > 170) {
                angle -= 10;
            }else {
                angle += 10;
            }
        } else if (targetRandom > 8) {

            if (angle < 0) {
                angle += 10;
            }else {
                angle -= 10;
            }
        }
        childView.setTag(R.id.tag_move_angle, angle);
        double sinValue = Math.sin(Math.PI * angle / 180);
        double cosValue = Math.cos(Math.PI * angle / 180);

        double xMove = stepDistance * cosValue ;
        double yMove = - stepDistance * sinValue ;

        int[] curLoc = getViewLoc(childView);


        if (curLoc[0] + xMove > getWidth()) {
            double random = Math.random();
            angle = (int) (random * 90) + 90;
            childView.setTag(R.id.tag_move_angle, angle);
        } else if (curLoc[0] + xMove < 0) {
            double random = Math.random();
            angle = (int) (random * 90);
            childView.setTag(R.id.tag_move_angle, angle);
        }


        if (curLoc[1] + yMove < -childView.getHeight()) {
            curLoc[0] = (int) (getWidth() * Math.random());
            xMove = 0;
            curLoc[1] = getHeight();
            yMove = 0;
            if (childView instanceof ConstraintLayout) {
                View bubbleView = ((ConstraintLayout) childView).getChildAt(0);
                if (bubbleView instanceof ImageView) {
                    randomImageView((ImageView) bubbleView, true, false);
                }
            }
        }

        setViewLoc(childView, curLoc[0] + (int) xMove, (int) (curLoc[1] + yMove));


    }

    private boolean xMoveAble(View tarView, int moveX,int moveY) {
        int[] tarSize = getViewSize(tarView);
        int[] tarCenter = getViewCenter(tarView);
        float tarCenterX = tarCenter[0] + moveX;
        float tarCenterY = tarCenter[1] + moveY;
        int totalWidth = getWidth();
        int totalHeight = getHeight();
        if (tarCenterX < (tarSize[0] / 2) || tarCenterX > totalWidth-(tarSize[0] / 2)) {
            return false;
        }
        if (tarCenterY < (tarSize[1] / 2) || tarCenterY > totalHeight-(tarSize[1] / 2)) {
            return false;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView == tarView || mBubbls.contains(childView)) {
                continue;
            }
            int[] size = getViewSize(childView);
            int[] center = getViewCenter(childView);
            float centerX = center[0];
            float centerY = center[1];
            if (Math.abs(centerX - tarCenterX) < (tarSize[0] + size[0])/2 - (tarSize[0] / 10)) {
                if (Math.abs(centerY - tarCenterY) < (tarSize[1] + size[1])/2 - (tarSize[1] / 10)) {
                    return false;
                }
            }

        }
        return true;
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int nextX = 10;
        int curLineY = 10;
        int nextLineY = 10;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (mBubbls != null && mBubbls.contains(childView)) {
                continue;
            }
            int[] viewSize = getViewSize(childView);
            int tarRight = nextX + viewSize[0];
            int tarLeft = nextX;
            int tarTop = curLineY;
            if (tarRight > getWidth()) {
                nextX = 0;
                curLineY = nextLineY;
                nextLineY += viewSize[1]+20;
                tarLeft = nextX;
                tarTop = curLineY;
                nextX += viewSize[0] + 20;
            }else {
                nextX += viewSize[0] + 20;
                if (nextLineY < viewSize[1]+20) {
                    nextLineY = viewSize[1]+20;
                }
            }
            setViewLoc(childView, tarLeft, tarTop);
        }
    }

    @Override
    public void addView(View child) {
        super.addView(child);
    }


    private int[] getViewSize(View view) {
        int[] size = new int[2];
        size[0] = view.getWidth();
        size[1] = view.getHeight();
        return size;
    }

    private int[] getViewLoc(View view) {
        int[] loc = new int[2];
        loc[0] = (int) view.getX();
        loc[1] = (int) view.getY();
        return loc;
    }

    private int[] getViewCenter(View view) {
        int[] loc = new int[2];
        if (view == null) {
            return loc;
        }
        int[] viewSize = getViewSize(view);
        int[] viewLoc = getViewLoc(view);
        loc[0] = (int) (viewLoc[0] + viewSize[0] / 2);
        loc[1] = (int) (viewLoc[1] + viewSize[1] / 2);
        return loc;
    }

    private void setViewLoc(View view, int left, int top) {
        if (view == null) {
            return;
        }
        view.setX(left);
        view.setY(top);
    }


    private void addBubble(int bubbleCount) {
        if (mBubbls != null) {
            clearBuubles();
        }
        mBubbls = new ArrayList<>();
        for (int i = 0; i < bubbleCount; i++) {
            ViewGroup bubbleContainer = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.view_bubble, this, false);
            ImageView bubbleView = bubbleContainer.findViewById(R.id.item_bubble);
            mBubbls.add(bubbleContainer);
            addView(bubbleContainer);
            randomImageView(bubbleView, true, true);
            int leftCount = (int) Math.round (Math.random() * 3);
            setViewLoc(bubbleContainer, (getWidth()/3)*leftCount -10,getHeight()-10);
            bubbleContainer.setAlpha((float) Math.random());
            bubbleContainer.setTag(R.id.tag_move_angle, 90);
        }
    }

    private void randomImageView(ImageView bubbleView,boolean randomColor,boolean randomSize) {
        if (randomColor) {
            bubbleView.setColorFilter(Color.parseColor(ColorUtil.getRanDomColor()));
        }
        if (randomSize) {
            int tarSize = (int) (Math.random() * ScreenUtils.dpToPx(getContext(), 30) + ScreenUtils.dpToPx(getContext(), 10));
            ViewGroup.LayoutParams layoutParams = bubbleView.getLayoutParams();
            layoutParams.width = tarSize;
            layoutParams.height = tarSize;
            bubbleView.setLayoutParams(layoutParams);
        }
    }

    private void clearBuubles() {
        if (mBubbls != null) {
            for (int i = 0; i < mBubbls.size(); i++) {
                View bubbleView = mBubbls.get(i);
                removeView(bubbleView);
            }
            mBubbls.clear();
        }
        mBubbls = null;
    }

}

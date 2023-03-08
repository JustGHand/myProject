package com.pw.base.ad;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

public abstract class YYInsertView extends FrameLayout {

    public interface ViewListener{
        void onDestory();

        void onResume();

        void setNightMode(boolean isNight);

        void toggleExtraClick(boolean extraClickOpen);

        boolean isExtraClick();
    }

    private YYFrame mFrame;
    private YYFrame mContentFrame;

    private ViewListener mListener;
    private boolean isVideo = false;

    public YYFrame getmFrame() {
        return mFrame;
    }

    public void setmFrame(YYFrame mFrame) {
        this.mFrame = mFrame;
    }

    public YYFrame getmContentFrame() {
        return mContentFrame;
    }

    public void setmContentFrame(YYFrame mContentFrame) {
        this.mContentFrame = mContentFrame;
    }


    public ViewListener getmListener() {
        return mListener;
    }

    public void setmListener(ViewListener mListener) {
        this.mListener = mListener;
    }

    public YYInsertView(Context context) {
        super(context);
    }

    public boolean isVideo(){
        return true;
    };

    public void setIsVideo(boolean isVideo) {
        this.isVideo = isVideo;
    }

    public void destory() {
        if (mListener != null) {
            mListener.onDestory();
        }
    }

    public void setNightMode(boolean isNight) {
        if (mListener != null) {
            mListener.setNightMode(isNight);
        }
    }

    public void resume() {
        if (mListener != null) {
            mListener.onResume();
        }
    }

    public void toggleExtraClick(boolean openExtraClick) {
        if (mListener != null) {
            mListener.toggleExtraClick(openExtraClick);
        }
    }

    public boolean isExtraClick() {
        if (mListener != null) {
            return mListener.isExtraClick();
        }
        return false;
    }

}

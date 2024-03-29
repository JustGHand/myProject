package com.pw.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pw.base.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SwipStatusView extends SwipeRefreshLayout {

    public static final int STATUS_ERROR = -1;
    public static final int STATUS_COMPLETE = 1;
    public static final int STATUS_EMPTY = 2;

    @IntDef({STATUS_EMPTY, STATUS_ERROR, STATUS_COMPLETE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {}

    private int mStatus = STATUS_EMPTY;

    public SwipStatusView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public SwipStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    LinearLayout mainContainer;
    LinearLayout emptyContainer;
    LinearLayout errorContainer;

    private void initView(Context context) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_swipstatus, this, true);
        mainContainer = findViewById(R.id.swipstatus_maincontainer);
        emptyContainer = findViewById(R.id.swipstatus_empty_container);
        errorContainer = findViewById(R.id.swipstatus_error_container);
    }

    public void setEmptyView(View view) {
        emptyContainer.removeAllViews();
        if (view.getParent() != null) {
            ((ViewGroup)view.getParent()).removeView(view);
        }
        emptyContainer.addView(view);
    }

    public void setErrorView(View view) {
        errorContainer.removeAllViews();
        if (view.getParent() != null) {
            ((ViewGroup)view.getParent()).removeView(view);
        }
        errorContainer.addView(view);
    }

    public void setMainView(View view) {
        mainContainer.removeAllViews();
        if (view.getParent() != null) {
            ((ViewGroup)view.getParent()).removeView(view);
        }
        mainContainer.addView(view);
    }

    public void showMainView() {
        setRefreshing(false);
        mainContainer.setVisibility(VISIBLE);
        errorContainer.setVisibility(GONE);
        emptyContainer.setVisibility(GONE);
    }
    public void showErrorView() {
        setRefreshing(false);
        mainContainer.setVisibility(GONE);
        errorContainer.setVisibility(VISIBLE);
        emptyContainer.setVisibility(GONE);
    }
    public void showEmptyView() {
        setRefreshing(false);
        mainContainer.setVisibility(GONE);
        errorContainer.setVisibility(GONE);
        emptyContainer.setVisibility(VISIBLE);
    }

    public void setStatus(@Status int status) {
        mStatus = status;
        switch (status) {
            case STATUS_COMPLETE:
                showMainView();
                break;
            case STATUS_EMPTY:
                showEmptyView();
                break;
            case STATUS_ERROR:
                showErrorView();
                break;
            default:
                showMainView();
                break;
        }
    }

    public int getCurStatus() {
        return mStatus;
    }

}

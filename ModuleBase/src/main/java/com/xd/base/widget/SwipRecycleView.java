package com.xd.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xd.base.R;

public class SwipRecycleView extends SwipStatusView {
    public SwipRecycleView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public SwipRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public interface ScrollListener{

        void onRefresh();

        void onScrollToBottom();

        void onScrollToTop();
    }

    RecyclerView mRecyclerView;
    ScrollListener mListener;

    private void initView(Context context) {
        mRecyclerView = new RecyclerView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mRecyclerView.setLayoutParams(layoutParams);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mRecyclerView.canScrollVertically(1)) {
                    scrollToBottom();
                } else if (!mRecyclerView.canScrollVertically(-1)) {
                    scrollToTop();
                }
            }
        });
        setMainView(mRecyclerView);
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mListener != null) {
                    mListener.onRefresh();
                }else {
                    setRefreshing(false);
                }
            }
        });
    }

    public void scrollToBottom() {
        if (mListener != null) {
            mListener.onScrollToBottom();
        }
    }

    public void scrollToTop() {
        if (mListener != null) {
            mListener.onScrollToTop();
        }
    }

    public void setScrollListener(ScrollListener listener) {
        mListener = listener;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

}

package com.xd.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
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

        void onScroll(int dx,int dy);
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
                }else {
                    scroll(dx, dy);
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

    public void scroll(int dx,int dy) {
        if (mListener != null) {
            mListener.onScroll(dx, dy);
        }
    }

    public void setScrollListener(ScrollListener listener) {
        mListener = listener;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
    public void moveToPosition(int position) {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
        }
//        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
//        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
//        if (position < firstItem||position>lastItem) {
//            mRecyclerView.smoothScrollToPosition(position);
//        } else {
//            int movePosition = position - firstItem;
//            int top = mRecyclerView.getChildAt(movePosition).getTop();
//            mRecyclerView.smoothScrollBy(0, top);
//        }
    }

    public void moveToPosition(RecyclerView recyclerView, int position) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        //因为只有LinearLayoutManager 才有获得可见位置的方法
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
            int lastItem = linearLayoutManager.findLastVisibleItemPosition();
            if (position < firstItem || position > lastItem) {
                mRecyclerView.smoothScrollToPosition(position);
            } else {
                int movePosition = position - firstItem;
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        }
    }
}

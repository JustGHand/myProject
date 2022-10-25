package com.pw.codeset.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.pw.codeset.R;
import com.pw.codeset.weidgt.ActivityHeaderView;
import com.pw.codeset.weidgt.MyProgressDialog;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseFragment extends Fragment {

    View mRootView;

    private static final int HANDLER_HIDE_DIALOG = 1;
    private static final int HANDLER_FINISH_DATA = 11;

    private boolean haveAttachedToWindow = false;

    private Thread mResumeThread;

    protected CompositeDisposable mDisposable;

    ActivityHeaderView mHeader;
    boolean isHeadVisible;

    private Animation mBottomOutAnim;
    private Animation mBottomInAnim;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = LayoutInflater.from(getContext()).inflate(getContentId(), null,false);
            isCreating = true;
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isCreating) {
            initHeader(view);
            initView(view);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initHeader(View view) {
        mHeader = view.findViewById(R.id.activity_header);
        if (mHeader != null) {
            mHeader.setOnBackClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackClick(v);
                }
            });
            mHeader.setOnMenuClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMenuClick(v);
                }
            });
            isHeadVisible = mHeader.getVisibility() == View.VISIBLE;
            initAnim();
        }
    }

    private boolean isCreating = false;

    @Override
    public void onResume() {
        super.onResume();

        if (isCreating) {
//            showLoading();
            startDataThread();
//            hideLoading();
        }else {
            onNormalResume();
        }
        isCreating = false;
        Log.e("BaseActivity", "onResume");
    }

    protected void onNormalResume() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("BaseActivity", "onAttachedToWindow");
        haveAttachedToWindow = true;
        try {
            notifyAll();
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
        if (mResumeThread != null && mResumeThread.isAlive()) {
            mResumeThread.interrupt();
            mResumeThread = null;
        }
        mRootView = null;
        super.onDestroy();
    }

    protected void showLoading() {
        showProgressDialog(getResources().getString(R.string.loading),true);
    }

    protected void hideLoading() {
        hideProgressDialog();
    }

    private void startDataThread() {
        mResumeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                dealWithData();
                finishDataThread();
            }
        });
        mResumeThread.start();
    }

    private void finishDataThread() {
        if (mResumeThread == null) {
            return;
        }
        synchronized (mResumeThread) {
            while (!haveAttachedToWindow) {
                try {
                    wait();
                } catch (Exception e) {

                }
            }
        }
        if (handler != null) {
            handler.sendEmptyMessage(HANDLER_FINISH_DATA);
        }
    }


    protected abstract int getContentId();

    protected abstract void initView(View view);

    /**
     * run on new thread
     */
    protected abstract void dealWithData();

    protected void finishData() {

        Log.e("BaseActivity", "data finish");
    }

    protected void onBackClick(View view) {

    }

    protected void onMenuClick(View view) {

    }

    protected void setActTitle(String title) {
        if (mHeader != null) {
            mHeader.getmTitleTextView().setText(title);
        }
    }


    MyProgressDialog progressDialog;
    private long showDialogTime = 0;

    public void showProgressDialog(String title,boolean cancelAble) {
        if (progressDialog == null) {
            progressDialog = new MyProgressDialog(getActivity(), title, cancelAble);
        }else {
            progressDialog.setContentText(title);
            progressDialog.setCancelable(cancelAble);
            progressDialog.setCanceledOnTouchOutside(cancelAble);
        }
        if (!this.getActivity().isFinishing()) {
            showDialogTime = System.currentTimeMillis();
            progressDialog.show();
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case HANDLER_HIDE_DIALOG:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    break;
                case HANDLER_FINISH_DATA:
                    finishData();
                    break;
                default:break;
            }
            return false;
        }
    });

    public void hideProgressDialog() {
        if (System.currentTimeMillis()-showDialogTime>500) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }, 500-System.currentTimeMillis() + showDialogTime);
        }
    }


    public void addDisposable(Disposable d) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(d);
    }

    public void removeDisposable(Disposable d){
        if (mDisposable != null && d != null){
            mDisposable.remove(d);
        }
    }


    private void initAnim() {

        mBottomInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_top_in);
        mBottomOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_top_out);
        mBottomOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mHeader != null) {
                    mHeader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBottomInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mHeader != null) {
                    mHeader.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void toggleHeaderVisible() {
        if (isHeadVisible) {
            hideHeader();
        }else {
            showHeader();
        }
    }

    public void hideHeader() {
        if (mHeader != null) {
            mHeader.startAnimation(mBottomOutAnim);
            isHeadVisible = false;
        }
    }

    public void showHeader() {
        if (mHeader != null) {
            mHeader.startAnimation(mBottomInAnim);
            isHeadVisible = true;
        }
    }

}

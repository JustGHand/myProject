package com.pw.codeset.weidgt;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.pw.codeset.R;
import com.xd.baseutils.utils.NStringUtils;

public class PwImageView extends ConstraintLayout {
    public PwImageView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public PwImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initAttrs(context, attrs);
    }

    public PwImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttrs(context, attrs);
    }

    public interface PwImageListener{
        void onDelete();
    }

    private ImageView mImageView;
    private IconImageView mDeleteBtn;
    private PwImageListener mListener;

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_image, this, true);
        mImageView = findViewById(R.id.view_img_image);
        mDeleteBtn = findViewById(R.id.view_img_delete);

        mDeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDelete();
                }
            }
        });
    }

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PwImageView);

        String url = typedArray.getString(R.styleable.PwImageView_android_src);
        setUrl(url);
        boolean showDelete = typedArray.getBoolean(R.styleable.PwImageView_showDelete, false);
        showDelete(showDelete);
    }

    public void setListener(PwImageListener listener) {
        mListener = listener;
    }

    public void showDelete(boolean show) {
        mDeleteBtn.setVisibility(show ? VISIBLE : GONE);
    }

    public void setUrl(String url) {
        if (NStringUtils.isNotBlank(url)) {
            mImageView.post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(getContext()).load(url).into(mImageView);
                }
            });
        }
    }
}

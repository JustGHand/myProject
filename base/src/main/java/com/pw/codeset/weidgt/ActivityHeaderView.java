package com.pw.codeset.weidgt;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.pw.codeset.R;

public class ActivityHeaderView extends ConstraintLayout {

    private IconImageView mBackView;
    private TextView mTitleTextView;

    public ActivityHeaderView(Context context) {
        super(context);
        initView(context);
    }

    public ActivityHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initAttrs(context,attrs);
    }

    public ActivityHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttrs(context,attrs);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_acitivity_header, this, true);
        mBackView = this.findViewById(R.id.activity_header_backview);
        mTitleTextView = this.findViewById(R.id.activity_header_title);
    }

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ActivityHeaderView);
        String titleText = typedArray.getString(R.styleable.ActivityHeaderView_title_text);
        mTitleTextView.setText(titleText);
    }

}

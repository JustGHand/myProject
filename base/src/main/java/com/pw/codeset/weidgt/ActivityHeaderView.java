package com.pw.codeset.weidgt;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatViewInflater;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pw.codeset.R;
import com.xd.baseutils.others.click.DeclaredOnClickListener;
import com.xd.baseutils.utils.NStringUtils;

public class ActivityHeaderView extends ConstraintLayout {

    private IconImageView mBackView;
    private IconImageView mMenuView;
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
        if (getId() == NO_ID) {
            setId(R.id.activity_header);
        }
        LayoutInflater.from(context).inflate(R.layout.view_acitivity_header, this, true);
        mBackView = this.findViewById(R.id.activity_header_backview);
        mMenuView = this.findViewById(R.id.activity_header_menuview);
        mTitleTextView = this.findViewById(R.id.activity_header_title);
    }

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ActivityHeaderView);
        String titleText = typedArray.getString(R.styleable.ActivityHeaderView_title_text);
        mTitleTextView.setText(titleText);

        boolean showBackView = typedArray.getBoolean(R.styleable.ActivityHeaderView_showback, true);
        boolean showMenuView = typedArray.getBoolean(R.styleable.ActivityHeaderView_showmenu, false);

        setBackViewVisiable(showBackView ? VISIBLE : GONE);
        setMenuViewVisiable(showMenuView ? VISIBLE : GONE);

        String menuText = typedArray.getString(R.styleable.ActivityHeaderView_menutext);
        if (NStringUtils.isNotBlank(menuText)) {
            mMenuView.setText(menuText);
        }

        String menuClick = typedArray.getString(R.styleable.ActivityHeaderView_onMenuClick);
        if (NStringUtils.isNotBlank(menuClick)) {
            mMenuView.setOnClickListener(new DeclaredOnClickListener(mMenuView, menuClick));
        }
    }

    public void setBackViewVisiable(int visiable) {
        if (mBackView!=null) {
            mBackView.setVisibility(visiable);
        }
    }

    public void setMenuViewVisiable(int visiable) {
        if (mMenuView!=null) {
            mMenuView.setVisibility(visiable);
        }
    }

    public void setOnBackClick(OnClickListener clickListener) {
        if (mBackView != null) {
            mBackView.setOnClickListener(clickListener);
        }
    }

    public void setOnMenuClick(OnClickListener clickListener) {
        if (mMenuView != null) {
            mMenuView.setOnClickListener(clickListener);
        }
    }

}

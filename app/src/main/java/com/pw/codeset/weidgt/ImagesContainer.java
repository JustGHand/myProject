package com.pw.codeset.weidgt;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pw.codeset.R;
import com.pw.codeset.abilities.imagePreview.ImagePreviewActivity;
import com.pw.codeset.utils.Constant;
import com.pw.baseutils.utils.NStringUtils;
import com.pw.baseutils.utils.ScreenUtils;

import java.util.HashMap;
import java.util.Map;

public class ImagesContainer extends ConstraintLayout {
    public ImagesContainer(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ImagesContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initAttrs(context, attrs);
    }

    public ImagesContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttrs(context, attrs);
    }

    public interface ImagesListener{
        void onDelete(String url);

        boolean onImageClick(String url);

        void onAddClick();
    }

    private ImagesListener mListener;

    Map<PwImageView,String> mImageViews;
    private boolean editAble;
    private int columnCount;
    private float horizontal_Space;
    private float vertical_Space;
    private float imageRatio;
    private int mChildWidth;
    private int mChildHeight;
    private WarpLinearLayout mContainer;
    private boolean showDelete = false;
    private IconImageView mAddBtn;

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_images, this, true);
        mContainer = findViewById(R.id.images_container);
        mAddBtn = findViewById(R.id.images_add);

        mAddBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onAddClick();
                }
            }
        });
        showDelete = false;
    }

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ImagesContainer);

        editAble = typedArray.getBoolean(R.styleable.ImagesContainer_editAble, true);
        columnCount = typedArray.getInt(R.styleable.ImagesContainer_columnCount, 3);
        imageRatio = typedArray.getFloat(R.styleable.ImagesContainer_imageRatio, 1);
        horizontal_Space = typedArray.getDimension(R.styleable.ImagesContainer_horizonSpace, horizontal_Space);
        vertical_Space = typedArray.getDimension(R.styleable.ImagesContainer_verticalSpace, vertical_Space);

        mContainer.setHorizontal_Space(horizontal_Space);
        mContainer.setVertical_Space(vertical_Space);

    }

    public void setListener(ImagesListener listener) {
        mListener = listener;
    }

    public void addImage(String url) {

        PwImageView imageView = new PwImageView(getContext());
        imageView.setUrl(url);
        imageView.showDelete(false);
        mContainer.addView(imageView,0);
        imageView.setListener(new PwImageView.PwImageListener() {
            @Override
            public void onDelete() {
                if (mListener != null) {
                    mListener.onDelete(url);
                }
            }
        });
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean haveDealWithClick = false;
                if (mListener != null) {
                    haveDealWithClick = mListener.onImageClick( url);
                }
                if (!haveDealWithClick) {
                    toImageDetail(v);
                }
            }
        });

        imageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDelete = !showDelete;
                for (PwImageView view : mImageViews.keySet()) {
                    view.showDelete(showDelete);
                }
                return true;
            }
        });

        if (mImageViews == null) {
            mImageViews = new HashMap<>();
        }
        mImageViews.put(imageView,url);
    }

    public void removeImage(String url) {
        if (NStringUtils.isBlank(url)) {
            return;
        }
        if (mImageViews != null) {
            for (PwImageView view : mImageViews.keySet()) {
                if (view != null) {
                    if (url.equals(mImageViews.get(view))) {
                        removeImage(view);
                        return;
                    }
                }
            }
        }
    }

    private void removeImage(PwImageView view) {
        if (mFadeOutAnim == null) {
            initAnim();
        }
        mDeleteView = view;
        view.startAnimation(mFadeOutAnim);
    }

    private void toImageDetail(View view) {
        if (mImageViews == null) {
            return;
        }
        String url = mImageViews.get(view);
        if (NStringUtils.isNotBlank(url)) {
            Intent intent = new Intent(getContext(), ImagePreviewActivity.class);
            intent.putExtra(Constant.INTENT_KEY_IMAGE_PREVIEW_URL, url);
            getContext().startActivity(intent);
        }
    }


    private Animation mFadeOutAnim;
    private PwImageView mDeleteView;

    private void initAnim() {
        mFadeOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        mFadeOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        mContainer.removeView(mDeleteView);
                        mImageViews.put(mDeleteView, null);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int totalWidth = getMeasuredWidth();
        int spaceWidth = (int) ((columnCount - 1) * horizontal_Space);
        int childWidth = (totalWidth - spaceWidth)/columnCount;
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            View childView = mContainer.getChildAt(i);
            ViewGroup.LayoutParams layoutParams = childView.getLayoutParams();
            layoutParams.width = childWidth;
            layoutParams.height = (int) (childWidth*imageRatio);
            childView.setLayoutParams(layoutParams);
            if (childView == mAddBtn) {
                mAddBtn.setTextSize(ScreenUtils.pxToSp(getContext(),childWidth/3));
            }
        }
    }



}

package com.pw.codeset.weidgt;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pw.codeset.R;


public class MySeekBar extends RelativeLayout {
    public MySeekBar(@NonNull Context context) {
        super(context);
        initView();
    }

    public MySeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public MySeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView();
    }

    boolean isNightMode = false;

    LayerDrawable progressDrawable;
    Drawable thumbDrawable;
    boolean showProgessText;
    int progresssTextShift = 0;
    int progressHeight;
    int thumbSize;
    int anchorSize;
    int viewWitdh;
    int anchorProgress = -1;

    int leftImageSize ;
    int rightImageSize;
    Drawable leftDrawable;
    Drawable rightDrawable;
    Drawable leftDrawableNight;
    Drawable rightDrawableNight;

    boolean showAnchor = false;
    Drawable anchorDrawableDaymode;
    Drawable anchorDrawableNightmode;

    private void initAttrs(AttributeSet attributeSet) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.MySeekBar);
        isNightMode = typedArray.getBoolean(R.styleable.MySeekBar_seekbarNightmode, false);
        thumbDrawable = typedArray.getDrawable(R.styleable.MySeekBar_android_thumb);
        progressDrawable = (LayerDrawable) typedArray.getDrawable(R.styleable.MySeekBar_android_progressDrawable);
        progressHeight = (int) typedArray.getDimension(R.styleable.MySeekBar_progressHight,20);
        thumbSize = (int) typedArray.getDimension(R.styleable.MySeekBar_thumbSize,20);
        anchorSize = (int) typedArray.getDimension(R.styleable.MySeekBar_anchorSize,20);
        showProgessText = typedArray.getBoolean(R.styleable.MySeekBar_showProgresstext, false);
        viewWitdh = (int) typedArray.getDimension(R.styleable.MySeekBar_android_layout_width,0);

        leftDrawable = typedArray.getDrawable(R.styleable.MySeekBar_progressDrawableLeft);
        rightDrawable = typedArray.getDrawable(R.styleable.MySeekBar_progressDrawableRight);

        leftDrawableNight = typedArray.getDrawable(R.styleable.MySeekBar_progressDrawableLeftNight);
        rightDrawableNight = typedArray.getDrawable(R.styleable.MySeekBar_progressDrawableRightNight);

        leftImageSize = (int) typedArray.getDimension(R.styleable.MySeekBar_progressDrawableLeftSize,20);
        rightImageSize = (int) typedArray.getDimension(R.styleable.MySeekBar_progressDrawableRightSize,20);

        showAnchor = typedArray.getBoolean(R.styleable.MySeekBar_showAnchor, false);
        anchorDrawableDaymode = typedArray.getDrawable(R.styleable.MySeekBar_anchorDrawable);
        anchorDrawableNightmode = typedArray.getDrawable(R.styleable.MySeekBar_anchorNightDrawable);
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_my_seekbar, this, true);
        seekBar = findViewById(R.id.seek);
        backView = findViewById(R.id.seek_back);
        progressBackView = findViewById(R.id.seek_progress_back);
        thumbView = findViewById(R.id.seek_progress_thumb);
        anchorView = findViewById(R.id.seek_progress_thumb_anchor);
        progressTextView = findViewById(R.id.seek_progress_text);
        mLeftImage = findViewById(R.id.seek_left_image);
        mRightImage = findViewById(R.id.seek_right_image);
        if (showProgessText) {
            progressTextView.setVisibility(VISIBLE);
        }else {
            progressTextView.setVisibility(GONE);
        }
        anchorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onAnchorClick(anchorProgress);
                }
            }
        });

        setAnchorDrawable(anchorDrawableDaymode);
        setShowAnchor(showAnchor);
        setAnchorSize(anchorSize);

        setThumbDrawable(thumbDrawable);
        setThumbSize(thumbSize);

        setProgressDrawable(progressDrawable);
        setProgressHeight(progressHeight);

        setLeftRightDrawable();

        seekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (showAnchor) {
                        if (seekBar.getMax() <= 0) {
                            return false;
                        }
                        int maxLeft = backView.getWidth()-anchorView.getWidth();
                        int anchorLeft = maxLeft * anchorProgress / seekBar.getMax();
                        int anchorRight = anchorLeft + anchorView.getWidth();
                        float touchX = event.getX();
                        if (touchX > anchorLeft && touchX < anchorRight) {
                            anchorView.performClick();
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                syncProgressBack();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        setWillNotDraw(false);
    }

    SeekBar seekBar ;
    View backView;
    View progressBackView;
    View thumbView;
    View anchorView;
    TextView progressTextView;

    ImageView mLeftImage;
    ImageView mRightImage;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        syncProgressBack();
//        drawScal(canvas,10);
    }


    private void drawScal(Canvas canvas,int percent) {
        if (percent <= 0) {
            return;
        }
        Paint paint = new Paint();
        paint.setColor(Color.rgb(3,3,3));
        paint.setStyle(Paint.Style.FILL);
        int totalScalCount = (seekBar.getMax() / percent);
        for (int i = 1; i < totalScalCount; i++) {
            int scalStart = backView.getLeft()+((backView.getMeasuredWidth() / totalScalCount) * i);
            int scalEnd = scalStart + 3;
            int scalTop = backView.getTop() + backView.getMeasuredHeight() / 2;
            int scalBottom = backView.getBottom();
            canvas.drawRect(scalStart,scalTop,scalEnd,scalBottom,paint);
        }
    }

    private void syncProgressBack() {
        if (seekBar == null || seekBar.getMax() <= 0) {
            return;
        }
        int progress = seekBar.getProgress();
        LayoutParams layoutParams = (LayoutParams) progressBackView.getLayoutParams();
        int maxWitdh = getMeasuredWidth();
        int minWitdh = thumbSize+4>progressHeight+4?thumbSize+4:progressHeight+4;
        int curWitdhOffset = (maxWitdh-minWitdh)* progress / seekBar.getMax();
        layoutParams.width = minWitdh+curWitdhOffset;
        layoutParams.height = progressHeight;
        progressBackView.setLayoutParams(layoutParams);
        progressTextView.setText(String.valueOf(progress + progresssTextShift));
        ViewGroup.LayoutParams layoutParams3 = (LayoutParams) backView.getLayoutParams();
        layoutParams3.height = progressHeight;
        layoutParams3.width = getMeasuredWidth();
        backView.setLayoutParams(layoutParams3);

    }

    public void setProgressDrawable(LayerDrawable layerDrawable) {
        progressBackView.setBackground(layerDrawable.getDrawable(1));
        backView.setBackground(layerDrawable.getDrawable(0));
    }

    public void setAnchorProgress(int progress) {
        if (progress < 0 || progress > seekBar.getMax()||seekBar.getMax()<=0) {
            return;
        }
        anchorProgress = progress;
        LayoutParams layoutParams = (LayoutParams) anchorView.getLayoutParams();
        int maxLeft = backView.getWidth()-anchorView.getWidth();
        layoutParams.leftMargin = maxLeft * progress / seekBar.getMax();
        anchorView.setLayoutParams(layoutParams);
    }

    public void setAnchorDrawable(Drawable drawable) {
        anchorDrawableDaymode = drawable;
        anchorView.setBackground(drawable);
    }

    public void setAnchorDrawableNightmode(Drawable drawable) {
        anchorDrawableNightmode = drawable;
    }

    public void setShowAnchor(boolean showAnchor) {
        this.showAnchor = showAnchor;
        if (showAnchor) {
            anchorView.setVisibility(VISIBLE);
        }else {
            anchorView.setVisibility(GONE);
        }
    }

    public void setProgressBackView(Drawable drawable) {
        progressBackView.setBackground(drawable);
    }

    public void setBackView(Drawable drawable) {
        backView.setBackground(drawable);
    }

    public void setAnchorSize(int size) {
        LayoutParams layoutParams = (LayoutParams) anchorView.getLayoutParams();
        layoutParams.width = size;
        layoutParams.height = size;
        anchorView.setLayoutParams(layoutParams);
    }

    public void setThumbSize(int size) {
        LayoutParams layoutParams = (LayoutParams) thumbView.getLayoutParams();
        layoutParams.width = size;
        layoutParams.height = size;
        thumbView.setLayoutParams(layoutParams);
        progressTextView.setMinWidth(size);
    }

    public void setThumbDrawable(Drawable drawable) {
        thumbView.setBackground(drawable);
    }

    public void setProgressHeight(int height) {
//        LayoutParams layoutParams2 = (LayoutParams) progressBackView.getLayoutParams();
//        layoutParams2.height = height;
//        progressBackView.setLayoutParams(layoutParams2);
//        ViewGroup.LayoutParams layoutParams3 = (LayoutParams) backView.getLayoutParams();
//        layoutParams3.height = height;
//        backView.setLayoutParams(layoutParams3);
    }

    public void setMax(int max) {
        seekBar.setMax(max);
    }

    public void setProgress(int progress) {
        seekBar.setProgress(progress);
    }

    public void setProgresssTextShift(int shift) {
        progresssTextShift = shift;
    }

    private MySeekBarListener mListener;

    public void setOnSeekBarChangeListener(MySeekBarListener listener) {
        mListener = listener;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                syncProgressBack();
                if (mListener != null) {
                    mListener.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mListener != null) {
                    mListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mListener != null) {
                    mListener.onStopTrackingTouch(seekBar);
                }
            }
        });
    }

    public LayerDrawable getProgressDrawable() {
        return progressDrawable;
    }

    public Drawable getThumb() {
        return thumbDrawable;
    }

    private void setLeftRightDrawable() {
        if (leftDrawable != null) {
            mLeftImage.setVisibility(VISIBLE);
            mLeftImage.setImageDrawable(leftDrawable);
            LayoutParams layoutParams1 = (LayoutParams) mLeftImage.getLayoutParams();;
            layoutParams1.width = leftImageSize;
            layoutParams1.height = leftImageSize;
            mLeftImage.setLayoutParams(layoutParams1);
        }else {
            mLeftImage.setVisibility(GONE);
        }

        if (rightDrawable != null) {
            mRightImage.setVisibility(VISIBLE);
            mRightImage.setImageDrawable(rightDrawable);
            LayoutParams layoutParams2 = (LayoutParams) mRightImage.getLayoutParams();
            layoutParams2.width = rightImageSize;
            layoutParams2.height = rightImageSize;
            mRightImage.setLayoutParams(layoutParams2);
        }else {
            mRightImage.setVisibility(GONE);
        }
    }


    public interface MySeekBarListener{
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(SeekBar seekBar);

        void onStopTrackingTouch(SeekBar seekBar);

        void onAnchorClick(int progress);
    }

}

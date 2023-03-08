package com.pw.codeset.abilities.imagePreview;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.utils.Constant;
import com.pw.baseutils.utils.NStringUtils;

public class ImagePreviewActivity extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.activity_image_detail;
    }

    private ImageView mImageView;
    private String previewImageUrl;

    @Override
    protected void initView() {
        mImageView = findViewById(R.id.image_detail_image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void dealWithData() {
        previewImageUrl = getIntent().getStringExtra(Constant.INTENT_KEY_IMAGE_PREVIEW_URL);
    }

    @Override
    protected void finishData() {
        super.finishData();
        if (NStringUtils.isNotBlank(previewImageUrl)) {
            Glide.with(this).load(previewImageUrl).into(mImageView);
        }else {
            finish();
        }
    }
}

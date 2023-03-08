package com.pw.readcore.ReadInterface;

import com.pw.readcore.bean.YYAdView;
import com.pw.base.ad.YYFrame;
import com.pw.base.ad.YYInsertView;

public interface NativeAdListener {

    boolean isHaveNativeAd();  //查询是否有广告

    boolean isHavePageAd();//查询是否有大页广告

    YYInsertView getInterAd(YYAdView adView, YYFrame frame);
    YYInsertView getPageAd(YYAdView adView, YYFrame frame);
    void updateNativeAdSize();
    void pauseAd();
}

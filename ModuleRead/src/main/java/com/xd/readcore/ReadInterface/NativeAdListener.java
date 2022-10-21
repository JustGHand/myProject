package com.xd.readcore.ReadInterface;

import com.xd.readcore.bean.YYAdView;
import com.xd.base.ad.YYFrame;
import com.xd.base.ad.YYInsertView;

public interface NativeAdListener {

    boolean isHaveNativeAd();  //查询是否有广告

    boolean isHavePageAd();//查询是否有大页广告

    YYInsertView getInterAd(YYAdView adView, YYFrame frame);
    YYInsertView getPageAd(YYAdView adView, YYFrame frame);
    void updateNativeAdSize();
    void pauseAd();
}

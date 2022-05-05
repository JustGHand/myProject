package com.xd.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xd.base.base.RxBus;
import com.xd.base.event.NetworkChangeEvent;

/**
 * 监听网络状态变化
 * Created by Travis on 2017/10/11.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == ConnectivityManager.CONNECTIVITY_ACTION) {
            try {
                ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {
                    switch (networkInfo.getType()) {
                        case ConnectivityManager.TYPE_MOBILE:
                            RxBus.getInstance().post(new NetworkChangeEvent(NetworkChangeEvent.TYPE_MOBILE));
                            break;
                        case ConnectivityManager.TYPE_WIFI:
                            RxBus.getInstance().post(new NetworkChangeEvent(NetworkChangeEvent.TYPE_WIFI));
                            break;
                        default:
                            break;
                    }
                } else {
                    RxBus.getInstance().post(new NetworkChangeEvent(NetworkChangeEvent.TYPE_NONE));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

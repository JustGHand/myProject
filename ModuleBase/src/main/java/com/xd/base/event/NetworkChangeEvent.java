package com.xd.base.event;


public class NetworkChangeEvent {
    public static final int TYPE_NONE = -1;
    public static final int TYPE_MOBILE = 1;
    public static final int TYPE_WIFI = 2;

    public int netType;
    public NetworkChangeEvent(int netType){
        this.netType = netType;
    }
}

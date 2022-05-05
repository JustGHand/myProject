package com.xd.base.bean;

public class ConfigBean {

    boolean userModule;
    boolean recommendModule;
    boolean ttsModule;

    public boolean isUserModule() {
        return userModule;
    }

    public void setUserModule(boolean userModule) {
        this.userModule = userModule;
    }

    public boolean isRecommendModule() {
        return recommendModule;
    }

    public void setRecommendModule(boolean recommendModule) {
        this.recommendModule = recommendModule;
    }

    public boolean isTtsModule() {
        return ttsModule;
    }

    public void setTtsModule(boolean ttsModule) {
        this.ttsModule = ttsModule;
    }
}

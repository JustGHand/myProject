package com.pw.codeset.databean;

import androidx.annotation.IdRes;

public class ToolsBean {
    String toolName;
    @IdRes
    Integer iconUrl;

    public ToolsBean(String toolName, Integer iconUrl) {
        this.toolName = toolName;
        this.iconUrl = iconUrl;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public Integer getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(int iconUrl) {
        this.iconUrl = iconUrl;
    }
}

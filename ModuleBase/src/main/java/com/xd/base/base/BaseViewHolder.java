package com.pw.base.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder{

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void onSelected(boolean selected) {

    }

    public boolean dragAble() {
        return false;
    }
}
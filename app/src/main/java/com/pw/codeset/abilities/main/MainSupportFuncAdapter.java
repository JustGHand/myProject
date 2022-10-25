package com.pw.codeset.abilities.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pw.codeset.R;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.others.recycle.BaseViewHolder;
import com.xd.baseutils.utils.NStringUtils;

import java.util.List;

public class MainSupportFuncAdapter extends BaseRecyclerAdapter<String, MainSupportFuncAdapter.MainSupportFuncViewHolder> {


    public MainSupportFuncAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public ItemGenerate GenerateItem() {
        return new ItemGenerate<String,MainSupportFuncViewHolder>(){
            @Override
            public void BindItemView(String data, MainSupportFuncViewHolder holder, int postion) {
                if (holder != null && NStringUtils.isNotBlank(data)) {
                    holder.mBtn.setText(data);
                }
            }

            @Override
            public MainSupportFuncViewHolder creatItemHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_main_support_func, parent, false);
                MainSupportFuncViewHolder viewHolder = new MainSupportFuncViewHolder(itemView);
                return viewHolder;
            }

            @Override
            public boolean updateItemView(MainSupportFuncViewHolder holder, int position, List<Object> payloads) {
                return false;
            }
        };
    }

    public class MainSupportFuncViewHolder extends BaseViewHolder {
        public TextView mBtn;
        public MainSupportFuncViewHolder(@NonNull View itemView) {
            super(itemView);
            mBtn = itemView.findViewById(R.id.main_sup_func_btn);
        }
    }

}

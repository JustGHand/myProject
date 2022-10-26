package com.pw.read;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.pw.read.bean.TxtPage;

import java.util.List;

public class ReadPageAdapter extends RecyclerView.Adapter<ReadPageAdapter.ViewHolder> {

    public ReadPageAdapter(CAllAbck mCallback) {
        this.mCallback = mCallback;
    }

    public interface CAllAbck{
        TxtPage getContent(int position);

        int getPageCount();
    }

    CAllAbck mCallback ;

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setContent(mCallback.getContent(position));
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemCount() {
        return mCallback == null ? 0 : mCallback.getPageCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        PageView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_page);
        }
    }
}

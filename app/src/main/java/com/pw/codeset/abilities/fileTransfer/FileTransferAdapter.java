package com.pw.codeset.abilities.fileTransfer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pw.codeset.R;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.others.recycle.BaseViewHolder;
import com.xd.baseutils.utils.FileUtil;

import java.io.File;
import java.util.List;
import com.bumptech.glide.Glide;

public class FileTransferAdapter extends BaseRecyclerAdapter<File, FileTransferAdapter.FileTransferViewHolder> {

    private FileListener mListener;

    public FileTransferAdapter(Context mContext,FileListener listener) {
        super(mContext);
        mListener = listener;
    }

    @Override
    public ItemGenerate GenerateItem() {
        return new ItemGenerate<File,FileTransferViewHolder>() {
            @Override
            public void BindItemView(File data, FileTransferViewHolder holder, int postion) {
                if (data==null||!data.exists()) {
                    holder.itemView.setOnClickListener(null);
                    holder.itemView.setOnLongClickListener(null);
                    holder.fileNameTextView.setText("N/A");
                    holder.fileSuffixTextView.setText("N/A");
                    return;
                }else {
                    String fileName = data.getName();
                    String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
                    holder.fileNameTextView.setText(fileName);
                    holder.fileSuffixTextView.setText(fileSuffix);
                    if (FileUtil.isImageFile(data.getAbsolutePath())) {
                        holder.fileImageView.setVisibility(View.VISIBLE);
                        holder.fileSuffixTextView.setVisibility(View.GONE);
                        Glide.with(mContext).load(data.getAbsolutePath()).into(holder.fileImageView);
                    }else {
                        holder.fileImageView.setVisibility(View.GONE);
                        holder.fileSuffixTextView.setVisibility(View.VISIBLE);
                    }
                }
                setItemCLickListener(new onItemClickListener<File>() {
                    @Override
                    public void onClick(File data, int pos) {
                        if (mListener != null) {
                            mListener.onCLick(data);
                        }
                    }

                    @Override
                    public boolean onLongClick(File data, int pos) {
                        if (mListener != null) {
                            mListener.onLongClick(data);
                            return true;
                        }
                        return false;
                    }
                });
            }

            @Override
            public FileTransferViewHolder creatItemHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_file_transfer, parent, false);
                return new FileTransferViewHolder(view);
            }

            @Override
            public boolean updateItemView(FileTransferViewHolder holder, int position, List<Object> payloads) {
                return false;
            }
        };
    }


    public interface FileListener{
        void onCLick(File file);

        void onLongClick(File file);
    }

    public class FileTransferViewHolder extends BaseViewHolder{
        TextView fileSuffixTextView;
        TextView fileNameTextView;
        ImageView fileImageView;
        public FileTransferViewHolder(@NonNull View itemView) {
            super(itemView);
            fileSuffixTextView = itemView.findViewById(R.id.item_file_transfer_suffix);
            fileNameTextView = itemView.findViewById(R.id.item_file_transfer_name);
            fileImageView = itemView.findViewById(R.id.item_file_transfer_img);
        }

    }

}

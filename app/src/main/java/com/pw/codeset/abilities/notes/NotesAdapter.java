package com.pw.codeset.abilities.notes;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pw.codeset.R;
import com.pw.codeset.databean.NotesBean;
import com.pw.codeset.utils.Constant;
import com.pw.codeset.utils.ResourceUtils;
import com.pw.codeset.weidgt.IconImageView;
import com.pw.codeset.weidgt.WarpLinearLayout;
import com.pw.baseutils.others.recycle.BaseRecyclerAdapter;
import com.pw.baseutils.others.recycle.BaseViewHolder;
import com.pw.baseutils.utils.ArrayUtils;
import com.pw.baseutils.utils.NStringUtils;

import java.util.List;

public class NotesAdapter extends BaseRecyclerAdapter<NotesBean, NotesAdapter.NotesViewHolder> {


    public NotesAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public ItemGenerate GenerateItem() {
        return new ItemGenerate<NotesBean,NotesViewHolder>() {
            @Override
            public void BindItemView(NotesBean data, NotesViewHolder holder, int postion) {
                notifyView(data, holder, postion);
            }

            @Override
            public NotesViewHolder creatItemHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_notes, parent, false);
                NotesViewHolder viewHolder = new NotesViewHolder(view);
                return viewHolder;
            }

            @Override
            public boolean updateItemView(NotesViewHolder holder, int position, List payloads) {
                return false;
            }
        };
    }

    private void notifyView(NotesBean data, NotesViewHolder holder, int pos) {
        if (data != null) {
            holder.alarmIcon.setVisibility(data.getPwCalendarId() >= 0?View.VISIBLE:View.GONE);
            holder.titleText.setText(data.getTitle());
            holder.dateText.setText(NStringUtils.dateConvert(data.getDate(), Constant.DATA_PARTNER_WITH_LINE));
            holder.contentText.setText(data.getContent());
            holder.itemView.setAlpha(data.haveDone() ? 0.3f : 1f);
            List<String> labelList = data.getLabel();
            holder.labelContainer.removeAllViews();
            holder.labelContainer.setVisibility(View.GONE);
            if (ArrayUtils.isArrayEnable(labelList)) {
                for (int i = 0; i < labelList.size(); i++) {
                    String label = labelList.get(i);
                    TextView textView = new TextView(mContext);
                    textView.setText("#"+label);
                    textView.setTextColor(ResourceUtils.getResColor(R.color.normal_text_color));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
                    textView.setPadding((int)ResourceUtils.getDefaultPadding(),0,(int)ResourceUtils.getDefaultPadding(),0);
                    holder.labelContainer.addView(textView);
                }
                holder.labelContainer.setVisibility(View.VISIBLE);
            }
            holder.contentText.post(new Runnable() {
                @Override
                public void run() {
                    if (holder.contentText.getLineCount() > 3) {
                        holder.showMoreBtn.setVisibility(View.VISIBLE);
                    }else {
                        holder.showMoreBtn.setVisibility(View.GONE);
                    }
                    holder.contentText.setMaxLines(3);
                    holder.showMoreBtn.setText(mContext.getResources().getString(R.string.icon_arrow_down));
                }
            });
            holder.showMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.contentText.getMaxLines() == 3) {
                        holder.contentText.setMaxLines(Integer.MAX_VALUE);
                        holder.showMoreBtn.setText(mContext.getResources().getString(R.string.icon_arrow_up));
                    }else {
                        holder.contentText.setMaxLines(3);
                        holder.showMoreBtn.setText(mContext.getResources().getString(R.string.icon_arrow_down));
                    }
                }
            });
        }
    }

    public class NotesViewHolder extends BaseViewHolder{
        TextView titleText;
        TextView dateText;
        TextView contentText;
        TextView showMoreBtn;
        IconImageView alarmIcon;
        WarpLinearLayout labelContainer;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.item_notes_title);
            dateText = itemView.findViewById(R.id.item_notes_date);
            contentText = itemView.findViewById(R.id.item_notes_content);
            showMoreBtn = itemView.findViewById(R.id.item_notes_more_icon);
            alarmIcon = itemView.findViewById(R.id.item_notes_alarm);
            labelContainer = itemView.findViewById(R.id.item_notes_label_container);
        }
    }

}

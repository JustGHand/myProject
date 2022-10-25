package com.pw.codeset.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.view.menu.MenuBuilder;

import com.pw.codeset.R;
import com.pw.codeset.abilities.read.bookshelf.BookShelfActivity;

import java.util.List;

public class DialogUtils {

    public interface MenuClickListener{
        void onItemClick(String itemTag);
    }

    public static void showPopUpView(Activity activity, View view, List<DialogMenu> menu, MenuClickListener listener) {
        if (view == null || menu == null ) {
            return;
        }
        View contentView = LayoutInflater.from(activity).inflate(R.layout.view_popup, null);
        PopupWindow mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);

        LinearLayout container = contentView.findViewById(R.id.pop_container);

        for (int i = 0; i < menu.size(); i++) {
            DialogMenu item = menu.get(i);
            Drawable icon = activity.getResources().getDrawable(item.getDrawableId());
            String itemTag = item.getTag();
            TextView textView = new TextView(activity);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setMinWidth(activity.getResources().getDimensionPixelSize(R.dimen.clickable_minsize));
            textView.setMinHeight(activity.getResources().getDimensionPixelSize(R.dimen.clickable_minsize));
            textView.setTextColor(activity.getResources().getColor(R.color.normal_text_color));
            textView.setText(item.getTitle());
            textView.setGravity(Gravity.CENTER);
            textView.setCompoundDrawablesWithIntrinsicBounds(icon,null,null,null);
            textView.setCompoundDrawablePadding(activity.getResources().getDimensionPixelOffset(R.dimen.view_defaultpadding));
            textView.setTag(itemTag);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick((String) v.getTag());
                    }
                }
            });

            container.addView(textView);
            if (i<menu.size()-1) {
                View cutView = new View(activity);
                cutView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                cutView.setBackgroundColor(activity.getResources().getColor(R.color.border_color));
                container.addView(cutView);
            }

        }


        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        //显示PopupWindow
        mPopWindow.showAsDropDown(view);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        activity.getWindow().setAttributes(lp);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (activity!=null&&!activity.isFinishing()&&!activity.isDestroyed()) {
                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                    lp.alpha = 1;
                    activity.getWindow().setAttributes(lp);
                }
            }
        });
    }


    public static class DialogMenu{
        String title;
        @DrawableRes int drawableId;
        String tag;

        public DialogMenu(String title, int drawableId, String tag) {
            this.title = title;
            this.drawableId = drawableId;
            this.tag = tag;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getDrawableId() {
            return drawableId;
        }

        public void setDrawableId(int drawableId) {
            this.drawableId = drawableId;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

}

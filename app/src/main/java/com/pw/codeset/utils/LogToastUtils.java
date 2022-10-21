package com.pw.codeset.utils;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pw.codeset.R;
import com.pw.codeset.application.MyApp;

public class LogToastUtils {

    public static void printLog(String contents) {
        Log.d(Constant.TAG,contents);
    }

    public static void show(String msg){
        MyApp.getInstance().showToast(msg);
    }

    public static void showLargeToast(String msg) {
        //自定义Toast控件
        View toastView = LayoutInflater.from(MyApp.getInstance()).inflate(R.layout.view_toast_large, null);
//        LinearLayout relativeLayout = (LinearLayout)toastView.findViewById(R.id.toast_linear);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) ScreenUtils.dpToPx(MyApplication.getInstance(),130), (int) ScreenUtils.dpToPx(MyApplication.getInstance(),130));
//        relativeLayout.setLayoutParams(layoutParams);
        TextView textView = (TextView)toastView.findViewById(R.id.large_toast_content);
        textView.setText(msg);
        Toast toast = new Toast(MyApp.getInstance());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(toastView);
        toast.show();
    }
}

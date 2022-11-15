package com.pw.codeset.abilities.read.leadbook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pw.codeset.R;
import com.pw.codeset.manager.BookManager;
import com.pw.codeset.abilities.read.leadbook.wifitransfer.Constants;
import com.pw.codeset.abilities.read.leadbook.wifitransfer.WebService;
import com.pw.codeset.application.MyApp;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.databean.BookBean;
import com.pw.codeset.event.LeadBookEvent;
import com.xd.baseutils.utils.FileUtil;
import com.pw.codeset.utils.LogToastUtils;
import com.pw.codeset.utils.MD5Utils;
import com.pw.codeset.utils.fileParase.TxtParser;
import com.xd.baseutils.others.rxbus.RxBus;
import com.xd.baseutils.utils.DeviceUtils;
import com.xd.baseutils.utils.NStringUtils;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class LeadBookActivity extends BaseActivity {
    private static final int FILE_SELECT_REQUESTCODE = 1001;
    private static final int REUQEST_CODE_PERMISSION = 1002;
    @Override
    protected int getContentId() {
        return R.layout.activity_lead_book;
    }


    private TextView mWifiBtn;
    private TextView mLocalBtn;
    private TextView mWifiAddressTextView;

    @Override
    protected void initView() {

        mWifiBtn = findViewById(R.id.lb_wifi_btn);
        mLocalBtn = findViewById(R.id.lb_local_btn);
        mWifiAddressTextView = findViewById(R.id.lb_wifi_address);

        mWifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyIpAddress();
            }
        });

        mLocalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leadBookByLocal();
            }
        });
        mWifiAddressTextView.setText(getServerIp());
    }

    @Override
    protected void dealWithData() {
        startServer();
    }

    @Override
    protected void onDestroy() {
        WebService.stop(MyApp.getInstance());
        super.onDestroy();
    }

    private void copyIpAddress() {
        String serverIp = getServerIp();
        if (NStringUtils.isNotBlank(serverIp) && !serverIp.equals(getResources().getString(R.string.leadbook_wifi_error))) {
            NStringUtils.copyString(this,serverIp);
            LogToastUtils.show("已复制到剪贴板");
        }
    }

    public void startServer() {

        WebService.start(MyApp.getInstance());
        Disposable adStrategyDisp = RxBus.getInstance()
                .toObservable(LeadBookEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(leadBookEvent -> {
                    if (leadBookEvent.isAdd()) {
                        onFileSelected(leadBookEvent.getFilePath());
                        FileUtil.deleteFile(leadBookEvent.getFilePath());
                    }else {
                        File file = new File(leadBookEvent.getFilePath());
                        if (file.exists()) {
                            String bookId;
                            if (leadBookEvent.getFilePath().endsWith(".epub")) {
                                bookId = "local"+ MD5Utils.strToMd5By16(leadBookEvent.getFilePath().split(".epub")[0]);
                            }else {
                                bookId = "local"+ MD5Utils.strToMd5By16(leadBookEvent.getFilePath());
                            }
                            LogToastUtils.show("书籍已删除");
                        }
                    }
                });
        addDisposable(adStrategyDisp);
    }

    private String getServerIp() {
        String serverIp = null;
        String deviceIp = DeviceUtils.getWifiIpAddress(this);
        if (NStringUtils.isNotBlank(deviceIp)) {
            serverIp = deviceIp+":" + Constants.HTTP_PORT;
        }else {
            serverIp = getResources().getString(R.string.leadbook_wifi_error);
        }
        return serverIp;
    }


    public void leadBookByLocal() {
        if (isWritePermissionGranted(this)) {
            openFileManager();
        }else {
            String[] permission = new String[1];
            permission[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permission,REUQEST_CODE_PERMISSION);
            }else {
                ActivityCompat.requestPermissions(this,permission,REUQEST_CODE_PERMISSION);
            }
        }
    }

    public static boolean isWritePermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }else {
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    public static boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (hasAllPermissionsGranted(grantResults)) {
            openFileManager();
        }else {
            LogToastUtils.show("缺少必要权限，无法进行文件选择");
        }
    }

    // 打开文件管理器选择文件
    private void openFileManager() {
        // 打开文件管理器选择文件
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType(“image/*”);//选择图片
        //intent.setType(“audio/*”); //选择音频
        //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
        //intent.setType(“video/*;image/*”);//同时选择视频和图片
        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, FILE_SELECT_REQUESTCODE);
    }

    private void onFileSelected(String filePath) {

        BookBean mCurBook = BookManager.getInstance().addBook(filePath);

        if (mCurBook != null) {
            String bookFilePath = mCurBook.getBookPath();
            File bookFile = FileUtil.getFile(bookFilePath);
            if (bookFile != null && bookFile.exists()) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TxtParser.parserFile(LeadBookActivity.this, mCurBook, new TxtParser.FileLisenter(){
                            @Override
                            public void onStart() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showProgressDialog("文件内容读取中...",false);
                                    }
                                });
                            }

                            @Override
                            public void onProgress(float progress) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showProgressDialog("文件内容读取中..."+progress+"%",false);
                                    }
                                });
                            }

                            @Override
                            public void onFinish() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgressDialog();
                                        LogToastUtils.show("书籍添加成功");
                                    }
                                });
                            }
                        });

                    }
                }).start();
                return;
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == FILE_SELECT_REQUESTCODE) {
                Uri uri = data.getData();
                if (uri != null) {
                    String filePath = FileUtil.getFilePathByUri(LeadBookActivity.this, uri);
                    onFileSelected(filePath);
                }
            }
        }
    }

}

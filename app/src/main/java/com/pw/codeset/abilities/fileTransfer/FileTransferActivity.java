package com.pw.codeset.abilities.fileTransfer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.utils.SaveFileUtils;
import com.pw.codeset.utils.StringUtil;
import com.pw.codeset.weidgt.SelectDialog;
import com.pw.baseutils.utils.FileUtil;
import com.pw.baseutils.utils.NStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileTransferActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveActionSend(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        receiveActionSend(intent);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_file_transfer;
    }

    private RecyclerView mRecyclerView;
    private FileTransferAdapter mAdapter;
    private List<File> mFileList;

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.file_transfer_recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new FileTransferAdapter(this, new FileTransferAdapter.FileListener() {
            @Override
            public void onCLick(File file) {
                try {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(FileTransferActivity.this, "com.pw.codeset.fileprovider", file);
//                    Uri uri = Uri.fromFile(file);
                    String mimeType = FileUtil.getMIMEType(file.getName());
                    intent.setDataAndType(uri,mimeType);
                    startActivity(intent);
//                    Intent.createChooser(intent, "请选择对应的软件打开该附件！");
                } catch (Exception e) {
                    Toast.makeText(FileTransferActivity.this, "sorry附件不能打开，请下载相关软件！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(File file) {
                showEditDialog(file);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void dealWithData() {
        mFileList = FileUtil.getFolderFiles(SaveFileUtils.getFileTransferFolder());
    }

    @Override
    protected void finishData() {
        super.finishData();
        mAdapter.setData(mFileList);
    }

    @Override
    protected void onNormalResume() {
        super.onNormalResume();
        refreshList();
    }

    private void refreshList() {
        mFileList = FileUtil.getFolderFiles(SaveFileUtils.getFileTransferFolder());
        mAdapter.setData(mFileList);
    }

    public void receiveActionSend(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        //判断action事件
        if (type == null || (!Intent.ACTION_VIEW.equals(action) && !Intent.ACTION_SEND.equals(action))) {
            return;
        }

        //取出文件uri
        Uri uri = intent.getData();
        if (uri == null) {
            uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        }

        //获取文件真实地址
        String filePath = StringUtil.getFileFromUri(this, uri);
        if (NStringUtils.isNotBlank(filePath)) {
            return;
        }

    }


    private void showEditDialog(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        List<String> items = new ArrayList<>();
        items.add("删除");
        SelectDialog selectDialog = new SelectDialog(this, R.style.transparentFrameWindowStyle, new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        FileUtil.deleteFile(file);
                        break;
                    default:break;
                }
                refreshList();
            }
        }, items, -1);
        selectDialog.show();
    }
}

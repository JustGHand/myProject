package com.pw.codeset.activity.pc_connect;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.async.http.body.UrlEncodedFormBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.pw.codeset.R;
import com.pw.codeset.activity.wifitransfer.Constants;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.databean.ChaptersBean;
import com.pw.codeset.utils.Constant;
import com.pw.codeset.utils.FileUtil;
import com.pw.codeset.utils.fileParase.TxtParser;
import com.xd.baseutils.utils.DeviceUtils;
import com.xd.baseutils.utils.NStringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ActivityPcConnect extends BaseActivity {

    public static final String TAG = "PC_CONNECT";

    private static final String TEXT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";
    private static final String BINARY_CONTENT_TYPE = "application/octet-stream";
    private static final String JS_CONTENT_TYPE = "application/javascript";
    private static final String PNG_CONTENT_TYPE = "application/x-png";
    private static final String JPG_CONTENT_TYPE = "application/jpeg";
    private static final String SWF_CONTENT_TYPE = "application/x-shockwave-flash";
    private static final String WOFF_CONTENT_TYPE = "application/x-font-woff";
    private static final String TTF_CONTENT_TYPE = "application/x-font-truetype";
    private static final String SVG_CONTENT_TYPE = "image/svg+xml";
    private static final String EOT_CONTENT_TYPE = "image/vnd.ms-fontobject";
    private static final String MP3_CONTENT_TYPE = "audio/mp3";
    private static final String MP4_CONTENT_TYPE = "video/mpeg4";

    @Override
    protected int getContentId() {
        return R.layout.activity_pc_connect;
    }

    private TextView mServerIpTextView;
    private TextView mFileContentTextView;
    private LinearLayout mExistFileContainer;

    private AsyncHttpServer mServer;
    private AsyncServer mAsyncServer;

    File mFile;
    FileOutputStream mFileOutputStream;

    FileUploadHolder fileUploadHolder = new FileUploadHolder();

    private List<ChaptersBean> mSelectedBookChapters;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {

        mServerIpTextView = findViewById(R.id.pc_server_ip_text);
        mServerIpTextView.setText(getServerIp());

        mFileContentTextView = findViewById(R.id.pc_server_file_holder);

        mExistFileContainer = findViewById(R.id.pc_exist_files);

//        initServer();
        startServer();

//        WebService.start(this);

    }

    @Override
    protected void dealWithData() {
        refreshBookList();
    }

    private void refreshBookList() {

        List<File> files = getFiles();
        if (files != null && files.size() > 0) {
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                if (file != null && file.exists() && file.isFile()) {

                    TextView textView = new TextView(this);
                    textView.setText(file.getName());
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            showLoading();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    getFileContent(file.getAbsolutePath());
//                                    String content = FileUtil.getFileContent(file);
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            mFileContentTextView.setText(content);
//                                            hideLoading();
//                                        }
//                                    });
                                }
                            }).start();
                        }
                    });
                    mExistFileContainer.addView(textView);
                }
            }
        }
    }

    private String getServerIp() {
        String serverIp = null;
        String deviceIp = DeviceUtils.getWifiIpAddress(this);
        if (NStringUtils.isNotBlank(deviceIp)) {
            serverIp = deviceIp + Constant.PCConnectPort;
        }else {
            serverIp = getResources().getString(R.string.pc_wifi_error);
        }
        return serverIp;
    }

    private String getHtmlContent() throws IOException {
        BufferedInputStream bInputStream = null;
        try {
            bInputStream = new BufferedInputStream(getAssets().open("wifi/index.html"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] tmp = new byte[10240];
            while ((len = bInputStream.read(tmp)) > 0) {
                baos.write(tmp, 0, len);
            }
            return new String(baos.toByteArray(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }finally {
            if (bInputStream != null) {
                try {
                    bInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendResources(final AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
        try {
            String fullPath = request.getPath();
            fullPath = fullPath.replace("%20", " ");
            String resourceName = fullPath;
            if (resourceName.startsWith("/")) {
                resourceName = resourceName.substring(1);
            }
            if (resourceName.indexOf("?") > 0) {
                resourceName = resourceName.substring(0, resourceName.indexOf("?"));
            }
            if (!TextUtils.isEmpty(getContentTypeByResourceName(resourceName))) {
                response.setContentType(getContentTypeByResourceName(resourceName));
            }
            BufferedInputStream bInputStream = new BufferedInputStream(getAssets().open("wifi/" + resourceName));
            response.sendStream(bInputStream, bInputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
            response.code(404).end();
            return;
        }
    }

    private String getContentTypeByResourceName(String resourceName) {
        if (resourceName.endsWith(".css")) {
            return CSS_CONTENT_TYPE;
        } else if (resourceName.endsWith(".js")) {
            return JS_CONTENT_TYPE;
        } else if (resourceName.endsWith(".swf")) {
            return SWF_CONTENT_TYPE;
        } else if (resourceName.endsWith(".png")) {
            return PNG_CONTENT_TYPE;
        } else if (resourceName.endsWith(".jpg") || resourceName.endsWith(".jpeg")) {
            return JPG_CONTENT_TYPE;
        } else if (resourceName.endsWith(".woff")) {
            return WOFF_CONTENT_TYPE;
        } else if (resourceName.endsWith(".ttf")) {
            return TTF_CONTENT_TYPE;
        } else if (resourceName.endsWith(".svg")) {
            return SVG_CONTENT_TYPE;
        } else if (resourceName.endsWith(".eot")) {
            return EOT_CONTENT_TYPE;
        } else if (resourceName.endsWith(".mp3")) {
            return MP3_CONTENT_TYPE;
        } else if (resourceName.endsWith(".mp4")) {
            return MP4_CONTENT_TYPE;
        }
        return "";
    }

    private void startServer() {
        mServer = new AsyncHttpServer();
        mAsyncServer = new AsyncServer();
        mServer.get("/images/.*", this::sendResources);
        mServer.get("/scripts/.*", this::sendResources);
        mServer.get("/css/.*", this::sendResources);
        //index page
        mServer.get("/", (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
            try {
                response.send(getIndexContent());
            } catch (IOException e) {
                e.printStackTrace();
                response.code(500).end();
            }
        });
        //query upload list
        mServer.get("/files", (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
            JSONArray array = new JSONArray();
            List<File> existFiles = getFiles();
            if (existFiles != null && existFiles.size() > 0) {
                for (int i = 0; i < existFiles.size(); i++) {
                    File file = existFiles.get(i);
                    if (file.exists() && file.isFile()) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", file.getName());
                            long fileLen = file.length();
                            DecimalFormat df = new DecimalFormat("0.00");
                            if (fileLen > 1024 * 1024) {
                                jsonObject.put("size", df.format(fileLen * 1f / 1024 / 1024) + "MB");
                            } else if (fileLen > 1024) {
                                jsonObject.put("size", df.format(fileLen * 1f / 1024) + "KB");
                            } else {
                                jsonObject.put("size", fileLen + "B");
                            }
                            array.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            response.send(array.toString());
        });
        //delete
        mServer.post("/files/.*", (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
            final UrlEncodedFormBody body = (UrlEncodedFormBody) request.getBody();
            if ("delete".equalsIgnoreCase(body.get().getString("_method"))) {
                String path = request.getPath().replace("/files/", "");
                try {
                    path = URLDecoder.decode(path, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                File file = new File(ActivityPcConnect.this.getFilesDir().getAbsoluteFile(), path);
                if (file.exists() && file.isFile()) {
                    file.delete();
//                    RxBus.get().post(Constants.RxBusEventType.LOAD_BOOK_LIST, 0);
                }
            }
            response.end();
        });
        //download
        mServer.get("/files/.*", (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
            String path = request.getPath().replace("/files/", "");
            try {
                path = URLDecoder.decode(path, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            File file = new File(ActivityPcConnect.this.getFilesDir().getAbsoluteFile(), path);
            if (file.exists() && file.isFile()) {
                try {
                    response.getHeaders().add("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                response.sendFile(file);
                return;
            }
            response.code(404).send("Not found!");
        });
        //upload
        mServer.post("/files", (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
                    final MultipartFormDataBody body = (MultipartFormDataBody) request.getBody();
                    body.setMultipartCallback((Part part) -> {
                        if (part.isFile()) {
                            body.setDataCallback((DataEmitter emitter, ByteBufferList bb) -> {
                                fileUploadHolder.write(bb.getAllByteArray());
                                bb.recycle();
                            });
                        } else {
                            if (body.getDataCallback() == null) {
                                body.setDataCallback((DataEmitter emitter, ByteBufferList bb) -> {
                                    try {
                                        String fileName = URLDecoder.decode(new String(bb.getAllByteArray()), "UTF-8");
                                        fileUploadHolder.setFileName(fileName);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    bb.recycle();
                                });
                            }
                        }
                    });
                    request.setEndCallback((Exception e) -> {
                        refreshBookList();
                        fileUploadHolder.reset();
                        response.end();
//                        RxBus.get().post(Constants.RxBusEventType.LOAD_BOOK_LIST, 0);
                    });
                }
        );
        mServer.get("/progress/.*", (final AsyncHttpServerRequest request,
                                    final AsyncHttpServerResponse response) -> {
                    JSONObject res = new JSONObject();

                    String path = request.getPath().replace("/progress/", "");

                    if (path.equals(fileUploadHolder.fileName)) {
                        try {
                            res.put("fileName", fileUploadHolder.fileName);
                            res.put("size", fileUploadHolder.totalSize);
                            res.put("progress", fileUploadHolder.fileOutPutStream == null ? 1 : 0.1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    response.send(res);
                }

        );
        mServer.listen(mAsyncServer, Constants.HTTP_PORT);
    }

    private String getIndexContent() throws IOException {
        BufferedInputStream bInputStream = null;
        try {
            bInputStream = new BufferedInputStream(getAssets().open("wifi/index.html"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] tmp = new byte[10240];
            while ((len = bInputStream.read(tmp)) > 0) {
                baos.write(tmp, 0, len);
            }
            return new String(baos.toByteArray(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (bInputStream != null) {
                try {
                    bInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public class FileUploadHolder {
        private String fileName;
        private File recievedFile;
        private BufferedOutputStream fileOutPutStream;
        private long totalSize;


        public BufferedOutputStream getFileOutPutStream() {
            return fileOutPutStream;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
            totalSize = 0;
            this.recievedFile = new File(ActivityPcConnect.this.getFilesDir().getAbsoluteFile(), this.fileName);
//            Timber.d(recievedFile.getAbsolutePath());
            try {
                fileOutPutStream = new BufferedOutputStream(new FileOutputStream(recievedFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void reset() {
            if (fileOutPutStream != null) {
                try {
                    fileOutPutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fileOutPutStream = null;
        }

        public void write(byte[] data) {
            if (fileOutPutStream != null) {
                try {
                    fileOutPutStream.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            totalSize += data.length;
        }
    }

    private List<File> getFiles() {
        List<File> files = new ArrayList<>();
        File dir = ActivityPcConnect.this.getFilesDir().getAbsoluteFile();
        if (dir.exists() && dir.isDirectory()) {
            String[] fileNames = dir.list();
            if (fileNames != null) {
                for (String fileName : fileNames) {
                    File file = new File(dir, fileName);
                    if (file.exists() && file.isFile()) {
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }
    private void getFileContent(String filePath) {
        if (NStringUtils.isFileReadable(filePath)) {
            File file = FileUtil.getFile(filePath);
            if (file != null && file.exists()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mSelectedBookChapters = TxtParser.parserFile(ActivityPcConnect.this, null, new TxtParser.FileLisenter() {
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
                                        showBookContent();
                                        hideProgressDialog();
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

    private void showBookContent() {
        if (mSelectedBookChapters != null && mSelectedBookChapters.size() > 0) {
            ChaptersBean chaptersBean = mSelectedBookChapters.get(0);
            if (chaptersBean != null) {
                StringBuilder vModParaTextSb = new StringBuilder();
                String paragraph = chaptersBean.getTitle();//默认展示标题

                int startLength = 0;
                try {
                    BufferedReader br = TxtParser.getChapterBuffer(chaptersBean);
                    vModParaTextSb.append(paragraph + "\n");
                    startLength = vModParaTextSb.length();

                    int value = 0;
                    int charCount = 0;
                    while (((chaptersBean.start <= 0 || chaptersBean.end <= 0) || charCount < (chaptersBean.end - chaptersBean.start)) && (value = br.read()) != -1) {
                        char c = (char) value;
                        vModParaTextSb.append(c);
                        charCount++;
                    }
                    mFileContentTextView.setText(vModParaTextSb);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServer != null) {
            mServer.stop();
        }
        if (mAsyncServer != null) {
            mAsyncServer.stop();
        }
//        WebService.stop(this);
    }
}

package com.gs.updateassistant;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.gs.updateassistant.GlobalConfig.DOWNING;
import static com.gs.updateassistant.GlobalConfig.DOWN_COMPLETE;
import static com.gs.updateassistant.GlobalConfig.DOWN_ERROR;
import static com.gs.updateassistant.GlobalConfig.DOWN_START;
import static com.gs.updateassistant.GlobalConfig.hundred;
import static com.gs.updateassistant.GlobalConfig.permissionsArray;

/**
 * @author husky
 * create on 2019/2/18-11:31
 * 下载任务
 */
public class DownLoadTools {
    private DownLoadDialog downLoadDialog;
    private Activity activity;
    private String downLoadUrl;
    private File apkFile;
    private Builder builder;

    public DownLoadTools(Activity activity, String downLoadUrl) {
        this.activity = activity;
        this.downLoadUrl = downLoadUrl;
        ActivityCompat.requestPermissions(activity, permissionsArray, 1);
    }

    public DownLoadTools(Builder builder) {
        this.builder = builder;
        this.activity = builder.activity;
        this.downLoadUrl = builder.downLoadUrl;
        ActivityCompat.requestPermissions(activity, permissionsArray, 1);

    }

    private void createFile() {
        apkFile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), downLoadUrl.substring(downLoadUrl.lastIndexOf("/") + 1));
        if (apkFile != null && apkFile.exists()) {
            apkFile.delete();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWN_START:
                    if (downLoadDialog == null) {
                        downLoadDialog = new DownLoadDialog(activity);
                        downLoadDialog.setCancelable(false);
                    }
                    if (!activity.isFinishing() && !downLoadDialog.isShowing()) {
                        downLoadDialog.show();
                    }
                    break;
                case DOWNING:
                    if (null != downLoadDialog) {
                        downLoadDialog.setProgressData(msg.arg1);
                    }
                    break;
                case DOWN_ERROR:
                    Toast.makeText(activity, "下载失败", Toast.LENGTH_LONG).show();
                    break;
                case DOWN_COMPLETE:
                    if (null != downLoadDialog) {
                        downLoadDialog.dismiss();
                    }
                    InstallTools.installApk(apkFile, activity);
                    break;
            }
        }
    };


    /**
     * 弹出默认的对话框
     */
    public void showDialog() {
        CommonWithCloseDialog.Builder dialogBuilder = new CommonWithCloseDialog.Builder(builder.activity)
                .setTitle(this.builder.titleMsg)
                .setContent(this.builder.descMsg)
                .setSureString(builder.activity.getString(R.string.sure))
                .setIvPath(this.builder.updateImageUrl)
                .setmOnClick(new DoubleClick() {
                    @Override
                    public void cancel(Dialog dialog) {
                        if (null != dialog) {
                            dialog.dismiss();
                        }

                    }

                    @Override
                    public void sure(Dialog dialog) {
                        //下载更新
                        if (null != dialog) {
                            dialog.dismiss();
                        }
                        downLoad();
                    }
                });
        if (this.builder.forceUpdate) {
            //强制更新
            dialogBuilder.setShowClose(false);
        } else {
            //非强制更新
            dialogBuilder.setShowClose(true);
        }
        Dialog dialog = dialogBuilder.create();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        if (builder.activity != null && !builder.activity.isFinishing()) {
            dialog.show();
        }
    }

    /**
     * 下载文件
     */
    public void downLoad() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                createFile();
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    URL url = new URL(downLoadUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(false);
                    urlConnection.setConnectTimeout(20 * 1000);
                    urlConnection.setReadTimeout(20 * 1000);
                    urlConnection.setRequestProperty("Connection", "Keep-Alive");
                    urlConnection.setRequestProperty("Charset", "UTF-8");
                    urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                    urlConnection.connect();
                    //文件总大小
                    long bytetotal = urlConnection.getContentLength();
                    //下载的大小
                    long bytesum = 0;
                    int byteread = 0;
                    in = urlConnection.getInputStream();
                    out = new FileOutputStream(apkFile);
                    byte[] buffer = new byte[1024];
                    int oldProgress = 0;
                    Message message = Message.obtain();
                    message.arg1 = oldProgress;
                    message.what = DOWN_START;
                    handler.sendMessage(message);
                    while ((byteread = in.read(buffer)) != -1) {
                        bytesum += byteread;
                        int newProgress = new BigDecimal(bytesum).divide(new BigDecimal(bytetotal), 2, RoundingMode.HALF_UP).multiply(hundred).intValue();
                        if (newProgress > oldProgress) {
                            oldProgress = newProgress;
                        }
                        message = Message.obtain();
                        message.arg1 = oldProgress;
                        message.what = DOWNING;
                        handler.sendMessage(message);
                        out.write(buffer, 0, byteread);

                    }
                    out.flush();
                    out.close();
                    in.close();
                    message = Message.obtain();
                    message.what = DOWN_COMPLETE;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = DOWN_ERROR;
                    handler.sendMessage(message);
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    /**
     * 构造器
     */
    public static class Builder {
        /**
         * 下载地址
         */
        private String downLoadUrl;
        /**
         * 展示的内容头
         */
        private String titleMsg;
        /**
         * 更新提示文案
         */
        private String descMsg;
        /**
         * 是否强制更新
         */
        private boolean forceUpdate = false;
        /**
         * 推荐的图片地址
         */
        private String updateImageUrl;


        private Activity activity;


        public Builder(@NonNull String downLoadUrl, Activity context) {
            this.downLoadUrl = downLoadUrl;
            activity = context;
        }

        public Builder setTitleMsg(String titleMsg) {
            this.titleMsg = titleMsg;
            return this;
        }

        public Builder setDescMsg(String descMsg) {
            this.descMsg = descMsg;
            return this;
        }

        public Builder setForceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
            return this;
        }

        public Builder setUpdateImageUrl(String updateImageUrl) {
            this.updateImageUrl = updateImageUrl;
            return this;
        }

        public DownLoadTools builder() {
            return new DownLoadTools(this);
        }
    }
}

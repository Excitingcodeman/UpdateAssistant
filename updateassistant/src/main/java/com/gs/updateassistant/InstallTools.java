package com.gs.updateassistant;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

/**
 * @author husky
 * create on 2019/2/15-11:51
 * 安装的工具类
 */
public class InstallTools {

    /**
     * 安装apk  FileProvider名称为  应用包名+".FileProvider";
     *
     * @param apkFile 安装的文件
     * @param context 上下文
     */
    public static void installApk(File apkFile, Context context) {
        int sdkVersion = context.getApplicationInfo().targetSdkVersion;
        if (sdkVersion >= Build.VERSION_CODES.O) {
            checkAndroidO(context, apkFile);
        } else {
            doInstall(apkFile, context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void checkAndroidO(Context context, File apkFile) {
        boolean requestPackageInstalls = context.getPackageManager().canRequestPackageInstalls();
        Log.d("InstallTools", "是否开启---》" + requestPackageInstalls);
        if (!requestPackageInstalls) {
            //注意这个是8.0新API
            Intent intent = new Intent(context, AndroidORequestActivity.class);
            intent.putExtra("filePath", apkFile.getPath());
            context.startActivity(intent);
        } else {
            doInstall(apkFile, context);
        }
    }


    private static void doInstall(File apkFile, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", apkFile);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * @param apkFile
     * @param context
     * @param fileProvider 自定义的FileProvider
     */
    public static void installApk(File apkFile, Context context, String fileProvider) {
        int sdkVersion = context.getApplicationInfo().targetSdkVersion;
        if (sdkVersion >= Build.VERSION_CODES.O) {
            checkAndroidO(context, apkFile, fileProvider);
        } else {
            doInstall(apkFile, context, fileProvider);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void checkAndroidO(Context context, File apkFile, String fileProvider) {
        boolean requestPackageInstalls = context.getPackageManager().canRequestPackageInstalls();
        Log.d("InstallTools", "是否开启---》" + requestPackageInstalls);
        if (!requestPackageInstalls) {
            //注意这个是8.0新API
            Intent intent = new Intent(context, AndroidORequestActivity.class);
            intent.putExtra("filePath", apkFile.getPath());
            intent.putExtra("fileProvider", fileProvider);
            context.startActivity(intent);
        } else {
            doInstall(apkFile, context, fileProvider);
        }
    }

    private static void doInstall(File apkFile, Context context, String fileProvider) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, fileProvider, apkFile);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}

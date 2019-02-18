package com.gs.updateassistant;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import static com.gs.updateassistant.GlobalConfig.REAUEST_O;


/**
 * 权限申请的页面
 */
public class AndroidORequestActivity extends Activity {

    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filePath = getIntent().getStringExtra("filePath");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        filePath = getIntent().getStringExtra("filePath");

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean requestPackageInstalls = getPackageManager().canRequestPackageInstalls();
            Log.d("AndroidORequestActivity", "是否开启---》" + requestPackageInstalls);
            if (!requestPackageInstalls) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, REAUEST_O);
            } else {
                //安装apk
                InstallTools.installApk(new File(filePath), this);
                finish();
            }

        } else {
            finish();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REAUEST_O:
                //有注册权限且用户允许安装
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    InstallTools.installApk(new File(filePath), this);
                } else {
                    //将用户引导至安装未知应用界面。
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, REAUEST_O);
                }
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REAUEST_O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (getPackageManager().canRequestPackageInstalls()) {
                    //安装apk
                    InstallTools.installApk(new File(filePath), this);
                    finish();
                } else {
                    //提示
                    Toast.makeText(this, "请您开启本应用的安装权限，进行更新", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

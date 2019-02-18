package com.gs.updateassistant;

import android.Manifest;

import java.math.BigDecimal;

/**
 * @author husky
 * create on 2019/2/18-11:28
 */
public final class GlobalConfig {
    /**
     * Android 8.0 打开未知权限的安装
     */
    public static final int REAUEST_O = 2000;


    /**
     * 下载开始
     */
    public static final int DOWN_START = 1000;
    /**
     * 下载中
     */
    public static final int DOWNING = 1001;

    /**
     * 下载完成
     */
    public static final int DOWN_COMPLETE = 1002;
    /**
     * 下载失败
     */
    public static final int DOWN_ERROR = 1003;

    /**
     * 读写的权限
     */
    public static final String[] permissionsArray = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};


    public static final BigDecimal hundred = new BigDecimal(100);

}

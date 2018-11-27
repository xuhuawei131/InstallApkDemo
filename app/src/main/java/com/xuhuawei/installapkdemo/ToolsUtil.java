package com.xuhuawei.installapkdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ToolsUtil {
    private static boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

    private static String getAppDir() {

        if (hasSDCard) { // SD卡根目录的hello.text
            return Environment.getExternalStorageDirectory() + "/upgrade_apk/";
        } else {  // 系统下载缓存根目录的hello.text
            return Environment.getDownloadCacheDirectory() + "/upgrade_apk/";
        }
    }


    /**
     * @param myContext
     * @param ASSETS_NAME 要复制的文件名
     * @param destFile    要保存的路径
     */
    public static void copyAssets(Context myContext, String ASSETS_NAME, File destFile) {
        try {
            if (!destFile.exists()) {
                InputStream is = myContext.getResources().getAssets()
                        .open(ASSETS_NAME);
                FileOutputStream fos = new FileOutputStream(destFile);
                byte[] buffer = new byte[7168];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream
                        (oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    private static String mkdirs(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }

    public static String getApkDir() {
        String dir = getAppDir();
        return mkdirs(dir);
    }


    /**
     * 安装APK
     *
     * @param context
     * @param apkPath
     */
    public static void installApk(Context context, String apkPath) {
        if (context == null || TextUtils.isEmpty(apkPath)) {
            return;
        }
        File file = new File(apkPath);

        if (file.exists()) {
            Log.v("xhw", "file exit");
        } else {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);

        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".file_provider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    public static String getApplicationName() {
        return "DTMFRecognizerKey.apk";
    }
}

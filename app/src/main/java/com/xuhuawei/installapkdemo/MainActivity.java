package com.xuhuawei.installapkdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_APP_INSTALL = 1;
    private View btn_install;
    private File destFile;
    private int REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_install = findViewById(R.id.btn_install);
        btn_install.setOnClickListener(listener);
        checkPermission();
    }

    private void checkPermission() { //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            } //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {

            File dir=new File(getCacheDir() + "/upgrade_apk/");
            if (!dir.exists()){
                dir.mkdirs();
            }

            File desFile = new File(dir, ToolsUtil.getApplicationName());
            if (!desFile.exists()) {
                try {
                    desFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ToolsUtil.copyAssets(getBaseContext(),"DTMFRecognizerKey.apk",desFile);
                destFile = desFile;
            } else {
                destFile = desFile;
            }
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkIsAndroidO();
        }
    };
    private void checkIsAndroidO() {
        if (Build.VERSION.SDK_INT >= 26) {
            boolean b = getPackageManager().canRequestPackageInstalls();
            if (b) {
                ToolsUtil.installApk(MainActivity.this, destFile.getAbsolutePath());
                //安装应用的逻辑(写自己的就可以)
            } else {
                //设置安装未知应用来源的权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                startActivityForResult(intent, REQUEST_CODE_APP_INSTALL);
            }
        } else {
            ToolsUtil.installApk(MainActivity.this, destFile.getAbsolutePath());
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_APP_INSTALL:
                    checkIsAndroidO();
                    break;
            }
        }
    }
}

package com.xuhuawei.installapkdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_APP_INSTALL = 1;
    private View btn_install;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_install=findViewById(R.id.btn_install);
        btn_install.setOnClickListener(listener);
    }

    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean hasInstallPermission = isHasInstallPermissionWithO(getBaseContext());
                if (!hasInstallPermission) {
                    startInstallPermissionSettingActivity(MainActivity.this);
                    return;
                }else{

                }
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID + ".fileProvider", new File("apk地址"));
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID + ".fileProvider", new File("apk地址"));
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            }else{
                intent.setDataAndType(Uri.fromFile(new File("apk地址")), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isHasInstallPermissionWithO(Context context){
        if (context == null){
            return false;
        }
        return context.getPackageManager().canRequestPackageInstalls();
    }

    /**
     * 开启设置安装未知来源应用权限界面
     * @param context
     */
    @RequiresApi (api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity(Activity context) {
        if (context == null){
            return;
        }
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        context.startActivityForResult(intent,REQUEST_CODE_APP_INSTALL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK ){
            switch (requestCode){
                REQUEST_CODE_APP_INSTALL:
//                onSettingCheckUpdate();
            }
        }
    }
}

package com.example.servicebestpractice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by wentai on 17-5-21.
 */

public class TaskExe {

    private DownloadService.DownloadBinder downloadBinder;
    ServiceConnection connection;
    public TaskExe() {

        connection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                downloadBinder = (DownloadService.DownloadBinder) service;
            }

        };

    }

    public void begin(Context context) {
        Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent); // 启动服务
        context.bindService(intent, connection, BIND_AUTO_CREATE); // 绑定服务

        String urlTest = "http://mvideo.spriteapp.cn/video/2017/0519/591e6b5b9bd8d_wpc.mp4";
        downloadBinder.startDownload(urlTest);
    }

    public void unBind(Context context) {
        context.unbindService(connection);
    }

}

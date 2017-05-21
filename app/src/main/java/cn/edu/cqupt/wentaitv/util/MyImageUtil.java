package cn.edu.cqupt.wentaitv.util;

import android.graphics.Bitmap;
import android.os.Handler;

public class MyImageUtil {
    public interface Callback_img{
        void onResponse(Bitmap response);
    }
    public static void get(final String url, final Callback_img callback){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap response = ImageNetUtil.get(url);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }
}


package cn.edu.cqupt.wentaitv.util;

import android.os.Handler;

public class MyHttpUtil {
    public interface Callback{
        void onResponse(String response);
    }
    public static void get(final String url, final Callback callback){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = NetUtil.get(url);
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



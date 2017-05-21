package cn.edu.cqupt.wentaitv.util;

import android.graphics.Bitmap;
import android.os.Handler;

/**
 * Created by wentai on 17-5-20.
 */

public class MyFirstFrameUtil {

    public interface Callback_img{
        void onResponse(Bitmap response);
    }

    public static void get(final String url, final MyImageUtil.Callback_img callback){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap response = FirstFrame.createVideoThumbnail(url, 100, 150);
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

package cn.edu.cqupt.wentaitv.util;

import android.accounts.NetworkErrorException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageNetUtil {
    public static Bitmap get(final String url) {
        HttpURLConnection conn = null;
        Bitmap bmp = null;
        try {
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();
//            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(10000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                conn.connect();
                InputStream is = conn.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                is.close();

            } else {
                throw new NetworkErrorException("response status is "+responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                conn.disconnect();
            }
        }
        return bmp;
    }

}

package cn.edu.cqupt.wentaitv.util;

import android.accounts.NetworkErrorException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetUtil {

    public static String get(final String url) {
        HttpURLConnection conn = null;
        try {
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET");  //conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(10000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {

                InputStream is = conn.getInputStream();
                String response = getStringFromInputStream(is);
                return response;
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

        return null;
    }

    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();
        os.close();
        return state;
    }
}


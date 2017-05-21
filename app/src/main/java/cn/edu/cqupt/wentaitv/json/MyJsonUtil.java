package cn.edu.cqupt.wentaitv.json;

import com.example.mygson.JsonArray;
import com.example.mygson.JsonElement;
import com.example.mygson.JsonParser;
import com.example.mygson.MyGson;

import java.util.ArrayList;
import java.util.List;

public class MyJsonUtil {

    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        MyGson gson = new MyGson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    public static <T> List<T> getObjectList(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            MyGson gson = new MyGson();
            JsonArray arry = new JsonParser().parse(jsonString).getAsJsonArray();
            for (JsonElement jsonElement : arry) {
                list.add(gson.fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}



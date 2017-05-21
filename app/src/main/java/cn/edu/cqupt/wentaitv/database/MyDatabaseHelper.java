package cn.edu.cqupt.wentaitv.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wentai on 17-5-21.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final  String CREATE_VIDEO = "creat table Video"
            + "id integer primary key autoincrement"
            + "name text"
            + "detail text";

    private Context context;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_VIDEO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

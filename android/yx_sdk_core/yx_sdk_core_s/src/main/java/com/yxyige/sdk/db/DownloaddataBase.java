package com.yxyige.sdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DownloaddataBase extends SQLiteOpenHelper {

    public static String TABLE = "yxdownload";
    public static String DATABASE = "yxdownload.db";
    private static int version = 1;
    private static DownloaddataBase base;

    private DownloaddataBase(Context context, String name) {
        super(context, name, null, version);
    }

    public DownloaddataBase(Context context, String name,
                            CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static DownloaddataBase getDownloaddataBase(Context context,
                                                       String name) {
        if (base == null) {
            base = new DownloaddataBase(context, name);
        }
        return base;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE
                + " ( _id integer primary key , " + " url varchar , "
                + "downloading varchar , " + "filename varchar , "
                + "path varchar , " + "id varchar ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }


}

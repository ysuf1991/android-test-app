package com.examples.android.showcategories.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ServiceDBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "services";
    public static final String KEY_ID = "_id";
    public static final String SERVICE_ID = "service_id";
    public static final String PARENT_ID = "parent_id";
    public static final String SERVICE_TITLE = "service_title";

    private static final String DATABASE_NAME = "catalogBD.db";
    private static final int DATABASE_Version = 1;

    private static final String CREATE_SERVICES_TABLE = "create table "
            + TABLE_NAME + "("
            + KEY_ID + " integer primary key autoincrement, "
            + SERVICE_ID + " integer, "
            + PARENT_ID + " integer not null, "
            + SERVICE_TITLE + " text);";

    public ServiceDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SERVICES_TABLE);
        db.execSQL("insert into " + TABLE_NAME + " (" + PARENT_ID + ") values (0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
}

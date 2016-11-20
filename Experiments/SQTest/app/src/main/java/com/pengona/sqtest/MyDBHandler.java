package com.pengona.sqtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mehmetatmaca on 2016-11-19.
 */

public class MyDBHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="databaseName";
    public static final String TABLE_NAME="tableName";

    public static final String COLUMN_ID="_id";
    public static final String COLUMN_DATE="_date";
    public static final String COLUMN_COL1="_col1";




    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME + "("+COLUMN_ID+" INTEGER PRIMARY KEY,"+ COLUMN_DATE+" TEXT,"+COLUMN_COL1+" TEXT"+")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }
    public void addData(CSVData csvdata){

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, csvdata.get_date());
        values.put(COLUMN_COL1, csvdata.get_col1());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();

    }
}

package com.loaneasy.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotifDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MsgStore";
    public static final String TABLE_NAME_1 = "Messages";
    public static final String TABLE_1_COL_1 = "sno";
    public static final String TABLE_1_COL_2 = "msg";
    SQLiteDatabase sdb;

    public NotifDatabase(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL("create table "
                + TABLE_NAME_1 + "(" + TABLE_1_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TABLE_1_COL_2 + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sdb, int i, int i1) {
        sdb.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_1);
        onCreate(sdb);
    }

    public boolean insertData(String msg) {
        sdb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_1_COL_2,msg);
        long result = sdb.insert(TABLE_NAME_1,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        sdb = this.getWritableDatabase();
        Cursor res = sdb.rawQuery("select * from "+TABLE_NAME_1,null);
        return res;
    }

    public void closedb(){sdb.close();}

    public void deleteRow(String value) {
        sdb = this.getWritableDatabase();
        sdb.execSQL("DELETE FROM " + TABLE_NAME_1+ " WHERE "+TABLE_1_COL_2+"='"+value+"'");
        sdb.close();
    }

    public void clearTable() {
        sdb = this.getWritableDatabase();
        sdb.execSQL("delete from "+ TABLE_NAME_1);
        sdb.close();
    }

    public boolean tableIsEmpty(){
        sdb = this.getWritableDatabase();
        Boolean isEmpty = true;
        Cursor mCursor = sdb.rawQuery("SELECT "+ TABLE_1_COL_1 +" FROM "+ TABLE_NAME_1, null);
        mCursor.moveToFirst();

        if(mCursor.getCount()>0){
            isEmpty = false;
        }
        return isEmpty;
    }


}
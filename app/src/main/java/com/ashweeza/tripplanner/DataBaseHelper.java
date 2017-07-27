package com.ashweeza.tripplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashweeza on 2/8/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "trips.db";
    public static final String TABLE_NAME = "tripdata_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "DAYNUM";
    public static final String COL_3 = "DAYTIME";
    public static final String COL_4 = "CITY";
    public static final String COL_5 = "PLACEONE";
    public static final String COL_6 = "PLACETWO";
    public static final String COL_7 = "PLACETHREE";
    public static final String COL_8 = "PLACEFOUR";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,DAYNUM INTEGER,DAYTIME TEXT,CITY TEXT,PLACEONE TEXT,PLACETWO TEXT,PLACETHREE TEXT,PLACEFOUR TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void createTable(String tablename) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table IF NOT EXISTS " + tablename + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,DAYNUM INTEGER,DAYTIME TEXT,CITY TEXT,PLACEONE TEXT,PLACETWO TEXT,PLACETHREE TEXT,PLACEFOUR TEXT)");
    }

    public boolean insertData(String tablename, String daynum, String daytime, String city, String placeone, String placetwo, String placethree, String placefour) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, daynum);
        contentValues.put(COL_3, daytime);
        contentValues.put(COL_4, city);
        contentValues.put(COL_5, placeone);
        contentValues.put(COL_6, placetwo);
        contentValues.put(COL_7, placethree);
        contentValues.put(COL_8, placefour);
        long result = db.insert(tablename, null, contentValues);
        if (result == -1) {
            return false;
        } else
            return true;
    }

    public Cursor getAllData(String tablename) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + tablename + ";", null);
        Log.v("tag", "Query is:" + "select * from " + tablename + ";");
        return res;
    }

    public Cursor getData(String tablename, int day) {

        SQLiteDatabase db = this.getWritableDatabase();
        int dayid = day;
        String query = "select * from " + tablename + " WHERE DAYNUM ==" + dayid + ";";
        Log.v("tag", "Query is:" + query);
        Cursor data = db.rawQuery(query, null);
        if (data != null) {
            data.moveToFirst();
        }
        return data;
    }

    public boolean updateData(String tablename, String id, String daynum, String daytime, String city, String placeone, String placetwo, String placethree, String placefour) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, daynum);
        contentValues.put(COL_3, daytime);
        contentValues.put(COL_4, city);
        contentValues.put(COL_5, placeone);
        contentValues.put(COL_6, placetwo);
        contentValues.put(COL_7, placethree);
        contentValues.put(COL_8, placefour);
        db.update(tablename, contentValues, "ID = ?", new String[]{id});
        return true;
    }

    public Integer deleteData(String tablename, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + tablename + "'");
        return db.delete(tablename, "ID = ?", new String[]{id});
    }

    public void deleteAll(String tablename) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tablename, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + tablename + "'");
    }

    public void deleteTable(String tablename) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tablename, null, null);
        db.execSQL("DROP TABLE IF EXISTS " + tablename);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + tablename + "'");
    }

    public ArrayList<String> getAllTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> arrTblNames = new ArrayList<String>();
        // Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name!='android_metadata' AND name!='sqlite_sequence'", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {

            arrTblNames.add(c.getString(c.getColumnIndex("name")));
            c.moveToNext();
        }
        // make sure to close the cursor
        c.close();
        return arrTblNames;
    }

    public void deleteAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> tables = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String tableName = cursor.getString(1);
            if (!tableName.equals("android_metadata") &&
                    !tableName.equals("sqlite_sequence"))
                tables.add(tableName);
            cursor.moveToNext();
        }
        cursor.close();

        for (String tableName : tables) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
    }

    public void createTableofTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table IF NOT EXISTS tableoftables " + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,TRIPNAME TEXT,DAYNUM INTEGER)");
    }

    public int deletefromtablesoftables(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'tableoftables'");
        return db.delete("tableoftables", "TRIPNAME = ?", new String[]{name});
    }

    public void droptableoftables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists tableoftables");
    }

    public boolean insertDataintotableoftables(String tablename, String tripname, int daynum) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("TRIPNAME", tripname);
        contentValues.put("DAYNUM", daynum);
        long result = db.insert(tablename, null, contentValues);
        if (result == -1) {
            return false;
        } else
            return true;
    }
}

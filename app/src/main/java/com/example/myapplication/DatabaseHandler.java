package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.model.History;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "historyManager";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "history";
    public static final String KEY_TIME = "time", KEY_NAME = "name", KEY_TYPE = "type", KEY_ACTION = "action", KEY_ID = "id";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_history_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT)", TABLE_NAME, KEY_ID, KEY_NAME, KEY_TYPE, KEY_ACTION, KEY_TIME);
        db.execSQL(create_history_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String drop_history_table = String.format("DROP TABLE IF EXIST %s", TABLE_NAME);
        db.execSQL(drop_history_table);
        onCreate(db);
    }

    public void addHistory(History history) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ACTION, history.getAction());
        values.put(KEY_NAME, history.getName());
        values.put(KEY_TYPE, history.getType());
        values.put(KEY_TIME, history.getTime());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<History> getAlHistory() {
        List<History> historyList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            History history = new History(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            historyList.add(0, history);
            cursor.moveToNext();
        }
        return historyList;
    }

    public void deleteHistory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME;
        db.execSQL(query);
        db.close();
    }
}

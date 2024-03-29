package com.namnoit.voicerecorder.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class RecordingsDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "recordings.db";
    public static int DATABASE_VERSION = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            RecordingsContract.RecordingsEntry.TABLE_NAME + " (" +
            RecordingsContract.RecordingsEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0," +
            RecordingsContract.RecordingsEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
            RecordingsContract.RecordingsEntry.COLUMN_SIZE + INT_TYPE + COMMA_SEP +
            RecordingsContract.RecordingsEntry.COLUMN_DATE + TEXT_TYPE + COMMA_SEP +
            RecordingsContract.RecordingsEntry.COLUMN_DURATION + INT_TYPE + COMMA_SEP +
            RecordingsContract.RecordingsEntry.COLUMN_MD5 + TEXT_TYPE + ")";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " +
            RecordingsContract.RecordingsEntry.TABLE_NAME;

    public RecordingsDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        DATABASE_VERSION++;
        onCreate(db);
    }

    public ArrayList<Recording> getAll(){
        ArrayList<Recording> recordings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RecordingsContract.RecordingsEntry.TABLE_NAME,
                new String[]{
                        RecordingsContract.RecordingsEntry.COLUMN_ID,
                        RecordingsContract.RecordingsEntry.COLUMN_NAME,
                        RecordingsContract.RecordingsEntry.COLUMN_SIZE,
                        RecordingsContract.RecordingsEntry.COLUMN_DURATION,
                        RecordingsContract.RecordingsEntry.COLUMN_DATE,
                        RecordingsContract.RecordingsEntry.COLUMN_MD5
                },
                null,
                null,
                null,
                null,
                RecordingsContract.RecordingsEntry.COLUMN_NAME + " DESC");
        if (cursor.moveToFirst()) {
            do {
                Recording recording = new Recording(
                        cursor.getInt(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_NAME)),
                        cursor.getLong(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_SIZE)),
                        cursor.getInt(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_DURATION)),
                        cursor.getString(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_MD5)));
                recordings.add(recording);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordings;
    }

    public void insert(String name, long size, int duration, String date, String md5){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecordingsContract.RecordingsEntry.COLUMN_NAME, name);
        values.put(RecordingsContract.RecordingsEntry.COLUMN_SIZE, size);
        values.put(RecordingsContract.RecordingsEntry.COLUMN_DURATION, duration);
        values.put(RecordingsContract.RecordingsEntry.COLUMN_DATE, date);
        values.put(RecordingsContract.RecordingsEntry.COLUMN_MD5, md5);
        db.insert(RecordingsContract.RecordingsEntry.TABLE_NAME,null,values);
        db.close();
    }

    public Recording getLast(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RecordingsContract.RecordingsEntry.TABLE_NAME,
                new String[]{
                        RecordingsContract.RecordingsEntry.COLUMN_ID,
                        RecordingsContract.RecordingsEntry.COLUMN_NAME,
                        RecordingsContract.RecordingsEntry.COLUMN_SIZE,
                        RecordingsContract.RecordingsEntry.COLUMN_DURATION,
                        RecordingsContract.RecordingsEntry.COLUMN_DATE,
                        RecordingsContract.RecordingsEntry.COLUMN_MD5
                },
                null,
                null,
                null,
                null,
                null);
        Recording recording = null;
        if (cursor.moveToLast()) {
            recording = new Recording(
                    cursor.getInt(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_NAME)),
                    cursor.getLong(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_SIZE)),
                    cursor.getInt(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_DURATION)),
                    cursor.getString(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_MD5)));
        }
        cursor.close();
        db.close();
        return recording;
    }

    public Recording getRecordingWithHash(String hash){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RecordingsContract.RecordingsEntry.TABLE_NAME,
                new String[]{
                        RecordingsContract.RecordingsEntry.COLUMN_ID,
                        RecordingsContract.RecordingsEntry.COLUMN_NAME,
                        RecordingsContract.RecordingsEntry.COLUMN_SIZE,
                        RecordingsContract.RecordingsEntry.COLUMN_DURATION,
                        RecordingsContract.RecordingsEntry.COLUMN_DATE,
                        RecordingsContract.RecordingsEntry.COLUMN_MD5
                },
                RecordingsContract.RecordingsEntry.COLUMN_MD5+ " = ?",
                new String[] {hash},
                null,
                null,
                null);
        Recording recording = null;
        if (cursor.moveToLast()) {
            recording = new Recording(
                    cursor.getInt(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_NAME)),
                    cursor.getLong(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_SIZE)),
                    cursor.getInt(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_DURATION)),
                    cursor.getString(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndex(RecordingsContract.RecordingsEntry.COLUMN_MD5)));
        }
        cursor.close();
        db.close();
        return recording;
    }

    public void delete(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RecordingsContract.RecordingsEntry.TABLE_NAME,
                RecordingsContract.RecordingsEntry.COLUMN_ID + "=?",
                new String[]{Integer.toString(id)});
        db.close();
    }

    public void updateName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecordingsContract.RecordingsEntry.COLUMN_NAME, name);
        db.update(RecordingsContract.RecordingsEntry.TABLE_NAME,
                values,
                RecordingsContract.RecordingsEntry.COLUMN_ID+ " = ?",
                new String[] {Integer.toString(id)});
        db.close();
    }
}

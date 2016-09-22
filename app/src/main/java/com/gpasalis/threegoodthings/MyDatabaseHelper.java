package com.gpasalis.threegoodthings;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Note_Manager";

    // Table name: Note.
    private static final String TABLE_NOTE = "Note";

    private static final String COLUMN_NOTE_ID = "Note_Id";
    private static final String COLUMN_DATE = "COLUMN_DATE";
    private static final String COLUMN_EXP_1 = "COLUMN_EXP_1";
    private static final String COLUMN_EXP_2 = "COLUMN_EXP_2";
    private static final String COLUMN_EXP_3 = "COLUMN_EXP_3";

    public MyDatabaseHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "MyDatabaseHelper.onCreate ... ");
        // Script.
        String script = "CREATE TABLE " + TABLE_NOTE + "("
                + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_EXP_1 + " TEXT,"
                + COLUMN_EXP_2 + " TEXT,"
                + COLUMN_EXP_3 + " TEXT" + ")";
        // Execute Script.
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(TAG, "MyDatabaseHelper.onUpgrade ... ");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);

        // Create tables again
        onCreate(db);
    }

    public void addNote(Note note) {
        Log.i(TAG, "MyDatabaseHelper.addNote ... " + note.getDate());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, note.getDate());
        values.put(COLUMN_EXP_1, note.getExp1());
        values.put(COLUMN_EXP_2, note.getExp2());
        values.put(COLUMN_EXP_3, note.getExp3());

        // Inserting Row
        db.insert(TABLE_NOTE, null, values);

        // Closing database connection
        db.close();
    }

    public List<Note> getAllNotes() {
        Log.i(TAG, "MyDatabaseHelper.getAllNotes ... " );

        List<Note> noteList = new ArrayList<Note>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setNoteId(Integer.parseInt(cursor.getString(0)));
                note.setDate(cursor.getString(1));
                note.setExp1(cursor.getString(2));
                note.setExp2(cursor.getString(3));
                note.setExp3(cursor.getString(4));
                // Adding note to list
                noteList.add(note);
            } while (cursor.moveToNext());
        }

        // return note list
        return noteList;
    }

    public boolean sameDateExists(String date) {
        Log.i(TAG, "MyDatabaseHelper.sameDateExists ... " );

        List<Note> noteList = new ArrayList<Note>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(1).equals(date)) {
                    return true;
                }
            } while (cursor.moveToNext());
        }

        return false;
    }

    public int getNotesCount() {
        Log.i(TAG, "MyDatabaseHelper.getNotesCount ... " );

        String countQuery = "SELECT  * FROM " + TABLE_NOTE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    public int updateNote(Note note) {
        Log.i(TAG, "MyDatabaseHelper.updateNote ... "  + note.getDate());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, note.getDate());
        values.put(COLUMN_EXP_1, note.getExp1());
        values.put(COLUMN_EXP_2, note.getExp2());
        values.put(COLUMN_EXP_3, note.getExp3());

        // updating row
        return db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(note.getNoteId())});
    }

    public void deleteNote(Note note) {
        Log.i(TAG, "MyDatabaseHelper.deleteNote ... " + note.getDate() );

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, COLUMN_NOTE_ID + " = ?",
                new String[] { String.valueOf(note.getNoteId()) });
        db.close();
    }

    public void deleteAllRecords() {
        // Drop older table if existed
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);

        // Create tables again
        onCreate(db);
    }

}

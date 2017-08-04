package com.example.kienpt.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kienpt.note.Bean.Note;

import java.util.ArrayList;
import java.util.List;


public class MyDatabaseHelper extends SQLiteOpenHelper {
    //Version
    private static final int DATABASE_VERSION = 1;

    //DB name
    private static final String DATABASE_NAME = "Note_Manager";

    //Table name: note
    private static final String TABLE_NOTE = "Note";

    //Column name
    private static final String COLUMN_NOTE_ID = "Id";
    private static final String COLUMN_NOTE_TITLE = "Title";
    private static final String COLUMN_NOTE_CONTENT = "Content";
    private static final String COLUMN_NOTE_TIME = "Time";
    private static final String COLUMN_NOTE_CREATED_TIME = "CreatedTime";
    private static final String COLUMN_NOTE_BACKGROUND_COLOR = "BackgroundColor";

    private Context mContext;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Script create table
        String script = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, "
                        + "%s TEXT, %s TEXT, %s TEXT)",
                TABLE_NOTE, COLUMN_NOTE_ID, COLUMN_NOTE_TITLE, COLUMN_NOTE_CONTENT,
                COLUMN_NOTE_TIME, COLUMN_NOTE_CREATED_TIME);
        // Create table
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_NOTE));
        onCreate(db);
    }

    // add new note
    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
        values.put(COLUMN_NOTE_TIME, note.getNoteTime());
        values.put(COLUMN_NOTE_CREATED_TIME, note.getCreatedTime());
        values.put(COLUMN_NOTE_BACKGROUND_COLOR, note.getBackgroundColor());
        db.insert(TABLE_NOTE, null, values);
        db.close();
    }

    // get one note
    public Note getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTE, new String[]{COLUMN_NOTE_ID,
                        COLUMN_NOTE_TITLE, COLUMN_NOTE_CONTENT, COLUMN_NOTE_TIME,
                        COLUMN_NOTE_CREATED_TIME, COLUMN_NOTE_BACKGROUND_COLOR},
                COLUMN_NOTE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Note note = new Note(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        cursor.close();
        db.close();
        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<>();
        // Select All Query
        String selectQuery = String.format("SELECT * FROM %s", TABLE_NOTE);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5));
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return noteList;
    }

    public int getNotesCount() {
        String countQuery = String.format("SELECT  * FROM %s", TABLE_NOTE);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
        values.put(COLUMN_NOTE_TIME, note.getNoteTime());
        values.put(COLUMN_NOTE_CREATED_TIME, note.getCreatedTime());
        values.put(COLUMN_NOTE_BACKGROUND_COLOR, note.getBackgroundColor());

        // updating row
        db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(note.getNoteID())});
        db.close();
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(note.getNoteID())});
        db.close();
    }

    public void deleteAllNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, null, null);
        db.close();
    }
}

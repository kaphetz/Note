package com.example.kienpt.note.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDatabaseHelper extends SQLiteOpenHelper {
    //Version
    private static final int DATABASE_VERSION = 7;

    //DB name
    private static final String DATABASE_NAME = "Note_Manager";

    //Table name: note
    private static final String TABLE_NOTE = "Note";

    //Column name of Note table
    private static final String COLUMN_NOTE_ID = "Id";
    private static final String COLUMN_NOTE_TITLE = "Title";
    private static final String COLUMN_NOTE_CONTENT = "Content";
    private static final String COLUMN_NOTE_TIME = "Time";
    private static final String COLUMN_NOTE_CREATED_TIME = "CreatedTime";
    private static final String COLUMN_NOTE_BACKGROUND_COLOR = "BackgroundColor";

    private static final String TABLE_NOTE_IMAGE = "NoteImage";

    private static final String COLUMN_NOTE_IMAGE_ID = "NoteId";
    private static final String COLUMN_IMAGE_PATH = "ImagePath";
    private Context mContext;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Script create table
        String script = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, "
                        + "%s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                TABLE_NOTE, COLUMN_NOTE_ID, COLUMN_NOTE_TITLE, COLUMN_NOTE_CONTENT,
                COLUMN_NOTE_TIME, COLUMN_NOTE_CREATED_TIME, COLUMN_NOTE_BACKGROUND_COLOR);
        // Create table
        db.execSQL(script);

        script = String.format("CREATE TABLE %s(%s INTEGER, %s TEXT)",
                TABLE_NOTE_IMAGE, COLUMN_NOTE_IMAGE_ID, COLUMN_IMAGE_PATH);

        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_NOTE));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_NOTE_IMAGE));
        onCreate(db);
    }
}

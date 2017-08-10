package com.example.kienpt.note.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class NoteRepo {
    private Note mNote;

    public NoteRepo() {
        mNote = new Note();
    }

    //Table name: note
    private static final String TABLE_NOTE = "Note";

    //Column name of Note table
    private static final String COLUMN_NOTE_ID = "Id";
    private static final String COLUMN_NOTE_TITLE = "Title";
    private static final String COLUMN_NOTE_CONTENT = "Content";
    private static final String COLUMN_NOTE_TIME = "Time";
    private static final String COLUMN_NOTE_CREATED_TIME = "CreatedTime";
    private static final String COLUMN_NOTE_BACKGROUND_COLOR = "BackgroundColor";

    // add new note
    public void addNote(Note note) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
        values.put(COLUMN_NOTE_TIME, note.getNoteTime());
        values.put(COLUMN_NOTE_CREATED_TIME, note.getCreatedTime());
        values.put(COLUMN_NOTE_BACKGROUND_COLOR, note.getBackgroundColor());
        db.insert(TABLE_NOTE, null, values);
        DatabaseManager.getInstance().closeDatabase();
    }

    // get one note
    public Note getNote(int id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Note note = null;
        Cursor cursor = db.query(TABLE_NOTE, new String[]{COLUMN_NOTE_ID,
                        COLUMN_NOTE_TITLE, COLUMN_NOTE_CONTENT, COLUMN_NOTE_TIME,
                        COLUMN_NOTE_CREATED_TIME, COLUMN_NOTE_BACKGROUND_COLOR},
                COLUMN_NOTE_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            note = new Note(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TIME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CREATED_TIME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_BACKGROUND_COLOR)));
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return note;
    }

    public Note getLastNote() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Note note = null;
        String selectQuery = String.format("SELECT * FROM %s WHERE %s = (SELECT MAX(%s) FROM %s)",
                TABLE_NOTE, COLUMN_NOTE_ID, COLUMN_NOTE_ID, TABLE_NOTE);

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                note = new Note(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TIME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CREATED_TIME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_BACKGROUND_COLOR)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return note;
    }

    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> noteList = new ArrayList<>();
        // Select All Query
        String selectQuery = String.format("SELECT * FROM %s", TABLE_NOTE);

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CONTENT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TIME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_CREATED_TIME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_BACKGROUND_COLOR)));
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return noteList;
    }

    public int getNotesCount() {
        String countQuery = String.format("SELECT  * FROM %s", TABLE_NOTE);
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void updateNote(Note note) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
        values.put(COLUMN_NOTE_TIME, note.getNoteTime());
        values.put(COLUMN_NOTE_CREATED_TIME, note.getCreatedTime());
        values.put(COLUMN_NOTE_BACKGROUND_COLOR, note.getBackgroundColor());

        // updating row
        db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(note.getNoteID())});
        DatabaseManager.getInstance().closeDatabase();
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(TABLE_NOTE, COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(note.getNoteID())});
        DatabaseManager.getInstance().closeDatabase();
    }

    public void deleteAllNotes() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(TABLE_NOTE, null, null);
        DatabaseManager.getInstance().closeDatabase();
    }
}

package com.example.kienpt.note.models;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class NoteImageRepo {
    private NoteImage mNoteImage;

    public NoteImageRepo() {
        mNoteImage = new NoteImage();
    }

    private static final String TABLE_NOTE_IMAGE = "NoteImage";

    private static final String COLUMN_NOTE_IMAGE_ID = "NoteId";
    private static final String COLUMN_IMAGE = "Image";

    // add new note
    public void addNoteImage(NoteImage noteImage) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_IMAGE_ID, noteImage.getNoteId());
        values.put(COLUMN_IMAGE, noteImage.getImg());
        db.insert(TABLE_NOTE_IMAGE, null, values);
        DatabaseManager.getInstance().closeDatabase();
    }

    // get one note
    public NoteImage getNoteImage(int id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        NoteImage noteImage = null;
        Cursor cursor = db.query(TABLE_NOTE_IMAGE, new String[]{COLUMN_NOTE_IMAGE_ID,
                        COLUMN_IMAGE},
                COLUMN_NOTE_IMAGE_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            noteImage = new NoteImage(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_IMAGE_ID)),
                    cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE)));
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return noteImage;
    }

    public List<NoteImage> getAllNoteImages() {
        List<NoteImage> noteImageList = new ArrayList<>();
        // Select All Query
        String selectQuery = String.format("SELECT * FROM %s BY %s", TABLE_NOTE_IMAGE);
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                NoteImage noteImage = new NoteImage(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_IMAGE_ID)),
                        cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE)));
                noteImageList.add(noteImage);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return noteImageList;
    }

    public ArrayList<NoteImage> getImageById(int noteId) {
        ArrayList<NoteImage> noteImageList = new ArrayList<>();
        // Select image that have id = noteId
        String selectQuery = String.format("SELECT * FROM %s WHERE %s = '%s'", TABLE_NOTE_IMAGE,
                COLUMN_NOTE_IMAGE_ID, noteId);

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                NoteImage noteImage = new NoteImage(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_IMAGE_ID)),
                        cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE)));
                noteImageList.add(noteImage);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return noteImageList;

    }

    public void deleteNoteImage(Integer noteId) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(TABLE_NOTE_IMAGE, COLUMN_NOTE_IMAGE_ID + " = ?",
                new String[]{String.valueOf(noteId)});
        DatabaseManager.getInstance().closeDatabase();
    }

    public void deleteAllNoteImages() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(TABLE_NOTE_IMAGE, null, null);
        DatabaseManager.getInstance().closeDatabase();
    }
}

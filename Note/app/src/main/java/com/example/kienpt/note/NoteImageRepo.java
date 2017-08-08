package com.example.kienpt.note;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kienpt.note.bean.NoteImage;

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
        db.close();
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
        db.close();
        return noteImage;
    }

    public List<NoteImage> getAllNoteImages() {
        List<NoteImage> noteImageList = new ArrayList<>();
        // Select All Query
        String selectQuery = String.format("SELECT * FROM %s", TABLE_NOTE_IMAGE);

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
        db.close();
        return noteImageList;
    }

//    public void updateNoteImage(Note note) {
//        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
//        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
//        values.put(COLUMN_NOTE_TIME, note.getNoteTime());
//        values.put(COLUMN_NOTE_CREATED_TIME, note.getCreatedTime());
//        values.put(COLUMN_NOTE_BACKGROUND_COLOR, note.getBackgroundColor());
//
//        // updating row
//        db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?",
//                new String[]{String.valueOf(note.getNoteID())});
//        db.close();
//    }

    public void deleteNoteImage(NoteImage noteImage) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(TABLE_NOTE_IMAGE, COLUMN_NOTE_IMAGE_ID + " = ?",
                new String[]{String.valueOf(noteImage.getNoteId())});
        db.close();
    }
}

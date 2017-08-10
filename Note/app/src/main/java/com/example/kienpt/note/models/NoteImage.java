package com.example.kienpt.note.models;

public class NoteImage {
    private int mNoteId;
    private byte[] mImg;

    public NoteImage() {
    }

    public NoteImage(int noteId, byte[] img) {
        mNoteId = noteId;
        mImg = img;
    }

    public int getNoteId() {
        return mNoteId;
    }

    public void setNoteId(int noteId) {
        this.mNoteId = noteId;
    }

    public byte[] getImg() {
        return mImg;
    }

    public void setImg(byte[] img) {
        this.mImg = img;
    }
}

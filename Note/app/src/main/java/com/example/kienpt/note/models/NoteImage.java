package com.example.kienpt.note.models;

public class NoteImage {
    private int mNoteId;
    private String mImgPath;

    public NoteImage() {
    }

    public NoteImage(int noteId, String imgPath) {
        mNoteId = noteId;
        mImgPath = imgPath;
    }

    public int getNoteId() {
        return mNoteId;
    }

    public void setNoteId(int noteId) {
        mNoteId = noteId;
    }

    public String getImgPath() {
        return mImgPath;
    }

    public void setImgPath(String imgPath) {
        mImgPath = imgPath;
    }

    public boolean hasImage() {
        return getImgPath() != null || !getImgPath().isEmpty();
    }

    @Override
    public String toString() {
        return "NoteImage{" +
                "mNoteId=" + mNoteId +
                ", mImgPath='" + mImgPath + '\'' +
                '}';
    }
}

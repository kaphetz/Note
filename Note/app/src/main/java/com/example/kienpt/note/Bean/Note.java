package com.example.kienpt.note.Bean;


import java.io.Serializable;
import java.sql.Time;

public class Note implements Serializable {
    private int mNoteID;
    private String mCreateTime;
    private String mNoteContent;
    private String mNoteTitle;
    private String mNoteTime;

    public Note() {
    }

    public Note(int mNoteID, String mCreateTime, String mNoteContent) {
        this.mNoteID = mNoteID;
        this.mCreateTime = mCreateTime;
        this.mNoteContent = mNoteContent;
    }

    public Note(String noteTitle, String noteContent) {
        mNoteTitle = noteTitle;
        mNoteContent = noteContent;
    }

    public Note(String noteTitle, String noteContent, String createTime) {
        mNoteTitle = noteTitle;
        mNoteContent = noteContent;
        mCreateTime = createTime;
    }

    public Note(String noteTitle, String noteContent, String noteTime,
                String createTime) {
        mNoteTitle = noteTitle;
        mNoteContent = noteContent;
        mNoteTime = noteTime;
        mCreateTime = createTime;
    }

    public int getNoteID() {
        return mNoteID;
    }

    public void setNoteID(int noteID) {
        mNoteID = noteID;
    }

    public String getCreatedTime() {
        return mCreateTime;
    }

    public void setCreatedTime(String createTime) {
        mCreateTime = createTime;
    }

    public String getNoteContent() {
        return mNoteContent;
    }

    public void setNoteContent(String noteContent) {
        mNoteContent = noteContent;
    }

    public String getNoteTitle() {
        return mNoteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        mNoteTitle = noteTitle;
    }

    public String getNoteTime() {
        return mNoteTime;
    }

    public void setNoteTime(String noteTime) {
        mNoteTime = noteTime;
    }
}

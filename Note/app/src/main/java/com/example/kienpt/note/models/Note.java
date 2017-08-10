package com.example.kienpt.note.models;

import java.io.Serializable;

public class Note implements Serializable {
    private int mNoteID;
    private String mNoteTitle;
    private String mNoteContent;
    private String mNoteTime;
    private String mCreatedTime;
    private String mBackgroundColor;


    public Note() {
    }

    public Note(int noteID,
                String noteTitle,
                String noteContent,
                String noteTime,
                String createdTime,
                String backgroundColor) {
        mNoteID = noteID;
        mNoteTitle = noteTitle;
        mNoteContent = noteContent;
        mNoteTime = noteTime;
        mCreatedTime = createdTime;
        mBackgroundColor = backgroundColor;
    }

    public Note(String noteTitle,
                String noteContent,
                String noteTime,
                String createdTime,
                String backgroundColor) {
        mNoteTitle = noteTitle;
        mNoteContent = noteContent;
        mNoteTime = noteTime;
        mCreatedTime = createdTime;
        mBackgroundColor = backgroundColor;
    }

    public int getNoteID() {
        return mNoteID;
    }

    public void setNoteID(int noteID) {
        mNoteID = noteID;
    }

    public String getNoteTitle() {
        return mNoteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        mNoteTitle = noteTitle;
    }

    public String getNoteContent() {
        return mNoteContent;
    }

    public void setNoteContent(String noteContent) {
        mNoteContent = noteContent;
    }

    public String getNoteTime() {
        return mNoteTime;
    }

    public void setNoteTime(String noteTime) {
        mNoteTime = noteTime;
    }

    public String getCreatedTime() {
        return mCreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        mCreatedTime = createdTime;
    }

    public String getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    @Override
    public String toString() {
        return "Note{" +
                "mNoteID=" + mNoteID +
                ", mNoteTitle='" + mNoteTitle + '\'' +
                ", mNoteContent='" + mNoteContent + '\'' +
                ", mNoteTime='" + mNoteTime + '\'' +
                ", mCreatedTime='" + mCreatedTime + '\'' +
                ", mBackgroundColor='" + mBackgroundColor + '\'' +
                '}';
    }
}

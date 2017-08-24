package com.example.kienpt.note.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.kienpt.note.models.NoteImageRepo;
import com.example.kienpt.note.models.NoteRepo;
import com.example.kienpt.note.R;
import com.example.kienpt.note.models.Note;
import com.example.kienpt.note.models.NoteImage;

import java.util.Calendar;
import java.util.Date;


public class AddActivity extends ControlActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_left);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.mipmap.ic_launcher);
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.colorCyan)));
        getActionBar().setTitle(getString(R.string.note));
        setContentView(R.layout.activity_add);
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        initView();
        mGvImage.setExpanded(true);
        // Set up for date spinner
        dateAdapter = new ArrayAdapter<>(AddActivity.this,
                android.R.layout.simple_spinner_item, mListDate);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpnDate.setAdapter(dateAdapter);
        mSpnDate.setOnItemSelectedListener(new DateSpinnerInfo(AddActivity.this));

        // Set up for time spinner
        timeAdapter = new ArrayAdapter<>(AddActivity.this,
                android.R.layout.simple_spinner_item, mListTime);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpnTime.setAdapter(timeAdapter);
        mSpnTime.setOnItemSelectedListener(new TimeSpinnerInfo(AddActivity.this));
        Date date = new Date();
        mTvDateTime.setText(String.valueOf(DateFormat.format(
                getString(R.string.ddmmyyyy_hhmm_format), date)));
//        restoreMe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_save:
                addNewNote();
                return true;
            case R.id.mn_change_color:
                changeBackgroundColor();
                return true;
            case R.id.mn_camera:
                insertImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addNewNote() {
        Note note = new Note();
        // add notification if date time not null
        if (mLlDateTime.getVisibility() == View.VISIBLE) {
            Calendar calendar = getSelectedDateTime();
            if (mCalendar.getTime().after(calendar.getTime())) {
                Toast.makeText(this, R.string.please_change, Toast.LENGTH_SHORT).show();
            } else {
                insertIntoDB(note);
                NoteRepo dbNote = new NoteRepo();
                note = dbNote.getLastNote();
                createNotification(note, calendar);
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
            }
        } else {
            insertIntoDB(note);
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }
    }

    private void insertIntoDB(Note note) {
        if (!mEtTitle.getText().toString().equals("")) {
            note.setNoteTitle(mEtTitle.getText().toString().trim());
        } else {
            note.setNoteTitle(getString(R.string.untitled));
        }
        note.setNoteContent(mEtContent.getText().toString().trim());
        if (mLlDateTime.getVisibility() == View.VISIBLE) {
            note.setNoteTime(String.format("%s %s", mSelectedDate, mSelectedTime));
        } else {
            note.setNoteTime("");
        }
        note.setCreatedTime(String.valueOf(DateFormat.format(
                getString(R.string.ddmmyyyy_hhmmss_format), new Date())));
        note.setBackgroundColor(mColor);
        // add new note into db
        NoteRepo dbNote = new NoteRepo();
        dbNote.addNote(note);
        note = dbNote.getLastNote();
        NoteImageRepo dbNoteImage = new NoteImageRepo();
        for (String noteImage : mImageList) {
            NoteImage noteImg = new NoteImage();
            noteImg.setNoteId(note.getNoteID());
            noteImg.setImgPath(noteImage);
            dbNoteImage.addNoteImage(noteImg);
        }
    }
}

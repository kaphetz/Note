package com.example.kienpt.note.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.kienpt.note.AlarmReceiver;
import com.example.kienpt.note.models.NoteImageRepo;
import com.example.kienpt.note.models.NoteRepo;
import com.example.kienpt.note.R;
import com.example.kienpt.note.models.Note;
import com.example.kienpt.note.models.NoteImage;

import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddActivity extends ControlActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_left);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.mipmap.ic_launcher);
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.colorSky)));
        getActionBar().setTitle(getString(R.string.note));
        setContentView(R.layout.activity_add);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        initView();
        mGvImage.setExpanded(true);
        // Set up for date spinner
        dateAdapter = new ArrayAdapter<>(AddActivity.this,
                android.R.layout.simple_spinner_item, listDate);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpnDate.setAdapter(dateAdapter);
        mSpnDate.setOnItemSelectedListener(new DateSpinnerInfo());

        // Set up for time spinner
        timeAdapter = new ArrayAdapter<>(AddActivity.this,
                android.R.layout.simple_spinner_item, listTime);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpnTime.setAdapter(timeAdapter);
        mSpnTime.setOnItemSelectedListener(new TimeSpinnerInfo());
        Calendar now = Calendar.getInstance();
        mTvDateTime.setText(convert(now, getString(R.string.ddmmyyyy_hhmm_format)));
        restoreMe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

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

    private class DateSpinnerInfo implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView,
                                   int selectedIndex, long id) {
            String selectedDate = spinner.getItemAtPosition(selectedIndex).toString();
            switch (selectedIndex) {
                case 0:
                    SimpleDateFormat df = new SimpleDateFormat(getString(R.string.ddmmyyyy_format));
                    mSelectedDate = df.format(mCalendar.getTime());
                    updateAdapterForDateSpinner(getString(R.string.other));
                    break;
                case 1:
                    mSelectedDate = getTomorrow();
                    updateAdapterForDateSpinner(getString(R.string.other));
                    break;
                case 2:
                    mSelectedDate = getDayOfNextWeek();
                    updateAdapterForDateSpinner(getString(R.string.other));
                    break;
                case 3:
                    chooseOptionOtherDate(AddActivity.this, selectedDate);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> spinner) {
            //do something
        }
    }

    private class TimeSpinnerInfo implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView,
                                   int selectedIndex, long id) {
            String selectedTime = spinner.getItemAtPosition(selectedIndex).toString();
            switch (selectedIndex) {
                case 0:
                    mSelectedTime = getString(R.string.hour_9h);
                    updateAdapterForTimeSpinner(getString(R.string.other));
                    break;
                case 1:
                    mSelectedTime = getString(R.string.hour_13h);
                    updateAdapterForTimeSpinner(getString(R.string.other));
                    break;
                case 2:
                    mSelectedTime = getString(R.string.hour_17h);
                    updateAdapterForTimeSpinner(getString(R.string.other));
                    break;
                case 3:
                    mSelectedTime = getString(R.string.hour_20h);
                    updateAdapterForTimeSpinner(getString(R.string.other));
                    break;
                case 4:
                    chooseOptionOtherTime(AddActivity.this, selectedTime);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> spinner) {
            //do something
        }
    }

    private void addNewNote() {
        Note note = new Note();
        // add notification if date time not null
        if (mLlDateTime.getVisibility() == View.VISIBLE) {
            String[] selectTime = mSelectedTime.split(":");
            String[] selectDate = mSelectedDate.split("/");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.valueOf(selectDate[2]), Integer.valueOf(selectDate[1]) - 1,
                    Integer.valueOf(selectDate[0]), Integer.valueOf(selectTime[0]),
                    Integer.valueOf(selectTime[1]), 0);

            if (mCalendar.getTime().after(calendar.getTime())) {
                Toast.makeText(this, "The time you have just set before the current time. " +
                        "Please change!", Toast.LENGTH_SHORT).show();
            } else {
                insertIntoDB(note);
                NoteRepo dbNote = new NoteRepo();
                note = dbNote.getLastNote();
                mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                intent.putExtra(AlarmReceiver.ID, note.getNoteID());
                intent.putExtra(AlarmReceiver.TITLE, note.getNoteTitle());
                pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), note.getNoteID(), intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), pendingIntent);

                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
            }
        } else {
            insertIntoDB(note);
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }
    }

    private void insertIntoDB(Note note){
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
        //get create datetime
        mCalendar = Calendar.getInstance();
        note.setCreatedTime(String.format("%s", convert(mCalendar,
                getString(R.string.ddmmyyyy_hhmmss_format))));
        note.setBackgroundColor(mColor);
        // add new note into db
        NoteRepo dbNote = new NoteRepo();
        dbNote.addNote(note);
        note = dbNote.getLastNote();
        NoteImageRepo dbNoteImage = new NoteImageRepo();
        for (Bitmap noteImage : mImageList) {
            NoteImage noteImg = new NoteImage();
            noteImg.setNoteId(note.getNoteID());
            noteImg.setImg(getBitmapAsByteArray(noteImage));
            dbNoteImage.addNoteImage(noteImg);
        }
    }
}

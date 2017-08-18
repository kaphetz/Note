package com.example.kienpt.note.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kienpt.note.AlarmReceiver;
import com.example.kienpt.note.R;
import com.example.kienpt.note.adapters.CustomGridViewImageAdapter;
import com.example.kienpt.note.adapters.CustomGridViewNotesAdapter;
import com.example.kienpt.note.models.Note;
import com.example.kienpt.note.models.NoteImage;
import com.example.kienpt.note.models.NoteImageRepo;
import com.example.kienpt.note.models.NoteRepo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditActivity extends ControlActivity {
    public static String KEY = "KEY";
    public static String NOTE = "NOTE";
    private static final int LAST_OPTION_OF_DATE_SPINNER = 3;
    private static final int LAST_OPTION_OF_TIME_SPINNER = 4;
    private Note mNote;
    private List<Note> mListNote;
    private CustomGridViewNotesAdapter adapter;
    private int posOfNote;
    private ImageButton imbPrevious;
    private ImageButton imbForward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_left);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.mipmap.ic_launcher);
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.colorCyan)));
        setContentView(R.layout.activity_edit);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        initView();
        mGvImage.setExpanded(true);
        ImageButton imbDel = (ImageButton) findViewById(R.id.btn_delete);
        ImageButton imbShare = (ImageButton) findViewById(R.id.btn_share);
        imbPrevious = (ImageButton) findViewById(R.id.btn_previous);
        imbForward = (ImageButton) findViewById(R.id.btn_forward);
        getData();
        // Buttons of bottom menu
        // Event click for button Delete
        imbDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });
        // Event click for button Share
        imbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareNote();
            }
        });
        // Navigation button
        imbPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPreviousNote();
            }
        });
        imbForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNextNote();
            }
        });
        restoreMe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intentMain = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intentMain);
                return true;
            case R.id.mn_new_note:
                Intent intentAdd = new Intent(EditActivity.this, AddActivity.class);
                startActivity(intentAdd);
                return true;
            case R.id.mn_change_color:
                changeBackgroundColor();
                return true;
            case R.id.mn_save:
                saveNote();
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
                    Date date = new Date();
                    mSelectedDate = String.valueOf(DateFormat.format(
                            getString(R.string.ddmmyyyy_format), date));
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
                    chooseOptionOtherDate(EditActivity.this, selectedDate);
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
                    chooseOptionOtherTime(EditActivity.this, selectedTime);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> spinner) {
            //do something
        }

    }

    // get data of note that was selected
    private void getData() {
        if (getIntent().getExtras() != null) {
            mNote = (Note) getIntent().getSerializableExtra(NOTE);
            if (mNote == null) {
                Bundle bundle = getIntent().getExtras();
                int id = bundle.getInt(KEY);
                NoteRepo noteRepo = new NoteRepo();
                mNote = noteRepo.getNote(id);
            }
            getActionBar().setTitle(mNote.getNoteTitle());
            mEtTitle.setText(mNote.getNoteTitle());
            mEtContent.setText(mNote.getNoteContent());

            //format of mNote.getCreatedTime() is dd/MM/yyyy hh:mm:ss
            //Delete seconds located from 16 to 19
            StringBuffer strBuf = new StringBuffer(mNote.getCreatedTime());
            int start = 16;
            int end = 19;
            strBuf.replace(start, end, "");
            mTvDateTime.setText(strBuf);
            mColor = mNote.getBackgroundColor();
            switch (mNote.getBackgroundColor()) {
                case "Yellow":
                    mRlNote.setBackgroundColor(
                            getResources().getColor(R.color.colorYellow));
                    break;
                case "Green":
                    mRlNote.setBackgroundColor(
                            getResources().getColor(R.color.colorGreen));
                    break;
                case "Blue":
                    mRlNote.setBackgroundColor(
                            getResources().getColor(R.color.colorBlue));
                    break;
                default:
                    mRlNote.setBackgroundColor(
                            getResources().getColor(R.color.colorWhite));
                    break;
            }
            // Set up for date spinner
            dateAdapter = new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_spinner_item, listDate);
            dateAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            mSpnDate.setAdapter(dateAdapter);
            mSpnDate.setOnItemSelectedListener(new DateSpinnerInfo());

            // Set up for time spinner
            timeAdapter = new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_spinner_item, listTime);
            timeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            mSpnTime.setAdapter(timeAdapter);
            mSpnTime.setOnItemSelectedListener(new TimeSpinnerInfo());

            //if this note have reminder, show datetime was picked
            if (!mNote.getNoteTime().equals("")) {
                String[] datetime = mNote.getNoteTime().split(" ");
                mSelectedDate = datetime[0];
                mSelectedTime = datetime[1];
                mLlDateTime.setVisibility(View.VISIBLE);
                mTvAlarm.setVisibility(View.GONE);
                updateAdapterForDateSpinner(datetime[0]);
                mSpnDate.setSelection(LAST_OPTION_OF_DATE_SPINNER);
                updateAdapterForTimeSpinner(datetime[1]);
                mSpnTime.setSelection(LAST_OPTION_OF_TIME_SPINNER);
            }

            mAdapter = new CustomGridViewImageAdapter(this, mImageList);
            // Show image
            NoteImageRepo dbNoteImage = new NoteImageRepo();
            ArrayList<NoteImage> list = dbNoteImage.getImageById(mNote.getNoteID());
            for (NoteImage notImage : list) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(notImage.getImg(), 0,
                        notImage.getImg().length);
                mImageList.add(bitmap);
            }
            setUpForNavigationButton();
            mGvImage.setAdapter(mAdapter);
            //clear notification icon at status bar
            AlarmReceiver.cancelNotification(EditActivity.this, mNote.getNoteID());
        }
    }

    //delete note
    private void deleteNote() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    // click OK
                    case DialogInterface.BUTTON_POSITIVE:
                        NoteRepo dbNote = new NoteRepo();
                        NoteImageRepo dbNoteImage = new NoteImageRepo();
                        dbNote.deleteNote(mNote);
                        dbNoteImage.deleteNoteImage(mNote.getNoteID());
                        AlarmReceiver.cancelNotification(EditActivity.this, mNote.getNoteID());
                        // delete notification
                        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                getApplicationContext(), mNote.getNoteID(), intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        mAlarmManager.cancel(pendingIntent);
                        Intent mainIntent = new Intent(EditActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        break;
                    //click cancel
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder ab = new AlertDialog.Builder(EditActivity.this);
        ab.setMessage("Are you sure you want to delete this?").setTitle("Confirm Delete").
                setPositiveButton("OK", dialogClickListener).
                setNegativeButton("Cancel", dialogClickListener).show();
    }

    // share note
    private void shareNote() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, mNote.getNoteTitle());
        share.putExtra(Intent.EXTRA_TEXT, String.format("%s\n%s",
                mNote.getNoteTitle(), mNote.getNoteContent()));
        startActivity(Intent.createChooser(share, getString(R.string.share_via)));
    }

    // save note
    private void saveNote() {
        //get create datetime
        mCalendar = Calendar.getInstance();
        // update notification
        if (mLlDateTime.getVisibility() == View.VISIBLE) {
            //mSelectedTime: hh:mm
            String[] selectTime = mSelectedTime.split(":");
            //mSelectedDate: dd/MM/yyyy
            String[] selectDate = mSelectedDate.split("/");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.valueOf(selectDate[2]), Integer.valueOf(selectDate[1]) - 1,
                    Integer.valueOf(selectDate[0]), Integer.valueOf(selectTime[0]),
                    Integer.valueOf(selectTime[1]), 0);
            if (mCalendar.getTime().after(calendar.getTime())) {
                Toast.makeText(this, "The time you have just set before the current time. " +
                        "Please change!", Toast.LENGTH_SHORT).show();
            } else {
                updateDB();
                mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                intent.putExtra(AlarmReceiver.ID, mNote.getNoteID());
                intent.putExtra(AlarmReceiver.TITLE, mNote.getNoteTitle());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                        mNote.getNoteID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), pendingIntent);
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
            }
        } else {
            updateDB();
            // delete notification
            AlarmReceiver.cancelNotification(EditActivity.this, mNote.getNoteID());
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    mNote.getNoteID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager.cancel(pendingIntent);
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }
    }

    private void updateDB() {
        if (!mEtTitle.getText().toString().equals("")) {
            mNote.setNoteTitle(mEtTitle.getText().toString().trim());
        } else {
            mNote.setNoteTitle(getString(R.string.untitled));
        }
        mNote.setNoteContent(mEtContent.getText().toString().trim());
        if (!mEtTitle.getText().toString().equals("")) {
            mNote.setNoteTitle(mEtTitle.getText().toString());
        } else {
            mNote.setNoteTitle(getString(R.string.untitled));
        }
        if (mLlDateTime.getVisibility() == View.VISIBLE) {
            mNote.setNoteTime(String.format("%s %s", mSelectedDate, mSelectedTime));
        } else {
            mNote.setNoteTime("");
        }

        Date date = new Date();
        mNote.setCreatedTime(String.valueOf(DateFormat.format(
                getString(R.string.ddmmyyyy_hhmmss_format), date)));
        mNote.setBackgroundColor(mColor);
        // add new note into db
        NoteRepo dbNote = new NoteRepo();
        dbNote.updateNote(mNote);

        // save images of Note
        NoteImageRepo dbNoteImage = new NoteImageRepo();
        dbNoteImage.deleteNoteImage(mNote.getNoteID());
        for (Bitmap noteImage : mImageList) {
            NoteImage noteImg = new NoteImage();
            noteImg.setNoteId(mNote.getNoteID());
            noteImg.setImg(getBitmapAsByteArray(noteImage));
            dbNoteImage.addNoteImage(noteImg);
        }
    }

    private void setUpForNavigationButton() {
        NoteRepo dbNote = new NoteRepo();
        mListNote = dbNote.getAllNotes();
        mListNote = orderByCreatedTime(mListNote);
        adapter = new CustomGridViewNotesAdapter(EditActivity.this, mListNote);
        for (int i = 0; i < mListNote.size(); i++) {
            if (mListNote.get(i).getNoteID() == mNote.getNoteID()) {
                posOfNote = i;
            }
        }
        if (posOfNote - 1 < 0) {
            imbPrevious.setImageResource(R.drawable.ic_left_disable);
            imbPrevious.setClickable(false);
            imbPrevious.setBackgroundResource(R.drawable.button_color_disable);
        }
        if ((posOfNote + 1) >= (mListNote.size())) {
            imbForward.setImageResource(R.drawable.ic_right_disable);
            imbForward.setClickable(false);
            imbForward.setBackgroundResource(R.drawable.button_color_disable);
        }
    }

    private void moveToPreviousNote() {
        if (posOfNote - 1 >= 0) {
            if (mListNote.get(posOfNote - 1) != null) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra(EditActivity.NOTE, (Note) adapter.getItem(posOfNote - 1));
                EditActivity.this.startActivity(intent);
            }
        }
    }

    private void moveToNextNote() {
        if ((posOfNote + 1) < (mListNote.size())) {
            if (mListNote.get(posOfNote + 1) != null) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra(EditActivity.NOTE, (Note) adapter.getItem(posOfNote + 1));
                EditActivity.this.startActivity(intent);
            }
        }
    }

    /*
     *Order by time created
     */
    public List<Note> orderByCreatedTime(List<Note> listNote) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
                getString(R.string.ddmmyyyy_hhmmss_format));
        for (int i = 0; i < listNote.size() - 1; i++) {
            for (int j = i + 1; j < listNote.size(); j++) {
                try {
                    Date a = formatter.parse(listNote.get(i).getCreatedTime());
                    Date b = formatter.parse(listNote.get(j).getCreatedTime());
                    if (a.before(b)) {
                        Note mediate = listNote.get(j);
                        listNote.set(j, listNote.get(i));
                        listNote.set(i, mediate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return listNote;
    }
}

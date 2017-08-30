package com.example.kienpt.note.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kienpt.note.R;
import com.example.kienpt.note.adapters.CustomGridViewImageAdapter;
import com.example.kienpt.note.adapters.CustomGridViewNotesAdapter;
import com.example.kienpt.note.models.Note;
import com.example.kienpt.note.models.NoteImage;
import com.example.kienpt.note.models.NoteImageRepo;
import com.example.kienpt.note.models.NoteRepo;
import com.example.kienpt.note.notifications.AlarmReceiver;
import com.example.kienpt.note.utils.ListUtil;
import com.example.kienpt.note.utils.SpinnerUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditActivity extends ControlActivity {
    private static final int START_OF_SECOND = 16;
    private static final int END_OF_SECOND = 19;
    private static final int LAST_OPTION_OF_DATE_SPINNER = 3;
    private static final int LAST_OPTION_OF_TIME_SPINNER = 4;
    private int mPosOfNote;
    private Note mNote;
    private List<Note> mListNote;
    private CustomGridViewNotesAdapter adapter;
    private ImageButton mImbPrevious;
    private ImageButton mImbForward;
    public static String sKEY = "sKEY";
    public static String sNOTE = "sNOTE";

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
        mImbPrevious = (ImageButton) findViewById(R.id.btn_previous);
        mImbForward = (ImageButton) findViewById(R.id.btn_forward);
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
        mImbPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPreviousNote();
            }
        });
        mImbForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNextNote();
            }
        });
//        restoreMe();
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

    // get data of note that was selected
    private void getData() {
        if (getIntent().getExtras() != null) {
            mNote = (Note) getIntent().getSerializableExtra(sNOTE);
            if (null == mNote) {
                mNote = new NoteRepo().getNote(getIntent().getExtras().getInt(sKEY));
            }
            getActionBar().setTitle(mNote.getNoteTitle());
            mEtTitle.setText(mNote.getNoteTitle());
            mEtContent.setText(mNote.getNoteContent());
            //format of mNote.getCreatedTime() is dd/MM/yyyy hh:mm:ss
            //Delete seconds located from START_OF_SECOND to END_OF_SECOND
            StringBuffer strBuf = new StringBuffer(mNote.getCreatedTime());
            strBuf.replace(START_OF_SECOND, END_OF_SECOND, "");
            mTvDateTime.setText(strBuf);
            mColor = mNote.getBackgroundColor();
            switch (mColor) {
                case "Yellow":
                    mRlNote.setBackgroundColor( getResources().getColor(R.color.colorYellow));
                    break;
                case "Green":
                    mRlNote.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    break;
                case "Blue":
                    mRlNote.setBackgroundColor( getResources().getColor(R.color.colorBlue));
                    break;
                default:
                    mRlNote.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    break;
            }
            // Set up for date spinner
            dateAdapter = new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_spinner_item, mListDate);
            dateAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            mSpnDate.setAdapter(dateAdapter);
            mSpnDate.setOnItemSelectedListener(new DateSpinnerInfo(EditActivity.this));
            // Set up for time spinner
            timeAdapter = new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_spinner_item, mListTime);
            timeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            mSpnTime.setAdapter(timeAdapter);
            mSpnTime.setOnItemSelectedListener(new TimeSpinnerInfo(EditActivity.this));
            //if this note has reminder, show datetime was picked
            if (!mNote.getNoteTime().isEmpty()) {
                String[] datetime = mNote.getNoteTime().split(" ");
                mSelectedDate = datetime[0];
                mSelectedTime = datetime[1];
                SpinnerUtil.showSpinner(mLlDateTime, mTvAlarm);
                SpinnerUtil.updateAdapterOfDateSpinner(dateAdapter, mSelectedDate, mListDate);
                mSpnDate.setSelection(LAST_OPTION_OF_DATE_SPINNER);
                SpinnerUtil.updateAdapterOfTimeSpinner(timeAdapter, mSelectedTime, mListTime);
                mSpnTime.setSelection(LAST_OPTION_OF_TIME_SPINNER);
            }
            mAdapter = new CustomGridViewImageAdapter(this, mImageList);
            ArrayList<NoteImage> list = new NoteImageRepo().getImageById(mNote.getNoteID());
            for (NoteImage noteImage : list) {
                mImageList.add(noteImage.getImgPath());
            }
            mGvImage.setAdapter(mAdapter);
            setUpForNavigationButton();
            //clear notification icon at status bar
            AlarmReceiver.cancelNotification(EditActivity.this, mNote.getNoteID());
            restoreMe();
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
                        new NoteRepo().deleteNote(mNote);
                        new NoteImageRepo().deleteNoteImage(mNote.getNoteID());
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
        ab.setMessage(R.string.confirm_delete).setTitle(R.string.confirm_del_title).
                setPositiveButton(R.string.ok, dialogClickListener).
                setNegativeButton(R.string.cancel, dialogClickListener).show();
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
        // update notification
        if (mLlDateTime.getVisibility() == View.VISIBLE) {
            Calendar calendar = getSelectedDateTime();
            if (mCalendar.getTime().after(calendar.getTime())) {
                Toast.makeText(this, R.string.please_change, Toast.LENGTH_SHORT).show();
            } else {
                updateDB();
                createNotification(mNote, calendar);
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
            }
        } else {
            updateDB();
            // delete notification at bar
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
        if (!mEtTitle.getText().toString().isEmpty()) {
            mNote.setNoteTitle(mEtTitle.getText().toString().trim());
        } else {
            mNote.setNoteTitle(getString(R.string.untitled));
        }
        mNote.setNoteContent(mEtContent.getText().toString().trim());
        if (mLlDateTime.getVisibility() == View.VISIBLE) {
            mNote.setNoteTime(String.format("%s %s", mSelectedDate, mSelectedTime));
        } else {
            mNote.setNoteTime("");
        }
        mNote.setCreatedTime(String.valueOf(DateFormat.format(
                getString(R.string.ddmmyyyy_hhmmss_format), new Date())));
        mNote.setBackgroundColor(mColor);
        // add new note into db
        new NoteRepo().updateNote(mNote);
        // save images of Note
        NoteImageRepo dbNoteImage = new NoteImageRepo();
        dbNoteImage.deleteNoteImage(mNote.getNoteID());
        for (String noteImage : mImageList) {
            NoteImage noteImg = new NoteImage();
            noteImg.setNoteId(mNote.getNoteID());
            noteImg.setImgPath(noteImage);
            dbNoteImage.addNoteImage(noteImg);
        }
    }

    private void setUpForNavigationButton() {
        mListNote = ListUtil.orderByCreatedTime(new NoteRepo().getAllNotes());
        adapter = new CustomGridViewNotesAdapter(EditActivity.this, mListNote);
        for (int i = 0; i < mListNote.size(); i++) {
            if (mListNote.get(i).getNoteID() == mNote.getNoteID()) {
                mPosOfNote = i;
            }
        }
        if (mPosOfNote - 1 < 0) {
            disableButton(mImbPrevious, R.drawable.ic_left_disable);
        }
        if ((mPosOfNote + 1) >= (mListNote.size())) {
            disableButton(mImbForward, R.drawable.ic_right_disable);
        }
    }

    private void disableButton(ImageButton imgButton, int icon) {
        imgButton.setImageResource(icon);
        imgButton.setClickable(false);
        imgButton.setBackgroundResource(R.drawable.button_color_disable);
    }

    private void moveToPreviousNote() {
        if (mPosOfNote - 1 >= 0) {
            if (mListNote.get(mPosOfNote - 1) != null) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra(EditActivity.sNOTE, (Note) adapter.getItem(mPosOfNote - 1));
                startActivity(intent);
            }
        }
    }

    private void moveToNextNote() {
        if ((mPosOfNote + 1) < (mListNote.size())) {
            if (mListNote.get(mPosOfNote + 1) != null) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra(EditActivity.sNOTE, (Note) adapter.getItem(mPosOfNote + 1));
                startActivity(intent);
            }
        }
    }
}

package com.example.kienpt.note;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.example.kienpt.note.bean.Note;

import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EditActivity extends ActivityParent {
    private Note mNote;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_left);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.mipmap.ic_launcher);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7e1b7eff")));
        initView();

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

        getData();
        ImageButton imbDel = (ImageButton) findViewById(R.id.btn_delete);
        ImageButton imbShare = (ImageButton) findViewById(R.id.btn_share);
        ImageButton imbPrevious = (ImageButton) findViewById(R.id.btn_previous);
        ImageButton imbForward = (ImageButton) findViewById(R.id.btn_forward);
        imbDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });
        imbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareNote();
            }
        });
        imbPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareNote();
            }
        });
        imbForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareNote();
            }
        });
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
            case R.id.mn_new_note:
                Intent intent = new Intent(EditActivity.this, AddActivity.class);
                startActivity(intent);
                return true;
            case R.id.mn_change_color:
                changeBackgroundColor();
                return true;
            case R.id.mn_save:
                saveNote();
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
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM");
                    mSelectedDate = df.format(mCalendar.getTime());
                    updateAdapterForDateSpinner("Other...");
                    break;
                case 1:
                    mSelectedDate = getTomorrow();
                    updateAdapterForDateSpinner("Other...");
                    break;
                case 2:
                    mSelectedDate = getDayOfNextWeek();
                    updateAdapterForDateSpinner("Other...");
                    break;
                case 3:
                    if (selectedDate.equals("Other...")) {
                        final int mYear, mMonth, mDay;
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(EditActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                          int dayOfMonth) {
                                        String s = String.format("%s/%s/%s", dayOfMonth, monthOfYear + 1, year);
                                        updateAdapterForDateSpinner(s);
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.setTitle("Choose Date");
                        datePickerDialog.show();
                    }
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
                    mSelectedTime = "09:00";
                    updateAdapterForTimeSpinner("Other...");
                    break;
                case 1:
                    mSelectedTime = "13:00";
                    updateAdapterForTimeSpinner("Other...");
                    break;
                case 2:
                    mSelectedTime = "17:00";
                    updateAdapterForTimeSpinner("Other...");
                    break;
                case 3:
                    mSelectedTime = "20:00";
                    updateAdapterForTimeSpinner("Other...");
                    break;
                case 4:
                    if (selectedTime.equals("Other...")) {
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(EditActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        String selectedTime = String.format("%s:%s",
                                                selectedHour, selectedMinute);
                                        updateAdapterForTimeSpinner(selectedTime);
                                    }
                                }, hour, minute, false);
                        timePickerDialog.setTitle("Choose Time");
                        timePickerDialog.show();
                    }
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
            mNote = (Note) getIntent().getSerializableExtra("EDIT");
            getActionBar().setTitle(mNote.getNoteTitle());
            mEtTitle.setText(mNote.getNoteTitle());
            mEtContent.setText(mNote.getNoteContent());
            mTvDateTime.setText(mNote.getCreatedTime());// Set up for date spinner
            switch (mNote.getBackgroundColor()) {
                case "Yellow":
                    mLlNote.setBackgroundColor(
                            getResources().getColor(R.color.colorYellow));
                    break;
                case "Green":
                    mLlNote.setBackgroundColor(
                            getResources().getColor(R.color.colorGreen));
                    break;
                case "Blue":
                    mLlNote.setBackgroundColor(
                            getResources().getColor(R.color.colorBlue));
                    break;
                default:
                    mLlNote.setBackgroundColor(
                            getResources().getColor(R.color.colorWhite));
                    break;
            }
            if(!mNote.getNoteTime().equals("")){
                mLlDateTime.setVisibility(View.VISIBLE);
                mTvAlarm.setVisibility(View.GONE);
                String[] datetime = mNote.getNoteTime().split(" ");
                updateAdapterForDateSpinner(datetime[0]);
                updateAdapterForTimeSpinner(datetime[1]);
            }
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
                        MyDatabaseHelper db = new MyDatabaseHelper(EditActivity.this);
                        db.deleteNote(mNote);
                        Intent mainIntent = new Intent(EditActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        break;
                    // click Cancel
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
        startActivity(Intent.createChooser(share, "Share Via"));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveNote(){
        mNote.setNoteTitle(mEtTitle.getText().toString());
        mNote.setNoteContent(mEtContent.getText().toString());
        if (mLlDateTime.getVisibility() == View.VISIBLE) {
            mNote.setNoteTime(String.format("%s %s", mSelectedDate, mSelectedTime));
        } else {
            mNote.setNoteTime("");
        }
        //get create datetime
        mCalendar = Calendar.getInstance();
        mNote.setCreatedTime(String.format("%s", convert(mCalendar, "dd/MM HH:mm")));
        mNote.setBackgroundColor(mColor);
        // add new note into db
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        db.updateNote(mNote);
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }
}

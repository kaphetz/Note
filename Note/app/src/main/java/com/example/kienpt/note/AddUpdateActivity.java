package com.example.kienpt.note;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kienpt.note.Bean.Note;

import java.util.Calendar;
import java.util.Date;

public class AddUpdateActivity extends Activity {
    private Spinner mSpnDate;
    private Spinner mSpnTime;
    private LinearLayout mLlDateTime;
    private TextView mTvAlarm;
    private TextView mTvDateTime;
    private EditText mEtTitle;
    private EditText mEtContent;
    private Calendar mCalendar = Calendar.getInstance();

    private String mSpinnerDate = "";
    private String mSpinnerTime = "";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update); // lấy ActionBar
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_previous);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.drawable.ic_note);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7eafc12b")));
        getActionBar().setTitle("Note");

        mSpnDate = (Spinner) findViewById(R.id.spn_date);
        mSpnTime = (Spinner) findViewById(R.id.spn_time);
        mLlDateTime = (LinearLayout) findViewById(R.id.ll_dateTime);
        mTvAlarm = (TextView) findViewById(R.id.tv_alarm);
        mTvDateTime = (TextView) findViewById(R.id.tv_dateTime);
        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEtContent = (EditText) findViewById(R.id.et_content);
        //Thu Aug 03 16:29:14 GMT+07:00 2017
        String[] parts = mCalendar.getTime().toString().split(" ");
        String[] listDate = {"Today", "Tomorrow", dayOfNextWeek(parts[0]), "Other"};
        ArrayAdapter<String> spinnerToAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listDate);
        spinnerToAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpnDate.setAdapter(spinnerToAdapter);
        mSpnDate.setOnItemSelectedListener(new DateSpinnerInfo());

        String[] listTime = {"09:00", "13:00", "17:00", "20:00", "Other"};
        ArrayAdapter<String> spinner2Adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listTime);
        spinner2Adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpnTime.setAdapter(spinner2Adapter);
        mSpnTime.setOnItemSelectedListener(new TimeSpinnerInfo());
        Calendar now = Calendar.getInstance();
        mTvDateTime.setText(convert(now, "dd/MM/YYYY hh:mm"));
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
            case R.id.mnSave:
                AddNewNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DateSpinnerInfo implements AdapterView.OnItemSelectedListener {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onItemSelected(AdapterView<?> spinner, View selectedView,
                                   int selectedIndex, long id) {
            String selectedDate = spinner.getItemAtPosition(selectedIndex).toString();
            switch (selectedDate) {
                case "Today":
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM");
                    mSpinnerDate = df.format(mCalendar.getTime());
                    break;
                case "Tomorrow":
                    mSpinnerDate = getTomorrow();
                    break;
                case "Other":
                    break;
                default:
                    mSpinnerDate = getDayOfNextWeek();
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
            switch (selectedTime) {
                case "09:00":
                    mSpinnerTime = "09:00";
                    break;
                case "13:00":
                    mSpinnerTime = "13:00";
                    break;
                case "17:00":
                    mSpinnerTime = "17:00";
                    break;
                case "20:00":
                    mSpinnerTime = "20:00";
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> spinner) {
            //do something
        }
    }

    public void showDateTimePicker(View v) {
        mLlDateTime.setVisibility(View.VISIBLE);
        mTvAlarm.setVisibility(View.GONE);
    }

    public void hideDateTimePicker(View v) {
        mLlDateTime.setVisibility(View.GONE);
        mTvAlarm.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void AddNewNote() {
        Note note = new Note();
        note.setNoteTitle(mEtTitle.getText().toString());
        note.setNoteContent(mEtContent.getText().toString());
        if (mLlDateTime.getVisibility() == View.VISIBLE) {
            note.setNoteTime(String.format("%s %s", mSpinnerDate, mSpinnerTime));
        } else {
            note.setNoteTime("");
        }
        //get create datetime
        mCalendar = Calendar.getInstance();
        note.setCreatedTime(String.format("%s", convert(mCalendar, "dd/MM HH:mm")));

        // add new note into db
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        db.addNote(note);
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    public String dayOfNextWeek(String dayOfWeek) {
        switch (dayOfWeek) {
            case "Mon":
                return "Next Monday";
            case "Tue":
                return "Next Tuesday";
            case "Wed":
                return "Next Wednesday";
            case "Thu":
                return "Next Thursday";
            case "Fri":
                return "Next Friday";
            case "Sat":
                return "Next Saturday";
            default:
                return "Next Sunday";

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getTomorrow() {
        Date dt = new Date();
        mCalendar.setTime(dt);
        mCalendar.add(Calendar.DATE, 1);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM");
        return df.format(mCalendar.getTime());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getDayOfNextWeek() {
        Date dt = new Date();
        mCalendar.setTime(dt);
        mCalendar.add(Calendar.DATE, 7);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM");
        return df.format(mCalendar.getTime());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String convert(Calendar calendar, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(calendar.getTime());
    }
}

package com.example.kienpt.note;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
    private RelativeLayout mRlNote;
    private Calendar mCalendar = Calendar.getInstance();

    private String mSpinnerDate = "";
    private String mSpinnerTime = "";
    private String mColor = "";
    private AlertDialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update); // lấy ActionBar
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_previous);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(R.mipmap.ic_launcher);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7e1b7eff")));
        getActionBar().setTitle("Note");

        mSpnDate = (Spinner) findViewById(R.id.spn_date);
        mSpnTime = (Spinner) findViewById(R.id.spn_time);
        mLlDateTime = (LinearLayout) findViewById(R.id.ll_dateTime);
        mTvAlarm = (TextView) findViewById(R.id.tv_alarm);
        mTvDateTime = (TextView) findViewById(R.id.tv_dateTime);
        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mRlNote = (RelativeLayout) findViewById(R.id.rl_note);
        //Thu Aug 03 16:29:14 GMT+07:00 2017
        String[] parts = mCalendar.getTime().toString().split(" ");
        String[] listDate = {"Today", "Tomorrow", dayOfNextWeek(parts[0]), "Other..."};
        ArrayAdapter<String> spinnerToAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listDate);
        spinnerToAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpnDate.setAdapter(spinnerToAdapter);
        mSpnDate.setOnItemSelectedListener(new DateSpinnerInfo());

        String[] listTime = {"09:00", "13:00", "17:00", "20:00", "Other..."};
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
                addNewNote();
                return true;
            case R.id.mnChangeColor:
                changeBackgroundColor();
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
                case "Other...":
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
                case "Other...":
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddUpdateActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.clock_xml, null);
                    mBuilder.setView(mView).setTitle("Choose Time");
                    mBuilder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {

                                TimePicker timePicker =
                                        (TimePicker) findViewById(R.id.tp_clock);
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    int hour = 0;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                        hour = timePicker.getHour();
                                    }else{
                                        hour = timePicker.getCurrentHour();
                                    }
                                    int minute = 0;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                        minute = timePicker.getMinute();
                                    }else{
                                        minute = timePicker.getCurrentMinute();
                                    }
                                    dialog.dismiss();
                                    Toast.makeText(AddUpdateActivity.this, String.format("%s", hour), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    mBuilder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog = mBuilder.create();
                    dialog.show();
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
    public void addNewNote() {
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
        note.setBackgroundColor(mColor);
        // add new note into db
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        db.addNote(note);
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void changeBackgroundColor() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.alert_window, null);
        mBuilder.setView(mView).setTitle("Choose Color").setIcon(R.drawable.ic_change_color);
        Button btnWhite = (Button) mView.findViewById(R.id.btn_white);
        Button btnYellow = (Button) mView.findViewById(R.id.btn_yellow);
        Button btnGreen = (Button) mView.findViewById(R.id.btn_green);
        Button btnBlue = (Button) mView.findViewById(R.id.btn_blue);
        btnWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                mColor = "White";
                dialog.dismiss();
            }
        });
        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorYellow));
                mColor = "Yellow";
                dialog.dismiss();
            }
        });
        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                mColor = "Green";
                dialog.dismiss();
            }
        });
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorBlue));
                mColor = "Blue";
                dialog.dismiss();
            }
        });

        dialog = mBuilder.create();
        dialog.show();

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

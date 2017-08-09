package com.example.kienpt.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.N)
public class ActivityParent extends Activity {
    protected ListView mLvCamera;
    protected GridView mGvImage;
    protected Spinner mSpnDate;
    protected Spinner mSpnTime;
    protected LinearLayout mLlDateTime;
    protected TextView mTvAlarm;
    protected TextView mTvDateTime;
    protected EditText mEtTitle;
    protected EditText mEtContent;
    protected LinearLayout mLlNote;
    protected LinearLayout mLlImageContainer;
    protected Calendar mCalendar = Calendar.getInstance();
    protected String mSelectedDate = "";
    protected String mSelectedTime = "";
    protected String mColor = "";
    protected AlertDialog dialog;
    protected Context mContext;
    protected String[] parts = mCalendar.getTime().toString().split(" ");
    protected ArrayAdapter dateAdapter;
    protected ArrayAdapter timeAdapter;

    public List<String> listDate = new ArrayList<>();
    public List<String> listTime = new ArrayList<>();

    //Khoi tao initview
    protected void initView() {
        listDate.addAll(Arrays.asList(getString(R.string.today),
                getString(R.string.tomorrow),
                dayOfNextWeek(parts[0]),
                getString(R.string.other)));
        listTime.addAll(Arrays.asList(getString(R.string.hour_9h),
                getString(R.string.hour_13h),
                getString(R.string.hour_17h),
                getString(R.string.hour_20h),
                getString(R.string.other)));
        mSpnDate = (Spinner) findViewById(R.id.spn_date);
        mSpnTime = (Spinner) findViewById(R.id.spn_time);
        mLlDateTime = (LinearLayout) findViewById(R.id.ll_dateTime);
        mLlNote = (LinearLayout) findViewById(R.id.ll_note);
        mLlImageContainer = (LinearLayout) findViewById(R.id.lL_image_container);
        mGvImage = (GridView) findViewById(R.id.gv_listImage);
        mTvAlarm = (TextView) findViewById(R.id.tv_alarm);
        mTvDateTime = (TextView) findViewById(R.id.tv_dateTime);
        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEtContent = (EditText) findViewById(R.id.et_content);
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

    // update adapter of date spinner
    public void updateAdapterForDateSpinner(String newDay) {
//        dateAdapter = new ArrayAdapter<>(context,
//                android.R.layout.simple_spinner_item, list);
        dateAdapter.remove(listDate.get(3));
        dateAdapter.insert(newDay, 3);
        dateAdapter.notifyDataSetChanged();
    }

    // update adapter of time spinner
    public void updateAdapterForTimeSpinner(String newTime) {
        timeAdapter.remove(listTime.get(4));
        timeAdapter.insert(newTime, 4);
        timeAdapter.notifyDataSetChanged();
    }

    public void showDateTimePicker(View v) {
        mLlDateTime.setVisibility(View.VISIBLE);
        mTvAlarm.setVisibility(View.GONE);
    }

    public void hideDateTimePicker(View v) {
        mLlDateTime.setVisibility(View.GONE);
        mTvAlarm.setVisibility(View.VISIBLE);
    }

    //get time of tomorrow (day, month, yeah)
    public String getTomorrow() {
        Date dt = new Date();
        mCalendar.setTime(dt);
        mCalendar.add(Calendar.DATE, 1);
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.ddmmyyyy_format));
        return df.format(mCalendar.getTime());
    }


    //get time of next 7 days (day, month, yeah)
    public String getDayOfNextWeek() {
        Date dt = new Date();
        mCalendar.setTime(dt);
        mCalendar.add(Calendar.DATE, 7);
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.ddmmyyyy_format));
        return df.format(mCalendar.getTime());
    }

    //convert from date to string type
    public String convert(Calendar calendar, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(calendar.getTime());
    }

    public void changeBackgroundColor() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.change_color_alert, null);
        mBuilder.setView(mView).setTitle(R.string.choose_color).setIcon(R.drawable.ic_change_color);
        Button btnWhite = (Button) mView.findViewById(R.id.btn_white);
        Button btnYellow = (Button) mView.findViewById(R.id.btn_yellow);
        Button btnGreen = (Button) mView.findViewById(R.id.btn_green);
        Button btnBlue = (Button) mView.findViewById(R.id.btn_blue);
        btnWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                mColor = "White";
                dialog.dismiss();
            }
        });
        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorYellow));
                mColor = "Yellow";
                dialog.dismiss();
            }
        });
        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                mColor = "Green";
                dialog.dismiss();
            }
        });
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlNote.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorBlue));
                mColor = "Blue";
                dialog.dismiss();
            }
        });

        dialog = mBuilder.create();
        dialog.show();
    }

    // When click option "Other..." in TimeSpinner
    public void chooseOptionOtherTime(Context context, String selectedTime) {
        if (selectedTime.equals(getString(R.string.other))) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String time = String.format("%s:%s",
                                    selectedHour, selectedMinute);
                            updateAdapterForTimeSpinner(time);
                            mSelectedTime = time;
                        }
                    }, hour, minute, false);
            timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        mSpnTime.setSelection(0);
                    }
                }
            });
            timePickerDialog.setTitle(getString(R.string.choose_time));
            timePickerDialog.show();
        }
    }

    // When click option "Other..." in DateSpinner
    public void chooseOptionOtherDate(Context context, final String selectedDate) {
        if (selectedDate.equals(getString(R.string.other))) {
            final int mYear, mMonth, mDay;
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            String date = String.format("%s/%s/%s", dayOfMonth, monthOfYear + 1, year);
                            updateAdapterForDateSpinner(date);
                            mSelectedDate = date;
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        mSpnDate.setSelection(0);
                    }
                }
            });
            datePickerDialog.setTitle(getString(R.string.choose_date));
            datePickerDialog.show();
        }
    }
}

package com.example.kienpt.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.N)
public class ActivityParent extends Activity {
    protected ListView mLvCamera;
    protected Spinner mSpnDate;
    protected Spinner mSpnTime;
    protected LinearLayout mLlDateTime;
    protected TextView mTvAlarm;
    protected TextView mTvDateTime;
    protected EditText mEtTitle;
    protected EditText mEtContent;
    protected LinearLayout mLlNote;
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
        listDate.addAll(Arrays.asList("Today", "Tomorrow", dayOfNextWeek(parts[0]), "Other..."));
        listTime.addAll(Arrays.asList("09:00", "13:00", "17:00", "20:00", "Other..."));
        mSpnDate = (Spinner) findViewById(R.id.spn_date);
        mSpnTime = (Spinner) findViewById(R.id.spn_time);
        mLlDateTime = (LinearLayout) findViewById(R.id.ll_dateTime);
        mTvAlarm = (TextView) findViewById(R.id.tv_alarm);
        mTvDateTime = (TextView) findViewById(R.id.tv_dateTime);
        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mLlNote = (LinearLayout) findViewById(R.id.ll_note);
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

    public void updateAdapterForDateSpinner(String newDay) {
//        dateAdapter = new ArrayAdapter<>(context,
//                android.R.layout.simple_spinner_item, list);
        dateAdapter.remove(listDate.get(3));
        dateAdapter.insert(newDay, 3);
        dateAdapter.notifyDataSetChanged();
    }

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

    public String getTomorrow() {
        Date dt = new Date();
        mCalendar.setTime(dt);
        mCalendar.add(Calendar.DATE, 1);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM");
        return df.format(mCalendar.getTime());
    }

    public String getDayOfNextWeek() {
        Date dt = new Date();
        mCalendar.setTime(dt);
        mCalendar.add(Calendar.DATE, 7);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM");
        return df.format(mCalendar.getTime());
    }

    public String convert(Calendar calendar, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(calendar.getTime());
    }

    public void changeBackgroundColor() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.change_color_alert, null);
        mBuilder.setView(mView).setTitle("Choose Color").setIcon(R.drawable.ic_change_color);
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
}

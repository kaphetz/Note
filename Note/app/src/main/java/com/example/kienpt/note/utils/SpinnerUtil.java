package com.example.kienpt.note.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.kienpt.note.R;

import java.util.Calendar;
import java.util.List;

public final class SpinnerUtil {
    private static final int LAST_OPTION_OF_DATE_SPINNER = 3;
    private static final int LAST_OPTION_OF_TIME_SPINNER = 4;
    private static final int FIRST_OPTION = 0;

    // update adapter of date spinner
    public static void updateAdapterForDateSpinner(ArrayAdapter<String> dateAdapter,
                                            String newDay, List<String> listDate) {
        dateAdapter.remove(listDate.get(LAST_OPTION_OF_DATE_SPINNER));
        dateAdapter.insert(newDay, LAST_OPTION_OF_DATE_SPINNER);
        dateAdapter.notifyDataSetChanged();
    }

    // update adapter of time spinner
    public static void updateAdapterForTimeSpinner(ArrayAdapter<String> timeAdapter,
                                            String newTime, List<String> listTime) {
        timeAdapter.remove(listTime.get(LAST_OPTION_OF_TIME_SPINNER));
        timeAdapter.insert(newTime, LAST_OPTION_OF_TIME_SPINNER);
        timeAdapter.notifyDataSetChanged();
    }

    public static void chooseOptionOtherTime(Context context,
                                      final Spinner spnTime,
                                      final ArrayAdapter<String> timeAdapter,
                                      final List<String> listTime,
                                      final String selectedTime) {
        final Calendar c = Calendar.getInstance();
        final String[] time = new String[1];
        if (selectedTime.equals(context.getString(R.string.other))) {
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            time[0] = selectedMinute < 10 ? String.format("%s:0%s",
                                    selectedHour, selectedMinute) : String.format("%s:%s",
                                    selectedHour, selectedMinute);
                            SpinnerUtil.updateAdapterForTimeSpinner(timeAdapter, time[0], listTime);
                        }
                    }, hour, minute, false);
            timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    context.getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                spnTime.setSelection(FIRST_OPTION);
                            }
                        }
                    });
            timePickerDialog.setTitle(context.getString(R.string.choose_time));
            timePickerDialog.show();
        }
    }

    // When click option "Other..." in DateSpinner
    public static void chooseOptionOtherDate(Context context,
                                      final Spinner spnDate,
                                      final ArrayAdapter<String> dateAdapter,
                                      final List<String> listDate,
                                      final String selectedDate) {
        if (selectedDate.equals(context.getString(R.string.other))) {
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
                            SpinnerUtil.updateAdapterForDateSpinner(dateAdapter, date, listDate);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    context.getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                spnDate.setSelection(FIRST_OPTION);
                            }
                        }
                    });
            datePickerDialog.setTitle(context.getString(R.string.choose_date));
            datePickerDialog.show();
        }
    }
}

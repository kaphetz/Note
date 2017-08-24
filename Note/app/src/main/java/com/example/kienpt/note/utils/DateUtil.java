package com.example.kienpt.note.utils;

import android.text.format.DateFormat;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

public class DateUtil  {
    private static final int TOMORROW = 1;
    private static final int NEXT_WEEK = 7;
    private static final String dateFormat = "dd/MM/yyyy";

    private static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    //get time of tomorrow (day, month, yeah)
    public static String getTomorrow() {
        Date date = new Date();
        date = addDays(date, TOMORROW);
        return String.valueOf(DateFormat.format(dateFormat, date));
    }

    //get time of next 7 days (day, month, yeah)
    public static String getDayOfNextWeek() {
        Date date = new Date();
        date = addDays(date, NEXT_WEEK);
        return String.valueOf(DateFormat.format(dateFormat, date));
    }

    public static String dayOfNextWeek(int dayOfWeek) {
        String weekday = new DateFormatSymbols().getWeekdays()[dayOfWeek];
        return String.format("Next %s", weekday);
    }
}


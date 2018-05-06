package cm.g2i.lalalaworker.controllers.services;

import android.content.Context;

import cm.g2i.lalalaworker.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Sim'S on 02/08/2017.
 */

public class Date {
    private int year;
    private int month;
    private int day;
    private int date;
    private int hours;
    private int minutes;
    private int second;
    private int millisecond;

    public Date() {
        GregorianCalendar d = new GregorianCalendar();
        year = d.get(Calendar.YEAR);
        month = d.get(Calendar.MONTH);
        day = d.get(Calendar.DAY_OF_WEEK);
        date = d.get(Calendar.DATE);
        hours = d.get(Calendar.HOUR);
        minutes = d.get(Calendar.MINUTE);
        second = d.get(Calendar.SECOND);
        millisecond = d.get(Calendar.MILLISECOND);
    }

    public Date(String s){
        long time = Long.parseLong(s);
        GregorianCalendar d = new GregorianCalendar();
        d.setTime(new java.util.Date(time));
        year = d.get(Calendar.YEAR);
        month = d.get(Calendar.MONTH);
        day = d.get(Calendar.DAY_OF_WEEK);
        date = d.get(Calendar.DATE);
        hours = d.get(Calendar.HOUR);
        minutes = d.get(Calendar.MINUTE);
        second = d.get(Calendar.SECOND);
        millisecond = d.get(Calendar.MILLISECOND);
    }

    public long getTimeMillis(){
        GregorianCalendar d = new GregorianCalendar(year, month, date, hours, minutes, second);
        d.set(Calendar.MILLISECOND, millisecond);
        return d.getTimeInMillis();
    }

    public int compareTo(Date date){
        if (getTimeMillis()>date.getTimeMillis()) return 1;
        else if (getTimeMillis()<date.getTimeMillis()) return -1;
        return 0;
    }

    public String dateToString(Context context){
        String[] DAYS = context.getResources().getStringArray(R.array.days);
        String[] MONTHS = context.getResources().getStringArray(R.array.months);
        return DAYS[day-1] + " " + date + " " + MONTHS[month] + " " + year + " " + hours + ":" + minutes;
    }

    public static long getTimeMillisString(String date){
        return new Date(date).getTimeMillis();
    }

    public static String TimeMillisdateToString(String date, Context context){
        return new Date(date).dateToString(context);
    }
}

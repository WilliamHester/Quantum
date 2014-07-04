package me.williamhester.Quantum.models;

import java.util.Calendar;

/**
 * Created by William on 5/29/13.
 */
public class Date {

    private int mDayOfMonth, mDayOfWeek, mMonth, mYear, mWeekOfYear, mDayOfYear;

    /**
     * Creates a new date object with specific parts to be filled in
     */
    public Date() {}

    /**
     * Creates a new Date object with all fields filled in.
     *
     * @param dayOfMonth the day of the month
     * @param dayOfWeek the day of the week (Sunday, Monday, etc.)
     * @param month the month of the year
     * @param year the year
     * @param weekOfYear the week of the year
     */
    public Date(int dayOfMonth, int dayOfWeek, int month, int year,
        int weekOfYear, int dayOfYear) {
        mDayOfMonth = dayOfMonth;
        mDayOfWeek = dayOfWeek;
        mMonth = month;
        mYear = year;
        mWeekOfYear = weekOfYear;
        mDayOfYear = dayOfYear;
    }

    /**
     * Creates a new date object with the current date and fills in all fields
     * with the current system time.
     */
    public static Date today() {
        Date d = new Date();
        Calendar c = Calendar.getInstance();
        d.setDayOfMonth(c.get(Calendar.DAY_OF_MONTH));
        d.setDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
        d.setMonth(c.get(Calendar.MONTH));
        d.setYear(c.get(Calendar.YEAR));
        d.setWeekOfYear(c.get(Calendar.WEEK_OF_YEAR));
        d.setDayOfYear(c.get(Calendar.DAY_OF_YEAR));
        return d;
    }

    public int getDayOfMonth() {
        return mDayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        mDayOfMonth = dayOfMonth;
    }

    public int getDayOfWeek() {
        return mDayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        mDayOfWeek = dayOfWeek;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        mMonth = month;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }

    public int getWeekOfYear() {
        return mWeekOfYear;
    }

    public void setWeekOfYear(int weekOfYear) {
        mWeekOfYear = weekOfYear;
    }

    public int getDayOfYear(){
        return mDayOfYear;
    }

    public void setDayOfYear(int dayOfYear) {
        mDayOfYear = dayOfYear;
    }

    public String toString() {
        return mDayOfMonth+ "/" + mMonth + "/" + mYear;
    }
}

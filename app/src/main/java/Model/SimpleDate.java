package Model;

import org.joda.time.DateTime;

public class SimpleDate {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public SimpleDate() {
        DateTime dt = new DateTime();
        this.year = dt.getYear();
        this.month = dt.getMonthOfYear();
        this.day = dt.getDayOfMonth();
        this.hour = dt.getHourOfDay();
        this.minute = dt.getMinuteOfHour();
        this.second = dt.getSecondOfMinute();
    }

    public SimpleDate(DateTime dt) {
        this.year = dt.getYear();
        this.month = dt.getMonthOfYear();
        this.day = dt.getDayOfMonth();
        this.hour = dt.getHourOfDay();
        this.minute = dt.getMinuteOfHour();
        this.second = dt.getSecondOfMinute();
    }

    public SimpleDate(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}

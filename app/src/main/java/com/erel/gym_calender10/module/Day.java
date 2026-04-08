package com.erel.gym_calender10.module;

public class Day {
    int day,month;
    String time;

    public Day(int day, int month) {
        this.day = day;
        this.month = month;
        this.time = "12:00"; // default
    }

    public Day(int day, int month, String time) {
        this.day = day;
        this.month = month;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    @Override
    public String toString() {
        return "Day{" +
                "day=" + day +
                ", month=" + month +
                ", time='" + time + '\'' +
                '}';
    }
}

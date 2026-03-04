package com.erel.gym_calender10.module;

public class Day {
    int day,month;

    public Day(int day, int month) {
        this.day = day;
        this.month = month;
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
                '}';
    }
}

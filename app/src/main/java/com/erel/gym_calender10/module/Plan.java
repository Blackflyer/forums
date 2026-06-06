package com.erel.gym_calender10.module;

import java.util.ArrayList;
import java.util.List;

public class Plan {


    private String planId;      // מזהה ייחודי ל-Firebase
    private String userId;      // למי התוכנית שייכת?
    private String date;
    private String planName;
    private ArrayList<Exercise> plan;
    private String type;
    private String time;

    /**
     * בנאי ליצירת תוכנית אימון עם שעת ברירת מחדל (12:00).
     * @param planId מזהה התוכנית.
     * @param userId מזהה המשתמש.
     * @param date תאריך האימון.
     * @param planName שם התוכנית.
     * @param type סוג האימון.
     */
    public Plan(String planId, String userId, String date, String planName,String type) {
        this.planId = planId;
        this.userId = userId;
        this.date = date;
        this.planName = planName;
        this.plan = new ArrayList<Exercise>();
        this.type = type;
        this.time = "12:00"; // default
    }

    /**
     * בנאי מלא ליצירת תוכנית אימון הכולל שעה.
     * @param planId מזהה התוכנית.
     * @param userId מזהה המשתמש.
     * @param date תאריך האימון.
     * @param planName שם התוכנית.
     * @param type סוג האימון.
     * @param time שעת האימון.
     */
    public Plan(String planId, String userId, String date, String planName,String type, String time) {
        this.planId = planId;
        this.userId = userId;
        this.date = date;
        this.planName = planName;
        this.plan = new ArrayList<Exercise>();
        this.type = type;
        this.time = time;
    }

    /**
     * בנאי ריק הנדרש עבור עבודה עם Firebase.
     */
    public Plan(){

    }

    /** @return שעת האימון המתוכננת. */
    public String getTime() {
        return time;
    }

    /** @param time הגדרת שעת האימון. */
    public void setTime(String time) {
        this.time = time;
    }

    /** @return תאריך האימון. */
    public String getDate() {
        return date;
    }

    /** @return מזהה התוכנית ב-Firebase. */
    public String getPlanId() {
        return planId;
    }

    /** @return מזהה המשתמש לו שייכת התוכנית. */
    public String getUserId() {
        return userId;
    }

    /** @param date הגדרת תאריך האימון. */
    public void setDate(String date) {
        this.date = date;
    }

    /** @param planId הגדרת מזהה התוכנית. */
    public void setPlanId(String planId) {
        this.planId = planId;
    }

    /** @param userId הגדרת מזהה המשתמש. */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * מוסיפה תרגיל לרשימת התרגילים של התוכנית.
     * @param exercise התרגיל להוספה.
     */
    public void addExercise(Exercise exercise) {
        this.plan.add(exercise);
    }

    /** @return שם תוכנית האימון. */
    public String getPlanName() {
        return planName;
    }

    /** @param planName הגדרת שם תוכנית האימון. */
    public void setPlanName(String planName) {
        this.planName = planName;
    }

    /** @return רשימת התרגילים הכלולים בתוכנית. */
    public ArrayList<Exercise> getPlan() {
        return plan;
    }

    /** @param plan הגדרת רשימת התרגילים. */
    public void setPlan(ArrayList<Exercise> plan) {
        this.plan = plan;
    }

    /** @return סוג התוכנית (למשל A, B, C). */
    public String getType() {
        return type;
    }

    /** @param type הגדרת סוג התוכנית. */
    public void setType(String type) {
        this.type = type;
    }
}

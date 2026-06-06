package com.erel.gym_calender10.module;

public class ProgressRecord {
    private String id;
    private String date;
    private float weight;
    private int reps;

    /**
     * בנאי ריק הנדרש עבור עבודה עם Firebase.
     */
    public ProgressRecord() {}

    /**
     * בנאי ליצירת רשומה של התקדמות.
     * @param date תאריך התיעוד.
     * @param weight המשקל שהורם.
     * @param reps מספר החזרות שבוצעו.
     */
    public ProgressRecord(String date, float weight, int reps) {
        this.date = date;
        this.weight = weight;
        this.reps = reps;
    }

    /** @return מזהה הרשומה. */
    public String getId() { return id; }
    /** @param id הגדרת מזהה הרשומה. */
    public void setId(String id) { this.id = id; }
    /** @return תאריך התיעוד. */
    public String getDate() { return date; }
    /** @param date הגדרת תאריך התיעוד. */
    public void setDate(String date) { this.date = date; }
    /** @return המשקל שתועד. */
    public float getWeight() { return weight; }
    /** @param weight הגדרת המשקל. */
    public void setWeight(float weight) { this.weight = weight; }
    /** @return מספר החזרות שתועדו. */
    public int getReps() { return reps; }
    /** @param reps הגדרת מספר החזרות. */
    public void setReps(int reps) { this.reps = reps; }
}
package com.erel.gym_calender10.module;

public class ProgressRecord {
    private String id;
    private String date;
    private float weight;
    private int reps;

    // קונסטרוקטור ריק חובה עבור Firebase
    public ProgressRecord() {}

    public ProgressRecord(String date, float weight, int reps) {
        this.date = date;
        this.weight = weight;
        this.reps = reps;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }
    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }
}
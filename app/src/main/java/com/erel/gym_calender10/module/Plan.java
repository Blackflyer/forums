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

    public Plan(String planId, String userId, String date, String planName,String type) {
        this.planId = planId;
        this.userId = userId;
        this.date = date;
        this.planName = planName;
        this.plan = new ArrayList<Exercise>();
        this.type = type;
    }

    public Plan(){

    }

    public String getDate() {
        return date;
    }

    public String getPlanId() {
        return planId;
    }

    public String getUserId() {
        return userId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void addExercise(Exercise exercise) {
        this.plan.add(exercise);
    }


    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public ArrayList<Exercise> getPlan() {
        return plan;
    }

    public void setPlan(ArrayList<Exercise> plan) {
        this.plan = plan;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

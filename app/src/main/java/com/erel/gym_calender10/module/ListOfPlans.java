package com.erel.gym_calender10.module;

import java.util.ArrayList;

public class ListOfPlans {
    private String userId;
    private ArrayList<Plan> planArray;

    // 1. בנאי ריק - חובה עבור Firebase!
    public ListOfPlans() {
        this.planArray = new ArrayList<>();
    }

    // 2. בנאי חדש שמקבל רק את ה-ID (לזה שקראת לו בקוד שלך)
    public ListOfPlans(String userId) {
        this.userId = userId;
        this.planArray = new ArrayList<>(); // חובה לאתחל את הרשימה שלא תהיה null
    }

    public ListOfPlans(String userId, ArrayList<Plan> planArray) {
        this.userId = userId;
        this.planArray = planArray;
    }

    // 3. פונקציית ההוספה שחסרה לך (addPlan)
    public void addPlan(Plan plan) {
        // נוודא שהרשימה קיימת לפני שמוסיפים אליה
        if (this.planArray == null) {
            this.planArray = new ArrayList<>();
        }

        // בדיקה אם התוכנית כבר קיימת לפי ה-ID, ואם כן - החלפה שלה (בשביל עריכה)
        for (int i = 0; i < planArray.size(); i++) {
            if (planArray.get(i).getPlanId() != null && 
                planArray.get(i).getPlanId().equals(plan.getPlanId())) {
                planArray.set(i, plan);
                return;
            }
        }

        this.planArray.add(plan);
    }

    // --- Getters & Setters ---
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Plan> getPlanArray() {
        return planArray;
    }

    public void setPlanArray(ArrayList<Plan> planArray) {
        this.planArray = planArray;
    }
}
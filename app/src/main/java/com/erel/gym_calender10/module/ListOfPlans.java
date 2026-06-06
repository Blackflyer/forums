package com.erel.gym_calender10.module;

import java.util.ArrayList;

public class ListOfPlans {
    private String userId;
    private ArrayList<Plan> planArray;

    /**
     * בנאי ריק הנדרש עבור עבודה עם Firebase. מאתחל רשימה ריקה של תוכניות.
     */
    public ListOfPlans() {
        this.planArray = new ArrayList<>();
    }

    /**
     * בנאי המקבל מזהה משתמש ומאתחל רשימה ריקה.
     * @param userId מזהה המשתמש.
     */
    public ListOfPlans(String userId) {
        this.userId = userId;
        this.planArray = new ArrayList<>(); // חובה לאתחל את הרשימה שלא תהיה null
    }

    /**
     * בנאי מלא המקבל מזהה משתמש ורשימת תוכניות.
     * @param userId מזהה המשתמש.
     * @param planArray רשימת התוכניות.
     */
    public ListOfPlans(String userId, ArrayList<Plan> planArray) {
        this.userId = userId;
        this.planArray = planArray;
    }

    /**
     * מוסיפה תוכנית חדשה לרשימה. אם התוכנית כבר קיימת (לפי מזהה), היא תעודכן.
     * @param plan התוכנית להוספה או עדכון.
     */
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

    /**
     * מסירה תוכנית מהרשימה לפי המזהה שלה.
     * @param planId המזהה של התוכנית להסרה.
     */
    public void removePlan(String planId) {
        if (this.planArray != null) {
            for (int i = 0; i < planArray.size(); i++) {
                if (planArray.get(i).getPlanId() != null && 
                    planArray.get(i).getPlanId().equals(planId)) {
                    planArray.remove(i);
                    return;
                }
            }
        }
    }

    /** @return מזהה המשתמש. */
    public String getUserId() {
        return userId;
    }

    /** @param userId הגדרת מזהה המשתמש. */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** @return רשימת תוכניות האימון. */
    public ArrayList<Plan> getPlanArray() {
        return planArray;
    }

    /** @param planArray הגדרת רשימת תוכניות האימון. */
    public void setPlanArray(ArrayList<Plan> planArray) {
        this.planArray = planArray;
    }
}
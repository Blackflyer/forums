package com.erel.gym_calender10.module;

import java.util.ArrayList;
import java.util.List;

public class User {

    String id;
    String fname;
    String lname;
    String phone;
    String email;
    String password;
    ListOfPlans maarachedPlans;
    Boolean isAdmin;
    List<String> achievements = new ArrayList<>();

    /**
     * בנאי ליצירת משתמש חדש במערכת.
     * @param id מזהה המשתמש (מ-Firebase Auth).
     * @param fname שם פרטי.
     * @param lname שם משפחה.
     * @param phone מספר טלפון.
     * @param email כתובת אימייל.
     * @param password סיסמה (מוצפנת בדרך כלל).
     */
    public User(String id, String fname, String lname, String phone, String email, String password) {
        this.email = email;
        this.fname = fname;
        this.id = id;
        this.lname = lname;
        this.password = password;
        this.phone = phone;
        this.isAdmin = false;
        this.maarachedPlans = new ListOfPlans();
        this.achievements = new ArrayList<>();
    }

    /**
     * בנאי ריק הנדרש עבור עבודה עם Firebase.
     */
    public User() {}

    /**
     * מוסיפה תוכנית אימון חדשה לרשימת התוכניות של המשתמש.
     * @param plan התוכנית להוספה.
     */
    public void addNewPlanToUser(Plan plan) {
        if (this.maarachedPlans == null) {
            this.maarachedPlans = new ListOfPlans();
        }
        this.maarachedPlans.addPlan(plan);
    }

    /** @return אובייקט המכיל את רשימת תוכניות האימון של המשתמש. */
    public ListOfPlans getMaarachedPlans() {
        return maarachedPlans;
    }

    /** @param maarachedPlans הגדרת מערך התוכניות של המשתמש. */
    public void setMaarachedPlans(ListOfPlans maarachedPlans) {
        this.maarachedPlans = maarachedPlans;
    }

    /** @return האם המשתמש הוא מנהל מערכת. */
    public Boolean getAdmin() {
        return isAdmin;
    }

    /** @param admin הגדרת מצב הרשאות ניהול. */
    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    /** @return רשימת מזהי ההישגים שהמשתמש צבר. */
    public List<String> getAchievements() {
        return achievements;
    }

    /** @param achievements הגדרת רשימת הישגים. */
    public void setAchievements(List<String> achievements) {
        this.achievements = achievements;
    }

    /**
     * מוסיפה הישג למשתמש אם הוא לא קיים כבר ברשימה שלו.
     * @param achievementId מזהה ההישג להוספה.
     */
    public void addAchievement(String achievementId) {
        if (this.achievements == null) this.achievements = new ArrayList<>();
        if (!this.achievements.contains(achievementId)) {
            this.achievements.add(achievementId);
        }
    }

    /** @return כתובת האימייל של המשתמש. */
    public String getEmail() {
        return email;
    }

    /** @param email הגדרת כתובת אימייל. */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return השם הפרטי של המשתמש. */
    public String getFname() {
        return fname;
    }

    /** @param fname הגדרת שם פרטי. */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /** @return מזהה המשתמש הייחודי. */
    public String getId() {
        return id;
    }

    /** @param id הגדרת מזהה משתמש. */
    public void setId(String id) {
        this.id = id;
    }

    /** @return שם המשפחה של המשתמש. */
    public String getLname() {
        return lname;
    }

    /** @param lname הגדרת שם משפחה. */
    public void setLname(String lname) {
        this.lname = lname;
    }

    /** @return סיסמת המשתמש. */
    public String getPassword() {
        return password;
    }

    /** @param password הגדרת סיסמה. */
    public void setPassword(String password) {
        this.password = password;
    }

    /** @return מספר הטלפון של המשתמש. */
    public String getPhone() {
        return phone;
    }

    /** @param phone הגדרת מספר טלפון. */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** @return ייצוג טקסטואלי של אובייקט המשתמש על כל נתוניו. */
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", maarachedPlans=" + maarachedPlans +
                ", isAdmin=" + isAdmin +
                ", achievements=" + achievements +
                '}';
    }
}

package com.erel.gym_calender10.module;

public class User {

    String id;
    String fname;
    String lname;
    String phone;
    String email;
    String password;
   ListOfPlans MaarachedPlans;
    Boolean isAdmin;




    public User(String id, String fname, String lname, String phone, String email, String password) {
        this.email = email;
        this.fname = fname;
        this.id = id;
        this.lname = lname;
        this.password = password;
        this.phone = phone;
        this.isAdmin = false;
        this.MaarachedPlans = new ListOfPlans(); // אתחול הרשימה
    }

    public User()
    {

    }
    public void addNewPlanToUser(Plan plan) {
        if (this.MaarachedPlans == null) {
            this.MaarachedPlans = new ListOfPlans();
        }
        this.MaarachedPlans.addPlan(plan);
    }
    public ListOfPlans getMaarachedPlans() {
        return MaarachedPlans;
    }

    public void setMaarachedPlans(ListOfPlans maarachedPlans) {
        MaarachedPlans = maarachedPlans;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin =admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", MaarachedPlans=" + MaarachedPlans +
                ", isAdmin=" + isAdmin +
                '}';
    }
}

package com.erel.gym_calender10.module;

public class Achievement {
    private String id;
    private String name;
    private String description;
    private String icon; // emoji or resource name
    private boolean isUnlocked;
   /**
    * בנאי ריק הנדרש עבור עבודה עם Firebase.
    */
   public  Achievement() {}

    /**
     * בנאי מלא ליצירת אובייקט הישג.
     * @param id מזהה ההישג.
     * @param name שם ההישג.
     * @param description תיאור ההישג.
     * @param icon אייקון המייצג את ההישג.
     * @param isUnlocked האם ההישג פתוח.
     */
    public Achievement(String id, String name, String description, String icon, boolean isUnlocked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.isUnlocked = isUnlocked;
    }

    /** @return מזהה ההישג. */
    public String getId() { return id; }
    /** @return שם ההישג. */
    public String getName() { return name; }
    /** @return תיאור ההישג. */
    public String getDescription() { return description; }
    /** @return אייקון ההישג. */
    public String getIcon() { return icon; }
    /** @return האם ההישג פתוח. */
    public boolean isUnlocked() { return isUnlocked; }
    /** @param unlocked הגדרת מצב פתיחת ההישג. */
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
}
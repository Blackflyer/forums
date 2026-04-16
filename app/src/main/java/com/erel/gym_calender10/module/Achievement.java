package com.erel.gym_calender10.module;

public class Achievement {
    private String id;
    private String name;
    private String description;
    private String icon; // emoji or resource name
    private boolean isUnlocked;

    public Achievement(String id, String name, String description, String icon, boolean isUnlocked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.isUnlocked = isUnlocked;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
}
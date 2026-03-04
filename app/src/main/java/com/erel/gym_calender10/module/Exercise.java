package com.erel.gym_calender10.module;

public class Exercise {

    private String id;
    private String name;
    private String muscleGroup;
    private String equipment;
    private String description;
    private String sets;
    private String times;

    // חובה ל-Firebase
    public Exercise() {}

    public Exercise(String id, String name, String equipment,
                    String muscleGroup, String description,
                    String sets, String times) {
        this.id = id;
        this.name = name;
        this.equipment = equipment;
        this.muscleGroup = muscleGroup;
        this.description = description;
        this.sets = sets;
        this.times = times;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public String getEquipment() {
        return equipment;
    }

    public String getDescription() {
        return description;
    }

    public String getSets() {
        return sets;
    }

    public String getTimes() {
        return times;
    }
}

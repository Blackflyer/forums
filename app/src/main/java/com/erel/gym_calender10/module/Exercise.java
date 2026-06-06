package com.erel.gym_calender10.module;

public class Exercise {

    private String id;
    private String name;
    private String muscleGroup;
    private String equipment;
    private String description;
    private String sets;
    private String times;

    /**
     * בנאי ריק הנדרש עבור עבודה עם Firebase.
     */
    public Exercise() {}

    /**
     * בנאי מלא ליצירת אובייקט תרגיל.
     * @param id מזהה התרגיל.
     * @param name שם התרגיל.
     * @param equipment ציוד נדרש.
     * @param muscleGroup קבוצת שרירים.
     * @param description תיאור התרגיל.
     * @param sets מספר סטים מתוכנן.
     * @param times מספר חזרות מתוכנן.
     */
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

    /** @return מזהה התרגיל. */
    public String getId() {
        return id;
    }

    /** @param id הגדרת מזהה התרגיל. */
    public void setId(String id) {
        this.id = id;
    }

    /** @return שם התרגיל. */
    public String getName() {
        return name;
    }

    /** @return קבוצת השרירים הרלוונטית. */
    public String getMuscleGroup() {
        return muscleGroup;
    }

    /** @return הציוד הנדרש לתרגיל. */
    public String getEquipment() {
        return equipment;
    }

    /** @return תיאור התרגיל. */
    public String getDescription() {
        return description;
    }

    /** @return מספר הסטים. */
    public String getSets() {
        return sets;
    }

    /** @return מספר החזרות. */
    public String getTimes() {
        return times;
    }
}

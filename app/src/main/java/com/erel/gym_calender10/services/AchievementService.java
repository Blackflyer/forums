package com.erel.gym_calender10.services;

import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.module.ProgressRecord;

import java.util.ArrayList;
import java.util.List;

public class AchievementService {

    public static List<String> checkAchievements(User user, float lastWeight) {
        List<String> newAchievements = new ArrayList<>();
        List<String> current = user.getAchievements();
        if (current == null) current = new ArrayList<>();

        // 1. First Workout
        if (!current.contains("first_workout")) {
            if (user.getMaarachedPlans() != null && user.getMaarachedPlans().getPlanArray() != null && !user.getMaarachedPlans().getPlanArray().isEmpty()) {
                newAchievements.add("first_workout");
            }
        }

        // 2. 100kg Club
        if (!current.contains("100kg_club")) {
            if (lastWeight >= 100) {
                newAchievements.add("100kg_club");
            }
        }

        // 3. 10 Tons Lifted (placeholder for volume)
        // This would require more complex calculation, skipping for now.

        return newAchievements;
    }

    public static String getAchievementName(String id) {
        switch (id) {
            case "first_workout": return "אימון ראשון!";
            case "100kg_club": return "מועדון ה-100 ק\"ג!";
            default: return "הישג חדש!";
        }
    }
}
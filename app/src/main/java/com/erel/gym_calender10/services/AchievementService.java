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

        // 3. Consistency King (5 plans)
        if (!current.contains("consistency_king")) {
            if (user.getMaarachedPlans() != null && user.getMaarachedPlans().getPlanArray() != null && user.getMaarachedPlans().getPlanArray().size() >= 5) {
                newAchievements.add("consistency_king");
            }
        }

        return newAchievements;
    }

    public static String getAchievementName(String id) {
        switch (id) {
            case "first_workout": return "אימון ראשון!";
            case "100kg_club": return "מועדון ה-100 ק\"ג!";
            case "consistency_king": return "מלך העקביות!";
            default: return "הישג חדש!";
        }
    }

    public static List<com.erel.gym_calender10.module.Achievement> getAllAchievements(List<String> unlockedIds) {
        List<com.erel.gym_calender10.module.Achievement> list = new ArrayList<>();
        if (unlockedIds == null) unlockedIds = new ArrayList<>();

        list.add(new com.erel.gym_calender10.module.Achievement(
                "first_workout",
                "אימון ראשון!",
                "בצע את האימון הראשון שלך באפליקציה",
                "💪",
                unlockedIds.contains("first_workout")
        ));

        list.add(new com.erel.gym_calender10.module.Achievement(
                "100kg_club",
                "מועדון ה-100 ק\"ג!",
                "הרם משקל של 100 ק\"ג ומעלה בתרגיל כלשהו",
                "🏋️",
                unlockedIds.contains("100kg_club")
        ));

        list.add(new com.erel.gym_calender10.module.Achievement(
                "consistency_king",
                "מלך העקביות",
                "צור לפחות 5 תוכניות אימון",
                "👑",
                unlockedIds.contains("consistency_king")
        ));

        return list;
    }
}
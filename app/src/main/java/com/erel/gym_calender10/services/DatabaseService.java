package com.erel.gym_calender10.services;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.erel.gym_calender10.module.ListOfPlans;
import com.erel.gym_calender10.module.Plan;
import com.erel.gym_calender10.module.ProgressRecord;
import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.module.Exercise;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class DatabaseService {

    private static final String TAG = "DatabaseService";

    // נתיבים בבסיס הנתונים - וודא שהם תואמים למבנה ב-Firebase
    private static final String USERS_PATH = "users",
            EXERCISE_PATH = "exercise", // שיניתי מ-exercise ל-exercises (מקובל יותר ברבים)
            PLANS_PATH = "plans";

    public interface DatabaseCallback<T> {
        void onCompleted(T object);
        void onFailed(Exception e);
    }

    private static DatabaseService instance;
    private final DatabaseReference databaseReference;

    private DatabaseService() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /**
     * מחזירה מופע יחיד (Singleton) של מחלקת השירות.
     * @return מופע של DatabaseService.
     */
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    // region Generic Methods
    /**
     * פעולה גנרית לכתיבת נתונים לנתיב מסוים בבסיס הנתונים.
     * @param path הנתיב ב-Firebase.
     * @param data האובייקט לשמירה.
     * @param callback פונקציית חזרה לעדכון הצלחה/כישלון.
     */
    private void writeData(@NotNull final String path, @NotNull final Object data, final @Nullable DatabaseCallback<Void> callback) {
        databaseReference.child(path).setValue(data, (error, ref) -> {
            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
            } else {
                if (callback != null) callback.onCompleted(null);
            }
        });
    }

    /**
     * פעולה גנרית למחיקת נתונים מנתיב מסוים.
     * @param path הנתיב למחיקה.
     * @param callback פונקציית חזרה.
     */
    private void deleteData(@NotNull final String path, @Nullable final DatabaseCallback<Void> callback) {
        databaseReference.child(path).removeValue((error, ref) -> {
            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
            } else {
                if (callback != null) callback.onCompleted(null);
            }
        });
    }

    /**
     * פעולה גנרית לקבלת אובייקט בודד מנתיב מסוים.
     * @param path הנתיב ב-Firebase.
     * @param clazz סוג המחלקה של האובייקט.
     * @param callback פונקציית חזרה עם האובייקט שהתקבל.
     */
    private <T> void getData(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<T> callback) {
        databaseReference.child(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onFailed(task.getException());
                return;
            }
            callback.onCompleted(task.getResult().getValue(clazz));
        });
    }

    /**
     * פעולה גנרית לקבלת רשימת אובייקטים מנתיב מסוים.
     * @param path הנתיב ב-Firebase.
     * @param clazz סוג המחלקה של האובייקטים ברשימה.
     * @param callback פונקציית חזרה עם רשימת האובייקטים.
     */
    private <T> void getDataList(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<List<T>> callback) {
        databaseReference.child(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onFailed(task.getException());
                return;
            }
            List<T> tList = new ArrayList<>();
            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                T t = dataSnapshot.getValue(clazz);
                if (t != null) tList.add(t);
            }
            callback.onCompleted(tList);
        });
    }

    /**
     * מייצרת מזהה ייחודי (ID) חדש עבור נתיב מסוים בבסיס הנתונים.
     * @param path הנתיב עבורו נדרש ה-ID.
     * @return מחרוזת מזהה ייחודית.
     */
    public String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }
    // endregion

    // region User Section
    /**
     * יוצרת משתמש חדש במערכת (Firebase Auth + Database).
     * @param user אובייקט המשתמש.
     * @param callback פונקציית חזרה עם ה-UID של המשתמש שנוצר.
     */
    public void createNewUser(@NotNull final User user, @Nullable final DatabaseCallback<String> callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        String uid = mAuth.getCurrentUser().getUid();
                        user.setId(uid);
                        // כברירת מחדל משתמש חדש אינו אדמין
                        user.setAdmin(false);
                        writeData(USERS_PATH + "/" + uid, user, new DatabaseCallback<Void>() {
                            @Override
                            public void onCompleted(Void v) {
                                if (callback != null) callback.onCompleted(uid);
                            }
                            @Override
                            public void onFailed(Exception e) {
                                if (callback != null) callback.onFailed(e);
                            }
                        });
                    } else {
                        if (callback != null) callback.onFailed(task.getException());
                    }
                });
    }
    
    /**
     * יוצרת מנהל חדש במערכת (Firebase Auth + Database).
     * @param user אובייקט המשתמש.
     * @param callback פונקציית חזרה עם ה-UID.
     */
    public void createNewAdmin(@NotNull final User user, @Nullable final DatabaseCallback<String> callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        String uid = mAuth.getCurrentUser().getUid();
                        user.setId(uid);
                        // כאן ההבדל: מגדירים את המשתמש כמנהל!
                        user.setAdmin(true);
                        writeData(USERS_PATH + "/" + uid, user, new DatabaseCallback<Void>() {
                            @Override
                            public void onCompleted(Void v) {
                                if (callback != null) callback.onCompleted(uid);
                            }
                            @Override
                            public void onFailed(Exception e) {
                                if (callback != null) callback.onFailed(e);
                            }
                        });
                    } else {
                        if (callback != null) callback.onFailed(task.getException());
                    }
                });
    }

    /**
     * מבצעת התחברות למערכת עם אימייל וסיסמה.
     */
    public void LoginUser(@NotNull final String email, final String password, @Nullable final DatabaseCallback<String> callback) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && FirebaseAuth.getInstance().getCurrentUser() != null) {
                        callback.onCompleted(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    } else {
                        if (callback != null) callback.onFailed(task.getException());
                    }
                });
    }

    /**
     * מקבלת נתוני משתמש לפי UID.
     */
    public void getUser(@NotNull final String uid, @NotNull final DatabaseCallback<User> callback) {
        getData(USERS_PATH + "/" + uid, User.class, callback);
    }
    
    /**
     * מקבלת את רשימת כל המשתמשים במערכת.
     */
    public void getUserList(@NotNull final DatabaseCallback<List<User>> callback) {
        getDataList(USERS_PATH, User.class, callback);
    }
    
    /**
     * מוחקת משתמש מה-Database לפי UID.
     */
    public void deleteUser(@NotNull final String uid, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(USERS_PATH + "/" + uid, callback);
    }

    /**
     * מעדכנת נתוני משתמש קיים.
     */
    public void updateUser(@NotNull final User user, @Nullable final DatabaseCallback<Void> callback) {
        if (user.getId() != null) {
            writeData(USERS_PATH + "/" + user.getId(), user, callback);
        } else {
            if (callback != null) callback.onFailed(new Exception("User ID cannot be null"));
        }
    }

    /**
     * מעדכנת את רשימת ההישגים של המשתמש.
     */
    public void updateUserAchievements(@NotNull final String uid, final List<String> achievements, @Nullable final DatabaseCallback<Void> callback) {
        databaseReference.child(USERS_PATH).child(uid).child("achievements").setValue(achievements, (error, ref) -> {
            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
            } else {
                if (callback != null) callback.onCompleted(null);
            }
        });
    }

    /**
     * מעדכנת האם משתמש הוא מנהל או לא.
     */
    public void updateUserAdminStatus(@NotNull final String uid, final boolean isAdmin, @Nullable final DatabaseCallback<Void> callback) {
        databaseReference.child(USERS_PATH).child(uid).child("admin").setValue(isAdmin, (error, ref) -> {
            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
            } else {
                if (callback != null) callback.onCompleted(null);
            }
        });
    }
    // endregion

    // region Exercise Section
    /**
     * יוצרת תרגיל חדש בבסיס הנתונים הכללי.
     */
    public void createNewExercise(@NotNull final Exercise exercise, @Nullable final DatabaseCallback<Void> callback) {
        if (exercise.getId() == null) {
            exercise.setId(generateNewId(EXERCISE_PATH));
        }
        writeData(EXERCISE_PATH + "/" + exercise.getId(), exercise, callback);
    }

    /**
     * מקבלת את רשימת כל התרגילים הקיימים במערכת.
     */
    public void getExerciseList(@NotNull final DatabaseCallback<List<Exercise>> callback) {
        getDataList(EXERCISE_PATH, Exercise.class, callback);
    }

    /**
     * מייצרת מזהה חדש עבור תרגיל.
     */
    public String generateExerciseId() {
        return generateNewId(EXERCISE_PATH);
    }
    // endregion

    // region Plan Section
    /**
     * יוצרת תוכנית אימון חדשה, שומרת אותה כללית וגם משייכת אותה למשתמש הספציפי.
     */
    public void createNewPlan(@NotNull final Plan plan, @Nullable final DatabaseCallback<Void> callback) {
        if (plan.getPlanId() == null || plan.getPlanId().isEmpty()) {
            plan.setPlanId(generateNewId(PLANS_PATH));
        }

        // 1. שמירה בנתיב הכללי
        writeData(PLANS_PATH + "/" + plan.getPlanId(), plan, null);

        // 2. עדכון רשימת התוכניות בתוך אובייקט המשתמש באמצעות טרנזקציה (למניעת התנגשויות)
        DatabaseReference userPlansRef = databaseReference
                .child(USERS_PATH)
                .child(plan.getUserId())
                .child("maarachedPlans");

        userPlansRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                ListOfPlans listOfPlans = currentData.getValue(ListOfPlans.class);

                if (listOfPlans == null) {
                    listOfPlans = new ListOfPlans(plan.getUserId());
                }

                listOfPlans.addPlan(plan);
                currentData.setValue(listOfPlans);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    if (callback != null) callback.onFailed(error.toException());
                } else {
                    if (callback != null) callback.onCompleted(null);
                }
            }
        });
    }

    /**
     * מחפשת תוכניות אימון המשויכות למשתמש בתאריך מסוים.
     */
    public void getPlansByDate(@NotNull final String userId, @NotNull final String date, @NotNull final DatabaseCallback<List<Plan>> callback) {
        databaseReference.child(USERS_PATH).child(userId).child("maarachedPlans").child("planArray")
                .get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        callback.onFailed(task.getException());
                        return;
                    }

                    List<Plan> filteredPlans = new ArrayList<>();
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        Plan p = snapshot.getValue(Plan.class);
                        if (p != null && date.equals(p.getDate())) {
                            filteredPlans.add(p);
                        }
                    }
                    callback.onCompleted(filteredPlans);
                });
    }

    /**
     * מקבלת את כל תוכניות האימון המשויכות למשתמש.
     */
    public void getAllPlans(@NotNull final String userId, @NotNull final DatabaseCallback<List<Plan>> callback) {
        databaseReference.child(USERS_PATH).child(userId).child("maarachedPlans").child("planArray")
                .get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        callback.onFailed(task.getException());
                        return;
                    }

                    List<Plan> allPlans = new ArrayList<>();
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        Plan p = snapshot.getValue(Plan.class);
                        if (p != null) {
                            allPlans.add(p);
                        }
                    }
                    callback.onCompleted(allPlans);
                });
    }

    /**
     * מקבלת פרטי תוכנית אימון לפי ה-ID שלה.
     */
    public void getPlanById(@NotNull final String planId, @NotNull final DatabaseCallback<Plan> callback) {
        getData(PLANS_PATH + "/" + planId, Plan.class, callback);
    }

    /**
     * מייצרת מזהה חדש עבור תוכנית.
     */
    public String generatePlanId() {
        return generateNewId(PLANS_PATH);
    }
    // endregion
    
    // --- אזור מעקב התקדמות (Progress Tracking) ---

    /**
     * שומרת תיעוד של ביצוע תרגיל (משקל וחזרות) בתוך היסטוריית ההתקדמות של המשתמש.
     */
    public void saveExerciseProgress(String userId, String exerciseId, ProgressRecord record, @Nullable final DatabaseCallback<Void> callback) {
        // נייצר ID ייחודי לביצוע הזה
        String recordId = databaseReference.child(USERS_PATH).child(userId).child("progressLogs").child(exerciseId).push().getKey();
        if (recordId != null) {
            record.setId(recordId);
            databaseReference.child(USERS_PATH).child(userId).child("progressLogs").child(exerciseId).child(recordId)
                    .setValue(record, (error, ref) -> {
                        if (error != null) {
                            if (callback != null) callback.onFailed(error.toException());
                        } else {
                            if (callback != null) callback.onCompleted(null);
                        }
                    });
        }
    }

    /**
     * שולפת את היסטוריית ההתקדמות של משתמש עבור תרגיל ספציפי.
     */
    public void getExerciseProgress(String userId, String exerciseId, @NotNull final DatabaseCallback<List<ProgressRecord>> callback) {
        databaseReference.child(USERS_PATH).child(userId).child("progressLogs").child(exerciseId)
                .get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        callback.onFailed(task.getException());
                        return;
                    }
                    List<ProgressRecord> records = new ArrayList<>();
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        ProgressRecord record = snapshot.getValue(ProgressRecord.class);
                        if (record != null) {
                            records.add(record);
                        }
                    }
                    callback.onCompleted(records);
                });
    }
}
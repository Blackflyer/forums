package com.erel.gym_calender10.ImageUtil;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;

/**
 * מחלקת עזר (Utility) לפעולות הקשורות בתמונות.
 * כוללת שיטות לבקשת הרשאות, המרת תמונות למחרוזת Base64 ופענוחן בחזרה.
 */
public class ImageUtil {

    /**
     * מבקשת הרשאות מצלמה ואחסון מהמשתמש.
     * @param activity האקטיביטי ממנה מתבצעת הבקשה.
     */
    public static void requestPermission(@NotNull Activity activity) {
        // Request permissions for camera and storage
        ActivityCompat.requestPermissions(activity,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
    }

    /**
     * ממירה תמונה מתוך ImageView למחרוזת בפורמט Base64.
     * @param postImage ה-ImageView המכיל את התמונה.
     * @return מחרוזת Base64 המייצגת את התמונה, או null אם התמונה ריקה.
     */
    public static @Nullable String convertTo64Base(@NotNull final ImageView postImage) {
        if (postImage.getDrawable() == null) {
            return null;
        }
        Bitmap bitmap = ((BitmapDrawable) postImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * ממירה מחרוזת בפורמט Base64 בחזרה לאובייקט Bitmap של תמונה.
     * @param base64Code המחרוזת המקודדת ב-Base64.
     * @return אובייקט Bitmap של התמונה, או null אם המחרוזת ריקה.
     */
    public static @Nullable Bitmap convertFrom64base(@NotNull final String base64Code) {
        if (base64Code.isEmpty()) {
            return null;
        }
        byte[] decodedString = Base64.decode(base64Code, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}

package com.baltazarstudio.kossen.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Base64;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    private static final SimpleDateFormat sdf =
            new SimpleDateFormat("d MMM yyyy, HH:mm", new Locale("pt", "BR"));

    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        return size;
    }

    public static String getFormattedDate(long timeInMillis) {
        return sdf.format(new Date(timeInMillis)).toUpperCase();
    }

    public static Bitmap getBitmapFromBase64(String base64String) {
        byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String getBase64FromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        return Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
    }
}

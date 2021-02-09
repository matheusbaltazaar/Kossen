package com.baltazarstudio.kossen.util;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

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
}

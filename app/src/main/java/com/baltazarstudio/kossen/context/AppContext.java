package com.baltazarstudio.kossen.context;

import com.baltazarstudio.kossen.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AppContext {

    private static Locale mLocale = new Locale("pt", "BR");
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", mLocale);

    public static final String PREFS = "preferences";
    public static final String DAIMOKU_GOAL_SOUND = "target_sound";
    public static final int DEFAULT_DAIMOKU_GOAL_SOUND = R.raw.camila_cabello_havana;

    public static String getCurrentDateTime() {
        return sdf.format(Calendar.getInstance().getTime());
    }

}

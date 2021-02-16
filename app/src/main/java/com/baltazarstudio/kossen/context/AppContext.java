package com.baltazarstudio.kossen.context;

import android.content.Context;
import android.content.SharedPreferences;

import com.baltazarstudio.kossen.model.Perfil;

public class AppContext {

    private static Perfil perfil;

    private static final String PREFS_NAME = "prefs";
    private static final String PREFS_FIRST_USE = "first_use";
    private static SharedPreferences mPrefs;

    public static Perfil getPerfil() {
        return perfil;
    }

    public static void setPerfil(Perfil perfil) {
        AppContext.perfil = perfil;
    }

    public static boolean isFirstUse(Context context) {
        if (mPrefs != null)
            mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return mPrefs.getBoolean(PREFS_FIRST_USE, true);
    }

    public static void assertFirstUse(Context context) {
        if (mPrefs != null)
            mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putBoolean(PREFS_FIRST_USE, false).apply();
    }
}

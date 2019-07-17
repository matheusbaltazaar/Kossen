package com.baltazarstudio.kossen.model;

import android.util.SparseIntArray;

import com.baltazarstudio.kossen.R;

public class Sounds {

    private static SparseIntArray sounds;

    public static int get(int position) {
        init();
        return sounds.get(position);

    }

    public static int getPosition(int id) {
        init();
        return sounds.indexOfValue(id);
    }

    private static void init() {
        if (sounds == null) {
            sounds = new SparseIntArray();
            sounds.put(0, R.raw.camila_cabello_havana);
            sounds.put(1, R.raw.magical_morning);
            sounds.put(2, R.raw.light_ringtone);
        }
    }

}

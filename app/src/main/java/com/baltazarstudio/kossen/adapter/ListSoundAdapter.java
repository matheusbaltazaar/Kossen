package com.baltazarstudio.kossen.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.context.AppContext;

import java.util.ArrayList;
import java.util.Collections;

public class ListSoundAdapter extends BaseAdapter {

    private Context context;
    private SharedPreferences preferences;
    private ArrayList<SoundMapObject> soundList;

    public ListSoundAdapter(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(AppContext.PREFS, Context.MODE_PRIVATE);
        initializeSoundList();
    }

    private void initializeSoundList() {
        soundList = new ArrayList<>();
        soundList.add(new SoundMapObject("Camila Cabello Havana", R.raw.camila_cabello_havana));
        soundList.add(new SoundMapObject("Magical Morning", R.raw.magical_morning));
        soundList.add(new SoundMapObject("Light Ringtone", R.raw.light_ringtone));

        Collections.sort(soundList);
    }

    @Override
    public int getCount() {
        return soundList.size();
    }

    @Override
    public Object getItem(int position) {
        return soundList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return soundList.get(position).resId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.sound_list_item, null);

        ((TextView) view.findViewById(R.id.tv_sound_item_description)).setText(soundList.get(position).description);

        ImageView selected = view.findViewById(R.id.iv_sound_item_selected);
        long targetSound = preferences.getInt(AppContext.DAIMOKU_GOAL_SOUND, AppContext.DEFAULT_DAIMOKU_GOAL_SOUND);
        if (getItemId(position) == targetSound) {
            selected.setVisibility(View.VISIBLE);
        } else {
            selected.setVisibility(View.GONE);
        }

        return view;
    }

    private class SoundMapObject implements Comparable<SoundMapObject> {
        String description;
        int resId;

        SoundMapObject(String description, int resId) {
            this.description = description;
            this.resId = resId;
        }

        @Override
        public int compareTo(SoundMapObject other) {
            return this.description.compareTo(other.description);
        }
    }
}

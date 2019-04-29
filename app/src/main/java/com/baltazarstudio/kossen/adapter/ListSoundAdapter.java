package com.baltazarstudio.kossen.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baltazarstudio.kossen.ui.MainActivity;
import com.baltazarstudio.kossen.R;

import java.util.ArrayList;

public class ListSoundAdapter extends BaseAdapter {


    public static final int DEFAULT = 0;
    public static final int MELODIES = 1;

    private Context context;
    private SharedPreferences preferences;
    private ArrayList<SoundMapObject> soundList;

    public ListSoundAdapter(Context context, int soundGroup) {
        this.context = context;
        preferences = context.getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE);
        initializeSoundList(soundGroup);
    }

    private void initializeSoundList(int soundGroup) {
        soundList = new ArrayList<>();

        if (soundGroup == DEFAULT) {
            soundList.add(new SoundMapObject("Som 1", R.raw.sound_1));
            soundList.add(new SoundMapObject("Som 2", R.raw.sound_2));
            soundList.add(new SoundMapObject("Som 3", R.raw.sound_3));
            soundList.add(new SoundMapObject("Som 4", R.raw.sound_4));
        } else if (soundGroup == MELODIES) {
            soundList.add(new SoundMapObject("Camila Cabello Havana", R.raw.camila_cabello_havana));
            soundList.add(new SoundMapObject("Magical Morning", R.raw.magical_morning));
            soundList.add(new SoundMapObject("Light Ringtone", R.raw.light_ringtone));
        }
    }

    @Override
    public int getCount() {
        return soundList.size();
    }

    @Override
    public Object getItem(int position) {
        //return soundList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return soundList.get(position).resId;
    }

    @Override
    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.sound_list_item, parent);

        ((TextView) view.findViewById(R.id.tv_sound_item_description)).setText(soundList.get(position).description);

        ImageView selected = view.findViewById(R.id.iv_sound_item_selected);
        long targetSound = preferences.getInt(MainActivity.TARGET_SOUND, MainActivity.DEFAULT_SOUND);
        if (getItemId(position) == targetSound) {
            selected.setVisibility(View.VISIBLE);
        } else {
            selected.setVisibility(View.GONE);
        }

        return view;
    }

    public void updateTargetRing(int position) {
        preferences.edit()
                .putInt(MainActivity.TARGET_SOUND, (int) getItemId(position))
                .apply();

        notifyDataSetChanged();
    }

    private class SoundMapObject {
        String description;
        int resId;

        SoundMapObject(String description, int resId) {
            this.description = description;
            this.resId = resId;
        }
    }
}

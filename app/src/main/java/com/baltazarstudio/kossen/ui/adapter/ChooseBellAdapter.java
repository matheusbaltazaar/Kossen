package com.baltazarstudio.kossen.ui.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.context.PlayerContext;
import com.baltazarstudio.kossen.util.AnimationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ChooseBellAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private ArrayList<String> nomes;
    private int[] rawIds;
    private int selectedBell;

    public ChooseBellAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        extractListsFromMap(PlayerContext.getBellMap());
        selectedBell = PlayerContext.getCurrentBell(context);
    }

    private void extractListsFromMap(Map<String, Integer> bellMap) {
        this.nomes = new ArrayList<>();
        this.nomes.addAll(bellMap.keySet());
        Collections.sort(this.nomes);

        this.rawIds = new int[this.nomes.size()];

        int i = 0;
        for (String key : this.nomes) {
            rawIds[i] = bellMap.get(key);
            i++;
        }

    }

    public int getSelectedBell() {
        return selectedBell;
    }

    @Override
    public int getItemCount() {
        return this.nomes.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(layoutInflater.inflate(R.layout.item_choose_bell_sound, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder) holder).bind(nomes.get(position), rawIds[position], position != nomes.size() - 1);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNome;
        private final ImageView ivEqualizer;
        private final View leftIndicator;
        private final View divider;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tv_item_choose_bell_name);
            ivEqualizer = itemView.findViewById(R.id.iv_item_choose_bell_equalizer);
            leftIndicator = itemView.findViewById(R.id.view_item_chooser_bell_left_indicator);
            divider = itemView.findViewById(R.id.divider_item_choose_bell);
        }

        void bind(String nome, final int rawId, boolean showDivider) {

            tvNome.setText(nome);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayerContext.playBell(itemView.getContext(), rawId, new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            ivEqualizer.setVisibility(View.GONE);
                        }
                    });

                    ivEqualizer.setVisibility(View.VISIBLE);
                    //AnimationUtil.fadeOut(itemView.getContext(), ivEqualizer);

                    selectedBell = rawId;
                    notifyDataSetChanged();
                }
            });

            if (rawId == selectedBell) {
                leftIndicator.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary));
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.off_primary));
            } else {
                leftIndicator.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.off_primary));
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), android.R.color.white));
                ivEqualizer.setVisibility(View.GONE);
            }

            divider.setVisibility(showDivider ? View.VISIBLE : View.GONE);

        }

    }

}

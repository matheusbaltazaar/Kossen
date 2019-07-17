package com.baltazarstudio.kossen.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.model.Meta;

import java.util.List;

public class MetaAdapter extends BaseAdapter {
    private final Context context;
    private final List<Meta> metas;

    public MetaAdapter(Context context, List<Meta> metas) {
        this.context = context;
        this.metas = metas;
    }

    @Override
    public int getCount() {
        return metas.size();
    }

    @Override
    public Object getItem(int position) {
        return metas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.meta_list_item, null);
            holder = new ViewHolder();

            holder.duracao = convertView.findViewById(R.id.meta_list_item_duracao);
            holder.data = convertView.findViewById(R.id.meta_list_item_data);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.duracao.setText(metas.get(position).getDuracao());
        holder.data.setText(metas.get(position).getData());

        convertView.setTag(holder);
        return convertView;
    }

    private static class ViewHolder {
        TextView data;
        TextView duracao;
    }
}

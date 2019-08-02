package com.baltazarstudio.kossen.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.model.Daimoku;

import java.util.List;

public class MetaAdapter extends BaseAdapter {
    private final Context context;
    private final List<Daimoku> list;

    public MetaAdapter(Context context, List<Daimoku> daimokuList) {
        this.context = context;
        this.list = daimokuList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            holder.informacao = convertView.findViewById(R.id.meta_list_item_info);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.duracao.setText(list.get(position).getDuracao());
        holder.data.setText(list.get(position).getData());

        String info = list.get(position).getInformacao();
        if (info != null && !info.equals("")) {
            if (info.length() > 50) {
                info = info.substring(0, 50) + "...";
            }
            holder.informacao.setVisibility(View.VISIBLE);
            holder.informacao.setText(info);
        }

        convertView.setTag(holder);
        return convertView;
    }

    private static class ViewHolder {
        TextView data;
        TextView duracao;
        TextView informacao;
    }
}

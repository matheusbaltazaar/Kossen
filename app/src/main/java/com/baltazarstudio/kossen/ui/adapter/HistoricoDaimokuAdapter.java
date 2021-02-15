package com.baltazarstudio.kossen.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.database.Database;
import com.baltazarstudio.kossen.model.Daimoku;
import com.baltazarstudio.kossen.util.Utils;

import java.util.List;

public class HistoricoDaimokuAdapter extends RecyclerView.Adapter<HistoricoDaimokuAdapter.ItemViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private final List<Daimoku> listaDaimoku;
    private final RecyclerView.LayoutManager layoutManager;

    public HistoricoDaimokuAdapter(Context context, List<Daimoku> daimokuList, RecyclerView.LayoutManager layoutManager) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listaDaimoku = daimokuList;
        this.layoutManager = layoutManager;
    }

    @Override
    public int getItemCount() {
        return listaDaimoku.size();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(inflater.inflate(R.layout.item_historico_daimoku, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        final Daimoku daimoku = listaDaimoku.get(position);

        holder.textViewDuracao.setText(daimoku.getDuracao());
        holder.textViewData.setText(Utils.getFormattedDate(daimoku.getData()));
        holder.textViewInformacao.setText(daimoku.getInformacao());

        if (daimoku.getInformacao() != null && !daimoku.getInformacao().isEmpty()) {
            holder.buttonInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.textViewInformacao.getVisibility() == View.GONE) {
                        holder.textViewInformacao.setVisibility(View.VISIBLE);
                        layoutManager.scrollToPosition(position);
                    } else {
                        holder.textViewInformacao.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            holder.buttonInfo.setColorFilter(context.getResources().getColor(R.color.off_white));
        }

        holder.buttonRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Remover")
                        .setMessage("Quer mesmo remover este daimoku?")
                        .setPositiveButton("Remover", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Database(holder.itemView.getContext()).removerDaimoku(daimoku);

                                Toast.makeText(holder.itemView.getContext(), "Daimoku removido", Toast.LENGTH_SHORT).show();
                                listaDaimoku.remove(daimoku);
                                notifyItemRemoved(position);
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create().show();
            }
        });

        if (position == listaDaimoku.size() - 1) {
            holder.itemView.findViewById(R.id.divider_item_historico_daimoku)
                    .setVisibility(View.GONE);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textViewData;
        TextView textViewDuracao;
        TextView textViewInformacao;
        ImageView buttonInfo;
        ImageView buttonRemover;

        ItemViewHolder(View itemView) {
            super(itemView);

            textViewDuracao = itemView.findViewById(R.id.tv_item_historico_daimoku_duracao);
            textViewData = itemView.findViewById(R.id.tv_item_historico_daimoku_data);
            textViewInformacao = itemView.findViewById(R.id.tv_item_historico_daimoku_informacoes);
            buttonInfo = itemView.findViewById(R.id.button_item_historico_daimoku_info);
            buttonRemover = itemView.findViewById(R.id.button_item_historico_daimoku_remover);
        }

    }

}

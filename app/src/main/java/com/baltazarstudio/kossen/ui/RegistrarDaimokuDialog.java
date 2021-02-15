package com.baltazarstudio.kossen.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.database.Database;
import com.baltazarstudio.kossen.model.Daimoku;
import com.baltazarstudio.kossen.util.Utils;

import java.util.Calendar;
import java.util.Date;

public class RegistrarDaimokuDialog extends Dialog {

    private OnSavedDaimokuListener mListener;

    public RegistrarDaimokuDialog(Context context, final String duration) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null)
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        setUpView(duration);
        setUpDimensions();
    }

    @SuppressLint("SimpleDateFormat")
    private void setUpView(final String duration) {
        setContentView(R.layout.dialog_registrar_daimoku);

        final TextView tvDuracaoDaimoku = findViewById(R.id.tv_dialog_registrar_daimoku_duracao);
        tvDuracaoDaimoku.setText(duration);

        final EditText etInformacoes = findViewById(R.id.et_dialog_registrar_daimoku_informacoes);

        final long timeInMillis = Calendar.getInstance().getTimeInMillis();

        final TextView etData = findViewById(R.id.tv_dialog_registrar_daimoku_data);
        etData.setText(Utils.getFormattedDate(timeInMillis));

        findViewById(R.id.button_dialog_registrar_daimoku_salvar)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Daimoku daimoku = new Daimoku();
                        daimoku.setData(timeInMillis);
                        daimoku.setDuracao(duration);
                        daimoku.setInformacao(etInformacoes.getText().toString());

                        new Database(getContext()).registrarDaimoku(daimoku);
                        Toast.makeText(getContext(), "Daimoku salvo", Toast.LENGTH_LONG).show();

                        if (mListener != null)
                            mListener.onSave();

                        cancel();
                    }
                });
    }

    private void setUpDimensions() {
        if (getWindow() != null) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();

            Point screenSize = Utils.getScreenSize(getContext());
            lp.width = (int) (screenSize.x * .8);
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            getWindow().setAttributes(lp);
        }
    }

    public void setOnSavedDaimokuListener(OnSavedDaimokuListener onSavedDaimokuListener) {
        mListener = onSavedDaimokuListener;
    }

    public interface OnSavedDaimokuListener {
        void onSave();
    }
}

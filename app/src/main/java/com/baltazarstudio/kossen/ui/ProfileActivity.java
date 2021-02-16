package com.baltazarstudio.kossen.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baltazarstudio.kossen.R;
import com.baltazarstudio.kossen.context.AppContext;
import com.baltazarstudio.kossen.database.Database;
import com.baltazarstudio.kossen.model.Perfil;
import com.baltazarstudio.kossen.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etNome;
    private CircularImageView imageViewFoto;

    private final Database database = new Database(this);
    private final Perfil perfil = AppContext.getPerfil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        ImageView buttonVoltar = findViewById(R.id.button_action_voltar);
        buttonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.this.finish();
            }
        });

        imageViewFoto = findViewById(R.id.iv_perfil_foto);

        FloatingActionButton buttonEditarFoto = findViewById(R.id.fab_editar_foto);
        buttonEditarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarFotoGaleria();
            }
        });

        etNome = findViewById(R.id.et_perfil_nome);
        etNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                perfil.setNome(s.toString());
                database.salvarPerfil(perfil);
            }
        });


        loadData();
    }

    private void loadData() {
        etNome.setText(perfil.getNome());

        String base64 = perfil.getArquivoFotoBase64();
        if (base64 != null && !base64.isEmpty()) {
            imageViewFoto.setImageBitmap(Utils.getBitmapFromBase64(base64));
        }
    }

    private void buscarFotoGaleria() {
        final CropImage.ActivityBuilder cropping = CropImage.activity()
                .setAllowFlipping(false)
                .setAllowRotation(false)
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL);

        if (perfil.getArquivoFotoBase64() != null && !perfil.getArquivoFotoBase64().isEmpty()) {
            final BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(R.layout.bottom_sheet_buscar_foto);
            dialog.findViewById(R.id.button_bottom_sheet_buscar_foto_alterar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cropping.start(ProfileActivity.this);
                    dialog.cancel();
                }
            });
            dialog.findViewById(R.id.button_bottom_sheet_buscar_foto_remover).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageViewFoto.setImageResource(R.drawable.ic_user_profile);
                    perfil.setArquivoFotoBase64(null);
                    database.salvarPerfil(perfil);
                    dialog.cancel();
                }
            });
            dialog.show();
        } else {
            cropping.start(ProfileActivity.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                try {
                    //ImageDecoder.createSource(getContentResolver(), resultUri);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    imageViewFoto.setImageBitmap(bitmap);
                    perfil.setArquivoFotoBase64(Utils.getBase64FromBitmap(bitmap));
                    database.salvarPerfil(perfil);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Arquivo não encontrado", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.getError().printStackTrace();
                Toast.makeText(this, "Erro ao carregar a foto", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof TextInputEditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
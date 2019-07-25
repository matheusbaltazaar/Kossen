package com.baltazarstudio.kossen.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.baltazarstudio.kossen.model.Daimoku;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {


    private static final int VERSAO_BANCO = 1;
    private static final String NOME_BANCO = "kossenDB";

    private static final String TABELA_HISTORICO = "Historico";

    private static final String HISTORICO_DURACAO = "duracao";
    private static final String HISTORICO_DATA = "data";

    private SQLiteStatement stmtInsertHistory;


    public Database(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
        prepareStatements();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_HISTORICO);

        String create_table_history = "CREATE TABLE " + TABELA_HISTORICO + " (";
        create_table_history += HISTORICO_DURACAO + " TEXT,";
        create_table_history += HISTORICO_DATA + " TEXT)";

        db.execSQL(create_table_history);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    private void prepareStatements() {
        SQLiteDatabase db = getWritableDatabase();
        stmtInsertHistory = db.compileStatement("INSERT INTO " + TABELA_HISTORICO + "("
                + HISTORICO_DURACAO + ","
                + HISTORICO_DATA + ")"
                + " VALUES (?, ?)"
        );

    }

    public void register(String duracao, String tempo) {
        stmtInsertHistory.clearBindings();

        stmtInsertHistory.bindString(1, duracao);
        stmtInsertHistory.bindString(2, tempo);

        stmtInsertHistory.executeInsert();

    }

    public ArrayList<Daimoku> getAllDaimoku() {
        String sql = "SELECT * FROM " + TABELA_HISTORICO;
        ArrayList<Daimoku> daimokuList = new ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        // ORDEM INVERSA
        if (cursor.move(cursor.getCount())) {
            do {
                Daimoku daimoku = new Daimoku();
                daimoku.setDuracao(cursor.getString(cursor.getColumnIndex(HISTORICO_DURACAO)));
                daimoku.setData(cursor.getString(cursor.getColumnIndex(HISTORICO_DATA)));

                daimokuList.add(daimoku);
            } while (cursor.moveToPrevious());
        }
        cursor.close();

        return daimokuList;
    }


}

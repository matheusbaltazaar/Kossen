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

    private static final String HISTORICO_ID = "id";
    private static final String HISTORICO_DATA = "data";
    private static final String HISTORICO_DURACAO = "duracao";
    private static final String HISTORICO_INFORMACAO = "informacao";

    private SQLiteStatement stmtInsertHistory;


    public Database(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
        prepareStatements();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_HISTORICO);

        String create_table_history = "CREATE TABLE " + TABELA_HISTORICO + " (";
        create_table_history += HISTORICO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,";
        create_table_history += HISTORICO_DURACAO + " TEXT,";
        create_table_history += HISTORICO_DATA + " TEXT,";
        create_table_history += HISTORICO_INFORMACAO + " TEXT)";

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
                + HISTORICO_DATA + ","
                + HISTORICO_INFORMACAO + ")"
                + " VALUES (?, ?, ?)"
        );

    }

    public void register(Daimoku daimoku) {
        stmtInsertHistory.clearBindings();

        stmtInsertHistory.bindString(1, daimoku.getDuracao());
        stmtInsertHistory.bindString(2, daimoku.getData());
        stmtInsertHistory.bindString(3, daimoku.getInformacao());

        stmtInsertHistory.executeInsert();

    }

    public ArrayList<Daimoku> getAllDaimoku() {
        String sql = "SELECT * FROM " + TABELA_HISTORICO
                + " ORDER BY " + HISTORICO_ID + " DESC";
        ArrayList<Daimoku> daimokuList = new ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Daimoku daimoku = new Daimoku();
            daimoku.setId(cursor.getInt(cursor.getColumnIndex(HISTORICO_ID)));
            daimoku.setDuracao(cursor.getString(cursor.getColumnIndex(HISTORICO_DURACAO)));
            daimoku.setData(cursor.getString(cursor.getColumnIndex(HISTORICO_DATA)));
            daimoku.setInformacao(cursor.getString(cursor.getColumnIndex(HISTORICO_INFORMACAO)));

            daimokuList.add(daimoku);
        }
        cursor.close();

        return daimokuList;
    }

    public void remove(Daimoku daimoku) {
        String sql = "DELETE * FROM " + TABELA_HISTORICO
                + " WHERE "
                + HISTORICO_ID + " = " + daimoku.getId();

        getWritableDatabase().execSQL(sql);
    }
}

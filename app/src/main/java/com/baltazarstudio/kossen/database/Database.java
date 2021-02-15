package com.baltazarstudio.kossen.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.baltazarstudio.kossen.model.Daimoku;
import com.baltazarstudio.kossen.model.Perfil;

import java.sql.Statement;
import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {


    private static final int VERSAO_BANCO = 1;
    private static final String NOME_BANCO = "kossenDB";

    private static final String TABELA_HISTORICO = "Historico";
    private static final String TABELA_PERFIL = "Perfil";

    private static final String HISTORICO_ID = "id";
    private static final String HISTORICO_DATA = "data";
    private static final String HISTORICO_DURACAO = "duracao";
    private static final String HISTORICO_INFORMACAO = "informacao";

    private static final String PERFIL_NOME = "nome";
    private static final String PERFIL_ARQUIVO_FOTO_BASE_64 = "arquivo_foto";


    public Database(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_HISTORICO);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_PERFIL);

        String create_table_history = "CREATE TABLE " + TABELA_HISTORICO + " (";
        create_table_history += HISTORICO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,";
        create_table_history += HISTORICO_DURACAO + " TEXT,";
        create_table_history += HISTORICO_DATA + " INTEGER,";
        create_table_history += HISTORICO_INFORMACAO + " TEXT)";

        String create_table_perfil = "CREATE TABLE " + TABELA_PERFIL + " (";
        create_table_perfil += PERFIL_NOME + " TEXT,";
        create_table_perfil += PERFIL_ARQUIVO_FOTO_BASE_64 + " TEXT)";

        db.execSQL(create_table_history);
        db.execSQL(create_table_perfil);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public ArrayList<Daimoku> getTodosDaimoku() {
        String sql = "SELECT * FROM " + TABELA_HISTORICO + " ORDER BY " + HISTORICO_ID + " DESC";
        ArrayList<Daimoku> daimokuList = new ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Daimoku daimoku = new Daimoku();
            bind(cursor, daimoku);

            daimokuList.add(daimoku);
        }

        cursor.close();
        return daimokuList;
    }

    public void registrarDaimoku(Daimoku daimoku) {
        String sql = "INSERT INTO " + TABELA_HISTORICO + "("
                + HISTORICO_DURACAO + ","
                + HISTORICO_DATA + ","
                + HISTORICO_INFORMACAO + ")"
                + " VALUES (?, ?, ?)";

        SQLiteStatement stmt = getWritableDatabase().compileStatement(sql);

        stmt.bindString(1, daimoku.getDuracao());
        stmt.bindLong(2, daimoku.getData());
        if (daimoku.getInformacao() == null || daimoku.getInformacao().isEmpty()) stmt.bindNull(3);
        else stmt.bindString(3, daimoku.getInformacao());

        stmt.executeInsert();
        stmt.close();
    }

    public void removerDaimoku(Daimoku daimoku) {
        String sql = "DELETE FROM " + TABELA_HISTORICO + " WHERE " + HISTORICO_ID + " = " + daimoku.getId();
        getWritableDatabase().execSQL(sql);
    }

    public void bind(Cursor cursor, Daimoku daimoku) {
        daimoku.setId(cursor.getInt(cursor.getColumnIndex(HISTORICO_ID)));
        daimoku.setDuracao(cursor.getString(cursor.getColumnIndex(HISTORICO_DURACAO)));
        daimoku.setData(cursor.getLong(cursor.getColumnIndex(HISTORICO_DATA)));
        daimoku.setInformacao(cursor.getString(cursor.getColumnIndex(HISTORICO_INFORMACAO)));
    }

    public Perfil carregarPerfil() {
        String sql = "SELECT * FROM " + TABELA_PERFIL;
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        Perfil perfil = new Perfil();

        if (cursor.moveToNext()) {
            if (!cursor.isNull(cursor.getColumnIndex(PERFIL_NOME)))
                perfil.setNome(cursor.getString(cursor.getColumnIndex(PERFIL_NOME)));

            if (!cursor.isNull(cursor.getColumnIndex(PERFIL_ARQUIVO_FOTO_BASE_64)))
                perfil.setArquivoFotoBase64(cursor.getString(cursor.getColumnIndex(PERFIL_ARQUIVO_FOTO_BASE_64)));
        }

        cursor.close();
        return perfil;
    }

    public void salvarPerfil(Perfil perfil) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        String sqlRemove = "DELETE FROM " + TABELA_PERFIL;
        db.execSQL(sqlRemove);

        String sqlPersist = "INSERT INTO " + TABELA_PERFIL + "(" + PERFIL_NOME + ", " + PERFIL_ARQUIVO_FOTO_BASE_64 + ")";
        sqlPersist += " VALUES ('" + perfil.getNome() + "','" + perfil.getArquivoFotoBase64() + "')";

        db.execSQL(sqlPersist);

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
}

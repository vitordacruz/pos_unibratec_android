package br.com.aprendendo.vitor.marvel.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vitor on 19/09/2016.
 */
public class MarvelDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "dbMarvel";
    private static final int DB_VERSION = 1;

    public MarvelDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + PersonagemContract.TABLE_NAME + " (" +
                        PersonagemContract._ID + " INTEGER PRIMARY KEY, " +
                        PersonagemContract.COL_NOME + " TEXT NOT NULL, " +
                        PersonagemContract.COL_DESCRICAO + " TEXT NOT NULL, " +
                        PersonagemContract.COL_IMAGEM_PATH + " TEXT NOT NULL, " +
                        PersonagemContract.COL_IMAGEM_EXTENSAO + " TEXT NOT NULL " +
                        "); ");
        db.execSQL(
                "CREATE TABLE " + ComicSummaryContract.TABLE_NAME + " (" +
                        ComicSummaryContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ComicSummaryContract.COL_NOME + " TEXT, " +
                        ComicSummaryContract.COL_ID_PERSONAGEM + " INTEGER NOT NULL" +
                        ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

}

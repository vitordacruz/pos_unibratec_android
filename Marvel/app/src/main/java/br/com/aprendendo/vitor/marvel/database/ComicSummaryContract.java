package br.com.aprendendo.vitor.marvel.database;

import android.provider.BaseColumns;

/**
 * Created by vitor on 19/09/2016.
 */
public interface ComicSummaryContract extends BaseColumns {

    // Nome da tabela no banco de dados
    String TABLE_NAME = "comic_summary";

    String COL_NOME = "nome";
    String COL_ID_PERSONAGEM = "id_personagem";

}

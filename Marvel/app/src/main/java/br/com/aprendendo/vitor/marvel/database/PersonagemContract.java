package br.com.aprendendo.vitor.marvel.database;

import android.provider.BaseColumns;

/**
 * Created by vitor on 19/09/2016.
 */
public interface PersonagemContract extends BaseColumns {

    // Nome da tabela no banco de dados
    String TABLE_NAME = "Personagem";

    // Colunas do banco de dados
    String COL_NOME  = "nome";
    String COL_DESCRICAO = "descricao";
    String COL_IMAGEM_PATH = "path_imagem";
    String COL_IMAGEM_EXTENSAO = "extensao_imagem";


    // Colunas utilizadas pelo adapter do fragment de favoritos
    String[] LIST_COLUMNS = new String[]{
            PersonagemContract._ID,
            PersonagemContract.COL_NOME,
            PersonagemContract.COL_IMAGEM_EXTENSAO,
            PersonagemContract.COL_IMAGEM_PATH
    };

}

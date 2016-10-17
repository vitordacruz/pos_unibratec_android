package br.com.aprendendo.vitor.marvel;

/**
 * Created by vitor on 20/09/2016.
 */

public class PersonagemEvent {

    // Eventos disparados/recebidos pelos Broadcasts da aplicação
    public static final String PERSONAGEM_LOADED = "loaded";
    public static final String UPDATE_FAVORITE = "favorite";
    public static final String PERSONAGEM_FAVORITE_UPDATED = "updated";

    // Chave para obter o Movie a partir das intents de broadcast
    public static final String EXTRA_PERSONAGEM = "personagem";

}

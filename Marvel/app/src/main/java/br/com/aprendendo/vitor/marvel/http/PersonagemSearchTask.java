package br.com.aprendendo.vitor.marvel.http;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import br.com.aprendendo.vitor.marvel.model.Personagem;

/**
 * Created by vitor on 18/09/2016.
 */
public class PersonagemSearchTask extends AsyncTaskLoader<List<Personagem>> {

    List<Personagem> lista;
    String query;

    public PersonagemSearchTask(Context context,  String query) {
        super(context);
        this.lista = new ArrayList<Personagem>();
        this.query = query;
    }

    @Override
    public List<Personagem> loadInBackground() {
        lista.addAll(MarvelHttp.buscarPersonagens(query));
        return lista;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (query != null) {
            forceLoad();
        } else {
            deliverResult(lista);
        }
    }
}

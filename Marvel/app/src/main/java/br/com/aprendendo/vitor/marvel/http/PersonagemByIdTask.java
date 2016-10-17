package br.com.aprendendo.vitor.marvel.http;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import br.com.aprendendo.vitor.marvel.model.Personagem;

/**
 * Created by vitor on 19/09/2016.
 */
public class PersonagemByIdTask extends AsyncTaskLoader<Personagem> {

    private Personagem personagem;
    private Long id;

    public PersonagemByIdTask(Context context, Long id) {
        super(context);
        this.id = id;
    }

    @Override
    public Personagem loadInBackground() {

        personagem = MarvelHttp.loadPersonagem(id);

        return personagem;

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (id != null) {
            forceLoad();
        } else {
            deliverResult(personagem);
        }
    }
}

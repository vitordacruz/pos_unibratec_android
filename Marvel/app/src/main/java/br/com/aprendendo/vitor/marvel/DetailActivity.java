package br.com.aprendendo.vitor.marvel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import br.com.aprendendo.vitor.marvel.model.Personagem;

public class DetailActivity extends AppCompatActivity  {

    public static final String EXTRA_PERSONAGEM = "Personagem";

    Personagem personagem;
    FloatingActionButton fab;

    LocalBroadcastManager mLocalBroadcastManager;
    PersonagemReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        personagem = (Personagem) getIntent().getSerializableExtra(EXTRA_PERSONAGEM);

        DetailPersonagemFragment detailMovieFragment;
        detailMovieFragment = DetailPersonagemFragment.newInstance(personagem);

        // Todas as informações do filme estão no DetailMovieFragment,
        // exceto a capa que já carregamos aqui, uma vez que essa informação
        // já existe no objeto Movie.
        ImageView imgPoster = (ImageView)findViewById(R.id.detail_image_poster);
        Glide.with(imgPoster.getContext()).load(personagem.getImagem().getPath() + "." + personagem.getImagem().getExtension()).into(imgPoster);

        // Esse receiver detectará se o Movie foi adicionado ou removido dos favoritos
        // TODO Substituir pelo EventBus?
        mReceiver = new PersonagemReceiver();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(PersonagemEvent.PERSONAGEM_LOADED));
        mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(PersonagemEvent.PERSONAGEM_FAVORITE_UPDATED));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PersonagemEvent.UPDATE_FAVORITE);
                mLocalBroadcastManager.sendBroadcast(it);
            }
        });

        if (savedInstanceState == null) {
            // Adicionando o fragment de detalhes na tela
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.placeholderDetail, detailMovieFragment)
                    .commit();
        }

    }

    // Esse receiver atualizará o status do botão de favoritos.
    class PersonagemReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            fab.setVisibility(View.VISIBLE);
            Personagem personagem = (Personagem)intent.getSerializableExtra(PersonagemEvent.EXTRA_PERSONAGEM);
            PersonagemDetailUtils.toggleFavorite(context, fab, personagem.getId());
        }
    }
}

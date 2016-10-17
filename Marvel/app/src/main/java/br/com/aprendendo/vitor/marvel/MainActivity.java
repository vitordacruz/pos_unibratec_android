package br.com.aprendendo.vitor.marvel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import br.com.aprendendo.vitor.marvel.model.Personagem;

public class MainActivity extends AppCompatActivity implements OnPersonagemClickListener {


    public static final String EXTRA_PERSONAGEM = "PERSONAGEM_MARVEL_ID";

    FloatingActionButton fab;
    LocalBroadcastManager mLocalBroadcastManager;
    MovieReceiver mReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializando o PagerAdapter, ViewPager e TabLayout para exibir as abas
        PersonagemPagerAdapter pagerAdapter = new PersonagemPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Definimos alguns comportamentos especiais para tablets...
        if (getResources().getBoolean(R.bool.tablet)){
            // As abas ficam alinhadas a esquerda
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

            // Inicializamos esse receiver para saber quando o filme no fragment de detalhe
            // foi carregado (ver método notifyUpdate da DetailMovieFragment)
            mReceiver = new MovieReceiver();
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
            mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(PersonagemEvent.PERSONAGEM_LOADED));
            mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(PersonagemEvent.PERSONAGEM_FAVORITE_UPDATED));

            // O FAB envia a mensagem para o DetailFragment inserir/excluir filme no banco
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(PersonagemEvent.UPDATE_FAVORITE);
                    mLocalBroadcastManager.sendBroadcast(it);
                }
            });
        }

    }

    @Override
    public void onPersonagemClick(Personagem personagem, int position) {
        // Esse método é chamado pelas telas de listagem quando o usuário
        // clica em um item da lista (ver MovieListFragment e FavoriteMoviesFragment)
        if (getResources().getBoolean(R.bool.phone)) {
            // Se for smartphone, abra uma nova activity
            Intent it = new Intent(MainActivity.this, DetailActivity.class);
            it.putExtra(DetailActivity.EXTRA_PERSONAGEM, personagem);
            startActivity(it);
        } else {
            // Se for tablet, exiba um fragment a direita
            DetailPersonagemFragment detailPersonagemFragment = DetailPersonagemFragment.newInstance(personagem);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.placeholderDetail, detailPersonagemFragment)
                    .commit();
        }
    }

    // Esse receiver será chamado quando o fragment de detalhe carrega os dados do filme
    // (ver método notifyUpdate de DetailMovieFragment)
    class MovieReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Personagem personagem = (Personagem)intent.getSerializableExtra(PersonagemEvent.EXTRA_PERSONAGEM);
            fab.setVisibility(View.VISIBLE);
            PersonagemDetailUtils.toggleFavorite(context, fab, personagem.getId());
        }
    }

    // O PagerAdapter é o que determina o que será exibido em cada aba
    class PersonagemPagerAdapter extends FragmentPagerAdapter {
        public PersonagemPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            if (position == 1){
                PersonagemListFragment movieListFragment = new PersonagemListFragment();
                return movieListFragment;
            } else {
                FavoritoPersonagemFragment favoriteMoviesFragment = new FavoritoPersonagemFragment();
                return favoriteMoviesFragment;
            }
        }
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) return getString(R.string.tab_search);
            else return getString(R.string.tab_favorites);
        }
        @Override
        public int getCount() {
            return 2;
        }
    }

}

package br.com.aprendendo.vitor.marvel;


import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import br.com.aprendendo.vitor.marvel.database.ComicSummaryContract;
import br.com.aprendendo.vitor.marvel.database.PersonagemContract;
import br.com.aprendendo.vitor.marvel.http.PersonagemByIdTask;
import br.com.aprendendo.vitor.marvel.model.ComicList;
import br.com.aprendendo.vitor.marvel.model.ComicSummary;
import br.com.aprendendo.vitor.marvel.model.Image;
import br.com.aprendendo.vitor.marvel.model.Personagem;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailPersonagemFragment extends Fragment {

    private static final String EXTRA_PERSONAGEM = "personagem";
    private static final int LOADER_DB = 0;
    private static final int LOADER_WEB = 1;


    Personagem personagem;

    TextView textViewnome;
    TextView textViewComicSummary;
    TextView textViewDescricao;
    ImageView imageViewPersonagem;

    LocalBroadcastManager mLocalBroadcastManager;
    MarvelEventReceiver mReceiver;

    // Para criarmos um DetailMovieFragment precisamos passar um objeto Personagem
    public static DetailPersonagemFragment newInstance(Personagem personagem) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_PERSONAGEM, personagem);

        DetailPersonagemFragment detailMovieFragment = new DetailPersonagemFragment();
        detailMovieFragment.setArguments(args);
        return detailMovieFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_personagem, container, false);

        textViewnome = (TextView) view.findViewById(R.id.detail_nome_personagem_id);
        textViewComicSummary = (TextView) view.findViewById(R.id.detail_comic_summary);
        imageViewPersonagem = (ImageView) view.findViewById(R.id.detail_image_poster);
        textViewDescricao = (TextView) view.findViewById(R.id.detail_descricao_personagem);

        // Inicializamos mMovie (ver onSaveInsatnceState)
        if (savedInstanceState == null){
            // Se não tem um estado anterior, use o que foi passado no método newInstance.
            personagem = (Personagem) getArguments().getSerializable(EXTRA_PERSONAGEM);
        } else {
            // Se há um estado anterior, use-o
            personagem = (Personagem)savedInstanceState.getSerializable(EXTRA_PERSONAGEM);
        }

        // Se o objeto mMovie possui um ID (no banco local), carregue do banco local,
        // senão carregue do servidor.
        if (personagem.getId() > 0){
            // Faz a requisição em background ao banco de dados (ver mCursorCallback)
            getLoaderManager().initLoader(LOADER_DB, null, mCursorCallback);
        } else {
            // Faz a requisição em background ao servidor (ver mMovieCallback)
           // getLoaderManager().initLoader(LOADER_WEB, null, mMarvelCallback);
            updateUI(personagem);
        }

        // Registramos o receiver para tratar sabermos quando o botão de favoritos da
        // activity de detalhes foi chamado.
        mReceiver = new MarvelEventReceiver();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(PersonagemEvent.UPDATE_FAVORITE));

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Precisamos manter o objeto personagem atualizado pois ele pode ter sido
        // incluído e excluído dos favoritos.
        outState.putSerializable(EXTRA_PERSONAGEM, personagem);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Desregistramos o receiver ao destruir a View do fragment
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }

    // --------------- LoaderManager.LoaderCallbacks<Movie>
    // Esse callback trata o retorno da requisição feita ao servidor
    LoaderManager.LoaderCallbacks mMarvelCallback = new LoaderManager.LoaderCallbacks<Personagem>() {
        @Override
        public Loader<Personagem> onCreateLoader(int id, Bundle args) {
            // inicializa a requisição em background para o servidor usando AsyncTaskLoader
            // (veja a classe MovieByIdTask)
            return new PersonagemByIdTask(getActivity(), personagem.getId());
        }

        @Override
        public void onLoadFinished(Loader<Personagem> loader, Personagem personagem) {
            updateUI(personagem);
        }

        @Override
        public void onLoaderReset(Loader<Personagem> loader) {
        }
    };

    // --------------- LoaderManager.LoaderCallbacks<Cursor>
    // Esse callback trata o retorno da requisição feita ao servidor
    LoaderManager.LoaderCallbacks<Cursor> mCursorCallback = new LoaderManager.LoaderCallbacks<Cursor>(){

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // inicializa a requisição em background para o ContentProvider usando CursorLoader
            // perceba que estamos utilizando a Uri específica
            // (veja o método query do MovieProvider)
            return new CursorLoader(getActivity(),
                    ContentUris.withAppendedId(PersonagemProvider.PERSONAGENS_URI, personagem.getId()),
                    null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            // Ao receber o retorno do cursor, criamos um objeto Movie e preenchemos a tela
            // (ver updateUI)
            if (cursor != null && cursor.moveToFirst()) {
                Personagem personagem = new Personagem();
                personagem.setId(cursor.getLong(cursor.getColumnIndex(PersonagemContract._ID)));
                personagem.setNome(cursor.getString(cursor.getColumnIndex(PersonagemContract.COL_NOME)));
                personagem.setDescricao(cursor.getString(cursor.getColumnIndex(PersonagemContract.COL_DESCRICAO)));

                Image imagem = new Image();

                imagem.setPath(cursor.getString(cursor.getColumnIndex(PersonagemContract.COL_IMAGEM_PATH)));
                imagem.setExtension(cursor.getString(cursor.getColumnIndex(PersonagemContract.COL_IMAGEM_EXTENSAO)));
                personagem.setImagem(imagem);

                Cursor c = getActivity().getContentResolver().query(PersonagemProvider.COMIC_SUMMARY_URI, null, ComicSummaryContract.COL_ID_PERSONAGEM +" = ?", new String[] { String.valueOf(personagem.getId()) }, null);

                if (c != null ) {

                    List<ComicSummary> lista = new ArrayList<ComicSummary>();

                    if  (c.moveToFirst()) {
                        do {

                            long idComic = c.getLong(c.getColumnIndex(ComicSummaryContract._ID));
                            String nome = c.getString(c.getColumnIndex(ComicSummaryContract.COL_NOME));
                            String idPersonagem = c.getString(c.getColumnIndex(ComicSummaryContract.COL_ID_PERSONAGEM));

                            ComicSummary comicSummary = new ComicSummary();

                            comicSummary.setName(nome);

                            lista.add(comicSummary);

                        }while (c.moveToNext());
                    }

                    if (lista.size() > 0) {

                        ComicList comicList = new ComicList();

                        comicList.setItems(lista.toArray(new ComicSummary[lista.size()]));

                        personagem.setComics(comicList);
                    }
                }


                updateUI(personagem);
            } else {
                updateUI(personagem);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    // --------------- INNER
    // Esse receiver é chamado pelo FAB da DetailActivity para iniciar o processo
    // de inserir/excluir o movie nos favoritos
    class MarvelEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(PersonagemEvent.UPDATE_FAVORITE)) {
                toggleFavorite();
            }
        }
    }

    private void updateUI(Personagem personagem){
        // Atualiza o objeto Personagem com os dados vindos dos callbacks
        // (ver mCursorCallback e mMovieCallback)

        Glide.with(imageViewPersonagem.getContext()).load(personagem.getImagem().getPath() + "." + personagem.getImagem().getExtension()).into(imageViewPersonagem);

        textViewnome.setText(personagem.getNome());

        String textoComicSumarry = "";

        if (personagem.getComics() != null && personagem.getComics().getItems() != null) {
            for (ComicSummary comic : personagem.getComics().getItems()) {
                if (comic.getName() != null) {
                    textoComicSumarry += comic.getName() + ",\n";
                }
            }
        }


        if (personagem.getDescricao() != null) {
            textViewDescricao.setText(personagem.getDescricao());
        } else {
            textViewDescricao.setText("");
        }

        textViewComicSummary.setText(textoComicSumarry);


        // Enviando mensagem para todos que querem saber que o filme carregou
        // (ver DetailActivity.MovieReceiver)
        notifyUpdate(PersonagemEvent.PERSONAGEM_LOADED);

        // Quando estiver em tablet, exiba o poster no próprio fragment
        if (getResources().getBoolean(R.bool.tablet)){

            if (personagem.getImagem() != null && personagem.getImagem().getPath() != null && personagem.getImagem().getExtension() != null) {

                String imagemURL = personagem.getImagem().getPath() + "." + personagem.getImagem().getExtension();

                imageViewPersonagem.setVisibility(View.VISIBLE);
                Glide.with(imageViewPersonagem.getContext()).load(imagemURL).into(imageViewPersonagem);

            }
        }
    }

    // Método auxiliar que insere/remove o movie no banco de dados
    private void toggleFavorite() {
        if (personagem == null) return; // isso não deve acontecer...

        // Primeiro verificamos se o livro está no banco de dados
        boolean isFavorite = PersonagemDetailUtils.isFavorite(getActivity(), personagem.getId());

        boolean success = false;
        if (isFavorite) {
            // Se já é favorito, exclua
            if (deleteFavorite(personagem.getId())){
                success = true;
                //personagem.setId(0);
                getLoaderManager().destroyLoader(LOADER_DB);
            }
            //TODO Mensagem de erro ao excluir

        } else {
            // Se não é favorito, inclua...
            long id = insertFavorite(personagem);
            success = id > 0;
            personagem.setId(id);
        }

        // Se deu tudo certo...
        if (success) {
            // Envia a mensagem para as activities (para atualizar o FAB)
            notifyUpdate(PersonagemEvent.PERSONAGEM_FAVORITE_UPDATED);

            // Exibe o snackbar que permite o "desfazer"
            //TODO Internailizar a aplicação
            Snackbar.make(getView(),
                    isFavorite ? R.string.msg_removed_favorites : R.string.msg_added_favorites,
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.text_undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toggleFavorite();
                            }
                        }
                    )
                   .show();
        }
    }

    private void notifyUpdate(String action){
        // Cria a intent e dispara o broadcast
        Intent it = new Intent(action);
        it.putExtra(PersonagemEvent.EXTRA_PERSONAGEM, personagem);
        mLocalBroadcastManager.sendBroadcast(it);
    }

    // Método auxiliar para excluir nos favoritos
    //TODO fazer delete em background
    private boolean deleteFavorite(long id){

        boolean deleted = false;



        Cursor c = getActivity().getContentResolver().query(PersonagemProvider.COMIC_SUMMARY_URI, new String[] {ComicSummaryContract._ID}, ComicSummaryContract.COL_ID_PERSONAGEM +" = ?", new String[] { String.valueOf(id) }, null);

        if (c != null ) {
            if  (c.moveToFirst()) {
                do {

                    long idComic = c.getLong(c.getColumnIndex(ComicSummaryContract._ID));
                    boolean deletedComic = getActivity().getContentResolver().delete(
                            ContentUris.withAppendedId(PersonagemProvider.COMIC_SUMMARY_URI, idComic), null, null) > 0;

                }while (c.moveToNext());
            }
        }

        deleted = getActivity().getContentResolver().delete(
                ContentUris.withAppendedId(PersonagemProvider.PERSONAGENS_URI, id),
                null, null) > 0;

        return deleted;
    }

    // Método auxiliar para inserir nos favoritos
    //TODO fazer insert em background
    private long insertFavorite(Personagem personagem){
        ContentValues contentValues = new ContentValues();
        contentValues.put(PersonagemContract._ID, personagem.getId());
        contentValues.put(PersonagemContract.COL_NOME, personagem.getNome());
        contentValues.put(PersonagemContract.COL_DESCRICAO, personagem.getDescricao());
        contentValues.put(PersonagemContract.COL_IMAGEM_PATH, personagem.getImagem().getPath());
        contentValues.put(PersonagemContract.COL_IMAGEM_EXTENSAO, personagem.getImagem().getExtension());


        Uri uri = getActivity().getContentResolver().insert(PersonagemProvider.PERSONAGENS_URI, contentValues);
        long id  = ContentUris.parseId(uri);

        if (personagem.getComics() != null && personagem.getComics().getItems() != null) {

            ContentValues cvComic = new ContentValues();

            for (ComicSummary commic : personagem.getComics().getItems()) {

                cvComic = new ContentValues();

                //cvComic.put(ComicSummaryContract._ID, personagem.getId());
                cvComic.put(ComicSummaryContract.COL_NOME, commic.getName());
                cvComic.put(ComicSummaryContract.COL_ID_PERSONAGEM, id);

                getActivity().getContentResolver().insert(PersonagemProvider.COMIC_SUMMARY_URI, cvComic);

            }

        }

        return id;
    }


}

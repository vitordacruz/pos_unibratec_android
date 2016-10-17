package br.com.aprendendo.vitor.marvel;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import br.com.aprendendo.vitor.marvel.database.PersonagemContract;
import br.com.aprendendo.vitor.marvel.model.Image;
import br.com.aprendendo.vitor.marvel.model.Personagem;


public class FavoritoPersonagemFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    OnPersonagemClickListener mPersonagemClickListener;
    PersonagemCursorAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Registrando o listener para saber quando movie foi clicado
        // Essa abordagem é a mais usada, e mais rápida
        // entretanto requer um atributo adicional
        if (context instanceof OnPersonagemClickListener) {
            mPersonagemClickListener = (OnPersonagemClickListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorito_personagem, container, false);
        ListView listView = (ListView) view.findViewById(R.id.favorites_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPersonagemClickListener != null) {
                    // Pegamos o cursor do adapter
                    Cursor cursor = mAdapter.getCursor();
                    // Movemos para a posição correspondente da lista
                    if (cursor.moveToPosition(position)) {
                        // Criamos um objeto Movie para passamos para a MainActivity
                        // perceba que esse Movie não tem todos os campos. Pois na tela
                        // de listagem apenas os campos necessários são utilizados
                        Personagem personagem = new Personagem();
                        personagem.setId(cursor.getLong(cursor.getColumnIndex(PersonagemContract._ID)));
                        personagem.setNome(cursor.getString(cursor.getColumnIndex(PersonagemContract.COL_NOME)));
                        personagem.setDescricao(cursor.getString(cursor.getColumnIndex(PersonagemContract.COL_DESCRICAO)));

                        Image imagem = new Image();
                        imagem.setPath(cursor.getString(cursor.getColumnIndex(PersonagemContract.COL_IMAGEM_PATH)));
                        imagem.setExtension(cursor.getString(cursor.getColumnIndex(PersonagemContract.COL_IMAGEM_EXTENSAO)));

                        personagem.setImagem(imagem);

                        mPersonagemClickListener.onPersonagemClick(personagem, position);

                    }
                }
            }
        });

        // Inicializamos e definimos o adapter da lista
        mAdapter = new PersonagemCursorAdapter(getActivity(), null);
        listView.setAdapter(mAdapter);

        // Definimos a view a ser exibida se a lista estiver vazia
        listView.setEmptyView(view.findViewById(R.id.empty_view_root));

        // Inicializamos o loader para trazer os registros em background
        getLoaderManager().initLoader(0, null, this);


        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Realizando a query em bacground (ver método query do MovieProvider)
        return new CursorLoader(getActivity(),
                PersonagemProvider.PERSONAGENS_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}

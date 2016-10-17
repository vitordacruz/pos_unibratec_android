package br.com.aprendendo.vitor.marvel;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.com.aprendendo.vitor.marvel.http.PersonagemSearchTask;
import br.com.aprendendo.vitor.marvel.model.Personagem;

public class PersonagemListFragment extends Fragment implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<List<Personagem>> {

    private static final int LOADER_ID = 0;
    private static final String QUERY_PARAM = "busca";

    SearchView mSearchView;
    RecyclerView mRecyclerView;
    PersonagemAdapter mPersonagemAdapter;
    List<Personagem> mPersonagemList;
    LoaderManager mLoaderManager;
    View mEmptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPersonagemList = new ArrayList<Personagem>();
        mPersonagemAdapter = new PersonagemAdapter(getActivity(), mPersonagemList);
        mPersonagemAdapter.setOnPersonagemClickListener(new OnPersonagemClickListener() {
            @Override
            public void onPersonagemClick(Personagem personagem, int position) {
                // Nessa abordagem o click é mais lento,
                // mas não precisamos usar um atributo adicional
                Activity activity = getActivity();
                if (activity instanceof OnPersonagemClickListener){
                    ((OnPersonagemClickListener)activity).onPersonagemClick(personagem, position);
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personagem_list, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.main_recycler_personagens);
        mEmptyView = view.findViewById(R.id.empty_view_root);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && getResources().getBoolean(R.bool.phone)) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        mRecyclerView.setAdapter(mPersonagemAdapter);

        mLoaderManager = getActivity().getSupportLoaderManager();
        mLoaderManager.initLoader(LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Bundle params = new Bundle();
        params.putString(QUERY_PARAM, query);
        mLoaderManager.restartLoader(LOADER_ID, params, this);
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public Loader<List<Personagem>> onCreateLoader(int id, Bundle args) {
        String s = args != null ? args.getString(QUERY_PARAM) : null;
        return new PersonagemSearchTask(getContext(), s);
    }

    @Override
    public void onLoadFinished(Loader<List<Personagem>> loader, List<Personagem> data) {
        if (data != null && data.size() > 0) {
            mPersonagemList.clear();
            mPersonagemList.addAll(data);
            mPersonagemAdapter.notifyDataSetChanged();
            mEmptyView.setVisibility(View.GONE);
        } else {
            mPersonagemList.clear();
            mPersonagemAdapter.notifyDataSetChanged();
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Personagem>> loader) {
    }


}

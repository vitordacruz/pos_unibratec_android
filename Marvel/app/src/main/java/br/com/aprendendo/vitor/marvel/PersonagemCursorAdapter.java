package br.com.aprendendo.vitor.marvel;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import br.com.aprendendo.vitor.marvel.database.PersonagemContract;

/**
 * Created by vitor on 20/09/2016.
 */

public class PersonagemCursorAdapter extends SimpleCursorAdapter {

    private static final int LAYOUT = R.layout.item_personagem_layout;

    public PersonagemCursorAdapter(Context context, Cursor c) {
        super(context, LAYOUT, c, PersonagemContract.LIST_COLUMNS, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(LAYOUT, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageViewPersonagem;
        TextView textViewNome;

        imageViewPersonagem = (ImageView) view.findViewById(R.id.personagem_item_image);
        textViewNome = (TextView) view.findViewById(R.id.personagem_item_text_name);

        String nome = cursor.getString(cursor.getColumnIndex(PersonagemContract.COL_NOME));
        String path = cursor.getString(cursor.getColumnIndex(PersonagemContract.COL_IMAGEM_PATH));
        String extension = cursor.getString(cursor.getColumnIndex(PersonagemContract.COL_IMAGEM_EXTENSAO));

        Glide.with(mContext)
                .load(path + "."  + extension)
                .placeholder(R.drawable.ic_empty_photo)
                .into(imageViewPersonagem);
        textViewNome.setText(nome);
    }

}

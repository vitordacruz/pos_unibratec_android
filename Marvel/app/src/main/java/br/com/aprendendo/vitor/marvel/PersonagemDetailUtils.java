package br.com.aprendendo.vitor.marvel;

import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;

import br.com.aprendendo.vitor.marvel.database.PersonagemContract;

/**
 * Created by vitor on 20/09/2016.
 */

public class PersonagemDetailUtils {


    public static boolean isFavorite(Context ctx, long id){
        Cursor cursor = ctx.getContentResolver().query(
                PersonagemProvider.PERSONAGENS_URI,
                new String[]{ PersonagemContract._ID },
                PersonagemContract._ID +" = ?",
                new String[]{ String.valueOf(id) },
                null
        );
        boolean isFavorite = false;
        if (cursor != null) {
            isFavorite = cursor.getCount() > 0;
            cursor.close();
        }
        return isFavorite;
    }

    public static void toggleFavorite(Context ctx, FloatingActionButton fab, long id){
        if (PersonagemDetailUtils.isFavorite(ctx, id)){
            fab.setImageResource(R.drawable.ic_favorite);
        } else {
            fab.setImageResource(R.drawable.ic_unfavorite);
        }
    }

}

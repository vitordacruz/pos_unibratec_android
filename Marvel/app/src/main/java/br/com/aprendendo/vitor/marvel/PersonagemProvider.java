package br.com.aprendendo.vitor.marvel;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import br.com.aprendendo.vitor.marvel.database.ComicSummaryContract;
import br.com.aprendendo.vitor.marvel.database.MarvelDbHelper;
import br.com.aprendendo.vitor.marvel.database.PersonagemContract;

public class PersonagemProvider extends ContentProvider {

    private static final String PATH = "personagens";
    private static final String PATH_COMIC_SUMMARY = "comic_summary";
    private static final String AUTHORITY = "br.com.aprendendo.vitor.marvel";

    // BASE_URI   = "content://" + AUTHORITY +"/"+ PATH
    public static Uri BASE_URI = Uri.parse("content://"+ AUTHORITY);

    // PERSONAGENS_URI = "content://" + AUTHORITY +"/"+ PATH +"/personagens"
    public static Uri PERSONAGENS_URI = BASE_URI.withAppendedPath(BASE_URI, PATH);

    public static Uri COMIC_SUMMARY_URI = BASE_URI.withAppendedPath(BASE_URI, PATH_COMIC_SUMMARY);

    // Conforme implementação do getType(), nosso provider aceita dois tipos de Uri:
    // GENERICA, usada no insert e query   = content://br.com.aprendendo.vitor.marvel/personagens
    // POR ID, usada no delete e query = content://br.com.aprendendo.vitor.marvel/personagens/{id_personagen}
    private static final int TYPE_GENERIC = 0;
    private static final int TYPE_ID = 1;
    private static final int TYPE_GENERIC_COMIC_SUMMARY = 2;
    private static final int TYPE_ID_COMIC_SUMMARY = 3;

    private UriMatcher mMatcher;
    private MarvelDbHelper mHelper;

    public PersonagemProvider() {
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(AUTHORITY, PATH, TYPE_GENERIC);
        mMatcher.addURI(AUTHORITY, PATH +"/#", TYPE_ID);
        mMatcher.addURI(AUTHORITY, PATH_COMIC_SUMMARY, TYPE_GENERIC_COMIC_SUMMARY);
        mMatcher.addURI(AUTHORITY, PATH_COMIC_SUMMARY +"/#", TYPE_ID_COMIC_SUMMARY);
    }

    @Override
    public boolean onCreate() {
        mHelper = new MarvelDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {
        int uriType = mMatcher.match(uri);
        switch (uriType){
            case TYPE_GENERIC:
                // Informamos ao android que vamos retornar vários registros
                return ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+ AUTHORITY;
            case TYPE_ID:
                // Informamos ao android que vamos retornar um único registro
                return ContentResolver.CURSOR_ITEM_BASE_TYPE +"/"+ AUTHORITY;
            case TYPE_GENERIC_COMIC_SUMMARY:
                return ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+ AUTHORITY;
            case TYPE_ID_COMIC_SUMMARY:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE +"/"+ AUTHORITY;
            default:
                throw new IllegalArgumentException("Invalid Uri");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = mMatcher.match(uri);
        // Como não temos o ID no momento da inserção, só aceitamos
        // inserir usando a Uri genérica.
        if (uriType == TYPE_GENERIC){
            SQLiteDatabase db = mHelper.getWritableDatabase();
            long id = db.insert(PersonagemContract.TABLE_NAME, null, values);
            db.close();
            // Se der erro na inclusão o id retornado é -1,
            // então levantamos a exceção para ser tratada na tela.
            if (id == -1){
                throw new RuntimeException("Error inserting moving.");
            }
            notifyChanges(uri);
            return ContentUris.withAppendedId(PERSONAGENS_URI, id);

        } else if (uriType == TYPE_GENERIC_COMIC_SUMMARY) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            long id = db.insert(ComicSummaryContract.TABLE_NAME, null, values);
            db.close();
            // Se der erro na inclusão o id retornado é -1,
            // então levantamos a exceção para ser tratada na tela.
            if (id == -1){
                throw new RuntimeException("Error inserting moving.");
            }
            notifyChanges(uri);
            return ContentUris.withAppendedId(COMIC_SUMMARY_URI, id);
        } else {
            throw new IllegalArgumentException("Invalid Uri");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = mMatcher.match(uri);
        // Nossa implementação só aceita a exclusão baseada no id do personagem no banco
        if (uriType == TYPE_ID){
            SQLiteDatabase db = mHelper.getWritableDatabase();
            long id = ContentUris.parseId(uri);
            int rowsAffected = db.delete(
                    PersonagemContract.TABLE_NAME,
                    PersonagemContract._ID +" = ?",
                    new String[] { String.valueOf(id) } );
            db.close();
            // Se nenhuma linha foi afetada pela exclusão, levantamos uma exceção
            if (rowsAffected == 0){
                throw new RuntimeException("Fail deleting movie");
            }
            notifyChanges(uri);

            return rowsAffected;

        } else if(uriType == TYPE_ID_COMIC_SUMMARY) {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            long id = ContentUris.parseId(uri);
            int rowsAffected = db.delete(
                    ComicSummaryContract.TABLE_NAME,
                    ComicSummaryContract._ID +" = ?",
                    new String[] { String.valueOf(id) } );
            db.close();
            // Se nenhuma linha foi afetada pela exclusão, levantamos uma exceção
            if (rowsAffected == 0){
                throw new RuntimeException("Fail deleting movie");
            }
            notifyChanges(uri);

            return rowsAffected;
        } else {
            throw new IllegalArgumentException("Invalid Uri");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // Não utilizamos a atualização em nossa aplicação
        throw new IllegalArgumentException("Invalid Uri");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int uriType = mMatcher.match(uri);
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor;
        // Na nossa implementação, o método query é o único que aceita os dois tipos de Uris.
        switch (uriType){
            // Esse tipo faz uma busca genérica. Estamos usando ele na listagem dos personagens
            // e para checar se um personagem é favorito (ou seja, se já existe no banco)
            case TYPE_GENERIC:
                cursor = db.query(PersonagemContract.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            // Esse segundo tipo de Uri está sendo usado na tela de detalhes
            // para trazer todas as informações do filme.
            case TYPE_ID:
                long id = ContentUris.parseId(uri);
                cursor = db.query(PersonagemContract.TABLE_NAME,
                        projection, PersonagemContract._ID +" = ?",
                        new String[] { String.valueOf(id) }, null, null, sortOrder);
                break;
            case TYPE_GENERIC_COMIC_SUMMARY:
                cursor = db.query(ComicSummaryContract.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case TYPE_ID_COMIC_SUMMARY:
                long idCommic = ContentUris.parseId(uri);
                cursor = db.query(ComicSummaryContract.TABLE_NAME,
                        projection, ComicSummaryContract._ID +" = ?",
                        new String[] { String.valueOf(idCommic) }, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Invalid Uri");
        }
        // Essa linha está definindo a Uri que será notificada para que o cursor
        // seja atualizado. Veja método notifyChanges abaixo.
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    private void notifyChanges(Uri uri) {
        // Caso a operação no banco ocorra sem problemas, notificamos a Uri
        // para que a listagem de favoritos seja atualizada.
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }
}

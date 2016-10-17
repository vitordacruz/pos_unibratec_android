package br.com.aprendendo.vitor.marvel.http;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.aprendendo.vitor.marvel.model.Personagem;
import br.com.aprendendo.vitor.marvel.util.MD5;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by vitor on 18/09/2016.
 */
public class MarvelHttp {

    private static final String URL_PERSONAGEM = "http://gateway.marvel.com:80/v1/public/characters?nameStartsWith=%s&apikey=" + MD5.PUBLIC_KEY;

    private static final String URL_PERSONAGEM_POR_ID = "http://gateway.marvel.com:80/v1/public/characters/%s?apikey=" + MD5.PUBLIC_KEY;


    public static List<Personagem> buscarPersonagens(String query) {

        List<Personagem> personagemList = new ArrayList<Personagem>();

        if (query == null) {
            return personagemList;
        }

        // Abre a conexão com o servidor
        OkHttpClient client = new OkHttpClient();

        long timestamp = (new Date()).getTime();

        String url = String.format(URL_PERSONAGEM, query) + "&ts=" + String.valueOf(timestamp) + "&hash=" + MD5.md5Marvel(timestamp);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;

        try {

            // Realiza a chamada ao servidor
            response = client.newCall(request).execute();

            // response.body retorna o corpo da resposta, que no nosso caso é JSON
            String json = response.body().string();
            JSONObject jsonObject = new JSONObject(json);

            JSONObject jsonDataObject = jsonObject.getJSONObject("data");
            JSONArray jsonArray = jsonDataObject.getJSONArray("results");
            String jsonList = jsonArray.toString();

            Gson gson = new Gson();
            Personagem[] personagensArray = gson.fromJson(jsonList, Personagem[].class);

            System.out.println("personagensArray.length: " + personagensArray.length);

            personagemList.addAll(Arrays.asList(personagensArray));

            System.out.println("personagensArray.size(): " + personagemList.size());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return personagemList;

    }

    public static Personagem loadPersonagem(long id) {

        Personagem personagem = null;


        // Abre a conexão com o servidor
        OkHttpClient client = new OkHttpClient();

        long timestamp = (new Date()).getTime();

        String url = String.format(URL_PERSONAGEM_POR_ID, String.valueOf(id)) + "&ts=" + String.valueOf(timestamp) + "&hash=" + MD5.md5Marvel(timestamp);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;

        try {

            // Realiza a chamada ao servidor
            response = client.newCall(request).execute();

            // response.body retorna o corpo da resposta, que no nosso caso é JSON
            String json = response.body().string();
            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonDataObject = jsonObject.getJSONObject("data");
            JSONArray jsonArray = jsonDataObject.getJSONArray("results");
            String jsonList = jsonArray.toString();

            Gson gson = new Gson();
            Personagem[] personagensArray = gson.fromJson(jsonList, Personagem[].class);

            System.out.println("personagensArray.length: " + personagensArray.length);

            if (personagensArray.length == 1) {
                personagem = personagensArray[0];
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return personagem;

    }

}

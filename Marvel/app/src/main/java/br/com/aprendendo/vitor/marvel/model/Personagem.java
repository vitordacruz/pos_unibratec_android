package br.com.aprendendo.vitor.marvel.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by vitor on 18/09/2016.
 */
public class Personagem implements Serializable {

    private long id;

    @SerializedName("name")
    private String nome;

    @SerializedName("description")
    private String descricao;

    @SerializedName("thumbnail")
    private Image imagem;

    @SerializedName("comics")
    private ComicList comics;

    public ComicList getComics() {
        return comics;
    }

    public void setComics(ComicList comics) {
        this.comics = comics;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Image getImagem() {
        return imagem;
    }

    public void setImagem(Image imagem) {
        this.imagem = imagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}

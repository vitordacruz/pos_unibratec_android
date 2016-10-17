package br.com.aprendendo.vitor.marvel.model;

import java.io.Serializable;

/**
 * Created by vitor on 18/09/2016.
 */
public class ComicList implements Serializable {

    private ComicSummary[] items;

    public ComicSummary[] getItems() {
        return items;
    }

    public void setItems(ComicSummary[] items) {
        this.items = items;
    }
}

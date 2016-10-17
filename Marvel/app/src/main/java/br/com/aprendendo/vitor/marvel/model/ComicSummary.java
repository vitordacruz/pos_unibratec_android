package br.com.aprendendo.vitor.marvel.model;

import java.io.Serializable;

/**
 * Created by vitor on 18/09/2016.
 */
public class ComicSummary implements Serializable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

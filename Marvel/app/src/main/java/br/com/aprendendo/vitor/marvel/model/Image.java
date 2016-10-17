package br.com.aprendendo.vitor.marvel.model;

import java.io.Serializable;

/**
 * Created by vitor on 18/09/2016.
 */
public class Image implements Serializable {

    private String path;

    private String extension;

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

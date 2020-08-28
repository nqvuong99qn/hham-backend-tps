package com.tpssoft.hham.dto;

import java.io.Serializable;

public class UrlWrapper implements Serializable {
    private String url = "";

    public UrlWrapper() {
    }

    public UrlWrapper(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "UrlWrapper{url='" + url + "'}";
    }
}

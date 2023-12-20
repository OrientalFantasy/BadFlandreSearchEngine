package com.badflandre.search.util;

public class QueryData {
    private String title;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "QueryData{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public QueryData(String title, String url, String content) {
        this.title = title;
        this.url = url;
        this.content = content;
    }
}

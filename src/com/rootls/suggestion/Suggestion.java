package com.rootls.suggestion;

import java.io.Serializable;

/**
 * Created by luowei on 2014/6/27.
 */
public class Suggestion implements Serializable {
    Integer id;
    String author;
    String username;
    String comment;

    public Suggestion() {
    }

    public Suggestion(Integer id, String author, String username, String comment) {
        this.id = id;
        this.author = author;
        this.username = username;
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

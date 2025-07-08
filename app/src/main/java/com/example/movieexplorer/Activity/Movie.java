package com.example.movieexplorer.Activity;

import java.io.Serializable;

public class Movie implements Serializable {

    String title;
    String score;
    String img;
    String overview;
    String releaseDate;

    public Movie() {}

    public Movie(String title, String score, String img) {
        this.title = title;
        this.score = score;
        this.img = img;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}

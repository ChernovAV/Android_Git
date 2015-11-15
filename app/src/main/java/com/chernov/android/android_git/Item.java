package com.chernov.android.android_git;

public class Item {

    // repository name
    private String title;
    // language code
    private String language;
    // count of stars
    private String stars;
    // count of forks
    private String forks;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public void setForks(String forks) {
        this.forks = forks;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public String getStars() {
        return stars;
    }

    public String getForks() {
        return forks;
    }

}
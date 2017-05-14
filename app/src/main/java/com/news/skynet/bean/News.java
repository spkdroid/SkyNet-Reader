package com.news.skynet.bean;

/**
 * Created by Ramkumar Velmurugan on 7/15/2016.
 *
 *  News.java
 *
 *  A simple bean class to hold the information of the News feed in the list.
 *
 */

public class News {

    private String newsTitle;
    private String newsLine;
    private String url;
    private String image;


    private String date;


    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsLine() {
        return newsLine;
    }

    public void setNewsLine(String newsLine) {
        this.newsLine = newsLine;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



}

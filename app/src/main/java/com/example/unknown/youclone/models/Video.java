package com.example.unknown.youclone.models;

public class Video {
    public String videoId;
    public Image image = new Image();
    public String title;
    public String description;


    public transient VideoBinder binder = new VideoBinder(this );

}

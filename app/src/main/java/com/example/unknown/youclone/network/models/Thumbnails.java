package com.example.unknown.youclone.network.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Unknown.
 */

public class Thumbnails {
    @SerializedName("default")
    public Image regular;
    public Image medium;
    public Image high;
}

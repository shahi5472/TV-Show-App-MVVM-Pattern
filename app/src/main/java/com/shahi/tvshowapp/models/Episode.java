package com.shahi.tvshowapp.models;

import com.google.gson.annotations.SerializedName;

public class Episode {

    @SerializedName("season")
    private int season;

    @SerializedName("episode")
    private int episode;

    @SerializedName("name")
    private String name;

    @SerializedName("air_date")
    private String airDate;


    public int getSeason() {
        return season;
    }

    public int getEpisode() {
        return episode;
    }

    public String getName() {
        return name;
    }

    public String getAirDate() {
        return airDate;
    }
}

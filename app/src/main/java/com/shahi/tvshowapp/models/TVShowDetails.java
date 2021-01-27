package com.shahi.tvshowapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TVShowDetails {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("permalink")
    private String permalink;

    @SerializedName("url")
    private String url;

    @SerializedName("description")
    private String description;

    @SerializedName("description_source")
    private String descriptionSource;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("country")
    private String country;

    @SerializedName("status")
    private String status;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("network")
    private String network;

    @SerializedName("youtube_link")
    private String youtubeLink;

    @SerializedName("image_path")
    private String imagePath;

    @SerializedName("image_thumbnail_path")
    private String imageThumbnailPath;

    @SerializedName("rating")
    private String rating;

    @SerializedName("countdown")
    private String countdown;

    @SerializedName("genres")
    private String[] genres;

    @SerializedName("pictures")
    private String[] pictures;

    @SerializedName("episodes")
    private List<Episode> episodes;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionSource() {
        return descriptionSource;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getCountry() {
        return country;
    }

    public String getStatus() {
        return status;
    }

    public int getRuntime() {
        return runtime;
    }

    public String getNetwork() {
        return network;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getImageThumbnailPath() {
        return imageThumbnailPath;
    }

    public String getRating() {
        return rating;
    }

    public String getCountdown() {
        return countdown;
    }

    public String[] getGenres() {
        return genres;
    }

    public String[] getPictures() {
        return pictures;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }
}

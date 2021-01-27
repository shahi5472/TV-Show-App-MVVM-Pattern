package com.shahi.tvshowapp.responses;

import com.google.gson.annotations.SerializedName;
import com.shahi.tvshowapp.models.TVShowDetails;

public class TVShowDetailsResponse {

    @SerializedName("tvShow")
    private TVShowDetails tvShowDetails;

    public TVShowDetails getTvShowDetails() {
        return tvShowDetails;
    }
}

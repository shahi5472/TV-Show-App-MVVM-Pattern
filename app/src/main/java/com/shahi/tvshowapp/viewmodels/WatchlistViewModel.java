package com.shahi.tvshowapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.shahi.tvshowapp.database.TVShowDatabase;
import com.shahi.tvshowapp.models.TVShow;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class WatchlistViewModel extends AndroidViewModel {

    private TVShowDatabase tvShowDatabase;

    public WatchlistViewModel(@NonNull Application application) {
        super(application);
        tvShowDatabase = TVShowDatabase.getTvShowDatabase(application);
    }

    public Flowable<List<TVShow>> loadWatchlist() {
        return tvShowDatabase.tvShowDao().getWatchList();
    }

    public Completable removeFromWatchlist(TVShow tvShow){
        return tvShowDatabase.tvShowDao().deleteFromWatchList(tvShow);
    }

}

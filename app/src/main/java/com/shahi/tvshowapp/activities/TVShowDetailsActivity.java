package com.shahi.tvshowapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.shahi.tvshowapp.R;
import com.shahi.tvshowapp.adapters.EpisodeAdapter;
import com.shahi.tvshowapp.adapters.ImageSliderAdapter;
import com.shahi.tvshowapp.adapters.WatchlistAdapter;
import com.shahi.tvshowapp.databinding.ActivityTVShowDetailsBinding;
import com.shahi.tvshowapp.databinding.LayoutEpisodesBottomSheetBinding;
import com.shahi.tvshowapp.models.TVShow;
import com.shahi.tvshowapp.models.TVShowDetails;
import com.shahi.tvshowapp.utilities.TempDataHolder;
import com.shahi.tvshowapp.viewmodels.TVShowDetailsModel;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {

    private ActivityTVShowDetailsBinding activityTVShowDetailsBinding;
    private TVShowDetailsModel tvShowDetailsModel;
    private BottomSheetDialog episodeBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;
    private Boolean isTVShowAvailableInWatchlist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTVShowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_t_v_show_details);
        doInitialization();
    }

    private void doInitialization() {
        tvShowDetailsModel = new ViewModelProvider(this).get(TVShowDetailsModel.class);
        activityTVShowDetailsBinding.imageBack.setOnClickListener(v -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        checkTVShowInWatchlist();
        getTVShowDetails();
    }

    private void checkTVShowInWatchlist() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(tvShowDetailsModel.getTVShowFromWatchlist(String.valueOf(tvShow.getId()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShows -> {
                    isTVShowAvailableInWatchlist = true;
                    activityTVShowDetailsBinding.imageWatchList.setImageResource(R.drawable.ic_check);
                    compositeDisposable.dispose();
                }));
    }

    private void getTVShowDetails() {
        activityTVShowDetailsBinding.setIsLoading(true);
        String tvShowId = String.valueOf(tvShow.getId());
        tvShowDetailsModel.getTVShowDetails(tvShowId).observe(this, tvShowDetailsResponse -> {
            activityTVShowDetailsBinding.setIsLoading(false);
            if (tvShowDetailsResponse.getTvShowDetails() != null) {
                if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                    loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                }
                activityTVShowDetailsBinding.setTvShowImageURL(tvShowDetailsResponse.getTvShowDetails().getImagePath());
                activityTVShowDetailsBinding.imageTVShow.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.buttonEpisodes.setOnClickListener(v -> {
                    if (episodeBottomSheetDialog == null) {
                        episodeBottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
                        layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(
                                LayoutInflater.from(TVShowDetailsActivity.this),
                                R.layout.layout_episodes_bottom_sheet,
                                findViewById(R.id.episodesContainer),
                                false
                        );
                        episodeBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                        layoutEpisodesBottomSheetBinding.episodesRecyclerView.setAdapter(
                                new EpisodeAdapter(tvShowDetailsResponse.getTvShowDetails().getEpisodes()));
                        layoutEpisodesBottomSheetBinding.textTitle.setText(
                                String.format("Episode | %s", tvShow.getName()));
                        layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(v1 -> episodeBottomSheetDialog.dismiss());
                    }

                    // -----  Optional section start ----- //
                    FrameLayout frameLayout = episodeBottomSheetDialog.findViewById(
                            com.google.android.material.R.id.design_bottom_sheet
                    );
                    if (frameLayout != null) {
                        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                        bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    // -----  Optional section end ----- //
                    episodeBottomSheetDialog.show();
                });

                activityTVShowDetailsBinding.imageWatchList.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.imageWatchList.setOnClickListener(v -> {
                    CompositeDisposable compositeDisposable = new CompositeDisposable();
                    if (isTVShowAvailableInWatchlist) {
                        compositeDisposable.add(tvShowDetailsModel.removeTVShowFromWatchlist(tvShow)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    isTVShowAvailableInWatchlist = false;
                                    TempDataHolder.IS_WATCH_LIST_UPDATED = true;
                                    activityTVShowDetailsBinding.imageWatchList.setImageResource(R.drawable.ic_watch_list);
                                    compositeDisposable.dispose();
                                    Toast.makeText(TVShowDetailsActivity.this, "Remove from watchlist", Toast.LENGTH_SHORT).show();
                                }));
                    } else {
                        compositeDisposable.add(tvShowDetailsModel.addToWatchList(tvShow)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    TempDataHolder.IS_WATCH_LIST_UPDATED = true;
                                    activityTVShowDetailsBinding.imageWatchList.setImageResource(R.drawable.ic_check);
                                    compositeDisposable.dispose();
                                    Toast.makeText(TVShowDetailsActivity.this, "Added to watchlist", Toast.LENGTH_SHORT).show();
                                }));
                    }
                });
                loadBasicTVShowDetails(tvShowDetailsResponse.getTvShowDetails());
            }
        });
    }

    private void loadImageSlider(String[] pictures) {
        activityTVShowDetailsBinding.sliderView.setOffscreenPageLimit(1);
        activityTVShowDetailsBinding.sliderView.setAdapter(new ImageSliderAdapter(pictures));
        activityTVShowDetailsBinding.sliderView.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicator(pictures.length);
        activityTVShowDetailsBinding.sliderView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setupCurrentSliderIndicator(position);
            }
        });
    }

    private void setupSliderIndicator(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.background_slider_indicator_inactive)
            );
            indicators[i].setLayoutParams(layoutParams);
            activityTVShowDetailsBinding.layoutSliderIndicators.addView(indicators[i]);
        }
        activityTVShowDetailsBinding.layoutSliderIndicators.setVisibility(View.VISIBLE);
        setupCurrentSliderIndicator(0);
    }

    private void setupCurrentSliderIndicator(int position) {
        int childCount = activityTVShowDetailsBinding.layoutSliderIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) activityTVShowDetailsBinding.layoutSliderIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.background_slider_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.background_slider_indicator_inactive));
            }
        }
    }

    private void loadBasicTVShowDetails(TVShowDetails tvShowDetails) {
        activityTVShowDetailsBinding.setTvShowName(tvShow.getName());
        activityTVShowDetailsBinding.textName.setVisibility(View.VISIBLE);

        activityTVShowDetailsBinding.setStartedDate(tvShow.getStart_date());
        activityTVShowDetailsBinding.textStarted.setVisibility(View.VISIBLE);

        activityTVShowDetailsBinding.setNetworkCountry(tvShow.getNetwork() +
                " (" + tvShow.getCountry() + ")");
        activityTVShowDetailsBinding.textNetworkCountry.setVisibility(View.VISIBLE);

        activityTVShowDetailsBinding.setStatus(tvShow.getStatus());
        activityTVShowDetailsBinding.textStatus.setVisibility(View.VISIBLE);

        activityTVShowDetailsBinding.setDescription(String.valueOf(HtmlCompat.fromHtml(tvShowDetails.getDescription(),
                HtmlCompat.FROM_HTML_MODE_LEGACY)));
        activityTVShowDetailsBinding.textDescription.setVisibility(View.VISIBLE);

        activityTVShowDetailsBinding.textReadMore.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.textReadMore.setOnClickListener(v -> {
            if (activityTVShowDetailsBinding.textReadMore.getText().toString().equals("Read More")) {
                activityTVShowDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                activityTVShowDetailsBinding.textDescription.setEllipsize(null);
                activityTVShowDetailsBinding.textReadMore.setText(R.string.read_less);
            } else {
                activityTVShowDetailsBinding.textDescription.setMaxLines(4);
                activityTVShowDetailsBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                activityTVShowDetailsBinding.textReadMore.setText(R.string.read_more);
            }
        });

        activityTVShowDetailsBinding.setRating(String.format(Locale.getDefault(), "%.2f", Double.parseDouble(tvShowDetails.getRating())));

        if (tvShowDetails.getGenres() != null) {
            activityTVShowDetailsBinding.setGenre(tvShowDetails.getGenres()[0]);
        } else {
            activityTVShowDetailsBinding.setGenre("N/A");
        }
        activityTVShowDetailsBinding.setRuntime(tvShowDetails.getRuntime() + " Min");
        activityTVShowDetailsBinding.viewDivider1.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.viewDivider2.setVisibility(View.VISIBLE);

        activityTVShowDetailsBinding.buttonWebsite.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(tvShowDetails.getUrl()));
            startActivity(intent);
        });
        activityTVShowDetailsBinding.buttonWebsite.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.buttonEpisodes.setVisibility(View.VISIBLE);

    }
}
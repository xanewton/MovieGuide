/*
 * Copyright (C) 2017 Angel Newton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xengar.android.movieguide.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.PaletteAsyncListener;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.ui.MovieActivity;
import com.xengar.android.movieguide.ui.PersonActivity;
import com.xengar.android.movieguide.ui.SearchActivity;
import com.xengar.android.movieguide.ui.SettingsActivity;
import com.xengar.android.movieguide.ui.TVShowActivity;

import static com.xengar.android.movieguide.utils.Constants.KNOWN_FOR_BACKGROUND_POSTER;
import static com.xengar.android.movieguide.utils.Constants.LOG;
import static com.xengar.android.movieguide.utils.Constants.MOVIE_ID;
import static com.xengar.android.movieguide.utils.Constants.PERSON_ID;
import static com.xengar.android.movieguide.utils.Constants.SHARED_PREF_NAME;
import static com.xengar.android.movieguide.utils.Constants.TV_SHOW_ID;

/**
 * ActivityUtils. To handle common tasks.
 */
public class ActivityUtils {

    private static final String TAG = ActivityUtils.class.getSimpleName();

    /**
     * Saves the variable into Preferences.
     * @param context context
     * @param name name of preference
     * @param value value
     */
    public static void saveIntToPreferences(final Context context, final String name,
                                            final int value) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, 0);
        SharedPreferences.Editor e = prefs.edit();
        e.putInt(name, value);
        e.commit();
    }

    /**
     * Saves the variable into Preferences.
     * @param context context
     * @param name name of preference
     * @param value value
     */
    public static void saveStringToPreferences(final Context context, final String name,
                                                final String value) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, 0);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(name, value);
        e.commit();
    }

    /**
     * Returns the max cast items from preferences.
     * @param context context
     * @return number or default(6)
     */
    public static int getPreferenceMaxCastItems(final Context context) {
        String key = context.getString(R.string.pref_max_cast_list);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String data = prefs.getString(key, null);
        return (data != null)? Integer.parseInt(data) : 6;
    }

    /**
     * Returns the max movie items from preferences.
     * @param context context
     * @return number of default(-1)
     */
    public static int getPreferenceMaxMovieItems(final Context context) {
        String key = context.getString(R.string.pref_max_movie_list);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String data = prefs.getString(key, null);
        return (data != null)? Integer.parseInt(data) : -1;
    }

    /**
     * Defines a palette listener.
     * @param title title
     * @param textRating text rating
     * @param rating rating
     * @param starRating star rating
     * @return palette
     */
    public static PaletteAsyncListener definePaletteAsyncListener(final Context context,
                                                                  final TextView title,
                                                                  final TextView textRating,
                                                                  final LinearLayout rating,
                                                                  final ImageView starRating) {
        return new PaletteAsyncListener() {
            @SuppressLint("NewApi")
            @Override
            public void onGenerated(Palette palette) {
                if (LOG) {
                    Log.v(TAG, "textSwatch.PaletteAsyncListener");
                }
                Palette.Swatch textSwatch = palette.getMutedSwatch();
                Palette.Swatch bgSwatch = palette.getDarkVibrantSwatch();

                if (textSwatch != null && bgSwatch != null) {
                    title.setTextColor(textSwatch.getTitleTextColor());
                    title.setBackgroundColor(textSwatch.getRgb());
                    textRating.setTextColor(bgSwatch.getTitleTextColor());
                    rating.setBackgroundColor(bgSwatch.getRgb());
                    starRating.setBackgroundColor(bgSwatch.getTitleTextColor());
                } else if (bgSwatch != null) {
                    title.setBackgroundColor(bgSwatch.getRgb());
                    title.setTextColor(bgSwatch.getBodyTextColor());
                    rating.setBackgroundColor(bgSwatch.getBodyTextColor());
                    textRating.setTextColor(bgSwatch.getRgb());
                    starRating.setBackgroundColor(bgSwatch.getRgb());
                } else if (textSwatch != null) {
                    title.setBackgroundColor(textSwatch.getRgb());
                    title.setTextColor(textSwatch.getBodyTextColor());
                    rating.setBackgroundColor(textSwatch.getBodyTextColor());
                    textRating.setTextColor(textSwatch.getRgb());
                    starRating.setBackgroundColor(textSwatch.getRgb());
                } else {
                    title.setTextColor(
                            context.getResources().getColor(R.color.textcolorPrimary, null));
                    title.setBackgroundColor(
                            context.getResources().getColor(R.color.colorPrimary, null));
                    textRating.setTextColor(
                            context.getResources().getColor(R.color.textcolorSec, null));
                    rating.setBackgroundColor(
                            context.getResources().getColor(R.color.colorBackground, null));
                }
            }
        };
    }

    /**
     * Defines a callback.
     * @param paletteAsyncListener palette
     * @param backgroundPoster imageView for poster
     * @param moviePoster imageView for poster
     * @return callback
     */
    public static Callback defineCallback(final PaletteAsyncListener paletteAsyncListener,
                                          final ImageView backgroundPoster,
                                          final ImageView moviePoster) {
        return new Callback() {
            @Override
            public void onSuccess() {
                if (backgroundPoster != null) {
                    Bitmap bitmapBg = ((BitmapDrawable) backgroundPoster.getDrawable()).getBitmap();
                    Palette.from(bitmapBg).generate(paletteAsyncListener);
                } else if (moviePoster != null) {
                    Bitmap bitmapBg = ((BitmapDrawable) moviePoster.getDrawable()).getBitmap();
                    Palette.from(bitmapBg).generate(paletteAsyncListener);
                }
            }
            @Override
            public void onError() {
                if (LOG) {
                    Log.v(TAG, "Callback error");
                }
                Bitmap bitmapBg = ((BitmapDrawable) backgroundPoster.getDrawable()).getBitmap();
                Palette.from(bitmapBg).generate(paletteAsyncListener);
            }
        };
    }


    /**
     * Loads an image into a target view.
     * @param context context
     * @param image image
     * @param centerCrop boolean
     * @param errorResourceId image of error
     * @param target imageView
     * @param callback callback
     */
    public static void loadImage(final Context context, final String image,
                                 final boolean centerCrop, final int errorResourceId,
                                 final ImageView target, final Callback callback) {
        Picasso pic = Picasso.with(context);
        RequestCreator request;
        int errorRId = errorResourceId;

        if (image != null )
            request = pic.load(image).fit();
        else {
            request = pic.load(errorResourceId).fit();
            errorRId = 0;
        }

        if (centerCrop)
            request = request.centerCrop();
        if (errorRId != 0)
            request = request.error(errorResourceId);

        if (callback != null)
            request.into(target, callback);
        else
            request.into(target);
    }

    /**
     * Loads the image.
     * @param context context
     * @param imageView imageView
     */
    public static void loadNoBackgroundPoster(final Context context, final ImageView imageView) {
        Glide.with(context).load(R.drawable.no_background_poster).centerCrop().into(imageView);
    }

    /**
     * Change the height of a gridview according to the elements contained.
     * @param gridview gridview
     * @param items number of items
     * @param gridViewResized boolean array
     */
    public static void changeGridViewHeight(final GridView gridview, final int items,
                                     final boolean gridViewResized[]) {
        /**
         *  THIS IS A HACK!
         *
         *  Problem: GridView inside a scrollView only shows one row.
         *  Solution: http://stackoverflow.com/questions/8481844/gridview-height-gets-cut
         *            Calculate the height for one row and then calculate many rows you have
         *            and resize the GridView height.
         */

        final int columns = gridview.getNumColumns();
        gridview.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!gridViewResized[0]) {
                    gridViewResized[0] = true;
                    ViewGroup.LayoutParams params = gridview.getLayoutParams();
                    int oneRowHeight = gridview.getHeight();
                    int rows = items / columns;
                    if (items % columns != 0) {
                        rows++;
                    }
                    params.height = oneRowHeight * rows;
                    gridview.setLayoutParams(params);
                }
            }
        });
    }

    /**
     * Changes the CollapsingToolbarLayout to hide the title when the image is visible.
     * @param collapsingToolbar collapsing toolbar
     * @param appBarLayout bar layout
     * @param title title
     */
    public static void changeCollapsingToolbarLayoutBehaviour(
            final CollapsingToolbarLayout collapsingToolbar, final AppBarLayout appBarLayout,
            final String title[]) {

        collapsingToolbar.setTitle(" ");
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(title[0]);
                    isShow = true;
                } else if(isShow) {
                    // there should a space between double quote otherwise it wont work
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Launches Settings Activity.
     * @param context context
     */
    public static void launchSettingsActivity(final Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                SettingsActivity.GeneralPreferenceFragment.class.getName() );
        intent.putExtra( PreferenceActivity.EXTRA_NO_HEADERS, true );
        intent.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT_TITLE, R.string.action_settings);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Launches Search Activity.
     * @param context context
     */
    public static void launchSearchActivity(final Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Launches Movie Details Activity.
     * @param context context
     * @param movieId movie id
     */
    public static void launchMovieActivity(final Context context, final int movieId) {
        // Save movieId to Preferences
        saveIntToPreferences(context, MOVIE_ID, movieId);

        // Launch activity
        Intent intent = new Intent(context, MovieActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Launches PersonProfile Activity.
     * @param context context
     * @param personId person id
     */
    public static void launchPersonActivity(final Context context, final int personId,
                                            final String backgroundPoster) {
        // Set PersonID to preferences
        saveIntToPreferences(context, PERSON_ID, personId);
        if (backgroundPoster != null) {
            saveStringToPreferences(context, KNOWN_FOR_BACKGROUND_POSTER, backgroundPoster);
        }
        // Launch activity
        Intent intent = new Intent(context, PersonActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Launches TVShow Activity.
     * @param context context
     * @param tvShowId tvShow id
     */
    public static void launchTVShowActivity(final Context context, final int tvShowId) {
        // Save tvShowId to Preferences
        saveIntToPreferences(context, TV_SHOW_ID, tvShowId);

        // Launch activity
        Intent intent = new Intent(context, TVShowActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Helper class to handle deprecated method.
     * Source: http://stackoverflow.com/questions/37904739/html-fromhtml-deprecated-in-android-n
     * @param html html string
     * @return Spanned
     */
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    /**
     * Sets the image id into the view.
     * @param context context
     * @param view imageView
     * @param id id
     */
    @SuppressWarnings("deprecation")
    public static void setImage(final Context context, final ImageView view, final int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setImageDrawable(context.getResources().getDrawable(id, context.getTheme()));
        } else {
            view.setImageDrawable(context.getResources().getDrawable(id));
        }
    }

    /**
     * Show the AdMob banner.
     * https://firebase.google.com/docs/admob/android/quick-start
     * @param activity activity
     */
    public static void showAdMobBanner(final AppCompatActivity activity) {
        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) activity.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    /**
     * Logs a Firebase Analytics select content event.
     * https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#SELECT_CONTENT
     * @param analytics FirebaseAnalytics
     * @param id id
     * @param name name
     * @param type type
     */
    public static void firebaseAnalyticsLogEventSelectContent(final FirebaseAnalytics analytics,
                                                              final String id, final String name,
                                                              final String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    /**
     * Logs a Firebase Analytics search event.
     * https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#SEARCH
     * @param analytics FirebaseAnalytics
     * @param search string to search
     */
    public static void firebaseAnalyticsLogEventSearch(final FirebaseAnalytics analytics,
                                                       final String search) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, search);
        analytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
    }

    /**
     * Logs a Firebase Analytics view search results event.
     * https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#VIEW_SEARCH_RESULTS
     * @param analytics FirebaseAnalytics
     * @param search string to search
     */
    public static void firebaseAnalyticsLogEventViewSearchResults(final FirebaseAnalytics analytics,
                                                                  final String search) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, search);
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, bundle);
    }

    /**
     * Logs a Firebase Analytics view item event.
     * https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#VIEW_ITEM
     * @param analytics FirebaseAnalytics
     * @param id id
     * @param name name
     * @param category category
     */
    public static void firebaseAnalyticsLogEventViewItem(final FirebaseAnalytics analytics,
                                                         final String id, final String name,
                                                         final String category) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }
}

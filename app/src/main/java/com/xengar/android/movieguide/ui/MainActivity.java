/*
 * Copyright (C) 2017 Angel Garcia
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
package com.xengar.android.movieguide.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.sync.OnItemClickListener;
import com.xengar.android.movieguide.utils.ActivityUtils;

import static com.xengar.android.movieguide.utils.Constants.FAVORITE_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.FAVORITE_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.ITEM_CATEGORY;
import static com.xengar.android.movieguide.utils.Constants.LAST_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.MAIN_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.NOW_PLAYING_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.ON_THE_AIR_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.POPULAR_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.POPULAR_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.SHARED_PREF_NAME;
import static com.xengar.android.movieguide.utils.Constants.TOP_RATED_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.TOP_RATED_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.UPCOMING_MOVIES;

/**
 * Main Activity class
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        boolean showToolbar = ActivityUtils.getPreferenceShowToolbar(getApplicationContext());
        if (!showToolbar) {
            toolbar.setVisibility(View.GONE);
        }

        // Save name of activity, in case of calling SettingsActivity
        ActivityUtils.saveStringToPreferences(getApplicationContext(), LAST_ACTIVITY,
                MAIN_ACTIVITY);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set the initial fragment
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        String itemType = prefs.getString(ITEM_CATEGORY, UPCOMING_MOVIES);
        launchFragment(itemType);

        // change title
        switch (itemType){
            case UPCOMING_MOVIES:
                getSupportActionBar().setTitle(R.string.menu_option_upcomming_movies);
                navigationView.setCheckedItem(R.id.nav_upcomming_movies);
                break;
            case TOP_RATED_MOVIES:
                getSupportActionBar().setTitle(R.string.menu_option_top_rated_movies);
                navigationView.setCheckedItem(R.id.nav_top_rated_movies);
                break;
            case POPULAR_MOVIES:
                getSupportActionBar().setTitle(R.string.menu_option_popular_movies);
                navigationView.setCheckedItem(R.id.nav_popular_movies);
                break;
            case NOW_PLAYING_MOVIES:
                getSupportActionBar().setTitle(R.string.menu_option_now_playing_movies);
                navigationView.setCheckedItem(R.id.nav_now_playing_movies);
                break;
            case FAVORITE_MOVIES:
                getSupportActionBar().setTitle(R.string.menu_option_favorite_movies);
                navigationView.setCheckedItem(R.id.nav_favorite_movies);
                break;
            case POPULAR_TV_SHOWS:
                getSupportActionBar().setTitle(R.string.menu_option_popular_tv_shows);
                navigationView.setCheckedItem(R.id.nav_popular_tv_shows);
                break;
            case TOP_RATED_TV_SHOWS:
                getSupportActionBar().setTitle(R.string.menu_option_top_rated_tv_shows);
                navigationView.setCheckedItem(R.id.nav_top_rated_tv_shows);
                break;
            case ON_THE_AIR_TV_SHOWS:
                getSupportActionBar().setTitle(R.string.menu_option_on_the_air_tv_shows);
                navigationView.setCheckedItem(R.id.nav_on_the_air_tv_shows);
                break;
            case FAVORITE_TV_SHOWS:
                getSupportActionBar().setTitle(R.string.menu_option_favorite_tv_shows);
                navigationView.setCheckedItem(R.id.nav_favorite_tv_shows);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ActivityUtils.launchSettingsActivity(getApplicationContext());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_top_rated_movies) {
            launchFragment(TOP_RATED_MOVIES);
            getSupportActionBar().setTitle(R.string.menu_option_top_rated_movies);

        } else if (id == R.id.nav_upcomming_movies) {
            launchFragment(UPCOMING_MOVIES);
            getSupportActionBar().setTitle(R.string.menu_option_upcomming_movies);

        } else if (id == R.id.nav_now_playing_movies) {
            launchFragment(NOW_PLAYING_MOVIES);
            getSupportActionBar().setTitle(R.string.menu_option_now_playing_movies);

        } else if (id == R.id.nav_popular_movies) {
            launchFragment(POPULAR_MOVIES);
            getSupportActionBar().setTitle(R.string.menu_option_popular_movies);

        } else if (id == R.id.nav_favorite_movies) {
            launchFragment(FAVORITE_MOVIES);
            getSupportActionBar().setTitle(R.string.menu_option_favorite_movies);

        } else if (id == R.id.nav_popular_tv_shows) {
            launchFragment(POPULAR_TV_SHOWS);
            getSupportActionBar().setTitle(R.string.menu_option_popular_tv_shows);

        } else if (id == R.id.nav_top_rated_tv_shows) {
            launchFragment(TOP_RATED_TV_SHOWS);
            getSupportActionBar().setTitle(R.string.menu_option_top_rated_tv_shows);

        } else if (id == R.id.nav_on_the_air_tv_shows) {
            launchFragment(ON_THE_AIR_TV_SHOWS);
            getSupportActionBar().setTitle(R.string.menu_option_on_the_air_tv_shows);

        } else if (id == R.id.nav_favorite_tv_shows) {
            launchFragment(FAVORITE_TV_SHOWS);
            getSupportActionBar().setTitle(R.string.menu_option_favorite_tv_shows);

        } else if (id == R.id.nav_share) {
            getSupportActionBar().setTitle(R.string.app_name);

        } else if (id == R.id.nav_send) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Launches the selected fragment.
     *
     * @param category The type of search
     */
    private void launchFragment(String category) {
        // Set sorting preference
        ActivityUtils.saveStringToPreferences(this, ITEM_CATEGORY, category);

        // Handle selecting item action
        UniversalFragment fragment = new UniversalFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onItemSelectionClick(String itemType, int itemId) {
        Log.v(TAG, "onItemSelectionClick itemId = " + itemId + " itemType = " + itemType);
        if (itemType.equals(POPULAR_TV_SHOWS) || itemType.equals(TOP_RATED_TV_SHOWS)
                || itemType.equals(ON_THE_AIR_TV_SHOWS) || itemType.equals(FAVORITE_TV_SHOWS)) {
            ActivityUtils.launchTVShowActivity(getApplicationContext(), itemId);
        } else {
            ActivityUtils.launchMoviActivity(getApplicationContext(), itemId);
        }
    }
}

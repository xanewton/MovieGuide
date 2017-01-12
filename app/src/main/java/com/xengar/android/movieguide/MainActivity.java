/*
 * Copyright (C) 2016 Angel Garcia
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
package com.xengar.android.movieguide;

import android.os.Bundle;
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

import com.xengar.android.movieguide.sync.OnItemClickListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemClickListener {

    private static final String SHARED_PREF_NAME = "com.xengar.android.movieguide";
    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar = null;
    private NavigationView navigationView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set the initial fragment
        Bundle bundle = new Bundle();
        bundle.putString("pref_sorting", "top_rated");
        MovieGuideUniversalFragment fragment = new MovieGuideUniversalFragment();
        fragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(R.string.menu_option_top_rated_movies);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_top_rated_movies) {
            // Set sorting preference
            Bundle bundle = new Bundle();
            bundle.putString("pref_sorting", "top_rated");

            // Handle selecting top rated movies action
            MovieGuideUniversalFragment fragment = new MovieGuideUniversalFragment();
            fragment.setArguments(bundle);
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(R.string.menu_option_top_rated_movies);

        } else if (id == R.id.nav_upcomming_movies) {
            GalleryFragment fragment = new GalleryFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(R.string.menu_option_upcomming_movies);

        } else if (id == R.id.nav_now_playing_movies) {
            getSupportActionBar().setTitle(R.string.menu_option_now_playing_movies);

        } else if (id == R.id.nav_popular_movies) {
            getSupportActionBar().setTitle(R.string.menu_option_popular_movies);

        } else if (id == R.id.nav_popular_tv_shows) {
            getSupportActionBar().setTitle(R.string.menu_option_popular_tv_shows);

        } else if (id == R.id.nav_top_rated_tv_shows) {
            getSupportActionBar().setTitle(R.string.menu_option_top_rated_tv_shows);

        } else if (id == R.id.nav_on_the_air_tv_shows) {
            getSupportActionBar().setTitle(R.string.menu_option_on_the_air_tv_shows);

        } else if (id == R.id.nav_airing_today_tv_shows) {
            getSupportActionBar().setTitle(R.string.menu_option_airing_today_tv_shows);

        } else if (id == R.id.nav_share) {
            getSupportActionBar().setTitle(R.string.app_name);

        } else if (id == R.id.nav_send) {
            getSupportActionBar().setTitle(R.string.app_name);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMovieClick(int movieId) {
        Log.v(TAG, "onMovieClick movieId = " + movieId);
        // TODO: Add details for item
    }
}

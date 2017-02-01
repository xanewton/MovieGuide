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
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.sync.OnItemClickListener;
import com.xengar.android.movieguide.utils.ActivityUtils;

import java.util.ArrayList;

import static com.xengar.android.movieguide.utils.Constants.FAVORITE_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.FAVORITE_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.ITEM_CATEGORY;
import static com.xengar.android.movieguide.utils.Constants.LAST_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.MAIN_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.MOVIES;
import static com.xengar.android.movieguide.utils.Constants.NOW_PLAYING_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.ON_THE_AIR_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.POPULAR_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.POPULAR_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.SHARED_PREF_NAME;
import static com.xengar.android.movieguide.utils.Constants.TOP_RATED_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.TOP_RATED_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.UPCOMING_MOVIES;

/**
 * Main Activity class
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FrameLayout fragmentLayout;
    private TabLayout tabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        String page = prefs.getString(ITEM_CATEGORY, MOVIES);

        fragmentLayout = (FrameLayout) findViewById(R.id.fragment_container);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), page);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        showPage(page);
        launchFragment(page);

        // set selected
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        switch (page){
            case MOVIES:
                navigationView.setCheckedItem(R.id.nav_movies);
                break;
            case TV_SHOWS:
                navigationView.setCheckedItem(R.id.nav_tv_shows);
                break;
            case FAVORITE_MOVIES:
                getSupportActionBar().setTitle(R.string.menu_option_favorite_movies);
                navigationView.setCheckedItem(R.id.nav_favorite_movies);
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

        if (id == R.id.nav_movies) {
            if (!mSectionsPagerAdapter.getType().equals(MOVIES)) {
                mSectionsPagerAdapter
                        = new SectionsPagerAdapter(getSupportFragmentManager(), MOVIES);
                mViewPager.setAdapter(mSectionsPagerAdapter);
            }
            showPage(MOVIES);

        } else if (id == R.id.nav_tv_shows) {
            if (!mSectionsPagerAdapter.getType().equals(TV_SHOWS)) {
                mSectionsPagerAdapter
                        = new SectionsPagerAdapter(getSupportFragmentManager(), TV_SHOWS);
                mViewPager.setAdapter(mSectionsPagerAdapter);
            }
            showPage(TV_SHOWS);

        } else if (id == R.id.nav_favorite_movies) {
            showPage(FAVORITE_MOVIES);

        } else if (id == R.id.nav_favorite_tv_shows) {
            showPage(FAVORITE_TV_SHOWS);

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
    public void onItemSelectionClick(String itemType, int itemId) {
        Log.v(TAG, "onItemSelectionClick itemId = " + itemId + " itemType = " + itemType);
        if (itemType.equals(POPULAR_TV_SHOWS) || itemType.equals(TOP_RATED_TV_SHOWS)
                || itemType.equals(ON_THE_AIR_TV_SHOWS) || itemType.equals(FAVORITE_TV_SHOWS)) {
            ActivityUtils.launchTVShowActivity(getApplicationContext(), itemId);
        } else {
            ActivityUtils.launchMovieActivity(getApplicationContext(), itemId);
        }
    }

    /***
     * Shows the correct page on screen.
     * @param page
     */
    private void showPage(String page) {
        switch (page){
            case MOVIES:
                showTabs(true);
                getSupportActionBar().setTitle(R.string.menu_option_movies);
                ActivityUtils.saveStringToPreferences(this, ITEM_CATEGORY, MOVIES);
                break;

            case TV_SHOWS:
                showTabs(true);
                getSupportActionBar().setTitle(R.string.menu_option_tv_shows);
                ActivityUtils.saveStringToPreferences(this, ITEM_CATEGORY, TV_SHOWS);
                break;

            case FAVORITE_MOVIES:
                showTabs(false);
                launchFragment(FAVORITE_MOVIES);
                getSupportActionBar().setTitle(R.string.menu_option_favorite_movies);
                break;

            case FAVORITE_TV_SHOWS:
                showTabs(false);
                launchFragment(FAVORITE_TV_SHOWS);
                getSupportActionBar().setTitle(R.string.menu_option_favorite_tv_shows);
                break;
        }
    }

    private void showTabs(boolean show){
        if (show){
            fragmentLayout.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
        } else {
            fragmentLayout.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
        }
    }

    /**
     * Launches the selected fragment.
     * @param category The type of search
     */
    private void launchFragment(String category) {
        // Save page type
        ActivityUtils.saveStringToPreferences(this, ITEM_CATEGORY, category);

        Bundle bundle = new Bundle();
        bundle.putString(ITEM_CATEGORY, category);

        // Handle selecting item action
        UniversalFragment fragment = new UniversalFragment();
        fragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }




    /**
     * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private final String CATEGORY_MOVIES[]
                = {TOP_RATED_MOVIES, UPCOMING_MOVIES, NOW_PLAYING_MOVIES, POPULAR_MOVIES};
        private final String CATEGORY_TV_SHOWS[]
                = {POPULAR_TV_SHOWS, TOP_RATED_TV_SHOWS, ON_THE_AIR_TV_SHOWS};
        private final String TITLE_CATEGORY_MOVIES[]
                = { getString(R.string.title_top_rated),
                    getString(R.string.title_upcomming),
                    getString(R.string.title_now_playing),
                    getString(R.string.title_popular)};
        private final String TITLE_CATEGORY_TV_SHOWS[]
                = { getString(R.string.title_popular),
                getString(R.string.title_top_rated),
                getString(R.string.title_on_the_air)};

        private ArrayList<UniversalFragment> fragments;
        private String tabs[];
        private String titleTabs[];
        private String type;

        /**
         * Constructor.
         * @param fm
         * @param type
         */
        public SectionsPagerAdapter(FragmentManager fm, String type) {
            super(fm);

            this.type = type;
            fragments = new ArrayList<UniversalFragment>();
            tabs = (type.contentEquals(MOVIES))? CATEGORY_MOVIES
                    : (type.contentEquals(TV_SHOWS))? CATEGORY_TV_SHOWS : null;
            titleTabs = (type.contentEquals(MOVIES))? TITLE_CATEGORY_MOVIES
                    : (type.contentEquals(TV_SHOWS))? TITLE_CATEGORY_TV_SHOWS : null;

            Bundle bundle;
            UniversalFragment fragment;
            for (int i = 0; tabs != null && i < tabs.length; i++){
                fragment = new UniversalFragment();
                bundle = new Bundle();
                bundle.putString(ITEM_CATEGORY, tabs[i]);
                fragment.setArguments(bundle);
                fragments.add( fragment );
            }
        }

        public String getType() {
            return type;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            UniversalFragment fragment = fragments.get(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return (tabs != null)? tabs.length : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = (position < titleTabs.length)? titleTabs[position]: null;
            return title;
        }
    }

}

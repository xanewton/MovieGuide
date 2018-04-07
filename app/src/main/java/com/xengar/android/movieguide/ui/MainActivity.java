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

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.sync.OnItemClickListener;
import com.xengar.android.movieguide.utils.ActivityUtils;

import java.util.ArrayList;

import static com.xengar.android.movieguide.utils.Constants.DISCOVER;
import static com.xengar.android.movieguide.utils.Constants.DISCOVER_RESULT;
import static com.xengar.android.movieguide.utils.Constants.FAVORITES;
import static com.xengar.android.movieguide.utils.Constants.HOME;
import static com.xengar.android.movieguide.utils.Constants.ITEM_CATEGORY;
import static com.xengar.android.movieguide.utils.Constants.LAST_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.LOG;
import static com.xengar.android.movieguide.utils.Constants.MAIN_ACTIVITY;
import static com.xengar.android.movieguide.utils.Constants.MOVIES;
import static com.xengar.android.movieguide.utils.Constants.NOW_PLAYING_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.ON_THE_AIR_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.PAGE_DISCOVER;
import static com.xengar.android.movieguide.utils.Constants.PAGE_DISCOVER_RESULTS;
import static com.xengar.android.movieguide.utils.Constants.PAGE_FAVORITES;
import static com.xengar.android.movieguide.utils.Constants.PAGE_HOME;
import static com.xengar.android.movieguide.utils.Constants.PAGE_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.PAGE_PEOPLE;
import static com.xengar.android.movieguide.utils.Constants.PAGE_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.PEOPLE;
import static com.xengar.android.movieguide.utils.Constants.POPULAR_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.POPULAR_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.SHARED_PREF_NAME;
import static com.xengar.android.movieguide.utils.Constants.TOP_RATED_MOVIES;
import static com.xengar.android.movieguide.utils.Constants.TOP_RATED_TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.TV_SHOWS;
import static com.xengar.android.movieguide.utils.Constants.TYPE_PAGE;
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

    private HomeFragment homeFragment;
    private FavoritesFragment favoritesFragment;
    private DiscoverFragment discoverFragment;
    private DiscoverResultFragment discoverResultFragment;
    private PeopleFragment peopleFragment;

    private FirebaseAnalytics mFirebaseAnalytics;
    private InterstitialAd mInterstitialAd;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences(SHARED_PREF_NAME, 0);
        String userName = mPrefs.getString("UserName", null);
        setupInterstitialAd();

        if (userName != null) {

            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Save name of activity, in case of calling SettingsActivity
            ActivityUtils.saveStringToPreferences(getApplicationContext(), LAST_ACTIVITY,
                    MAIN_ACTIVITY);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }
            };

            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View layout = (View) navigationView.getHeaderView(0);
            TextView user = (TextView) layout.findViewById(R.id.user);
            user.setText(getString(R.string.welcome) + " " + userName);

            mPrefs = getSharedPreferences(SHARED_PREF_NAME, 0);
            String page = mPrefs.getString(ITEM_CATEGORY, MOVIES);

            fragmentLayout = (FrameLayout) findViewById(R.id.fragment_container);
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), page);

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

            AdView adView = (AdView) findViewById(R.id.adView);
            AdView adBanner = (AdView) findViewById(R.id.adBanner);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            adBanner.loadAd(adRequest);

            // Obtain the FirebaseAnalytics instance.
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            homeFragment = new HomeFragment();
            favoritesFragment = new FavoritesFragment();
            discoverFragment = new DiscoverFragment();
            discoverResultFragment = new DiscoverResultFragment();
            peopleFragment = new PeopleFragment();
            showPage(page);
            assignCheckedItem(page);

            if (mPrefs.getBoolean("showRate", true))
                ActivityUtils.showRatingDialog(this);
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    private void setupInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
    }

    public void assignCheckedItem(String page) {
        // set selected
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        switch (page) {
            case HOME:
                navigationView.setCheckedItem(R.id.nav_home);
                break;
            case MOVIES:
                navigationView.setCheckedItem(R.id.nav_movies);
                break;
            case TV_SHOWS:
                navigationView.setCheckedItem(R.id.nav_tv_shows);
                break;
            case PEOPLE:
                navigationView.setCheckedItem(R.id.nav_people);
                break;
            case FAVORITES:
                navigationView.setCheckedItem(R.id.nav_favorites);
                break;
            case DISCOVER:
            case DISCOVER_RESULT:
                navigationView.setCheckedItem(R.id.nav_discover);
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
        if (id == R.id.action_search) {
            ActivityUtils.launchSearchActivity(getApplicationContext());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                showPage(HOME);
                break;

            case R.id.nav_movies:
                switchPagerAdapter(MOVIES);
                showPage(MOVIES);
                break;

            case R.id.nav_tv_shows:
                switchPagerAdapter(TV_SHOWS);
                showPage(TV_SHOWS);
                break;

            case R.id.nav_people:
                showPage(PEOPLE);
                break;

            case R.id.nav_favorites:
                showPage(FAVORITES);
                break;

            case R.id.nav_discover:
                showPage(DISCOVER);
                break;

            case R.id.nav_settings:
                ActivityUtils.launchSettingsActivity(getApplicationContext());
                break;

            case R.id.nav_feedback:
                sendFeedback();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Send feedback email.
     */
    private void sendFeedback() {
        Intent sendMessage = new Intent(Intent.ACTION_SEND);
        sendMessage.setType("message/rfc822");
        sendMessage.putExtra(Intent.EXTRA_EMAIL, new String[]{
                getResources().getString(R.string.feedback_email)});
        sendMessage.putExtra(Intent.EXTRA_SUBJECT, "Movie Guide Feedback");
        sendMessage.putExtra(Intent.EXTRA_TEXT,
                getResources().getString(R.string.feedback_message));
        try {
            startActivity(Intent.createChooser(sendMessage, "Send feedback"));
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, "Communication app not found",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void switchPagerAdapter(String page) {
        if (!mSectionsPagerAdapter.getType().equals(page)) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), page);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
    }

    @Override
    public void onItemSelectionClick(String itemType, int itemId) {
        if (LOG) {
            Log.v(TAG, "onItemSelectionClick itemId = " + itemId + " itemType = " + itemType);
        }

        switch (itemType) {
            case TV_SHOWS:
            case POPULAR_TV_SHOWS:
            case TOP_RATED_TV_SHOWS:
            case ON_THE_AIR_TV_SHOWS:
                ActivityUtils.launchTVShowActivity(getApplicationContext(), itemId);
                break;
            case PEOPLE:
                ActivityUtils.launchPersonActivity(getApplicationContext(), itemId, null);
                break;
            default:
                ActivityUtils.launchMovieActivity(getApplicationContext(), itemId);
                break;
        }
    }

    /***
     * Shows the correct page on screen.
     * @param page name of page
     */
    public void showPage(String page) {
        switch (page) {
            case HOME:
                showTabs(false);
                getSupportActionBar().setTitle(R.string.menu_option_home);
                ActivityUtils.saveStringToPreferences(this, ITEM_CATEGORY, HOME);
                ActivityUtils.firebaseAnalyticsLogEventSelectContent(
                        mFirebaseAnalytics, PAGE_HOME, PAGE_HOME, TYPE_PAGE);
                launchFragment(HOME);
                break;

            case MOVIES:
                showTabs(true);
                getSupportActionBar().setTitle(R.string.menu_option_movies);
                ActivityUtils.firebaseAnalyticsLogEventSelectContent(
                        mFirebaseAnalytics, PAGE_MOVIES, PAGE_MOVIES, TYPE_PAGE);
                ActivityUtils.saveStringToPreferences(this, ITEM_CATEGORY, MOVIES);
                break;

            case TV_SHOWS:
                showTabs(true);
                getSupportActionBar().setTitle(R.string.menu_option_tv_shows);
                ActivityUtils.firebaseAnalyticsLogEventSelectContent(
                        mFirebaseAnalytics, PAGE_TV_SHOWS, PAGE_TV_SHOWS, TYPE_PAGE);
                ActivityUtils.saveStringToPreferences(this, ITEM_CATEGORY, TV_SHOWS);
                break;

            case PEOPLE:
                showTabs(false);
                getSupportActionBar().setTitle(R.string.menu_option_people);
                ActivityUtils.saveStringToPreferences(this, ITEM_CATEGORY, PEOPLE);
                ActivityUtils.firebaseAnalyticsLogEventSelectContent(
                        mFirebaseAnalytics, PAGE_PEOPLE, PAGE_PEOPLE, TYPE_PAGE);
                launchFragment(PEOPLE);
                break;

            case FAVORITES:
                showTabs(false);
                getSupportActionBar().setTitle(R.string.menu_option_favorites);
                ActivityUtils.saveStringToPreferences(this, ITEM_CATEGORY, FAVORITES);
                ActivityUtils.firebaseAnalyticsLogEventSelectContent(
                        mFirebaseAnalytics, PAGE_FAVORITES, PAGE_FAVORITES, TYPE_PAGE);
                launchFragment(FAVORITES);
                break;

            case DISCOVER:
                showTabs(false);
                getSupportActionBar().setTitle(R.string.menu_option_discover);
                ActivityUtils.saveStringToPreferences(this, ITEM_CATEGORY, DISCOVER);
                ActivityUtils.firebaseAnalyticsLogEventSelectContent(
                        mFirebaseAnalytics, PAGE_DISCOVER, PAGE_DISCOVER, TYPE_PAGE);
                launchFragment(DISCOVER);
                break;

            case DISCOVER_RESULT:
                showTabs(false);
                getSupportActionBar().setTitle(R.string.menu_option_discover);
                ActivityUtils.saveStringToPreferences(this, ITEM_CATEGORY, DISCOVER_RESULT);
                ActivityUtils.firebaseAnalyticsLogEventSelectContent(
                        mFirebaseAnalytics, PAGE_DISCOVER_RESULTS, PAGE_DISCOVER_RESULTS, TYPE_PAGE);
                launchFragment(DISCOVER_RESULT);
                break;
        }
    }

    private void showTabs(boolean show) {
        if (show) {
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
     *
     * @param category The type of search
     */
    private void launchFragment(String category) {
        android.support.v4.app.FragmentTransaction fragmentTransaction
                = getSupportFragmentManager().beginTransaction();
        switch (category) {
            case HOME:
                fragmentTransaction.replace(R.id.fragment_container, homeFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case FAVORITES:
                fragmentTransaction.replace(R.id.fragment_container, favoritesFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case PEOPLE:
                fragmentTransaction.replace(R.id.fragment_container, peopleFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case DISCOVER:
                fragmentTransaction.replace(R.id.fragment_container, discoverFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case DISCOVER_RESULT:
                fragmentTransaction.replace(R.id.fragment_container, discoverResultFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
        }
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
                = {getString(R.string.title_top_rated), getString(R.string.title_upcomming),
                getString(R.string.title_now_playing), getString(R.string.title_popular)};
        private final String TITLE_CATEGORY_TV_SHOWS[]
                = {getString(R.string.title_popular), getString(R.string.title_top_rated),
                getString(R.string.title_on_the_air)};

        private final ArrayList<UniversalFragment> fragments;
        private final String type;
        private String tabs[] = null;
        private String titleTabs[] = null;

        /**
         * Constructor.
         *
         * @param fm   FragmentManager
         * @param type type of item
         */
        public SectionsPagerAdapter(FragmentManager fm, String type) {
            super(fm);
            this.type = type;
            switch (type) {
                case MOVIES:
                    tabs = CATEGORY_MOVIES;
                    titleTabs = TITLE_CATEGORY_MOVIES;
                    break;
                case TV_SHOWS:
                    tabs = CATEGORY_TV_SHOWS;
                    titleTabs = TITLE_CATEGORY_TV_SHOWS;
                    break;
            }

            fragments = new ArrayList<>();
            for (int i = 0; tabs != null && i < tabs.length; i++) {
                UniversalFragment fragment = new UniversalFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ITEM_CATEGORY, tabs[i]);
                fragment.setArguments(bundle);
                fragments.add(fragment);
            }
        }

        public String getType() {
            return type;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return (tabs != null) ? tabs.length : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (position < titleTabs.length) ? titleTabs[position] : null;
        }
    }

}

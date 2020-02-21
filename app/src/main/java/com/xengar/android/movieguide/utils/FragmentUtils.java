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

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * FragmentUtils
 */
public class FragmentUtils {

    public static String getFormatLocale(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return StringUtils.getFormatLocale(prefs.getString(Constants.PREF_QUERY_LANGUAGE, ""));
    }

    /**
     * Checks for internet connection.
     * @return true if connected or connecting
     */
    public static boolean checkInternetConnection(FragmentActivity fragmentActivity) {
        ConnectivityManager cm = (ConnectivityManager) fragmentActivity.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    /**
     * Changes to visible or gone the circular progress bar.
     * @param progressBar progress Bar
     * @param visibility boolean
     */
    public static void updateProgressBar(CircularProgressBar progressBar, boolean visibility) {
        if (progressBar != null) {
            progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }
}

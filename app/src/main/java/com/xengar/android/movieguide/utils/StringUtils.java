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

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * StringUtils
 */
public class StringUtils {

    public static String getFormatLocale(String locale) {
        if (TextUtils.isEmpty(locale)) {
            locale = Locale.getDefault().toString();
        }
        return locale.replace("_", "-");
    }

    public static String inTheatersLte() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar lte = Calendar.getInstance();
        lte.set(Calendar.DAY_OF_YEAR, lte.get(Calendar.DAY_OF_YEAR) + 2);
        return format.format(lte.getTime());
    }

    public static String inTheatersGte() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar gte = Calendar.getInstance();
        gte.set(Calendar.DAY_OF_YEAR, gte.get(Calendar.DAY_OF_YEAR) - 25);
        return format.format(gte.getTime());
    }

    public static String getDateOnTheAir() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar week = Calendar.getInstance();
        week.set(Calendar.DAY_OF_YEAR, week.get(Calendar.DAY_OF_YEAR) + 7);
        return format.format(week.getTime());
    }

    public static String getDateToday() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar today = Calendar.getInstance();
        return format.format(today.getTime());
    }

    public static String getYear(String dateStr) {
        dateStr = dateStr != null ? dateStr : "1970-01-01";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = format.parse(dateStr);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int year = calendar.get(Calendar.YEAR);
        return String.valueOf(year);
    }
}

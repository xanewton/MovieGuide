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

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.utils.ActivityUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.xengar.android.movieguide.utils.Constants.DISCOVER_DEFAULT_MIN_RATING;
import static com.xengar.android.movieguide.utils.Constants.DISCOVER_DEFAULT_SORT_TYPE;
import static com.xengar.android.movieguide.utils.Constants.DISCOVER_RESULT;
import static com.xengar.android.movieguide.utils.Constants.DISCOVER_GENRES;
import static com.xengar.android.movieguide.utils.Constants.DISCOVER_MIN_RATING;
import static com.xengar.android.movieguide.utils.Constants.DISCOVER_SORT_TYPE;
import static com.xengar.android.movieguide.utils.Constants.DISCOVER_TYPE;
import static com.xengar.android.movieguide.utils.Constants.MOVIES;
import static com.xengar.android.movieguide.utils.Constants.TV_SHOWS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment
        implements  AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener,
                    View.OnClickListener, CheckBox.OnCheckedChangeListener {

    private static final String TAG = DiscoverFragment.class.getSimpleName();
    private RadioButton moviesRadioButton;
    private RadioButton tvRadioButton;
    private TextView genresView;
    private TextView ratingView;

    private String mType = MOVIES;
    private String mSortValue = DISCOVER_DEFAULT_SORT_TYPE;
    private String mGenresValues = null;
    private String mMinRating = DISCOVER_DEFAULT_MIN_RATING;

    private HashSet<String> mGenresList;
    private HashSet<String> mGenresValuesList;
    private String mGenres;
    private boolean[] checkedGenres;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        RadioGroup toggleRadioGroup = (RadioGroup) view.findViewById(R.id.discover_toggle);
        moviesRadioButton = (RadioButton) view.findViewById(R.id.radio_movies);
        moviesRadioButton.setOnCheckedChangeListener(this);
        tvRadioButton = (RadioButton) view.findViewById(R.id.radio_tv);
        TextView discoverButton = (TextView) view.findViewById(R.id.discover_discover);
        discoverButton.setOnClickListener(this);
        LinearLayout genres = (LinearLayout) view.findViewById(R.id.genres_root);
        genres.setOnClickListener(this);
        genresView = (TextView) view.findViewById(R.id.discover_genres);
        Spinner sortSpinner = (Spinner) view.findViewById(R.id.discover_sort);
        ratingView = (TextView) view.findViewById(R.id.discover_rating);
        AppCompatSeekBar ratingSeekBar = (AppCompatSeekBar) view.findViewById(R.id.discover_rating_bar);

        mGenresList = new HashSet<>();
        mGenresValuesList = new HashSet<>();
        checkedGenres = new boolean[] {
                false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false
        };
        ratingSeekBar.setOnSeekBarChangeListener(this);

        final List<String> sort = Arrays.asList(getResources().getStringArray(R.array.sort));
        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, sort);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String[] sortValues;
        if (mType.contentEquals(MOVIES)) {
            sortValues = getResources().getStringArray(R.array.sortValuesMovie);
        } else {
            sortValues = getResources().getStringArray(R.array.sortValuesTV);
        }
        mSortValue = sortValues[adapterView.getSelectedItemPosition()];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mMinRating = String.valueOf(i);
        ratingView.setText(String.valueOf(i));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    // Called when an element is clicked
    @Override
    public void onClick(View view) {
        MainActivity activity = (MainActivity) getActivity();
        switch(view.getId()){
            case R.id.genres_root:
                getGenres();
                break;

            case R.id.discover_discover:
                ActivityUtils.saveStringToPreferences(getContext(), DISCOVER_TYPE, mType);
                ActivityUtils.saveStringToPreferences(getContext(), DISCOVER_GENRES, mGenresValues);
                ActivityUtils.saveStringToPreferences(getContext(), DISCOVER_SORT_TYPE, mSortValue);
                ActivityUtils.saveStringToPreferences(getContext(), DISCOVER_MIN_RATING, mMinRating);
                activity.showPage(DISCOVER_RESULT);
                break;
        }
    }

    // Called when a checkbox changes
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mType = b ? MOVIES : TV_SHOWS;
        int textColorLight = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
        int textColorDark = ContextCompat.getColor(getActivity(), R.color.colorGray);
        moviesRadioButton.setTextColor(b ? textColorLight : textColorDark);
        tvRadioButton.setTextColor(!b ? textColorLight : textColorDark);
    }

    private void getGenres() {
        final List<String> genresValues
                = Arrays.asList(getResources().getStringArray(R.array.genresValues));
        final String[] genres = getResources().getStringArray(R.array.genres);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle(R.string.genres_dialog_title);
        builder.setMultiChoiceItems(genres, checkedGenres, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                checkedGenres[i] = isChecked;
                if (isChecked) {
                    mGenresValuesList.add(genresValues.get(i));
                    mGenresList.add(genres[i]);
                } else {
                    mGenresValuesList.remove(genresValues.get(i));
                    mGenresList.remove(genres[i]);
                }
            }
        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mGenresList.isEmpty()){
                    genresView.setText(R.string.discover_any_genres);
                } else {
                    mGenresValues = TextUtils.join(",", mGenresValuesList);
                    mGenres = TextUtils.join(", ", mGenresList);
                    genresView.setText(mGenres);
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

package com.xengar.android.movieguide.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xengar.android.movieguide.R;
import com.xengar.android.movieguide.data.MovieData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Image adapter
 */
public class ImageAdapter extends BaseAdapter {

    private static final String TAG = ImageAdapter.class.getSimpleName();
    private static final int IMAGE_WIDTH = 185;
    private static final int IMAGE_HEIGHT = 278;
    private final ArrayList<MovieData> finalMoviePosters = new ArrayList<>();
    private final HashSet<Integer> movieIdSet = new HashSet<>();
    private final float density;

    private final Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
        density = mContext.getResources().getDisplayMetrics().density;
    }

    public int getCount() {
        return finalMoviePosters.size();
    }

    public Object getItem(int position) {
        return finalMoviePosters.get(position);
    }

    public long getItemId(int position) {
        return finalMoviePosters.get(position).getMovieId();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ImageView imageView;
        TextView textView;
        if (convertView == null) {
            view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.image_layout, parent, false);

        } else {
            view = convertView;
        }

        MovieData data = finalMoviePosters.get(position);
        imageView = (ImageView) view.findViewById(R.id.movie_poster_view);
        view.setLayoutParams(new GridView.LayoutParams((int) (IMAGE_WIDTH * density),
                (int) (IMAGE_HEIGHT * density)));

        //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (data.getMoviePoster() == null) {
            textView = (TextView) view.findViewById(R.id.movie_title);
            textView.setText(data.getMovieTitle());
        }

        Picasso pic = Picasso.with(mContext);
        pic.load(data.getMoviePoster())
                .into(imageView);
        return view;
    }


    public void add(MovieData res) {
        if (movieIdSet.contains(res.getMovieId())) {
            Log.w(TAG, "Movie duplicate found, movieID = " + res.getMovieId());
            return;
        }

        finalMoviePosters.add(res);
        movieIdSet.add(res.getMovieId());
    }

    public void addAll(List<MovieData> res) {
        finalMoviePosters.addAll(res);
    }

    public void clearData() {
        finalMoviePosters.clear();
        movieIdSet.clear();
        notifyDataSetChanged();
    }

    public List<MovieData> getMovieData() {
        return finalMoviePosters;
    }
}

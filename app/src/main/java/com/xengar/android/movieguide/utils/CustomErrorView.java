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
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xengar.android.movieguide.R;

/**
 * CustomErrorView
 */
public class CustomErrorView extends FrameLayout {

    private final TextView errorTextView;
    private final TextView errorMessageTextView;

    public CustomErrorView(Context context) {
        this(context, null);
    }

    public CustomErrorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = inflate(context, R.layout.custom_error_view, this);
        errorTextView = (TextView) view.findViewById(R.id.error_text);
        errorMessageTextView = (TextView) view.findViewById(R.id.error_message_text);
    }

    public void setError(@NonNull Throwable t) {
        errorTextView.setText(getResources().getString(R.string.network_error));
        errorMessageTextView.setText(t.getMessage());
    }
}

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
package com.xengar.android.movieguide.data;

/**
 * Container for data of a clickable, scrollable image.
 * It can be a movie imagePath, TV Show imagePath, cast image or movie image.
 */
public class ImageItem {

    private final int id;
    private final String imagePath;
    private final String title;
    private final String subtitle;

    // Constructor
    public ImageItem(String imagePath, int id, String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        this.id = id;
        this.imagePath = imagePath;
    }

    // Getters
    public String getImageTitle() {
        return title;
    }

    public String getImageSubtitle() {
        return subtitle;
    }

    public int getImageId() {
        return id;
    }

    public String getImagePath() {
        return imagePath;
    }
}

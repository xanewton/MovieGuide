# MovieGuide

<a href='https://play.google.com/store/apps/details?id=com.xengar.android.movieguide'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height=90px/></a>

![alt tag](https://github.com/an-garcia/MovieGuide/blob/master/readmeImages/Screenshot_1484265087.png)
![alt tag](https://github.com/an-garcia/MovieGuide/blob/master/readmeImages/Screenshot_1484265101.png)
![alt tag](https://github.com/an-garcia/MovieGuide/blob/master/readmeImages/Screenshot_1484437598.png)

MovieGuide is an android application designed to show reviews for Movies and TV Shows.
Discover a collection of movies, TV Shows, production details and audience reviews.
This product uses the TMDb API but is not endorsed or certified by TMDb.


## Requirements
- Android Design Support Library
- Android Support Library v7
- The Movie Database API  https://www.themoviedb.org/documentation/api https://developers.themoviedb.org/3/getting-started
- YouTubeAndroidPlayerApi  https://developers.google.com/youtube/v3/
- Picasso library https://github.com/square/picasso
- Glide library https://github.com/bumptech/glide
- Apache Commons Lang library https://commons.apache.org/proper/commons-lang/
- Gson library https://guides.codepath.com/android/Leveraging-the-Gson-Library
- Retrofit2 library https://github.com/square/retrofit
- okhttp3 librabry https://github.com/square/okhttp
- Firebase https://firebase.google.com/docs/android/setup

## Getting Started
The app uses The Movie Database API (posters and information) and YouTubeAndroidPlayerApi (play videos).
Download YouTubeAndroidPlayerApi from https://developers.google.com/youtube/android/player/downloads/ and place it under MovieGuide\app\libs
Get your API keys in order to run the app.
Create a new resources file (using this path:/app/src/main/res/values/api_keys.xml) and put "THE_MOVIE_DB_API_TOKEN" and "YOUTUBE_DATA_API_V3" keys values in it as strings.
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="THE_MOVIE_DB_API_TOKEN">1exxxxd326xxxx82d1xxxxf600xxxxxx</string>
    <string name="YOUTUBE_DATA_API_V3">AIzaXXXXXXXXXXHhK8XXXXXXXXXXr1cnXXXXXXX</string>
</resources>
```

## License

Copyright 2017 Angel Garcia

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.



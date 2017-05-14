# SkyNet Reader

<a href=""><img src="https://github.com/spkdroid/SkyNet-Reader/blob/master/app/src/main/res/mipmap-mdpi/ic_launcher.png" align="left" height="125" width="125" ></a>


SkyNet Reader is a simple news feed application for your android smartphone. The application uses a simple restful API call to the skynet server to fetch the details. The application is tailored in such a fashion that the information is presented neat and clean. The application will categorize the news feed into different news types and provides you a separate set list.

## Features

<a href=""><img src="https://github.com/spkdroid/SkyNet-Reader/blob/master/app/src/main/res/drawable/second.png" align="left" height="125" width="125" ></a>

1) Light Weight News Reader <br>
2) Fast and Robust <br>
3) Consumes fewer volume data <br>
4) Feeds are auto-categorized into different types <br>
5) Current Version 1.0 Supports World News, Entertainment, Business, Technology, and Politics.

## Restful API

<a href=""><img src="https://github.com/spkdroid/SkyNet-Reader/blob/master/app/src/main/res/drawable/first.png" align="left" height="125" width="125" ></a>

The Skynet uses a special Restful API to fetch its news feed from the server. The backend code is written with the help of PHP which will scan the news website and exposed back the news feeds to the application as a simple JSON response.


| New Feed        | API Call
| ------------- |:-------------:
| World News      | http://www.spkdroid.com/News/canada.php?type=1
| Entertainment    | http://www.spkdroid.com/News/canada.php?type=2
| Business | http://www.spkdroid.com/News/canada.php?type=3
| Technology      | http://www.spkdroid.com/News/canada.php?type=4
| Politics    | http://www.spkdroid.com/News/canada.php?type=5


<a href="url"><img src="https://github.com/spkdroid/SkyNet-Reader/blob/master/screenshot/screen.png" align="left" height="500" width="275" ></a>


## Getting Started

Just clone and import into the Android Studio. Perform the gradle build and perform a build operation to generate the apk file. <br>

The apk file can be downloaded from the following link <br>

<strong>
<a href="http://www.spkdroid.com/News/app.apk">Download Now!!</a>
</strong>

### Prerequisities

```
Android Studio
Android 5.0 and above
```

## Deployment

Please enable installation from "unknow source" in your settings to install the apk file.

Please change the minimum SDK version in the gradle file to support the lower android versions.

## Built With

Android Studio
Gradle
Android SDK 5.0 and above

## Contributing

Just submit a pull request.

## Authors

* **Ramkumar Velmurugan** - [Ramkumar Velmurugan](http://www.spkdroid/CV/)


## License

This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/spkdroid/SkyNet-Reader/blob/master/license.md) file for details

## Acknowledgments

* Apache HTTPComponents
* CustomWebView
* Volley

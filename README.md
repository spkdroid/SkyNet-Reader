# 📖 SkyNet Reader

<p>
  <a href=""><img src="https://github.com/spkdroid/SkyNet-Reader/blob/master/app/src/main/res/mipmap-mdpi/ic_launcher.png" align="left" height="125" width="125" alt="SkyNet Reader Icon"></a>
</p>

SkyNet Reader is a lightweight and fast **news feed application** designed for Android smartphones. It leverages a RESTful API to fetch categorized news feeds, ensuring a neat and clean presentation for users. The app currently supports news categories such as **World News**, **Entertainment**, **Business**, **Technology**, and **Politics**.

---

## ✨ Features

<p>
  <a href=""><img src="https://github.com/spkdroid/SkyNet-Reader/blob/master/app/src/main/res/drawable/second.png" align="left" height="125" width="125" alt="SkyNet Reader Features"></a>
</p>

- **Lightweight News Reader**: Minimalistic and optimized for performance.  
- **Fast & Robust**: Ensures smooth and quick access to news.  
- **Low Data Consumption**: Designed to be data-efficient.  
- **Auto-Categorized Feeds**: News is grouped into specific categories for easy navigation.  
- **Supports Multiple News Categories**: Includes World News, Entertainment, Business, Technology, and Politics.

---

## 🌐 RESTful API

<p>
  <a href=""><img src="https://github.com/spkdroid/SkyNet-Reader/blob/master/app/src/main/res/drawable/first.png" align="left" height="125" width="125" alt="SkyNet API"></a>
</p>

SkyNet Reader utilizes a custom-built RESTful API to fetch news feeds. The backend, written in **PHP**, scans various news websites and exposes the data to the application in **JSON format**.

| **News Feed**     | **API Endpoint**                                      |
|--------------------|-------------------------------------------------------|
| World News         | [API Call](http://www.spkdroid.com/News/canada.php?type=1)  |
| Entertainment      | [API Call](http://www.spkdroid.com/News/canada.php?type=2)  |
| Business           | [API Call](http://www.spkdroid.com/News/canada.php?type=3)  |
| Technology         | [API Call](http://www.spkdroid.com/News/canada.php?type=4)  |
| Politics           | [API Call](http://www.spkdroid.com/News/canada.php?type=5)  |

---

## 📸 Screenshots

<p align="center">
  <a href="url"><img src="https://github.com/spkdroid/SkyNet-Reader/blob/master/screenshot/screen.png" height="500" width="275" alt="SkyNet Reader Screenshot"></a>
</p>

---

## 🚀 Getting Started

### Clone and Build

1. Clone the repository.  
2. Import the project into **Android Studio**.  
3. Perform a Gradle sync and build the project to generate the APK file.  

Alternatively, download the APK directly from:  
**[Download Now!](http://www.spkdroid.com/News/app.apk)**

---

## 🛠 Prerequisites

- **Android Studio**: Development environment.  
- **Android 5.0 (Lollipop)** or higher: Minimum supported SDK version.

### Deployment Notes

- Enable installation from **Unknown Sources** in your device settings to install the APK manually.  
- To support lower Android versions, modify the **minimum SDK version** in the Gradle file.

---

## 🛠 Built With

- **Android Studio**: Integrated Development Environment (IDE).  
- **Gradle**: Build system for Android.  
- **Android SDK**: Targeting API level 21 and above.

---

## 🤝 Contributing

We welcome contributions! Feel free to submit a pull request to improve the application.

---

## 👨‍💻 Author

- **Ramkumar Velmurugan**  
  [Portfolio](http://www.spkdroid.com/CV/)

---

## 📝 License

This project is licensed under the **MIT License**. See the [LICENSE.md](https://github.com/spkdroid/SkyNet-Reader/blob/master/license.md) file for details.

---

## 📚 Acknowledgments

- **Apache HTTPComponents**: For HTTP request handling.  
- **CustomWebView**: For enhanced web content rendering.  
- **Volley**: For seamless and efficient API communication.

--- 

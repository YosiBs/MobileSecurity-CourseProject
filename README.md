<a id="readme-top"> </a>

<h1 align= center>WifiMapper [Background Service App]</h1>

<p align= center>
  <img src="https://github.com/user-attachments/assets/bd404a27-4bcc-4d00-91ae-5366ba6ba303" alt="icon" width="300" style="border-radius: 30px;">
</p>

<p align="center">
    <a href="https://github.com/yosibs/MobileSecurity-CourseProject/graphs/contributors"><img alt="GitHub Contributors" src="https://img.shields.io/github/contributors/yosibs/MobileSecurity-CourseProject" /></a>
    <a href="https://github.com/yosibs/github-readme-stats/issues"><img alt="Issues" src="https://img.shields.io/github/issues/yosibs/MobileSecurity-CourseProject?color=0088ff" /></a>
    <a href="https://github.com/yosibs/github-readme-stats/pulls"><img alt="GitHub pull requests" src="https://img.shields.io/github/issues-pr/yosibs/MobileSecurity-CourseProject?color=0088ff" /></a>
    <br />
    <br />
  </p>
    
  <h2><img src="https://github.com/YosiBs/Gotcha-App/assets/105666011/558f0957-6604-47a4-a202-66a02a2835e7" alt=pic5 width="40" height="40"> Overview</h2>
  <p>
  WifiMapper is an Android application designed as part of a mobile security course project. It features a background service that periodically scans nearby Wi-Fi networks and sends the data to a backend server. The app visualizes this data using Google Maps and a RecyclerView list, providing an interactive way to explore Wi-Fi networks in your vicinity.  
  </p>

<h2><img src="https://github.com/YosiBs/Pokemon-Escape-Mobile-Game/assets/105666011/008a508e-5484-46ba-be36-ac359d603f01" alt=pic5 width="40" height="40"> Features</h2>

- **Background Wi-Fi Scanning**: A service runs in the background, scanning for nearby Wi-Fi networks every 10 seconds.
- **Data Sync**: Scanned Wi-Fi data is sent to a Node.js backend and stored in a PostgreSQL database.
- **Map Visualization**: The `MapActivity` displays Wi-Fi network locations on Google Maps, with markers for each network.
- **Network List**: A RecyclerView-based activity lists all scanned Wi-Fi networks. Clicking an icon navigates to the corresponding marker on the map and highlights related scans.
- **Real-Time Updates**: Integrates with the backend to fetch and display the latest Wi-Fi data.

<h2><img src="https://github.com/YosiBs/Gotcha-App/assets/105666011/f09bd9dd-b5e2-4076-a617-fd71fe7deceb" alt=pic5 width="40" height="40"> Technologies Used</h2>

### Android App
- **Language**: Java/Kotlin (assumed; let me know if you used one specifically)
- **IDE**: Android Studio
- **Key Libraries**:
  - `Retrofit` + `Gson Converter`: For API communication and JSON parsing.
  - `OkHttp Logging Interceptor`: For debugging network requests.
  - `Google Play Services (Location & Maps)`: For location services and map integration.
  - `Glide`: For efficient image loading (if applicable; clarify if used).
  - `Lottie`: For animations (if applicable; clarify if used).
  - `Android RecyclerView`: For displaying the Wi-Fi list.
  - `Callbacks`: For handling asynchronous operations and communication between components.
  - `Shared Preferences`: For storing lightweight app data, such as user settings or scan preferences.


  ### Backend
  - **Language**: JavaScript
  - **Framework**: Node.js
  - **Database**: PostgreSQL
  
  ## Prerequisites
  - Android Studio (with an emulator or physical device running API 21+)
  - Node.js and npm installed
  - PostgreSQL installed and running
  - Google Maps API key (for map functionality)

<h2><img src="https://github.com/user-attachments/assets/4980fb42-e8b7-447c-86e9-007d8fb72644" alt=pic5 width="40" height="40"> Installation</h2>

### Android App
1. Open the app folder in Android Studio.
2. Add your Google Maps API key.
3. In the app’s code (e.g., Retrofit base URL), set the endpoint to your Node.js server (e.g., `http://localhost:3000` or your hosted URL).
4. Sync the project with Gradle.
5. Run the app on an emulator or device.

### Gradle Dependencies
Add these to your `app/build.gradle`:
```gradle
dependencies {
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:<version>'
    implementation 'com.squareup.retrofit2:converter-gson:<version>'
    implementation 'com.squareup.okhttp3:logging-interceptor:<version>'

    // Google Maps & Location
    implementation 'com.google.android.gms:play-services-location:<version>'
    implementation 'com.google.android.gms:play-services-maps:<version>'

    // Glide (optional, if used)
    implementation 'com.github.bumptech.glide:glide:<version>'
    annotationProcessor 'com.github.bumptech.glide:compiler:<version>'

    // Lottie (optional, if used)
    implementation 'com.airbnb.android:lottie:<version>'
}
```

<h2><img src="https://github.com/YosiBs/Gotcha-App/assets/105666011/0c7e3507-e910-4ac4-b5e3-8c5d484fa682" alt=pic5 width="40" height="40"> Usage</h2>

1. Launch the app on your Android device.
2. Grant necessary permissions (e.g., location and Wi-Fi access).
3. The background service will start scanning Wi-Fi networks every 10 seconds.
4. Use the **MapActivity** to view Wi-Fi locations on Google Maps.
5. Open the **ListActivity** to see all scanned networks; tap an icon to jump to its map marker and view related scans.


<p><strong>Screen Shots:</strong></p>


<img src="https://github.com/user-attachments/assets/42b3480b-bbd5-4e5d-8084-1418a542246f" alt="Screenshot 1" width="250" style="border-radius: 10px;">
<img src="https://github.com/user-attachments/assets/de846b71-877e-4b9e-a52f-001489d82107" alt="Screenshot 2" width="250" style="border-radius: 10px;">
<p></p>
<img src="https://github.com/user-attachments/assets/07d05026-a9ca-4df4-9a15-a27840293555" width="250" style="border-radius: 10px;">
<img src="https://github.com/user-attachments/assets/e6fb26f3-6533-4ed9-9948-d24c1365de91" width="250" style="border-radius: 10px;">


### Contributing
This is a course project, but feel free to fork it and submit pull requests with enhancements!


<h2><img src="https://github.com/YosiBs/Gotcha-App/assets/105666011/9f5d6637-b1e1-4037-8f60-64388e5ab109" alt=pic5 width="40" height="40"> Authors</h2>
<ul>
    <li><a href="https://github.com/YosiBs">Yosi Ben Shushan</a></li>
</ul>


<p align="right"><a href="#readme-top"><img src="https://github.com/user-attachments/assets/5390781c-77bb-4f30-8403-1f46f14f9058" alt=pic5 width="40" height="40"></a></p>




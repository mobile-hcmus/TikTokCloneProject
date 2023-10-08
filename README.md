# TopTop &ndash; A TikTok's Clone Project

[![Contributors][contributors-shield]][contributors-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/mobile-hcmus/TikTokCloneProject">
    <img src="app/src/main/res/drawable/toptoplogo.svg" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">TopTop</h3>
</div>


## Overview

To better understand Android app development, we took on the challenge of creating our own version of TikTok &ndash; one of the most popular entertainment apps for mobile devices. Our main objectives were to replicate all of the app's key features as accurately as we could.

This project is a part of the main assignments in our Android Development Course at HCMUS.

## Prerequisites

Before you start building **TopTop**, please ensure that you meet the following requirements:

-   **Android Studio:** You'll need [Android Studio](https://developer.android.com/studio) installed on your development machine. Make sure you have **Android SDK 28 or newer** installed as well.

-   **Android Device:** To run the app, you'll need an Android device with **Android 9 Pie or a newer version**. We recommend using a device with **4GB or more of RAM** for the best experience.

-   **Firebase Project:** Ensure you have your own Firebase project set up. You'll need to link it with the cloned repository using the `google-services.json` template.

-   **Gradle Version:** We recommend using **Gradle version 7.3.3** for building this project.

### Other Libraries

-   **ExoPlayer:** [ExoPlayer](https://exoplayer.dev/) is used for multimedia playback as each short video is scrolled onto in our app.

-   **Android GIF Drawable:** [Android GIF Drawable](https://github.com/koral--/android-gif-drawable) is used for the loading animation of the video downloading from the database.

## Features

TopTop offers a comprehensive set of features, including many of the core functionalities found in the original TikTok app. Here are some of them:

### Create a User Profile

Users can easily sign up or sign in using their existing Google account or phone number. This step is necessary for uploading content and engaging with videos on the platform.
<a href="https://imgur.com/ddz29hD"><img src="https://i.imgur.com/ddz29hD.png" title="source: imgur.com" /></a>

### Discover Recommended and Friends' Videos

Our home screen allows users to seamlessly swipe through videos uploaded by friends and recommendations based on their interests. Signed-in users can interact with videos by liking, commenting, and responding to comments, just like the original TikTok app.
<a href="https://imgur.com/qL9AyhB"><img src="https://i.imgur.com/qL9AyhB.png" title="source: imgur.com" /></a>

### Record or Upload Videos

We've developed a user-friendly camera system for recording videos and adding captions, which can then be uploaded to the user's profile. Users also have the option to choose videos from their device's library.
<a href="https://imgur.com/n4VVVyY"><img src="https://i.imgur.com/n4VVVyY.png" title="source: imgur.com" /></a>

### Share Profiles and Videos

Users can easily share profiles or individual videos using the app's generated URL. This shareable link can be posted across various platforms for maximum reach.
<a href="https://imgur.com/SkFCQOx"><img src="https://i.imgur.com/SkFCQOx.png" title="source: imgur.com" /></a>

### Additional Features

In addition to the core features mentioned above, TopTop includes:

-   Notifications
-   Search for hashtags and videos
-   Dark and Light themes that automatically adapt to the device's system theme.

## License

Distributed under the MIT License. See `LICENSE` for more information.

## References Acknowledgments

We would like to express our gratitude to the following resources, which have been invaluable in the development of this project:

-   **Permissions on Android:** The [Android Permissions Overview](https://developer.android.com/guide/topics/permissions/overview) guide.

-   **Firebase Authentication:** The [Get Started with Firebase Authentication on Android](https://firebase.google.com/docs/auth/android/start) guide by Firebase.

-   **Firebase Firestore:** [Firestore documentation](https://firebase.google.com/docs/firestore).

-   **Cloud Storage for Firebase:** [Cloud Storage for Firebase documentation](https://firebase.google.com/docs/storage).

-   **android.hardware.camera2:** [android.hardware.camera2](https://developer.android.com/reference/android/hardware/camera2/package-summary) documentation.

-   **Create Dynamic Lists with RecyclerView:** [RecyclerView documentation](https://developer.android.com/develop/ui/views/layout/recyclerview).

[contributors-shield]: https://img.shields.io/github/contributors/mobile-hcmus/TikTokCloneProject.svg?style=for-the-badge
[contributors-url]: https://github.com/mobile-hcmus/TikTokCloneProject/graphs/contributors

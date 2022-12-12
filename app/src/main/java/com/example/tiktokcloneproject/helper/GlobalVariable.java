package com.example.tiktokcloneproject.helper;

import android.app.Application;
import android.net.Uri;

public class GlobalVariable extends Application {
    private Uri avatarUri ;

    public void setAvatarUri(Uri avatarUri) {
        this.avatarUri = avatarUri;
    }

    public Uri getAvatarUri() {
        return avatarUri;
    }
}

package com.mstudio.android.mstory.app;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Mstory extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        com.google.firebase.FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

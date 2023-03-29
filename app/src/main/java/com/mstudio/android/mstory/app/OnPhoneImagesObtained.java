package com.mstudio.android.mstory.app;

import com.mstudio.android.mstory.app.model.PhoneAlbum;

import java.util.Vector;

public interface OnPhoneImagesObtained {

    void onComplete( Vector<PhoneAlbum> albums );
    void onError();

}
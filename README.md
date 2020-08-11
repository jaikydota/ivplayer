# ivplayer
An open source Interaction Video Player for Android.

### Using
Add to your layout xml:
```
<com.ctrlvideo.ivplayer.IVPlayer
    android:id="@+id/ivplayer"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
</com.ctrlvideo.ivplayer.IVPlayer>
```

Binding to your code:
```
IVPlayer ivplayer = findViewById(R.id.ivplayer);
ivplayer.init(pid, new IVListener());
```

Implement this Interface:
```
private class IVListener implements IVPlayerListener {

    void onIVPlayerStateChanged(String state);

    void onIVPlayerClick(String info);   
    
    void onEventStateChanged(String eType, String state);
    
    void onError(String errorType);
}
```

### *Enjoy it!*

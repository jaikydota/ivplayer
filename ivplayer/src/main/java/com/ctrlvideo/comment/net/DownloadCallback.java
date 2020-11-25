package com.ctrlvideo.comment.net;

import java.io.File;

public interface DownloadCallback {

    void onDownloadStart(String url);

    void onDownloadFailed(String url, String error);

    void onDownloadSuccess(String url, File file);

    void onDownloading(String url, int progress);
}

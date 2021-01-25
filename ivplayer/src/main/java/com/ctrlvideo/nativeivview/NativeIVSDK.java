package com.ctrlvideo.nativeivview;

import android.content.Context;

import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;
import com.ctrlvideo.nativeivview.net.HttpClient;
import com.ctrlvideo.nativeivview.net.callback.DownloadCallback;
import com.ctrlvideo.nativeivview.net.callback.GetIVideoInfoCallback;
import com.ctrlvideo.nativeivview.utils.LogUtils;
import com.ctrlvideo.nativeivview.utils.NativeViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NativeIVSDK {

    private String TAG = "NativeIVSDK";


    // 预加载前10秒的资源
    public static long MEDIA_ASSETS_PRELOAD_TIME = 10 * 1000;
    private static NativeIVSDK instance;
    private Context context;

    private List<String> loadingList = new ArrayList<>();

    private NativeIVSDK(Context context) {
        this.context = context;
    }

    public static NativeIVSDK getInstance(Context context) {


        if (instance == null) {
            synchronized (NativeIVSDK.class) {
                if (instance == null) {
                    instance = new NativeIVSDK(context.getApplicationContext());
                }
            }
        }
        return instance;
    }


    public void preloadMediaResource(List<String> urls) {

        if (urls != null) {
            for (String url : urls) {
                downloadResource(url);
            }
        }
    }

    private void downloadResource(String url) {


        if (url.startsWith("http")) {
            HttpClient.getInstanse().getIVideoInfo(url, new GetIVideoInfoCallback() {

                @Override
                protected void onFailure(String error) {

                }

                @Override
                protected void onResponse(VideoProtocolInfo videoProtocolInfo) {
                    onLoadVideoInfoFinish(context, videoProtocolInfo);
                }
            });
        }
    }

    private void onLoadVideoInfoFinish(Context context, VideoProtocolInfo videoProtocolInfo) {

        VideoProtocolInfo.Protocol protocol = videoProtocolInfo.protocol;
        if (protocol != null) {

            List<VideoProtocolInfo.EventRail> eventRails = protocol.event_list;
            if (eventRails != null) {

                for (VideoProtocolInfo.EventRail eventRail : eventRails) {

                    if (!eventRail.hide_track) {

                        List<VideoProtocolInfo.EventComponent> eventComponents = eventRail.obj_list;
                        if (eventComponents != null) {
                            for (VideoProtocolInfo.EventComponent eventComponent : eventComponents) {

                                if (eventComponent.start_time * 1000 <= MEDIA_ASSETS_PRELOAD_TIME) {

                                    List<VideoProtocolInfo.EventOption> options = eventComponent.options;
                                    if (options != null) {
                                        for (VideoProtocolInfo.EventOption option : options) {
                                            if (!option.hide_option && option.custom != null && option.custom.click_default != null) {

                                                String imageUrl = option.custom.click_default.image_url;


                                                if (!NativeViewUtils.isNullOrEmptyString(imageUrl) && !new File(NativeViewUtils.getDowmloadFilePath(context), NativeViewUtils.getFileName(imageUrl)).exists() && !loadingList.contains(imageUrl)) {

                                                    HttpClient.getInstanse().download(imageUrl, NativeViewUtils.getDowmloadFilePath(context), NativeViewUtils.getFileName(imageUrl), new DownloadCallback() {
                                                        @Override
                                                        public void onDownloadStart(String url) {
                                                            LogUtils.d(TAG, "onDownloadStart   url=" + url);
                                                            loadingList.add(url);
                                                        }

                                                        @Override
                                                        public void onDownloadFailed(String url, String error) {
                                                            LogUtils.d(TAG, "onDownloadFailed   url=" + url);
                                                            loadingList.remove(url);
                                                        }

                                                        @Override
                                                        public void onDownloadSuccess(String url, File file) {
                                                            LogUtils.d(TAG, "onDownloadSuccess   url=" + url);
                                                            loadingList.remove(url);
                                                        }

                                                        @Override
                                                        public void onDownloading(String url, int progress) {

                                                        }
                                                    });
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

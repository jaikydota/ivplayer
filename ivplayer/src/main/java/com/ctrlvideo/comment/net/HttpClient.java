package com.ctrlvideo.comment.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient {


    private static HttpClient client;
    private OkHttpClient okHttpClient;


    private HttpClient() {
        okHttpClient = new OkHttpClient();
    }

    public static HttpClient getInstanse() {
        if (client == null) {
            client = new HttpClient();
        }
        return client;
    }

    public void getIVideoInfo(GetIVideoInfoCallback callback) {


        Request request = new Request.Builder()
                .get()
                .url("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866")
//                .url("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/")
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(callback);

    }

    public void download(String url, String destFileDir, String destFileName, DownloadCallback downloadCallback) {





        downloadCallback.onDownloadStart(url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {

                downloadCallback.onDownloadFailed(url, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                //储存下载文件的目录
                File dir = new File(destFileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);

//                url.substring(url.lastIndexOf("/") + 1)

                try {

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中更新进度条
                        downloadCallback.onDownloading(url, progress);
                    }
                    fos.flush();
                    //下载完成
                    downloadCallback.onDownloadSuccess(url, file);
                } catch (Exception e) {
                    downloadCallback.onDownloadFailed(url, e.getMessage());
                } finally {

                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {

                    }
                }
            }
        });
    }


}

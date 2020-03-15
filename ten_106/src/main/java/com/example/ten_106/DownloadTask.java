package com.example.ten_106;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String,Integer,Integer>{
    private static final String TAG = "database";
public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSE = 2;
    public static final int TYPE_CANCELED = 3;
    private DownloadListener listener ;
    private boolean isCancel = false;
    private boolean isPause = false;
    private int lastProgress;
    @Override
    protected Integer doInBackground(String... strings) {
        InputStream is =null;
        RandomAccessFile savedFile = null;
        File file = null;
        long downloadedLength  = 0;
        String downloadUrl = strings[0];
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        Log.d(TAG, "doInBackground: +"+fileName+directory);
        file = new File(directory+fileName);
        if (file.exists()){
            downloadedLength = file.length();
        }
        long contentLength = 0;
        try {
            contentLength = getContentLength(downloadUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contentLength==0){
            return TYPE_FAILED;
        }else if (contentLength == downloadedLength){
            return TYPE_SUCCESS;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("RANGE","bytes="+downloadedLength+"-")
                .url(downloadUrl)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response!=null){
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file,"rw");
                savedFile.seek(downloadedLength);
                byte[] b = new byte[1024];
                int total = 0 ;
                int len;
                while ((len = is.read(b))!= -1){
                    if (isCancel){
                        return TYPE_CANCELED;
                    }else if (isPause){
                        return TYPE_PAUSE;
                    }else{
                        total+=len;
                        savedFile.write(b,0,len);
                        int progress = (int)((total+downloadedLength)*100/contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
                try {
                    if (is!=null) {
                        is.close();
                    }
                    if (savedFile!=null){
                        savedFile.close();
                    }
                    if (isCancelled()&&file!=null){
                        file.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        return TYPE_FAILED;
    }

    private long getContentLength(String downloadUrl) throws IOException {
        Log.d(TAG, "getContentLength: "+downloadUrl);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response!=null&&response.isSuccessful()){
        long contentLength = response.body().contentLength();
            Log.d(TAG, "getContentLength: "+contentLength);
        response.body().close();
        return contentLength;
        }

        return 0;
    }

    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int progress = values[0];
        if (progress>lastProgress){
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        switch (integer){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSE:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
                default:
                    break;
        }
    }
    public void pauseDownload(){
        isPause = true;
    }
    public void cancelDownload(){
        isCancel = true;
    }
}

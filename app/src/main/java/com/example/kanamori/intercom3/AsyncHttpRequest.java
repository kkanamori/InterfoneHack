package com.example.kanamori.intercom3;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class AsyncHttpRequest extends AsyncTask<byte[], Void, String> {

    private Activity mainActivity;
    private OkHttpClient client;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public AsyncHttpRequest(Activity activity) {
        this.mainActivity = activity;
        client = new OkHttpClient();
    }

    @Override
    protected String doInBackground(byte[] ... data) {
        Log.d("debug","doInBackground called. data : " + data[0].toString());
        fileoutput(data[0]);

        String result = null;
        try {
            /*
            // リクエストの用意
            String url = "http://s3-ap-northeast-1.amazonaws.com/kanamo-jsontest/auth.json";
            File file = new File("test");

            //ここでPOSTする内容を設定　"image/jpg"の部分は送りたいファイルの形式に合わせて変更する
            String boundary = String.valueOf(System.currentTimeMillis());
            RequestBody requestBody = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/jpg"), data[0]))
                    .build();

            // リクエストの実行
            result = post(url,requestBody);
            */

            /*
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("image/jpg"), data[0]))
                    .build();
            Response response = client.newCall(request).execute();
            Log.d("debug","response code : " + response.code());
            Log.d("debug","response message : " + response.message());
            */

        } catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    private void fileoutput(byte[] bytes) {
        try {
            File fil = new File("/sdcard/Pictures/test.jpg");
            fil.createNewFile();
            FileOutputStream fo = new FileOutputStream(fil);
            fo.write(bytes, 0, bytes.length);
            fo.close();

            uploadS3(fil);
        }catch(Exception e){
         e.printStackTrace();
        } finally {
        }

    }

    private void uploadS3(File fil) {
        AmazonS3Client s3Client = new AmazonS3Client(
                new BasicAWSCredentials(
                        "<AWS_ACCOUNT_ID>", "<AWS_SECRET_KEY>") );
        PutObjectRequest por = new PutObjectRequest(
                "doorfonehack",
                "img.jpg",
                fil );
        s3Client.putObject(por);

        s3Client.setObjectAcl(
                "doorfonehack",
                "img.jpg",
                CannedAccessControlList.PublicRead);

    }

    private String post(String url, RequestBody requestBody) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Log.d("debug","request body : " + requestBody.toString());

        Response response = client.newCall(request).execute();
        Log.d("debug","response code : " + response.code());
        Log.d("debug","response message : " + response.message());
        return response.body().string();
    }


    // このメソッドは非同期処理の終わった後に呼び出されます
    @Override
    protected void onPostExecute(String result) {
    }
}
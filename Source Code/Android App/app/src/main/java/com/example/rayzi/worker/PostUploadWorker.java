package com.example.rayzi.worker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.rayzi.SessionManager;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.worker.UploadingProgres.CountingRequestBody;
import com.example.rayzi.worker.UploadingProgres.ProgressRequestBody;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostUploadWorker extends Worker implements ProgressRequestBody.UploadCallbacks {
    private static final String TAG = "PostUploadWorker";
    private String postImage, selectedLocation, hashTag, mentionPeople, caption, isPrivate, isComment;
    private Context context;
    private SessionManager sessionManager;

    public PostUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        sessionManager = new SessionManager(context);
        postImage = workerParams.getInputData().getString(Const.POST_IMAGE_LINK);
        selectedLocation = workerParams.getInputData().getString(Const.SELECTED_LOCATION);
        hashTag = workerParams.getInputData().getString(Const.HASH_TAG);
        mentionPeople = workerParams.getInputData().getString(Const.MENTION_PEOPLE);
        caption = workerParams.getInputData().getString(Const.CAPTION);
        isPrivate = workerParams.getInputData().getString(Const.SHOW_POST);
        isComment = workerParams.getInputData().getString(Const.ALLOW_COMMENT);
    }

    @NonNull
    @Override
    public Result doWork() {
        uploadPost();
        return Result.success();
    }

    private void uploadPost() {
        File file = new File(postImage);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("post", file.getName(), requestFile);

        final CountingRequestBody.Listener progressListener = new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(long bytesRead, long contentLength) {
                if (bytesRead >= contentLength) {
                    // progress completed
                } else {
                    if (contentLength > 0) {
                        final int progress = (int) (((double) bytesRead / contentLength) * 100);

                        // progress every
                        Log.d(TAG, "onRequestProgress: progress == " + progress);
                        Intent intent = new Intent(Const.UPLOAD_PROGRESS);
                        intent.putExtra("progress", progress);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        if (progress >= 100) {
                         // progress completed
                        }
                        Log.d("uploadProgress called", progress + " ");
                    }
                }
            }
        };

        HashMap<String, RequestBody> hashMap = new HashMap<>();
        hashMap.put("userId", toRequestBody(sessionManager.getUser().getId()));
        hashMap.put("location", toRequestBody(selectedLocation));
        hashMap.put("caption", toRequestBody(caption));
        hashMap.put("hashtag", toRequestBody(hashTag));
        hashMap.put("mentionPeople", toRequestBody(mentionPeople));
        hashMap.put("showPost", toRequestBody(isPrivate));
        hashMap.put("allowComment", toRequestBody(String.valueOf(isComment)));

        Call<RestResponse> call = RetrofitBuilder.createUploadFile(progressListener).uploadPost(hashMap, body);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200 && response.body().isStatus()) {
//                    Toast.makeText(context.getApplicationContext(),
//                            "Upload Successfully", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "onResponse: success");
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: err  " + t.getLocalizedMessage());
            }
        });
    }

    public RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNullElse(value, ""));
    }

    @Override
    public void onProgressUpdate(int percentage) {
        Log.d(TAG, "onProgressUpdate: percentage == " + percentage);
        Intent intent = new Intent(Const.UPLOAD_PROGRESS);
        intent.putExtra("progress", percentage);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }

}

package com.example.rayzi.worker.UploadingProgres;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.reels.record.UploadActivity;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.utils.VideoUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryUploadWorker extends Worker implements ProgressRequestBody.UploadCallbacks {
    private static final String TAG = "PostUploadWorker";
    Context context;
    SessionManager sessionManager;
    String video, screenshot, preview, description, location, userId, heshtags, mentions;

    public StoryUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        sessionManager = new SessionManager(context);

        video = workerParams.getInputData().getString(Const.VIDEO);
        screenshot = workerParams.getInputData().getString(Const.SCREENSHOT);
        preview = workerParams.getInputData().getString(Const.PREVIEW);
        description = workerParams.getInputData().getString(Const.DESCRIPTION);
        location = workerParams.getInputData().getString(Const.LOCATION);
        userId = workerParams.getInputData().getString(Const.USERID);
        heshtags = workerParams.getInputData().getString(Const.HESHTAGS);
        mentions = workerParams.getInputData().getString(Const.MENTIONS);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            uploadPost();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.success();
    }

    private void uploadPost() throws IOException {
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
                            Log.d(TAG, "onRequestProgress: progress complete thai gy");
                        }
                        Log.d("uploadProgress called", progress + " ");
                    }
                }
            }
        };

        StoryUpload(progressListener);

    }


    private void StoryUpload( CountingRequestBody.Listener progressListener) throws IOException {    //type 1=video,  2=image

        UploadActivity.LocalVideo relite = sessionManager.getLocalVideo();
        String songId = relite.getSongId() != null ? relite.getSongId() : "";
        File video = new File(relite.getVideo());

        File screenshot = new File(relite.getScreenshot());
        File preview = new File(relite.getPreview());
        String description = relite.getDecritption();
        boolean hasComments = relite.isHasComments();
        String location = relite.getLocation();
        int privacy = relite.getPrivacy();
        String userId = relite.getUserId();
        long duration = VideoUtil.getDuration(getApplicationContext(), Uri.fromFile(video));
        duration = TimeUnit.MILLISECONDS.toSeconds(duration);


        long finalDuration = duration;
        MultipartBody.Part body1 = null;
        if (video != null && !video.getPath().isEmpty()) {
            File file1 = new File(video.getAbsolutePath());

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file1);
            body1 = MultipartBody.Part.createFormData("video", file1.getName(), requestFile);

        }

        MultipartBody.Part body2 = null;
        if (screenshot != null && !screenshot.getPath().isEmpty()) {
            File file2 = new File(screenshot.getAbsolutePath());
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file2);
            body2 = MultipartBody.Part.createFormData("screenshot", file2.getName(), requestFile);

        }

        MultipartBody.Part body3 = null;
        if (preview != null && !preview.getPath().isEmpty()) {
            File file3 = new File(preview.getAbsolutePath());
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file3);
            body3 = MultipartBody.Part.createFormData("thumbnail", file3.getName(), requestFile);

        }

        HashMap<String, RequestBody> hashMap = new HashMap<>();
        hashMap.put("userId", RequestBody.create(MediaType.parse("text/plain"), userId));

        if (songId.isEmpty()) {
            hashMap.put("isOriginalAudio", RequestBody.create(MediaType.parse("text/plain"), "true"));
        } else {
            hashMap.put("isOriginalAudio", RequestBody.create(MediaType.parse("text/plain"), "false"));
            hashMap.put("songId", RequestBody.create(MediaType.parse("text/plain"), songId));
        }
        hashMap.put("allowComment", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(hasComments)));
        hashMap.put("caption", RequestBody.create(MediaType.parse("text/plain"), description));
        hashMap.put("showVideo", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(privacy)));
        hashMap.put("location", RequestBody.create(MediaType.parse("text/plain"), location));
        hashMap.put("hashtag", RequestBody.create(MediaType.parse("text/plain"), heshtags));
        hashMap.put("mentionPeople", RequestBody.create(MediaType.parse("text/plain"), mentions));
        hashMap.put("duration", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(finalDuration)));
        hashMap.put("size", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(VideoUtil.getFileSizeInMB(video) + " MB")));

        Log.d(TAG, "doActualWork: size " + VideoUtil.getFileSizeInMB(video));
        Log.d(TAG, "doActualWork: duration " + finalDuration);

        final boolean[] success = {false};

        RetrofitBuilder.createStoryUploadFile(progressListener).uploadRelite(hashMap, body1, body2, body3).enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.isSuccessful()) {
                        Toast.makeText(context, R.string.reels_upload_successfully , Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(Const.PROGRESS_DONE);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
                    } else {
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG, "onFailure: error message " + t.getMessage());
                Toast.makeText(context, "hello " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

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

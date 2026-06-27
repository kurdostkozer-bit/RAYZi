package com.example.rayzi.reels.record.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.arthenica.ffmpegkit.ReturnCode;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class MergeAudiosWorker extends Worker {

    public static final String KEY_INPUT_1 = "input_1";
    public static final String KEY_INPUT_1_VOLUME = "input_1_volume";
    public static final String KEY_INPUT_2 = "input_2";
    public static final String KEY_INPUT_2_VOLUME = "input_2_volume";
    public static final String KEY_OUTPUT = "output";
    private static final String TAG = "===MergeAudiosWorker";

    public MergeAudiosWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public Result doWork() {
        File input1 = new File(getInputData().getString(KEY_INPUT_1));
        float volume1 = getInputData().getFloat(KEY_INPUT_1_VOLUME, 0f);
        File input2 = new File(getInputData().getString(KEY_INPUT_2));
        float volume2 = getInputData().getFloat(KEY_INPUT_2_VOLUME, 0f);
        File output = new File(getInputData().getString(KEY_OUTPUT));
        boolean success = doActualWork(input1, volume1, input2, volume2, output);
        if (!success && !output.delete()) {
            Log.w(TAG, "Could not delete failed output file: " + output);
        }

        return success ? Result.success() : Result.failure();
    }

    int code = 1;
    private boolean doActualWork(File input1, float volume1, File input2, float volume2, File output) {
        final CountDownLatch latch = new CountDownLatch(1);
        final int[] resultCode = new int[1]; // To hold the return code

        String filter = String.format(Locale.US,
                "[0:a]volume=1.00,aresample=ocl=stereo[a0];[1:a]volume=1.00,aresample=ocl=stereo[a1];[a0][a1]amix=inputs=2[out]",
                volume1, volume2);
        String command = "-i " + input1.getAbsolutePath() + " " +
                "-i " + input2.getAbsolutePath() + " " +
                "-filter_complex \"" + filter + "\" " +
                "-map [out] -vn " +
                output.getAbsolutePath();
        Log.d("===statusCheck", "doActualWork: command ="+command);
        FFmpegKit.executeAsync(command, session -> {
            resultCode[0] = session.getReturnCode().getValue();
            latch.countDown();
        });

        try {
            latch.await(); // Wait for FFmpeg to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return resultCode[0] == 0;
    }

   /* private boolean doActualWork(File input1, float volume1, File input2, float volume2, File output) {
        if (volume1 >= 0) {
            String filter = String.format(
                    Locale.US,
                    "[0:a]volume=%.2f[a0];[1:a]volume=%.2f[a1];[a0][a1]amix=inputs=2[out]",
                    volume1,
                    volume2);
            *//*FFmpegKit.executeAsync(
                    "-i" + input1.getAbsolutePath() + "-i" + input2.getAbsolutePath() +
                            "-filter_complex" + filter + "-map" + "[out]" + "-vn" +
                            output.getAbsolutePath()
                    , new FFmpegSessionCompleteCallback() {
                        @Override
                        public void apply(FFmpegSession session) {
                            code = session.getReturnCode().getValue();
                        }
                    });*//*

            String command = "-i " + input1.getAbsolutePath() + " " +
                    "-i " + input2.getAbsolutePath() + " " +
                    "-filter_complex \"" + filter + "\" " +
                    "-map [out] " +
                    "-vn " +
                    output.getAbsolutePath();
            Log.d("===FFmpegCommand", "Executing command: " + command);

            FFmpegKit.executeAsync(
                    command,
                    new FFmpegSessionCompleteCallback() {
                        @Override
                        public void apply(FFmpegSession session) {
                            code = session.getReturnCode().getValue();
                            Log.d("===FFmpegKit", "Return code: " + code);
                        }
                    }
            );
        } else {
            String filter = String.format(
                    Locale.US,
                    "[0:a]volume=%.2f[a0];[a0]amix=inputs=1[out]",
                    volume2);
            FFmpegKit.executeAsync(
                    "-i" + input2.getAbsolutePath() +
                            "-filter_complex" + filter + "-map" + "[out]" +
                            output.getAbsolutePath()
                    , new FFmpegSessionCompleteCallback() {
                        @Override
                        public void apply(FFmpegSession session) {
                            ReturnCode returnCode = session.getReturnCode();
                            code = returnCode.getValue();

                            if (ReturnCode.isSuccess(returnCode)) {
                                // Command completed successfully
                                Log.d("===FFmpegKit", "Command executed successfully!");
                            } else if (ReturnCode.isCancel(returnCode)) {
                                // Command was canceled
                                Log.d("===FFmpegKit", "Command execution canceled by user!");
                            } else {
                                // Command failed
                                Log.e("===FFmpegKit", "Command failed with return code: " + returnCode);
                                Log.e("===FFmpegKit", "Failed session logs: " + session.getAllLogsAsString());
                            }
                        }
                    });
        }

        return code==0;
    }*/
}

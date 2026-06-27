package com.example.rayzi.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * SvgaCacheManager
 *
 * A utility class to download, cache, and decode SVGA files locally.
 *
 * References:
 * 1. https://developer.android.com/training/data-storage/app-specific
 * 2. https://github.com/svga/SVGAPlayer-Android
 * 3. https://docs.oracle.com/javase/tutorial/essential/io/
 */
public class SvgaCacheManager {

    private static final String TAG = "SvgaCacheManager";

    /**
     * Return a subdirectory in the app's cache directory for storing .svga files.
     *
     * @param context Application or Activity context
     * @return A File object pointing to /cache/svga_cache
     */
    public static File getSvgaCacheDir(Context context) {
        File svgaCacheDir = new File(context.getCacheDir(), "svga_cache");
        if (!svgaCacheDir.exists()) {
            boolean created = svgaCacheDir.mkdirs();
            if (!created) {
                Log.w(TAG, "Failed to create svga_cache directory.");
            }
        }
        return svgaCacheDir;
    }

    /**
     * Checks whether a file for the given URL is already cached.
     *
     * @param url     The URL of the .svga file
     * @param context Context to find the cache directory
     * @return true if the file is found and has > 0 length, false otherwise
     */
    public static boolean isCached(String url, Context context) {
        File cachedFile = new File(getSvgaCacheDir(context), String.valueOf(url.hashCode()));
        return cachedFile.exists() && cachedFile.length() > 0;
    }

    /**
     * Downloads a .svga file from the given URL and saves it to the cache directory,
     * unless it is already cached.
     *
     * @param url     The URL of the .svga file
     * @param context Application or Activity context
     * @return The cached File, or null if an error occurred
     */
    public static File downloadAndCacheSvga(String url, Context context) {
        File svgaCacheDir = getSvgaCacheDir(context);

/*

        String url = "https://interactive.atmlive.me/storage/17249217813391710341021028gift2.svga";
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        System.out.println(fileName);
// Output: 17249217813391710341021028gift2.svga*/

        File cachedFile = new File(svgaCacheDir, String.valueOf(url.hashCode()));

        // Already cached?
        if (cachedFile.exists() && cachedFile.length() > 0) {
            Log.d(TAG, "Already cached: " + cachedFile.getAbsolutePath());
            return cachedFile;
        }

        // Download and save the file
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            Log.d(TAG, "Downloading from: " + url);
            URL urlObj = new URL(url);
            URLConnection connection = urlObj.openConnection();
            connection.connect();

            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(cachedFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

            Log.d(TAG, "File cached at: " + cachedFile.getAbsolutePath());
            return cachedFile;

        } catch (Exception e) {
            Log.e(TAG, "Error downloading file: " + e.getMessage(), e);
            return null;
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Decode a cached SVGA file into an SVGAVideoEntity using SVGAParser.
     *
     * @param context        Activity or Application context
     * @param url            The original URL of the .svga file (used to find the cached file)
     * @param parseCompletion Callback with onComplete / onError
     */
    public static void decodeSvgaFromCache(
            @NonNull Context context,
            @NonNull String url,
            @NonNull SVGAParser.ParseCompletion parseCompletion
    ) {
        File cachedFile = new File(getSvgaCacheDir(context), String.valueOf(url.hashCode()));

        if (!cachedFile.exists() || cachedFile.length() == 0) {
            Log.w(TAG, "No valid cache file found for: " + url);
            parseCompletion.onError();
            return;
        }

        try {
            SVGAParser parser = new SVGAParser(context);
            parser.decodeFromInputStream(
                    new BufferedInputStream(new FileInputStream(cachedFile)), // inputStream
                    url,                        // cacheKey (unique identifier)
                    parseCompletion,            // callback
                    true,                       // closeInputStream (default is true in Kotlin)
                    null,                       // playCallback (default is null)
                    null                        // alias (default is null)
            );
        } catch (Exception e) {
            Log.e(TAG, "Failed to decode from cache: " + e.getMessage(), e);
            parseCompletion.onError();
        }
    }
}
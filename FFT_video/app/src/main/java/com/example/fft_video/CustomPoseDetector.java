package com.example.fft_video;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class CustomPoseDetector {
    private static final String TAG = "CustomPoseDetector";
    private Map<Integer, Pose> cache;
    private Set<Integer> pending;
    private PoseDetector poseDetector;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private BlockingQueue<Bitmap> taskQueue = new LinkedBlockingQueue<>();
    private TextView statusText;

    public CustomPoseDetector(Activity activity){
        cache = new HashMap<>();
        pending = new HashSet<>();
        statusText = activity.findViewById(R.id.statusText);
        statusText.setText("waiting");

        AccuratePoseDetectorOptions options =
                new AccuratePoseDetectorOptions.Builder()
                        .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
                        .build();
        poseDetector = PoseDetection.getClient(options);

        startProcessingThread();

        Log.i(TAG, "processor initialized");
    }


    public void updateStatus(){
        statusText.setText(cache.keySet().size()+"/"+taskQueue.size());
    }

    private void startProcessingThread() {
        executor.execute(()->{
            while(true){
                try {
                    Bitmap bitmap = taskQueue.take(); // Blocks until a task is available
//                    Log.i(TAG, "taking bitmap with hash "+hashBitmap(bitmap));
                    process(bitmap);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public int hashBitmap(Bitmap bitmap){
        int[] buffer = new int[bitmap.getWidth()*bitmap.getHeight()];
        bitmap.getPixels(buffer, 0,bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return Arrays.hashCode(buffer);
    }
    public void process(Bitmap bitmap){
        if(cache.containsKey(hashBitmap(bitmap)))return;
        Log.i(TAG, "started processing "+hashBitmap(bitmap));
        Task<Pose> result = poseDetector.process(bitmap, 0).addOnSuccessListener(p->{
            cache.put(hashBitmap(bitmap), p);
            Log.i(TAG, "successfully processed! "+hashBitmap(bitmap));
        }).addOnFailureListener(e->{
            Log.e(TAG,"Failed to process image.");
        });
        try {
            Tasks.await(result);
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Exception in task");
            throw new RuntimeException(e);
        }
    }

    public Pose detect(Bitmap bitmap){
        Pose out = cache.getOrDefault(hashBitmap(bitmap), null);
        if(out == null) queue(bitmap);
        return out;
    }

    public void queue(Bitmap bitmap){
        if(pending.add(hashBitmap(bitmap)))
            taskQueue.offer(bitmap);
        updateStatus();
    }

    public void reset(){
        pending.clear();
        cache.clear();
        statusText.setText("waiting");
    }

    public void destroy(){
        executor.shutdown();
    }
}

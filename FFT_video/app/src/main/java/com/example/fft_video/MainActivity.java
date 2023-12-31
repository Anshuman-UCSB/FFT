package com.example.fft_video;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.example.fft_video.decoder.IVideoFrameExtractor;
import com.example.fft_video.decoder.Frame;
import com.example.fft_video.decoder.FrameExtractor;
import com.example.fft_video.gles.GlPlayerView;
import com.example.fft_video.gles.GlPlayerRenderer;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.mlkit.vision.pose.Pose;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements GlPlayerRenderer.FrameListener, IVideoFrameExtractor {
    private static final String TAG = "FFT";
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private GraphicOverlay graphicOverlay;

    private int frameWidth, frameHeight;

    private boolean processing;
    private boolean pending;
    private Bitmap lastFrame;
    private CustomPoseDetector imageProcessor;
    private FrameExtractor frameExtractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageProcessor = new CustomPoseDetector(this);
        frameExtractor = new FrameExtractor(this);

        player = new SimpleExoPlayer.Builder(this).build();

        playerView = findViewById(R.id.player_view);
        playerView.setPlayer(player);
        FrameLayout contentFrame = playerView.findViewById(com.google.android.exoplayer2.ui.R.id.exo_content_frame);
        View videoFrameView = createVideoFrameView();
        if(videoFrameView != null) contentFrame.addView(videoFrameView);

        graphicOverlay = new GraphicOverlay(this, null);
        contentFrame.addView(graphicOverlay);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        setupPlayer(uri);
                        preprocessVideo(uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        findViewById(R.id.selectVidBtn).setOnClickListener( v -> {
            startSelectVideo();
        });
    }

    private String getFilePathFromUri(Uri uri){
        String filePath = null;
        if (uri != null && "content".equals(uri.getScheme())) {
            Cursor cursor = this.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            cursor.close();
        } else {
            filePath = uri.getPath();
        }
        return filePath;
    }

    private void preprocessVideo(Uri uri) {
//            imageProcessor.queue(frame);
        try {
//            Log.i(TAG, "Should I kill myself: "+f.canRead()+" - uri is "+uri.toString());
            frameExtractor.extractFrames(getFilePathFromUri(uri));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.i(TAG, "Set all frames to be preprocessed");
        imageProcessor.updateStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageProcessor.destroy();
    }

    private void startSelectVideo() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                .build());
    }

    private View createVideoFrameView() {
        GlPlayerView glPlayerView = new GlPlayerView(this);
        glPlayerView.setSimpleExoPlayer(player);
        glPlayerView.setFrameListener(this);
        return glPlayerView;
    }

    private void setupPlayer(Uri uri){
        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.stop();
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void processFrame(Bitmap frame){
        lastFrame = frame;
        if(imageProcessor != null){
            pending = processing;
            if(!processing){
                processing = true;
                if(frameWidth != frame.getWidth() || frameHeight != frame.getHeight()){
                    frameWidth = frame.getWidth();
                    frameHeight = frame.getHeight();
                    graphicOverlay.setImageSourceInfo(frameWidth, frameHeight, false);
                }
//                Log.i(TAG, "procesed frame: "+imageProcessor.detect(frame));
//                Pose p = imageProcessor.detect(frame);
//                if(p!= null){
//                    Log.i(TAG, "Landmarks: "+p.getPoseLandmark(PoseLandmark.NOSE));
//                    drawPose(graphicOverlay, p);
//                }
                processing = false;
            }
        }
    }

    private void drawPose(GraphicOverlay graphicOverlay, Pose pose) {
        graphicOverlay.clear();
        graphicOverlay.add(new PoseGraphic(graphicOverlay, pose));
        graphicOverlay.postInvalidate();
    }


    @Override
    public void onFrame(Bitmap bitmap) {
        processFrame(bitmap);
        imageProcessor.updateStatus();
    }

    @Override
    public void onCurrentFrameExtracted(@NonNull Frame currentFrame) {
        imageProcessor.queue(frameExtractor.fromBufferToBitmap(currentFrame));
        // TODO: Figure out a way to extract frames more efficiently
        Log.i(TAG, "Frame added to queue");
    }

    @Override
    public void onAllFrameExtracted(int processedFrameCount, long processedTimeMs) {

    }
}
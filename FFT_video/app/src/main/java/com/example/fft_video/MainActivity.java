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

import com.example.fft_video.gles.GlPlayerView;
import com.example.fft_video.gles.GlPlayerRenderer;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.mlkit.vision.pose.Pose;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements GlPlayerRenderer.FrameListener {
    private static final String TAG = "FFT";
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private GraphicOverlay graphicOverlay;
    private FrameExtractor frameExtractor;

    private int frameWidth, frameHeight;

    private boolean processing;
    private boolean pending;
    private Bitmap lastFrame;
    private CustomPoseDetector imageProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameExtractor = new FrameExtractor(this);
        imageProcessor = new CustomPoseDetector(this);

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

    private String saveLocalCopy(Uri uri){
        String dirPath = getApplicationContext().getFilesDir().getParentFile().getPath()+"/tmpvid.mp4";

        try (InputStream ins = getContentResolver().openInputStream(uri)) {

            File dest = new File(dirPath);

            try (OutputStream os = new FileOutputStream(dest)) {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = ins.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
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
        String path = saveLocalCopy(uri);
        Log.i(TAG, path);
        File f = new File(path);
        Log.i(TAG, f.canRead()+" - can read "+f.getPath());
        Log.i(TAG, "extracted frames: "+frameExtractor.extractFrames(path).size());
//        Log.i(TAG, "Set all frames to be preprocessed");
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

}
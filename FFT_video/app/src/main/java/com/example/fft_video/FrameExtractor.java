package com.example.fft_video;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FrameExtractor {
    private static final String TAG = "FrameExtractor";
    private Context context;
    public FrameExtractor(Context context){
        this.context = context;
    }
    public List<Bitmap> extractFrames(String path) {
        List<Bitmap> frames = new ArrayList<>();
        File file = new File(path);
        FrameGrab grab = null;
        try {
            grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JCodecException e) {
            throw new RuntimeException(e);
        }
        Picture picture;
        while (true) {
            try {
                if (!(null != (picture = grab.getNativeFrame()))) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Log.i(TAG, (picture.getWidth() + "x" + picture.getHeight() + " " + picture.getColor()));
        }
        return frames;
    }
}

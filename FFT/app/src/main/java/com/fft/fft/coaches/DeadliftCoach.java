package com.fft.fft.coaches;

import static com.fft.fft.poseDetection.utils.diffX;
import static com.fft.fft.poseDetection.utils.diffY;
import static com.fft.fft.poseDetection.utils.dist;
import static java.lang.Math.abs;

import android.content.Context;
import android.util.Log;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

public class DeadliftCoach extends Coach{
    public final String TAG = "FFT_DeadliftCoach";

    private int reps;
    private float lowestY;

    private int tooFar;
    private boolean extended;

    enum State {
        UP,
        DOWN
    }

    private State state;
    public DeadliftCoach(Context ctx){
        super(ctx);
        Log.i(TAG, "Initialized deadlift coach");
    }
    @Override
    public String getAdvice() {
        str.setLength(0);
        note("Currently on rep "+reps+" and in state "+(state== State.UP?"UP":"DOWN"));
        if (tooFar == 1){
            note("Make sure to keep the bar as close to your body as possible.");
        } else if (tooFar == -1) {
            note("Good job keeping it close to your body!");
        }
        return super.getAdvice();
    }

    @Override
    public void reset() {
        state = State.UP;
        str.setLength(0);
        reps = 0;
        lowestY = 0;
        tooFar = 0;
        extended = false;
    }

    @Override
    public void process(Pose pose) {
        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
        if(leftShoulder == null) return;
        PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);

        PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
        PoseLandmark leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL);

        float margin = dist(leftShoulder, leftElbow)/5;
        float wristKneeDiff = diffY(leftKnee, leftWrist)/margin;
        float kneeHeelXDiff = 0;

        if(state == State.UP){
            if(lowestY != 0){
                lowestY = 0;
            }
            if(wristKneeDiff < margin/4){
                state = State.DOWN;
                extended = false;
            }
        } else { // Down state
            float wristY = leftWrist.getPosition().y;
            if (wristY > lowestY){
                lowestY = wristY;
            }
            kneeHeelXDiff = diffX(leftHeel, leftWrist);
            if (kneeHeelXDiff > margin*5){
                extended=true;
            }
            if(wristKneeDiff > margin/4){
                state = State.UP;
                reps++;
                tooFar = extended?1:-1;
            }
        }
        debug = "Margin: "+margin+"\nwrist vs knee diff: "+wristKneeDiff +"\nKnee to heel X-axis: "+kneeHeelXDiff+"\nLoweset y: "+lowestY;
    }
}

package com.example.woga1.navigation.SoundAnalyze;

import android.os.SystemClock;
import android.util.Log;

class FPSCounter {
    private long frameCount;
    private long timeOld, timeUpdateInterval;  // in ms
    private double fps;
    private String TAG_OUTSIDE;

    FPSCounter(String TAG) {
        timeUpdateInterval = 2000;
        TAG_OUTSIDE = TAG;
        frameCount = 0;
        timeOld = SystemClock.uptimeMillis();
    }

    // call this when number of frames plus one
    void inc() {
        frameCount++;
        long timeNow = SystemClock.uptimeMillis();
        if (timeOld + timeUpdateInterval <= timeNow) {
            fps = 1000 * (double) frameCount / (timeNow - timeOld);
            Log.d(TAG_OUTSIDE, "FPS: " + Math.round(100*fps)/100.0 +
                    " (" + frameCount + "/" + (timeNow - timeOld) + "ms)");
            timeOld = timeNow;
            frameCount = 0;
        }
    }

    public double getFPS() {
        return fps;
    }
}

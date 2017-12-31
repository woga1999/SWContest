package com.example.woga1.navigation.SoundAnalyze;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Toast;

import com.example.woga1.navigation.Navigation.NavigationActivity;
import com.example.woga1.navigation.R;

public class AnalyzerViews {
    final String TAG = "AnalyzerViews";
    private final NavigationActivity activity;
    public final AnalyzerGraphic graphView;

    private float DPRatio;
    private double fpsLimit = 8;

    public boolean bWarnOverrun = true;

    public AnalyzerViews(NavigationActivity _activity) {
        activity = _activity;
        graphView = (AnalyzerGraphic) activity.findViewById(R.id.plot);

        Resources res = activity.getResources();
        DPRatio = res.getDisplayMetrics().density;
    }

    // Prepare the spectrum and spectrogram plot (from scratch or full reset)
    // Should be called before samplingThread starts.
    public void setupView(AnalyzerParameters analyzerParam) {
        graphView.setupPlot(analyzerParam);
    }

    // Will be called by SamplingLoop (in another thread)
    public void update(final double[] spectrumDBcopy) {
        graphView.saveSpectrum(spectrumDBcopy);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // data will get out of synchronize here
                invalidateGraphView();
            }
        });
    }

    private double wavSecOld = 0;      // used to reduce frame rate
    void updateRec(double wavSec) {
        if (wavSecOld > wavSec) {
            wavSecOld = wavSec;
        }
        if (wavSec - wavSecOld < 0.1) {
            return;
        }
        wavSecOld = wavSec;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // data will get out of synchronize here
                invalidateGraphView(AnalyzerViews.VIEW_MASK_RecTimeLable);
            }
        });
    }

    void notifyWAVSaved(final String path) {
        String text = "WAV saved to " + path;
        notifyToast(text);
    }

    void notifyToast(final String st) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Context context = activity.getApplicationContext();
                Toast toast = Toast.makeText(context, st, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private long timeToUpdate = SystemClock.uptimeMillis();
    private volatile boolean isInvalidating = false;

    // Invalidate graphView in a limited frame rate
    public void invalidateGraphView() {
        invalidateGraphView(-1);
    }

    private static final int VIEW_MASK_graphView     = 1<<0;
    private static final int VIEW_MASK_RecTimeLable  = 1<<4;

    private void invalidateGraphView(int viewMask) {
        if (isInvalidating) {
            return ;
        }
        isInvalidating = true;
        long frameTime;                      // time delay for next frame
        if (graphView.getShowMode() != AnalyzerGraphic.PlotMode.SPECTRUM) {
            frameTime = (long)(1000/fpsLimit);  // use a much lower frame rate for spectrogram
        } else {
            frameTime = 1000/60;
        }
        long t = SystemClock.uptimeMillis();
        //  && !graphView.isBusy()
        if (t >= timeToUpdate) {    // limit frame rate
            timeToUpdate += frameTime;
            if (timeToUpdate < t) {            // catch up current time
                timeToUpdate = t+frameTime;
            }
            idPaddingInvalidate = false;
            // Take care of synchronization of graphView.spectrogramColors and iTimePointer,
            // and then just do invalidate() here.
            if ((viewMask & VIEW_MASK_graphView) != 0)
                graphView.invalidate();

        } else {
            if (! idPaddingInvalidate) {
                idPaddingInvalidate = true;
                paddingViewMask = viewMask;
                paddingInvalidateHandler.postDelayed(paddingInvalidateRunnable, timeToUpdate - t + 1);
            } else {
                paddingViewMask |= viewMask;
            }
        }
        isInvalidating = false;
    }

    public void setFpsLimit(double _fpsLimit) {
        fpsLimit = _fpsLimit;
    }

    private volatile boolean idPaddingInvalidate = false;
    private volatile int paddingViewMask = -1;
    private Handler paddingInvalidateHandler = new Handler();

    // Am I need to use runOnUiThread() ?
    private final Runnable paddingInvalidateRunnable = new Runnable() {
        @Override
        public void run() {
            if (idPaddingInvalidate) {
                // It is possible that t-timeToUpdate <= 0 here, don't know why
                invalidateGraphView(paddingViewMask);
            }
        }
    };
}

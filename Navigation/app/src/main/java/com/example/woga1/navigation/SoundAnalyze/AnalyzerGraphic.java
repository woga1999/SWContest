package com.example.woga1.navigation.SoundAnalyze;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AnalyzerGraphic extends View
{
    private final String TAG = "AnalyzerGraphic:";
    public Context context;
    public double xZoom, yZoom;     // horizontal and vertical scaling
    public double xShift, yShift;   // horizontal and vertical translation, in unit 1 unit
    public static final double minDB = -144f;    // hard lower bound for dB
    public static final double maxDB = 12f;      // hard upper bound for dB

    public int canvasWidth, canvasHeight;   // size of my canvas
    public int[] myLocation = {0, 0}; // window location on screen
    public volatile static boolean isBusy = false;
    public double freq_lower_bound_for_log = 0f;
    public double[] savedDBSpectrum = new double[0];
    public static final int VIEW_RANGE_DATA_LENGTH = 6;

    public SpectrogramPlot spectrogramPlot;

    public PlotMode showMode = PlotMode.SPECTRUM;

    public enum PlotMode {  // java's enum type is inconvenient
        SPECTRUM(0), SPECTROGRAM(1);

        public final int value;
        PlotMode(int value) { this.value = value; }
//        public int getValue() { return value; }
    }

    public AnalyzerGraphic(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context);
    }

    public AnalyzerGraphic(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public AnalyzerGraphic(Context context) {
        super(context);
        setup(context);
    }

    public void setup(Context _context) {
        context = _context;
        Log.i(TAG, "in setup()");

        xZoom  = 1f;
        xShift = 0f;
        yZoom  = 1f;
        yShift = 0f;
        canvasWidth = canvasHeight = 0;

        // Demo of full initialization
        spectrogramPlot = new SpectrogramPlot(context);

        spectrogramPlot.setCanvas(canvasWidth, canvasHeight, null);

        spectrogramPlot.setZooms(xZoom, xShift, yZoom, yShift);
    }

    public void setupAxes(AnalyzerParameters analyzerParam) {
        int sampleRate       = analyzerParam.sampleRate;
        int fftLen           = analyzerParam.fftLen;

        freq_lower_bound_for_log = (double)sampleRate/fftLen;
//        double freq_lower_bound_local = 0;
//
//        // Spectrogram
//        freq_lower_bound_local = 0;
//        if (spectrogramPlot.axisFreq.mapType == ScreenPhysicalMapping.Type.LOG) {
//            freq_lower_bound_local = freq_lower_bound_for_log;
//        }
    }

    // Call this when settings changed.
    public void setupPlot(AnalyzerParameters analyzerParam) {
        setupAxes(analyzerParam);
        spectrogramPlot.setupSpectrogram(analyzerParam);
    }

    public void setShowFreqAlongX(boolean b) {
        spectrogramPlot.setShowFreqAlongX(b);

        if (showMode == PlotMode.SPECTRUM) return;

        if (spectrogramPlot.showFreqAlongX) {
            xZoom  = spectrogramPlot.axisFreq.getZoom();
            xShift = spectrogramPlot.axisFreq.getShift();
            yZoom  = spectrogramPlot.axisTime.getZoom();
            yShift = spectrogramPlot.axisTime.getShift();
        } else {
            xZoom  = spectrogramPlot.axisTime.getZoom();
            xShift = spectrogramPlot.axisTime.getShift();
            yZoom  = spectrogramPlot.axisFreq.getZoom();
            yShift = spectrogramPlot.axisFreq.getShift();
        }
    }

    // Note: Assume setupPlot() was called once.
    public void switch2Spectrogram() {
        if (showMode == PlotMode.SPECTRUM && canvasHeight > 0) { // canvasHeight==0 means the program is just start
            Log.v(TAG, "switch2Spectrogram()");
            if (spectrogramPlot.showFreqAlongX) {
                // no need to change x scaling
                yZoom  = spectrogramPlot.axisTime.getZoom();
                yShift = spectrogramPlot.axisTime.getShift();
                spectrogramPlot.axisFreq.setZoomShift(xZoom, xShift);
            } else {
                //noinspection SuspiciousNameCombination
                yZoom = xZoom;
                yShift = 1 - 1/xZoom - xShift;         // axisFreq is reverted
                xZoom  = spectrogramPlot.axisTime.getZoom();
                xShift = spectrogramPlot.axisTime.getShift();
                spectrogramPlot.axisFreq.setZoomShift(yZoom, yShift);
            }
        }
        spectrogramPlot.spectrogramBMP.updateAxis(spectrogramPlot.axisFreq);
        spectrogramPlot.prepare();
        showMode = PlotMode.SPECTROGRAM;
    }

    public double[] setViewRange(double[] _ranges, double[] rangesDefault) {
        // See AnalyzerActivity::getViewPhysicalRange() for ranges[]
        if (_ranges.length < VIEW_RANGE_DATA_LENGTH) {
            Log.i(TAG, "setViewRange(): invalid input.");
            return null;
        }

        // do not modify input parameter
        double[] ranges = new double[VIEW_RANGE_DATA_LENGTH];
        System.arraycopy(_ranges, 0, ranges, 0, VIEW_RANGE_DATA_LENGTH);

        if (rangesDefault != null) {
            // Sanity check
            if (rangesDefault.length != 2 * VIEW_RANGE_DATA_LENGTH) {
                Log.i(TAG, "setViewRange(): invalid input.");
                return null;
            }
            for (int i = 0; i < 6; i += 2) {
                if (ranges[i  ] > ranges[i+1]) {                     // order reversed
                    double t = ranges[i]; ranges[i] = ranges[i+1]; ranges[i+1] = t;
                }
                if (ranges[i  ] < rangesDefault[i+6]) ranges[i  ] = rangesDefault[i+6];  // lower  than lower bound
                if (ranges[i+1] < rangesDefault[i+6]) ranges[i+1] = rangesDefault[i+7];  // all lower  than lower bound?
                if (ranges[i  ] > rangesDefault[i+7]) ranges[i  ] = rangesDefault[i+6];  // all higher than upper bound?
                if (ranges[i+1] > rangesDefault[i+7]) ranges[i+1] = rangesDefault[i+7];  // higher than upper bound
                if (ranges[i    ] == ranges[i + 1] || Double.isNaN(ranges[i]) || Double.isNaN(ranges[i + 1])) {  // invalid input value
                    ranges[i    ] = rangesDefault[i];
                    ranges[i + 1] = rangesDefault[i + 1];
                }
            }
        }

        // Set range
        if (showMode == PlotMode.SPECTRUM) {

        } else if (showMode == PlotMode.SPECTROGRAM) {
            if (spectrogramPlot.getSpectrogramMode() == SpectrogramPlot.TimeAxisMode.SHIFT) {
                spectrogramPlot.axisTime.setViewBounds(ranges[5], ranges[4]);
            } else {
                spectrogramPlot.axisTime.setViewBounds(ranges[4], ranges[5]);
            }
            if (spectrogramPlot.showFreqAlongX) {
                spectrogramPlot.axisFreq.setViewBounds(ranges[0], ranges[1]);
            } else {
                spectrogramPlot.axisFreq.setViewBounds(ranges[1], ranges[0]);
            }
            spectrogramPlot.spectrogramBMP.updateAxis(spectrogramPlot.axisFreq);
        }

        // Set zoom shift for view
        if (showMode == PlotMode.SPECTRUM) {

        } else if (showMode == PlotMode.SPECTROGRAM) {
            if (spectrogramPlot.showFreqAlongX) {
                xZoom  = spectrogramPlot.axisFreq.getZoom();
                xShift = spectrogramPlot.axisFreq.getShift();
                yZoom  = spectrogramPlot.axisTime.getZoom();
                yShift = spectrogramPlot.axisTime.getShift();
            } else {
                yZoom  = spectrogramPlot.axisFreq.getZoom();
                yShift = spectrogramPlot.axisFreq.getShift();
                xZoom  = spectrogramPlot.axisTime.getZoom();
                xShift = spectrogramPlot.axisTime.getShift();
            }
        }

        return ranges;
    }

    public void updateAxisZoomShift() {
        if (showMode == PlotMode.SPECTRUM) {

        } else {
            spectrogramPlot.setZooms(xZoom, xShift, yZoom, yShift);
        }
    }

    public void setSmoothRender(boolean b) {
        spectrogramPlot.setSmoothRender(b);
    }

    public PlotMode getShowMode() {
        return showMode;
    }

    public void setShowTimeAxis(boolean bSTA) {
        spectrogramPlot.setShowTimeAxis(bSTA);
    }

    public void setSpectrogramModeShifting(boolean b) {
        spectrogramPlot.setSpectrogramModeShifting(b);
    }

    public void setColorMap(String colorMapName) {
        spectrogramPlot.setColorMap(colorMapName);
    }

    private final FPSCounter fpsCounter = new FPSCounter("AnalyzerGraphic");

    @Override
    protected void onDraw(Canvas c) {
        fpsCounter.inc();
        isBusy = true;
        if (showMode == PlotMode.SPECTRUM) {

        } else {
            spectrogramPlot.drawSpectrogramPlot(c);
        }
        isBusy = false;
    }

    // All FFT data will enter this view through this interface
    // Will be called in another thread (SamplingLoop)
    public void saveSpectrum(double[] db)
    {
        synchronized (savedDBSpectrum)
        {  // TODO: need lock on savedDBSpectrum, but how?
            if (savedDBSpectrum == null || savedDBSpectrum.length != db.length)
            {
                savedDBSpectrum = new double[db.length];
            }
            System.arraycopy(db, 0, savedDBSpectrum, 0, db.length);
        }
        // TODO: Run on another thread? Lock on data ? Or use CompletionService?
        if (showMode == PlotMode.SPECTROGRAM) {
            spectrogramPlot.saveRowSpectrumAsColor(savedDBSpectrum);
        }
    }

    public void setSpectrogramDBLowerBound(double b) {
        spectrogramPlot.setSpectrogramDBLowerBound(b);
    }

    private boolean intersects(float x, float y) {
        getLocationOnScreen(myLocation);
        return x >= myLocation[0] && y >= myLocation[1] &&
                x < myLocation[0] + getWidth() && y < myLocation[1] + getHeight();
    }

    // return true if the coordinate (x,y) is inside graphView
    public boolean setCursor(float x, float y) {
        if (intersects(x, y)) {
            x = x - myLocation[0];
            y = y - myLocation[1];
            // Convert to coordinate in axis
            if (showMode == PlotMode.SPECTRUM) {

            } else {
                spectrogramPlot.setCursor(x, y);
            }
            return true;
        } else {
            return false;
        }
    }

    public void hideCursor() {
        spectrogramPlot.hideCursor();
    }

    public double[] getViewPhysicalRange() {
        double[] r = new double[12];
        if (getShowMode() == AnalyzerGraphic.PlotMode.SPECTRUM) {

        } else {
            r[0] = spectrogramPlot.axisFreq.vMinInView();
            r[1] = spectrogramPlot.axisFreq.vMaxInView();
            if (r[0] > r[1]) { double t=r[0]; r[0]=r[1]; r[1]=t; }
            r[2] = spectrogramPlot.spectrogramBMP.dBLowerBound;
            r[3] = spectrogramPlot.spectrogramBMP.dBUpperBound;
            r[4] = spectrogramPlot.axisTime.vMinInView();
            r[5] = spectrogramPlot.axisTime.vMaxInView();

            r[6] = spectrogramPlot.axisFreq.vLowerBound;
            r[7] = spectrogramPlot.axisFreq.vUpperBound;
            if (r[6] > r[7]) { double t=r[6]; r[6]=r[7]; r[7]=t; }
            r[8] = AnalyzerGraphic.minDB;
            r[9] = AnalyzerGraphic.maxDB;
            r[10]= spectrogramPlot.axisTime.vLowerBound;
            r[11]= spectrogramPlot.axisTime.vUpperBound;
        }
        for (int i=6; i<r.length; i+=2) {
            if (r[i] > r[i+1]) {
                double t = r[i]; r[i] = r[i+1]; r[i+1] = t;
            }
        }
        return r;
    }

    public double getXZoom() {
        return xZoom;
    }

    public double getYZoom() {
        return yZoom;
    }

    public double getXShift() {
        return xShift;
    }

    public double getYShift() {
        return yShift;
    }

    public double getCanvasWidth() {
        if (showMode == PlotMode.SPECTRUM) {
            return canvasWidth;
        } else {
            return canvasWidth - spectrogramPlot.labelBeginX;
        }
    }

    public double getCanvasHeight() {
        if (showMode == PlotMode.SPECTRUM) {
            return canvasHeight;
        } else {
            return spectrogramPlot.labelBeginY;
        }
    }

    private double clamp(double x, double min, double max) {
        if (x > max) {
            return max;
        } else if (x < min) {
            return min;
        } else {
            return x;
        }
    }

    private double clampXShift(double offset) {
        return clamp(offset, 0f, 1 - 1 / xZoom);
    }

    private double clampYShift(double offset) {
        if (showMode == PlotMode.SPECTRUM) {

            return 0;
        } else {
            // strict restrict, y can be frequency or time.
            return clamp(offset, 0f, 1 - 1 / yZoom);
        }
    }

    public void setXShift(double offset) {
        xShift = clampXShift(offset);
        updateAxisZoomShift();
    }

    public void setYShift(double offset) {
        yShift = clampYShift(offset);
        updateAxisZoomShift();
    }

    private double xMidOld = 100;
    private double xZoomOld = 1;
    private double xShiftOld = 0;
    private double yMidOld = 100;
    private double yZoomOld = 1;
    private double yShiftOld = 0;

    // record the coordinate frame state when starting scaling
    public void setShiftScaleBegin(double x1, double y1, double x2, double y2) {
        xMidOld = (x1+x2)/2f;
        xZoomOld  = xZoom;
        xShiftOld = xShift;
        yMidOld = (y1+y2)/2f;
        yZoomOld  = yZoom;
        yShiftOld = yShift;
    }

    // Do the scaling according to the motion event getX() and getY() (getPointerCount()==2)
    public void setShiftScale(double x1, double y1, double x2, double y2) {

        xShift = clampXShift(xShiftOld + (xMidOld/xZoomOld - (x1+x2)/2f/xZoom) / canvasWidth);

        yShift = clampYShift(yShiftOld + (yMidOld/yZoomOld - (y1+y2)/2f/yZoom) / canvasHeight);

        updateAxisZoomShift();
    }

    public Ready readyCallback = null;      // callback to caller when rendering is complete

    public void setReady(Ready ready) {
        this.readyCallback = ready;
    }

    public interface Ready {
        void ready();
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        Log.i(TAG, "onSizeChanged(): canvas (" + oldw + "," + oldh + ") -> (" + w + "," + h + ")");
        isBusy = true;
        this.canvasWidth = w;
        this.canvasHeight = h;
        spectrogramPlot.setCanvas(w, h, null);
        if (h > 0 && readyCallback != null) {
            readyCallback.ready();
        }
        isBusy = false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.i(TAG, "onSaveInstanceState(): xShift = " + xShift + "  xZoom = " + xZoom + "  yShift = " + yShift + "  yZoom = " + yZoom);
        Parcelable parentState = super.onSaveInstanceState();
        SavedState state = new SavedState(parentState);

        state.freqAxisAlongX = spectrogramPlot.showFreqAlongX ? 1 : 0;

        state.cFreqSpam  = spectrogramPlot.cursorFreq;
        state.xZ  = xZoom;
        state.xS  = xShift;
        state.yZ  = yZoom;
        state.yS  = yShift;
        state.SpamFZ = spectrogramPlot.axisFreq.getZoom();
        state.SpamFS = spectrogramPlot.axisFreq.getShift();
        state.SpamTZ = spectrogramPlot.axisTime.getZoom();
        state.SpamTS = spectrogramPlot.axisTime.getShift();

        state.tmpS     = savedDBSpectrum;

        state.nFreq    = spectrogramPlot.nFreqPoints;
        state.nTime    = spectrogramPlot.nTimePoints;
        state.iTimePinter = spectrogramPlot.spectrogramBMP.spectrumStore.iTimePointer;

        final short[] tmpSC    = spectrogramPlot.spectrogramBMP.spectrumStore.dbShortArray;

        // Get byte[] representation of short[]
        // Note no ByteBuffer view of ShortBuffer, see
        //   http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4489356
        byte[] input = new byte[tmpSC.length * 2];
        for (int i = 0; i < tmpSC.length; i++) {
            input[2*i  ] = (byte)(tmpSC[i] & 0xff);
            input[2*i+1] = (byte)(tmpSC[i] >> 8);
        }

        // Save spectrumStore.dbShortArray to a file.
        File tmpSCPath = new File(context.getCacheDir(), "spectrogram_short.raw");
        try {
            OutputStream fout = new FileOutputStream(tmpSCPath);
            fout.write(input);
            fout.close();
        } catch (IOException e) {
            Log.w("SavedState:", "writeToParcel(): Fail to save state to file.");
        }

        return state;
    }

    // Will be called after setup(), during AnalyzerActivity.onCreate()?
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState s = (SavedState) state;
            super.onRestoreInstanceState(s.getSuperState());

            spectrogramPlot.showFreqAlongX = s.freqAxisAlongX == 1;

            spectrogramPlot.cursorFreq = s.cFreqSpam;
            xZoom  = s.xZ;
            xShift = s.xS;
            yZoom  = s.yZ;
            yShift = s.yS;
            spectrogramPlot.axisFreq.setZoomShift(s.SpamFZ, s.SpamFS);
            spectrogramPlot.axisTime.setZoomShift(s.SpamTZ, s.SpamTS);

            savedDBSpectrum = s.tmpS;

            spectrogramPlot.nFreqPoints = s.nFreq;
            spectrogramPlot.nTimePoints = s.nTime;

            final SpectrogramBMP sBMP = spectrogramPlot.spectrogramBMP;
            final SpectrogramBMP.SpectrumCompressStore sBMPS = sBMP.spectrumStore;
            sBMPS.nFreq = s.nFreq;  // prevent reinitialize of LogFreqSpectrogramBMP
            sBMPS.nTime = s.nTime;
            sBMPS.iTimePointer = s.iTimePinter;

            // Read temporary saved spectrogram
            byte[] input = new byte[(s.nFreq+1) * s.nTime * 2]; // length of spectrumStore.dbShortArray
            int bytesRead = -1;
            File tmpSCPath = new File(context.getCacheDir(), "spectrogram_short.raw");
            try {
                InputStream fin = new FileInputStream(tmpSCPath);
                bytesRead = fin.read(input);
                fin.close();
            } catch (IOException e) {
                Log.w("SavedState:", "writeToParcel(): Fail to save state to file.");
            }

            if (bytesRead != input.length) {  // fail to get saved spectrogram, have a new start
                sBMPS.nFreq = 0;
                sBMPS.nTime = 0;
                sBMPS.iTimePointer = 0;
            } else {  // we have data!
                short[] tmpSC = new short[input.length/2];
                for (int i = 0; i < tmpSC.length; i++) {
                    tmpSC[i] = (short)(input[2*i] + (input[2*i+1] << 8));
                }
                sBMPS.dbShortArray = tmpSC;
                sBMP.rebuildLinearBMP();
            }

            Log.i(TAG, "onRestoreInstanceState(): xShift = " + xShift + "  xZoom = " + xZoom + "  yShift = " + yShift + "  yZoom = " + yZoom);
        } else {
            Log.i(TAG, "onRestoreInstanceState(): not SavedState?!");
            super.onRestoreInstanceState(state);
        }
    }

    private static class SavedState extends View.BaseSavedState {
        int freqAxisAlongX;
        double cFreqSpum;
        double cFreqSpam;
        double cDb;
        double xZ;
        double xS;
        double yZ;
        double yS;
        double SpumXZ;
        double SpumXS;
        double SpumYZ;
        double SpumYS;
        double SpamFZ;
        double SpamFS;
        double SpamTZ;
        double SpamTS;

        double[] tmpS;

        int nFreq;
        int nTime;
        int iTimePinter;

        SavedState(Parcelable state) {
            super(state);
        }

        private SavedState(Parcel in) {
            super(in);
            freqAxisAlongX = in.readInt();
            cFreqSpum  = in.readDouble();
            cFreqSpam  = in.readDouble();
            cDb  = in.readDouble();
            xZ   = in.readDouble();
            xS   = in.readDouble();
            yZ   = in.readDouble();
            yS   = in.readDouble();
            SpumXZ = in.readDouble();
            SpumXS = in.readDouble();
            SpumYZ = in.readDouble();
            SpumYS = in.readDouble();
            SpamFZ = in.readDouble();
            SpamFS = in.readDouble();
            SpamTZ = in.readDouble();
            SpamTS = in.readDouble();

            tmpS = in.createDoubleArray();

            nFreq       = in.readInt();
            nTime       = in.readInt();
            iTimePinter = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(freqAxisAlongX);
            out.writeDouble(cFreqSpum);
            out.writeDouble(cFreqSpam);
            out.writeDouble(cDb);
            out.writeDouble(xZ);
            out.writeDouble(xS);
            out.writeDouble(yZ);
            out.writeDouble(yS);
            out.writeDouble(SpumXZ);
            out.writeDouble(SpumXS);
            out.writeDouble(SpumYZ);
            out.writeDouble(SpumYS);
            out.writeDouble(SpamFZ);
            out.writeDouble(SpamFS);
            out.writeDouble(SpamTZ);
            out.writeDouble(SpamTS);

            out.writeDoubleArray(tmpS);

            out.writeInt(nFreq);
            out.writeInt(nTime);
            out.writeInt(iTimePinter);
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

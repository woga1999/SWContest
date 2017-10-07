package com.example.woga1.navigation.SoundAnalyze;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.Arrays;

public class SpectrogramBMP {
    final static String TAG = "SpectrogramBMP";
    final static double PERCENT = 1.2;

    public double dBLowerBound = -120;
    public double dBUpperBound = 0.0;

    SpectrumCompressStore spectrumStore = new SpectrumCompressStore();
    private PlainLinearSpamBMP linBmp = new PlainLinearSpamBMP();

    private int bmpWidthDefault = 1000;
    private int bmpWidthMax = 2000;
    private int avgCount=0;
    private int gapCount = 0;
    private double gap;
    private double [] avg = new double[512];
    private double [] oldAvg;

    enum LogAxisPlotMode { REPLOT, SEGMENT }
    LogAxisPlotMode logAxisMode = LogAxisPlotMode.REPLOT;

    void init(int _nFreq, int _nTime, ScreenPhysicalMapping _axis)
    {
        synchronized (this) { spectrumStore.init(_nFreq, _nTime); }
        synchronized (this) { linBmp.init(_nFreq, _nTime); }
        avgCount = 0;
    }

    void rebuildLinearBMP() {  // For state restore
        linBmp.rebuild(spectrumStore);
    }

    void rebuildAllBMP()
    {
        if (spectrumStore.dbShortArray.length == 0) return;
        rebuildLinearBMP();
    }

    private int calBmpWidth(ScreenPhysicalMapping _axisFreq) {
        int tmpBmpWidth = (int) _axisFreq.nCanvasPixel;
        if (tmpBmpWidth <= 1) tmpBmpWidth = bmpWidthDefault;
        if (tmpBmpWidth > 2000) tmpBmpWidth = bmpWidthMax;
        return tmpBmpWidth;
    }

    void updateAxis(ScreenPhysicalMapping _axisFreq)
    {
        if (_axisFreq.mapType == ScreenPhysicalMapping.Type.LINEAR)
        {
            return;  // a linear axis, do not update
        }
    }

    void setColorMap(String colorMapName) {
        rebuildAllBMP();
    }

    // return value between 0 .. nLevel - 1
    private static int levelFromDB(double d, double lowerBound, double upperBound, int nLevel) {
        if (d >= upperBound) {
            return 0;
        }
        if (d <= lowerBound || Double.isInfinite(d) || Double.isNaN(d)) {
            return nLevel - 1;
        }
        return (int)(nLevel * (upperBound - d) / (upperBound - lowerBound));
    }

    void fill(double[] db)
    {
        synchronized (this)
        {
            spectrumStore.fill(db);
            linBmp.fill(db);
        }
    }

    void draw(Canvas c, ScreenPhysicalMapping.Type freqAxisType, SpectrogramPlot.TimeAxisMode showModeSpectrogram,
              Paint smoothBmpPaint, Paint cursorTimePaint)
    {

        int pt;
        int lineLen;

        synchronized (this)
        {
            linBmp.draw(c, showModeSpectrogram, smoothBmpPaint);
        }
        pt = linBmp.iTimePointer;
        lineLen = linBmp.nFreq;

        // new data line
        if (showModeSpectrogram == SpectrogramPlot.TimeAxisMode.OVERWRITE)
        {
            c.drawLine(0, pt, lineLen, pt, cursorTimePaint);
        }
    }

    // Save spectrum in a lower resolution short[] (0~32767) instead of double[]
    class SpectrumCompressStore {
        private final static String TAG = "SpectrumCompressStore:";
        int nFreq;
        int nTime;
        int iTimePointer;
        short[] dbShortArray = new short[0];  // java don't have unsigned short

        void init(int _nFreq, int _nTime) {
            // _nFreq == 2^n
            if (dbShortArray.length != (_nFreq + 1) * _nTime) {
                dbShortArray = new short[(_nFreq + 1) * _nTime];
            }
            if (nFreq != _nFreq || nTime != _nTime) {
                clear();
            }
            nFreq = _nFreq;
            nTime = _nTime;
        }

        void clear() {
            Arrays.fill(dbShortArray, (short) 32767);
            iTimePointer = 0;
        }

        void fill(double[] db)
        {
            if (db.length - 1 != nFreq) {
                Log.e(TAG, "fill(): WTF");
                return;
            }
            int p0 = (nFreq + 1) * iTimePointer;
            for (int i = 0; i <= nFreq; i++) {
                dbShortArray[p0 + i] = (short) levelFromDB(db[i], AnalyzerGraphic.minDB, AnalyzerGraphic.maxDB, 32768);
            }
            iTimePointer++;
            if (iTimePointer >= nTime) iTimePointer = 0;
        }
    }

    public class PlainLinearSpamBMP
    {
        private final static String TAG = "PlainLinearSpamBMP:";
        private int nFreq;
        private int nTime;

        int[] spectrogramColors = new int[0];  // int:ARGB, nFreqPoints columns, nTimePoints rows
        int[] spectrogramColorsShifting;       // temporarily of spectrogramColors for shifting mode

        int iTimePointer;          // pointer to the row to be filled (row major)

        public void init(int _nFreq, int _nTime)
        {
            boolean bNeedClean = nFreq != _nFreq;
            if (spectrogramColors.length != _nFreq * _nTime)
            {
                spectrogramColors = new int[_nFreq * _nTime];
                spectrogramColorsShifting = new int[_nFreq * _nTime];
                bNeedClean = true;
            }
            if (!bNeedClean && iTimePointer >= _nTime) {
                Log.w(TAG, "setupSpectrogram(): Should not happen!!");
                Log.i(TAG, "setupSpectrogram(): iTimePointer=" + iTimePointer + "  nFreqPoints=" + _nFreq + "  nTimePoints=" + _nTime);
                bNeedClean = true;
            }
            if (bNeedClean) {
                clear();
            }
            nFreq = _nFreq;
            nTime = _nTime;
        }

        public void clear()
        {
            Arrays.fill(spectrogramColors, 0);
            iTimePointer = 0;
        }

        int num = 0;
        double average = 0;
        double sum = 0;

        public void fill(double[] db)
        {
            if (db.length - 1 != nFreq)
            {
                Log.e(TAG, "fill(): WTF");
                return;
            }

            int pRef = iTimePointer * nFreq - 1;

            avgCount = 0;
            Arrays.fill(avg, 0);

            for (int i = 1; i < db.length; i++)
            {  // no DC term
                avg[avgCount] = db[i];

                sum += db[i];

                if(i!=db.length-1)
                {
                    avgCount++;
                }
                else
                {
                    avgCount = 0;
                    gapCount = 0;

                    if(oldAvg != null)
                    {
                        for(int j=0; j<avg.length; j++)
                        {
                            if(avg[j] > oldAvg[j] && avg[j] >= -80)
                            {
                                gap = (avg[j] - oldAvg[j]) / PERCENT;
                                if(gap < 0.1)  { gapCount++;}
                            }
                            else if(avg[j] < oldAvg[j] && avg[j] >= -80)
                            {
                                gap = (oldAvg[j]-avg[j]) / PERCENT;
                                if(gap < 0.1)  { gapCount++;}
                            }
                            else if(avg[j] == oldAvg[j])
                            {
                                gapCount++;
                            }
                        }

                        if(gapCount > 8)
                        {
                            // 신호
                            Log.e("ss", "=========================================================");
                            Log.e("ss", "" + gapCount);

//                            ((MainActivity)MainActivity.mContext).sendMessage("100 100.");
//                            NavigationActivity.entireView.setBackgroundColor(Color.parseColor("#80FF0000"));
//                            NavigationActivity.resetButton.callOnClick();
                        }
                    }
                    else
                    {
                        oldAvg = new double[512];
                        Arrays.fill(oldAvg, 0);
                    }

                    System.arraycopy(avg,0,oldAvg,0,avg.length);
                }
            }


            if(num < 1000){

                average = sum / db.length;
                Log.v("avg", num + " : " + average);
                sum = 0;
                average = 0;

                Log.v("Hz", num + " : " + db[0] + " " + db[10] + " " + db[20] + " " + db[30] + " " + db[40] + " "
                        + db[50] + " " + db[60] + " " + db[70] + " " + db[80] + " " + db[90] + " " + db[100] + " "
                        + db[110] + " " + db[120] + " " + db[130] + " " + db[140] + " " + db[150] + " " + db[160] + " "
                        + db[170] + " " + db[180] + " " + db[190] + " " + db[200] + " " + db[210] + " " + db[220] + " "
                        + db[230] + " " + db[240] + " " + db[250] + " " + db[260] + " " + db[270] + " " + db[280] + " "
                        + db[290] + " " + db[300] + " " + db[310] + " " + db[320] + " " + db[330] + " " + db[340] + " "
                        + db[350] + " " + db[360] + " " + db[370] + " " + db[380] + " " + db[390] + " " + db[400] + " "
                        + db[410] + " " + db[420] + " " + db[430] + " " + db[440] + " " + db[450] + " " + db[460] + " "
                        + db[470] + " " + db[480] + " " + db[490] + " " + db[500]);
            }
            num++;


            iTimePointer++;
            if (iTimePointer >= nTime) iTimePointer = 0;
        }

        double[] dbTmp = new double[0];

        void rebuild(SpectrumCompressStore dbLevelPic)
        {
            nFreq = dbLevelPic.nFreq;
            nTime = dbLevelPic.nTime;
            init(nFreq, nTime);  // reallocate

            if (dbTmp.length != nFreq + 1)
            {
                dbTmp = new double[nFreq + 1];
            }

            iTimePointer = 0;

            for (int k = 0; k < nTime; k++)
            {
                int p0 = (nFreq + 1) * k;
                for (int i = 0; i <= nFreq; i++)
                {  // See colorFromDBLevel
                    dbTmp[i] = AnalyzerGraphic.maxDB - (AnalyzerGraphic.maxDB - AnalyzerGraphic.minDB) / 32768.0 * dbLevelPic.dbShortArray[p0 + i];
                }
                fill(dbTmp);
            }
            iTimePointer = dbLevelPic.iTimePointer;
        }

        void draw(Canvas c, SpectrogramPlot.TimeAxisMode showModeSpectrogram, Paint smoothBmpPaint)
        {
            if (spectrogramColors.length == 0) return;

            if (showModeSpectrogram == SpectrogramPlot.TimeAxisMode.SHIFT)
            {
                System.arraycopy(spectrogramColors, 0, spectrogramColorsShifting, (nTime - iTimePointer) * nFreq, iTimePointer * nFreq);
                System.arraycopy(spectrogramColors, iTimePointer * nFreq, spectrogramColorsShifting, 0, (nTime - iTimePointer) * nFreq);

                c.drawBitmap(spectrogramColorsShifting, 0, nFreq, 0, 0,
                        nFreq, nTime, false, smoothBmpPaint);
            }
            else
            {
                c.drawBitmap(spectrogramColors, 0, nFreq, 0, 0,
                        nFreq, nTime, false, smoothBmpPaint);
            }
        }
    }
}

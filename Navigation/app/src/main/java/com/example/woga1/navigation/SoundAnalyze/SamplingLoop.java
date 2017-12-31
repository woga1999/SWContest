package com.example.woga1.navigation.SoundAnalyze;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.SystemClock;
import android.util.Log;

import com.example.woga1.navigation.Navigation.NavigationActivity;

import java.util.Arrays;

public class SamplingLoop extends Thread {
    private final String TAG = "SamplingLoop";
    private volatile boolean isRunning = true;
    private volatile boolean isPaused1 = false;
    private STFT stft;   // use with care
    private final AnalyzerParameters analyzerParam;

    private SineGenerator sineGen1;
    private SineGenerator sineGen2;
    private double[] spectrumDBcopy;   // XXX, transfers data from SamplingLoop to AnalyzerGraphic

    private final NavigationActivity activity;

    volatile double wavSecRemain;
    volatile double wavSec = 0;

    public SamplingLoop(NavigationActivity _activity, AnalyzerParameters _analyzerParam) {
        activity = _activity;
        analyzerParam = _analyzerParam;

        // Signal sources for testing
//        double fq0 = Double.parseDouble(activity.getString(R.string.test_signal_1_freq1));
//        double amp0 = Math.pow(10, 1/20.0 * Double.parseDouble(activity.getString(R.string.test_signal_1_db1)));
//        double fq1 = Double.parseDouble(activity.getString(R.string.test_signal_2_freq1));
//        double amp1 = Math.pow(10, 1/20.0 * Double.parseDouble(activity.getString(R.string.test_signal_2_db1)));
//        double fq2 = Double.parseDouble(activity.getString(R.string.test_signal_2_freq2));
//        double amp2 = Math.pow(10, 1/20.0 * Double.parseDouble(activity.getString(R.string.test_signal_2_db2)));
//        if (analyzerParam.audioSourceId == 1000) {
//            sineGen1 = new SineGenerator(fq0, analyzerParam.sampleRate, analyzerParam.SAMPLE_VALUE_MAX * amp0);
//        } else {
//            sineGen1 = new SineGenerator(fq1, analyzerParam.sampleRate, analyzerParam.SAMPLE_VALUE_MAX * amp1);
//        }
//        sineGen2 = new SineGenerator(fq2, analyzerParam.sampleRate, analyzerParam.SAMPLE_VALUE_MAX * amp2);
    }

    private void SleepWithoutInterrupt(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private double baseTimeMs = SystemClock.uptimeMillis();

    private void LimitFrameRate(double updateMs) {
        // Limit the frame rate by wait `delay' ms.
        baseTimeMs += updateMs;
        long delay = (int) (baseTimeMs - SystemClock.uptimeMillis());
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Log.i(TAG, "Sleep interrupted");  // seems never reached
            }
        } else {
            baseTimeMs -= delay;  // get current time
        }
    }

    private double[] mdata;

    // Generate test data.
    private int readTestData(short[] a, int offsetInShorts, int sizeInShorts, int id) {
        if (mdata == null || mdata.length != sizeInShorts) {
            mdata = new double[sizeInShorts];
        }
        Arrays.fill(mdata, 0.0);
        switch (id - 1000) {
            case 1:
                sineGen2.getSamples(mdata);
                // No break, so values of mdata added.
            case 0:
                sineGen1.addSamples(mdata);
                for (int i = 0; i < sizeInShorts; i++) {
                    a[offsetInShorts + i] = (short) Math.round(mdata[i]);
                }
                break;
            case 2:
                for (int i = 0; i < sizeInShorts; i++) {
                    a[i] = (short) (analyzerParam.SAMPLE_VALUE_MAX * (2.0*Math.random() - 1));
                }
                break;
            default:
                Log.w(TAG, "readTestData(): No this source id = " + analyzerParam.audioSourceId);
        }
        // Block this thread, so that behave as if read from real device.
        LimitFrameRate(1000.0*sizeInShorts / analyzerParam.sampleRate);
        return sizeInShorts;
    }

    @Override
    public void run() {
        AudioRecord record;

        long tStart = SystemClock.uptimeMillis();
        try {
            activity.graphInit.join();  // TODO: Seems not working as intended....
        } catch (InterruptedException e) {
            Log.w(TAG, "run(): activity.graphInit.join() failed.");
        }
        long tEnd = SystemClock.uptimeMillis();
        if (tEnd - tStart < 500) {
            Log.i(TAG, "wait more.." + (500 - (tEnd - tStart)) + " ms");
            // Wait until previous instance of AudioRecord fully released.
            SleepWithoutInterrupt(500 - (tEnd - tStart));
        }

        int minBytes = AudioRecord.getMinBufferSize(analyzerParam.sampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        if (minBytes == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "SamplingLoop::run(): Invalid AudioRecord parameter.\n");
            return;
        }

        // Determine size of buffers for AudioRecord and AudioRecord::read()
        int readChunkSize    = analyzerParam.hopLen;  // Every hopLen one fft result (overlapped analyze window)
        readChunkSize        = Math.min(readChunkSize, 2048);  // read in a smaller chunk, hopefully smaller delay
        int bufferSampleSize = Math.max(minBytes / analyzerParam.BYTE_OF_SAMPLE, analyzerParam.fftLen/2) * 2;
        bufferSampleSize = (int)Math.ceil(1.0 * analyzerParam.sampleRate / bufferSampleSize) * bufferSampleSize;

        // Use the mic with AGC turned off. e.g. VOICE_RECOGNITION for measurement
        // The buffer size here seems not relate to the delay.
        // So choose a larger size (~1sec) so that overrun is unlikely.
        try {
            if (analyzerParam.audioSourceId < 1000) {
                record = new AudioRecord(analyzerParam.audioSourceId, analyzerParam.sampleRate, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, analyzerParam.BYTE_OF_SAMPLE * bufferSampleSize);
            } else {
                record = new AudioRecord(analyzerParam.RECORDER_AGC_OFF, analyzerParam.sampleRate, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, analyzerParam.BYTE_OF_SAMPLE * bufferSampleSize);
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Fail to initialize recorder.");
            activity.analyzerViews.notifyToast("Illegal recorder argument. (change source)");
            return;
        }
        Log.i(TAG, "SamplingLoop::Run(): Starting recorder... \n" +
                "  source          : " + analyzerParam.getAudioSourceName() + "\n" +
                String.format("  sample rate     : %d Hz (request %d Hz)\n", record.getSampleRate(), analyzerParam.sampleRate) +
                String.format("  min buffer size : %d samples, %d Bytes\n", minBytes / analyzerParam.BYTE_OF_SAMPLE, minBytes) +
                String.format("  buffer size     : %d samples, %d Bytes\n", bufferSampleSize, analyzerParam.BYTE_OF_SAMPLE*bufferSampleSize) +
                String.format("  read chunk size : %d samples, %d Bytes\n", readChunkSize, analyzerParam.BYTE_OF_SAMPLE*readChunkSize) +
                String.format("  FFT length      : %d\n", analyzerParam.fftLen) +
                String.format("  nFFTAverage     : %d\n", analyzerParam.nFFTAverage));
        analyzerParam.sampleRate = record.getSampleRate();

        if (record.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "SamplingLoop::run(): Fail to initialize AudioRecord()");
            activity.analyzerViews.notifyToast("Fail to initialize recorder.");
            // If failed somehow, leave user a chance to change preference.
            return;
        }

        short[] audioSamples = new short[readChunkSize];
        int numOfReadShort;

        stft = new STFT(analyzerParam);
        stft.setAWeighting(analyzerParam.isAWeighting);
        if (spectrumDBcopy == null || spectrumDBcopy.length != analyzerParam.fftLen/2+1) {
            spectrumDBcopy = new double[analyzerParam.fftLen/2+1];
        }

        WavWriter wavWriter = new WavWriter(analyzerParam.sampleRate);
        boolean bSaveWavLoop = activity.bSaveWav;  // change of bSaveWav during loop will only affect next enter.
        if (bSaveWavLoop) {
            wavWriter.start();
            wavSecRemain = wavWriter.secondsLeft();
            wavSec = 0;
            Log.i(TAG, "PCM write to file " + wavWriter.getPath());
        }

        // Start recording
        try {
            record.startRecording();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Fail to start recording.");
            activity.analyzerViews.notifyToast("Fail to start recording.");
            return;
        }

        // Main loop
        // When running in this loop (including when paused), you can not change properties
        // related to recorder: e.g. audioSourceId, sampleRate, bufferSampleSize
        // TODO: allow change of FFT length on the fly.
        while (isRunning) {
            // Read data
            if (analyzerParam.audioSourceId >= 1000) {
                numOfReadShort = readTestData(audioSamples, 0, readChunkSize, analyzerParam.audioSourceId);
            } else {
                numOfReadShort = record.read(audioSamples, 0, readChunkSize);   // pulling
            }

            if (bSaveWavLoop) {
                wavWriter.pushAudioShort(audioSamples, numOfReadShort);  // Maybe move this to another thread?
                wavSec = wavWriter.secondsWritten();
                activity.analyzerViews.updateRec(wavSec);
            }
            if (isPaused1) {
                continue;
            }

            stft.feedData(audioSamples, numOfReadShort);

            // If there is new spectrum data, do plot
            if (stft.nElemSpectrumAmp() >= analyzerParam.nFFTAverage) {
                // Update spectrum or spectrogram
                final double[] spectrumDB = stft.getSpectrumAmpDB();
                System.arraycopy(spectrumDB, 0, spectrumDBcopy, 0, spectrumDB.length);
                activity.analyzerViews.update(spectrumDBcopy);

                stft.calculatePeak();
                activity.maxAmpFreq = stft.maxAmpFreq;
                activity.maxAmpDB = stft.maxAmpDB;

                // get RMS
                activity.dtRMS = stft.getRMS();
                activity.dtRMSFromFT = stft.getRMSFromFT();
            }
        }
        Log.i(TAG, "SamplingLoop::Run(): Stopping and releasing recorder.");
        record.stop();
        record.release();
        if (bSaveWavLoop) {
            Log.i(TAG, "SamplingLoop::Run(): Ending saved wav.");
            wavWriter.stop();
            activity.analyzerViews.notifyWAVSaved(wavWriter.relativeDir);
        }
    }

    public void finish() {
        isRunning = false;
        interrupt();
    }
}

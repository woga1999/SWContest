package com.example.woga1.navigation.SoundAnalyze;

import android.content.res.Resources;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.woga1.navigation.R;

public class AnalyzerParameters
{
    public final int RECORDER_AGC_OFF = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    public int audioSourceId = RECORDER_AGC_OFF;
    public int sampleRate = 16000;
    public int fftLen = 2048;
    public int hopLen = 1024;
    public double overlapPercent = 50;  // = (1 - hopLen/fftLen) * 100%
    public String wndFuncName;
    public int nFFTAverage = 2;
    boolean isAWeighting = false;
    final int BYTE_OF_SAMPLE = 2;
    final double SAMPLE_VALUE_MAX = 32767.0;   // Maximum signal value
    public double spectrogramDuration = 4.0;

    double[] micGainDB = null;  // should have fftLen/2 elements

    public AnalyzerParameters(Resources res) {
        getAudioSourceNameFromIdPrepare(res);
    }

    String[] audioSourceNames;
    int[] audioSourceIDs;
    private void getAudioSourceNameFromIdPrepare(Resources res) {
        audioSourceNames   = res.getStringArray(R.array.audio_source);
        String[] sasid = res.getStringArray(R.array.audio_source_id);
        audioSourceIDs = new int[audioSourceNames.length];
        for (int i = 0; i < audioSourceNames.length; i++) {
            audioSourceIDs[i] = Integer.parseInt(sasid[i]);
        }
    }

    // Get audio source name from its ID
    // Tell me if there is better way to do it.
    String getAudioSourceNameFromId(int id) {
        for (int i = 0; i < audioSourceNames.length; i++) {
            if (audioSourceIDs[i] == id) {
                return audioSourceNames[i];
            }
        }
        Log.i("AnalyzerParameters", "getAudioSourceName(): non-standard entry.");
        return ((Integer)(id)).toString();
    }

    String getAudioSourceName() {
        return getAudioSourceNameFromId(audioSourceId);
    }
}

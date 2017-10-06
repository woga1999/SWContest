package com.example.woga1.navigation.SoundAnalyze;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.woga1.navigation.R;

import static java.lang.Math.abs;
import static java.lang.Math.round;

class AnalyzerUtil {
    private static String TAG = "AnalyzerUtil";
    private static final String[] LP = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    static double freq2pitch(double f) {
        return 69 + 12 * Math.log(f/440.0)/Math.log(2);  // MIDI pitch
    }

    static double pitch2freq(double p) {
        return Math.pow(2, (p - 69)/12) * 440.0;
    }

    static void pitch2Note(StringBuilder a, double p, int prec_frac, boolean tightMode) {
        int pi = (int) round(p);
        int po = (int) Math.floor(pi/12.0);
        int pm = pi-po*12;
        a.append(LP[pm]);
        SBNumFormat.fillInInt(a, po-1);
        if (LP[pm].length() == 1 && !tightMode) {
            a.append(' ');
        }
        double cent = round(100 * (p - pi) * Math.pow(10, prec_frac)) * Math.pow(10, -prec_frac);
        if (!tightMode) {
            SBNumFormat.fillInNumFixedWidthSignedFirst(a, cent, 2, prec_frac);
        } else {
            if (cent != 0) {
                a.append(cent < 0 ? '-' : '+');
                SBNumFormat.fillInNumFixedWidthPositive(a, abs(cent), 2, prec_frac, '\0');
            }
        }
    }

    static boolean isAlmostInteger(double x) {
        double i = round(x);
        if (i == 0) {
            return abs(x) < 1.2e-7;  // 2^-23 = 1.1921e-07
        } else {
            return abs(x - i) / i < 1.2e-7;
        }
    }

    static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    final int[]    stdSourceId;  // how to make it final?
    final int[]    stdSourceApi;
    final String[] stdSourceName;
    final String[] stdAudioSourcePermission;
    AnalyzerUtil(Context context) {
        stdSourceId   = context.getResources().getIntArray(R.array.std_audio_source_id);
        stdSourceApi  = context.getResources().getIntArray(R.array.std_audio_source_api_level);
        stdSourceName            = context.getResources().getStringArray(R.array.std_audio_source_name);
        stdAudioSourcePermission = context.getResources().getStringArray(R.array.std_audio_source_permission);
    }

}

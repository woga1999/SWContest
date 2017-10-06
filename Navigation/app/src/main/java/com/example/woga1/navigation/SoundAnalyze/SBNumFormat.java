package com.example.woga1.navigation.SoundAnalyze;

import android.util.Log;

class SBNumFormat {
    private static final char charDigits[] = {'0','1','2','3','4','5','6','7','8','9'};

    // Invent wheel... so we can eliminate GC
    static void fillInNumFixedWidthPositive(StringBuilder sb, double d, int nInt, int nFrac, char padChar) {
        if (d<0) {
            for (int i = 0; i < nInt+nFrac+(nFrac>0?1:0); i++) {
                sb.append(padChar);
            }
            Log.w("SBNumFormat", "fillInNumFixedWidthPositive: negative number");
            return;
        }
        if (d >= Math.pow(10,nInt)) {
            sb.append("OFL");
            for (int i = 3; i < nInt+nFrac+(nFrac>0?1:0); i++) {
                sb.append(' ');
            }
            return;
        }
        if (Double.isNaN(d)) {
            sb.append("NaN");
            for (int i = 3; i < nInt+nFrac+(nFrac>0?1:0); i++) {
                sb.append(' ');
            }
            return;
        }
        while (nInt > 0) {
            nInt--;
            if (d < Math.pow(10,nInt) && nInt>0) {
                if (padChar != '\0') {
                    sb.append(padChar);
                }
            } else {
                sb.append(charDigits[(int)(d / Math.pow(10,nInt) % 10.0)]);
            }
        }
        if (nFrac > 0) {
            sb.append('.');
            for (int i = 1; i <= nFrac; i++) {
                sb.append(charDigits[(int)(d * Math.pow(10,i) % 10.0)]);
            }
        }
    }

    static void fillInNumFixedWidthPositive(StringBuilder sb, double d, int nInt, int nFrac) {
        fillInNumFixedWidthPositive(sb, d, nInt, nFrac, ' ');
    }

    static void fillInNumFixedFrac(StringBuilder sb, double d, int nInt, int nFrac) {
        if (d < 0) {
            sb.append('-');
            d = -d;
        }
        fillInNumFixedWidthPositive(sb, d, nInt, nFrac, '\0');
    }

    static void fillInNumFixedWidthSignedFirst(StringBuilder sb, double d, int nInt, int nFrac) {
        if (d < 0) {
            sb.append('-');
        } else {
            sb.append('+');
        }
        fillInNumFixedWidthPositive(sb, Math.abs(d), nInt, nFrac);
    }

    static void fillInInt(StringBuilder sb, int in) {
        if (in == 0) {
            sb.append('0');
            return;
        }
        if (in<0) {
            sb.append('-');
            in = -in;
        }
        int it = sb.length();
        while (in > 0) {
            sb.insert(it, in%10);
            in /= 10;
        }
    }
}

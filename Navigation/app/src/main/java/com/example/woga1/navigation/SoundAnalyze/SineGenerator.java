package com.example.woga1.navigation.SoundAnalyze;

class SineGenerator {
    private double fs; 	// sampling frequency
    private double k;	// recursion constant
    private double n0, n1;	// first (next) 2 samples

    SineGenerator(double f, double fs, double a) {
        this.fs = fs;
        double w = 2.0 * Math.PI * f / fs;
        this.n0 = 0d;
        this.n1 = a * Math.cos(w + Math.PI/2.0);
        this.k  = 2.0 * Math.cos(w);
    }

    public void setF(double f) {
        double w = 2.0 * Math.PI * f / fs;
        k =  getK(f);

        double theta = Math.acos(n0);
        if (n1 > n0) theta = 2 * Math.PI - theta;
        n0 = Math.cos(theta);
        n1 = Math.cos(w + theta);
    }

    private double getK(double f) {
        double w = 2.0 * Math.PI * f / fs;
        return  2.0 * Math.cos(w);
    }

    private void getSamples(double[] samples, int start, int count) {
        for(int cnt = start; cnt < count; cnt += 2) {
            samples[cnt] = n0 = (k * n1) - n0;
            samples[cnt + 1] = n1 = (k * n0) - n1;
        }
    }

    void getSamples(double[] samples) {
        getSamples(samples, 0, samples.length);
    }

    private void addSamples(double[] samples, int start, int count) {
        for(int cnt=start; cnt<count; cnt+=2) {
            samples[cnt] += n0 = (k * n1) - n0;
            samples[cnt + 1] += n1 = (k * n0) - n1;
        }
    }

    void addSamples(double[] samples) {
        addSamples(samples, 0, samples.length);
    }

    public double getFs() {
        return fs;
    }
}
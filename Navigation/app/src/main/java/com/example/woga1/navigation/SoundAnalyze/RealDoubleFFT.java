package com.example.woga1.navigation.SoundAnalyze;

public class RealDoubleFFT extends RealDoubleFFT_Mixed {
    /**
     * <em>norm_factor</em> can be used to normalize this FFT transform. This is because
     * a call of forward transform (<em>ft</em>) followed by a call of backward transform
     * (<em>bt</em>) will multiply the input sequence by <em>norm_factor</em>.
     */
    public double norm_factor;
    private double wavetable[];
    private double[] ch;	// reusable work array
    private int ndim;

    /**
     * Construct a wavenumber table with size <em>n</em>.
     * The sequences with the same size can share a wavenumber table. The prime
     * factorization of <em>n</em> together with a tabulation of the trigonometric functions
     * are computed and stored.
     *
     * @param  n  the size of a real data sequence. When <em>n</em> is a multiplication of small
     * numbers (4, 2, 3, 5), this FFT transform is very efficient.
     */
    public RealDoubleFFT(int n)
    {
        ndim = n;
        norm_factor = n;
        if(wavetable == null || wavetable.length !=(2*ndim+15)) {
            wavetable = new double[2*ndim + 15];
        }
        rffti(ndim, wavetable);
        ch = new double[n];
    }

    /**
     * Forward real FFT transform. It computes the discrete transform of a real data sequence.
     *
     * @param x an array which contains the sequence to be transformed. After FFT,
     * <em>x</em> contains the transform coeffients used to construct <em>n</em> complex FFT coeffients.
     * <br>
     * The real part of the first complex FFT coeffients is <em>x</em>[0]; its imaginary part
     * is 0. If <em>n</em> is even set <em>m</em> = <em>n</em>/2, if <em>n</em> is odd set
     * <em>m</em> = <em>n</em>/2, then for
     * <br>
     * <em>k</em> = 1, ..., <em>m</em>-1 <br>
     * the real part of <em>k</em>-th complex FFT coeffients is <em>x</em>[2*<em>k</em>-1];
     * <br>
     * the imaginary part of <em>k</em>-th complex FFT coeffients is <em>x</em>[2*<em>k</em>-2].
     * <br>
     * If <em>n</em> is even,
     * the real of part of (<em>n</em>/2)-th complex FFT coeffients is <em>x</em>[<em>n</em>]; its imaginary part is 0.
     * The remaining complex FFT coeffients can be obtained by the symmetry relation:
     * the (<em>n</em>-<em>k</em>)-th complex FFT coeffient is the conjugate of <em>n</em>-th complex FFT coeffient.
     *
     */
    public void ft(double x[]) {
        if(x.length != ndim)
            throw new IllegalArgumentException("The length of data can not match that of the wavetable");
        rfftf(ndim, x, wavetable, ch);
    }
}

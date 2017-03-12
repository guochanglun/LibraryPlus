package com.gcl.library.bean;

/**
 * Created by gcl on 2017/3/5.
 */

public class Rating {
    private int max;
    private int numRates;
    private String average;
    private int min;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getNumRates() {
        return numRates;
    }

    public void setNumRates(int numRates) {
        this.numRates = numRates;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "max=" + max +
                ", numRates=" + numRates +
                ", average='" + average + '\'' +
                ", min=" + min +
                '}';
    }
}

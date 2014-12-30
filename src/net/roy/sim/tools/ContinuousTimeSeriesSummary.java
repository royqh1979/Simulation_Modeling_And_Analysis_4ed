package net.roy.sim.tools;

/**
 * Helper class for computing summary statistics of samples of a continuous time series variable,
 * which are added using the addValue method. The data values are not stored in memory, so this class
 * can be used to compute statistics for very large data streams.
 * Created by Roy on 2014/12/30.
 */
public class ContinuousTimeSeriesSummary {
    private long count;
    private double sum;
    private double max;
    private double min;
    private double integration;
    private double startTime;
    private double lastTime;
    private String name;

    /**
     * constructor
     * @param name name of the time series
     * @param time start time of the time series
     */
    public ContinuousTimeSeriesSummary(String name, double time) {
        this.name=name;
        this.startTime=time;
        reset();
    }

    /**
     * reset statistics for recalculation
     */
    public void reset() {
        count=0;
        sum=0;
        max=Long.MIN_VALUE;
        min=Long.MAX_VALUE;
        lastTime =startTime;
        integration =0;
    }

    /**
     * add a sample
     * note: samples must be added in time ascend order
     * @param value value of the sample
     * @param time time of the sample
     */
    public void addValue(double value,double time) {
        double timeInteval=time- lastTime;
        count++;
        sum+=value;
        integration +=timeInteval*value;
        if (value>max) {
            max=value;
        }
        if (value<min) {
            min=value;
        }
        lastTime =time;
    }

    /**
     * get total number of the samples
     * @return  count of the samples
     */
    public long getCount() {
        return count;
    }

    /**
     * get sum of the samples
     * @return sum of the samples
     */
    public double getSum() {
        return sum;
    }

    /**
     * get maximum value of the samples;
     * @return  maximum value
     */
    public double getMax() {
        return (count>0)?max:0;
    }

    /**
     * get minimum value of the samples
     * @return minimum value
     */
    public double getMin() {
        return (count>0)?min:0;
    }

    /**
     * get mean of the samples
     * @return mean of the samples
     */
    public double getMean() {
        return (count>0)?(sum/count):0;
    }

    /**
     * get time average of the time series
     * @return mean of the samples
     */
    public double getTimeAverage() {
        return (count>0)?(integration /lastTime):0;
    }

    /**
     * get the time integration of the time series
     * @return the time integration
     */
    public double getTimeIntegration() {
        return integration;
    }

    /**
     * get the time series' name
     * @return name of the variable
     */
    public String getName() {
        return name;
    }
}

package net.roy.sim.tools;

/**
 * Helper class for computing summary statistics of samples of a continuous variable, which are
 * added using the addValue method. The data values are not stored in memory, so this class
 * can be used to compute statistics for very large data streams.
 * Created by Roy on 2014/12/30.
 */
public class ContinuousVariableSummary {
    private long count;
    private double sum;
    private double max;
    private double min;
    private String name;

    /**
     * Constructor
     * @param name the discrete varaible's name
     */
    public ContinuousVariableSummary(String name) {
        this.name = name;
        reset();
    }

    /**
     * reset statistics for recalculation
     */
    public void reset() {
        count = 0;
        sum = 0;
        max = Long.MIN_VALUE;
        min = Long.MAX_VALUE;
    }

    /**
     * add a sample value
     * @param value  the sample value
     */
    public void addValue(double value) {
        count++;
        sum += value;
        if (value > max) {
            max = value;
        }
        if (value < min) {
            min = value;
        }
    }

    /**
     * get total number of the samples
     * @return  count of the samples
     */
    public double getCount() {
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
        return (count > 0) ? max : 0;
    }

    /**
     * get minimum value of the samples
     * @return minimum value
     */
    public double getMin() {
        return (count > 0) ? min : 0;
    }

    /**
     * get mean of the samples
     * @return mean of the samples
     */
    public double getMean() {
        return (count > 0) ? (sum / count) : 0;
    }

    /**
     * get variable's name
     * @return name of the variable
     */
    public String getName() {
        return name;
    }
}

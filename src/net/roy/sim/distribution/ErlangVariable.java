package net.roy.sim.distribution;

import org.apache.commons.math3.random.RandomDataGenerator;

/**
 * Created by Roy on 2015/1/7.
 */
public class ErlangVariable {
    int numOfVariables;
    double mean;
    double eachMean;
    RandomDataGenerator randomDataGenerator=new RandomDataGenerator();

    public ErlangVariable(int numOfVariables, double mean) {
        this.numOfVariables = numOfVariables;
        this.mean = mean;
        eachMean=mean/numOfVariables;
    }

    public int getNumOfVariables() {
        return numOfVariables;
    }

    public double getMean() {
        return mean;
    }

    public double nextValue() {
        double value=0;
        for (int i=0;i<numOfVariables;i++) {
            value+=randomDataGenerator.nextExponential(eachMean);
        }
        return value;
    }
}

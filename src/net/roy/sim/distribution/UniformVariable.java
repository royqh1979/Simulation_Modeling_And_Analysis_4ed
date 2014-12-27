package net.roy.sim.distribution;

import org.apache.commons.math3.random.RandomDataGenerator;

/**
 * Created by Roy on 2014/12/27.
 */
public class UniformVariable implements IContiuousRandomVariable {
    private double max;
    private double min;
    private RandomDataGenerator randomDataGenerator=new RandomDataGenerator();

    public UniformVariable(double max, double min) {
        this.max = max;
        this.min = min;
    }

    @Override
    public double nextValue() {
        return randomDataGenerator.nextUniform(min,max);
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }
}

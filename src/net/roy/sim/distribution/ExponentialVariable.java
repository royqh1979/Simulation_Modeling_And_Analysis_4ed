package net.roy.sim.distribution;

import org.apache.commons.math3.random.RandomDataGenerator;

/**
 * Created by Roy on 2014/12/27.
 */
public class ExponentialVariable implements IContiuousRandomVariable {
    private RandomDataGenerator generator=new RandomDataGenerator();
    private double mean;
    public ExponentialVariable(double mean) {
         this.mean=mean;
    }
    @Override
    public double nextValue() {
        return generator.nextExponential(mean);
    }

    public double getMean() {
        return mean;
    }
}

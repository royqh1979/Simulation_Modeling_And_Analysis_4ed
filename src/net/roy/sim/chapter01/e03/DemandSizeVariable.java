package net.roy.sim.chapter01.e03;

import net.roy.sim.distribution.IDiscreteRandomVariable;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;

/**
 * Created by Roy on 2014/12/27.
 */
public class DemandSizeVariable implements IDiscreteRandomVariable {
    private ArrayList<SizeProbability> sizes =new ArrayList<>();
    private RandomDataGenerator generator=new RandomDataGenerator();

    public void addSize(int size, double probability) {
        SizeProbability s=new SizeProbability(size,probability);
        Collections.sort(sizes);
    }
    @Override
    public int nextValue() {
        double r=generator.nextUniform(0,1);
        for (SizeProbability s: sizes) {
            if (r<=s.probability) {
                return s.getSize();
            }
        }
        return 0;
    }

    public void output() {
        for(SizeProbability s:sizes) {
            System.out.print(String.format("%12d",s.getSize()));
        }
        System.out.println();
        for(SizeProbability s:sizes) {
            System.out.print(String.format("%8.3fd",s.getProbability()));
        }
        System.out.println();
    }

    private class SizeProbability implements Comparable{
        private int size;
        private double probability;
        public SizeProbability(int size, double probability) {
            this.size=size;
            this.probability=probability;
        }

        public int getSize() {
            return size;
        }

        public double getProbability() {
            return probability;
        }

        @Override
        public int compareTo(Object o) {
            SizeProbability that=(SizeProbability)o;
            return Double.compare(this.probability,that.probability);
        }
    }
}

package net.roy.sim.chapter02.e05;

import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roy on 2015/1/6.
 */
public class JobGenerator {
    private RandomDataGenerator randomDataGenerator;
    private List<JobProbability> jobs;
    private double accumProb;
    public JobGenerator() {
        randomDataGenerator=new RandomDataGenerator();
        jobs=new ArrayList<>();
        accumProb=0;
    }

    public void addJob(double prob, JobType jobType){
        accumProb+=prob;
        jobs.add(new JobProbability(accumProb,jobType));
    }

    public JobType nextJobType() {
        double prob=randomDataGenerator.nextUniform(0,1);
        for (JobProbability j:jobs) {
            if (prob<=j.probability)
                return j.jobType;
        }
        throw new RuntimeException(String.format("not found job %.3f - %.3f", prob, accumProb));
    }

    private class JobProbability{
        double probability;
        JobType jobType;

        public JobProbability(double probability, JobType jobType) {
            this.probability = probability;
            this.jobType = jobType;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        jobs.stream().forEach(
                jp->sb.append(jp.probability+" "+jp.jobType.getName()+" ")
        );
        return sb.toString();
    }
}

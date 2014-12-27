package net.roy.sim.distribution;

/**
 * 连续随机变量接口
 * Created by Roy on 2014/12/27.
 */
public interface IContiuousRandomVariable {
    /**
     * 产生下一个随机数
     * @return 下一个随机数
     */
    double nextValue();
}

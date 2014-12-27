package net.roy.sim.distribution;

/**
 * 离散随机变量接口
 * Created by Roy on 2014/12/27.
 */
public interface IDiscreteRandomVariable {
    /**
     * 产生下一个随机数
     * @return
     */
    int nextValue();
}

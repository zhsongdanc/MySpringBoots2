package org.example.diy;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/10/8 17:32
 */
public class PoolFactory {

    public static DiyPool getDiyPool(int capacity) {
        return new DiyPool(capacity);
    }
}

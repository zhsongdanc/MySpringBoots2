package org.example.diy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * @Author: demussong
 * @Description:
 *  todo 评价： 1. 使用了List，能否保证线程安全，不太确定
 *        2. newInstance方法添加对象写的不太好
 *        3. 使用Closeable接口可能不太合适
 *        4. 必须存储MyObj类型，不通用
 *        5. 代码实现过于简单
 * @Date: 2023/10/8 17:30
 */
public class DiyPool {

    private int capacity;
    private List<MyObj> pool;


    protected DiyPool(int capacity) {
        this.capacity = capacity;
        this.pool = new ArrayList<>(capacity);
    }


    // 这里要保证线程安全
    public synchronized MyObj newInstance() {
        for (int i = 0; i < capacity; i++) {
            if (i >= pool.size()) {
                MyObj obj = new MyObj();
                pool.add(obj);
                return obj;
            } else if (pool.get(i).isClosed()) {
                pool.get(i).setClosed(false);
                return pool.get(i);
            }


        }

        // exceed pool size
        return new MyObj();
    }





    public static void main(String[] args) {
        DiyPool diyPool = PoolFactory.getDiyPool(5);
        try (MyObj obj = diyPool.newInstance()) {
            System.out.println(obj.hashCode());
        }catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
}

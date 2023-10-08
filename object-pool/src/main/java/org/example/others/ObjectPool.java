package org.example.others;

import java.util.Enumeration;
import java.util.Objects;
import java.util.Vector;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/10/8 19:20
 */
public abstract class ObjectPool<T> {

    private int initCapacity;
    private int maxCapacity;

    private static int INCREMENT_SIZE = 1;

    private Vector<PooledObject<T>> objects;

    synchronized void createPool(int initCapacity, int maxCapacity){
        this.initCapacity = initCapacity;
        this.maxCapacity = maxCapacity;

        // 保证只创建一次，双重校验用了volatile是因为要return，这里将成员属性返回，好像不会出现类似问题
        if (objects == null) {
            objects = new Vector<>();
            for (int i = 0; i < initCapacity; i++) {
                objects.add(create());
            }
        }
    }

    // todo 需要线程安全
    // todo 作为使用者，更希望获取的直接可用的对象
    synchronized T getObject(){

        T freeObject = getFreeObject();
        while (freeObject == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            freeObject = getFreeObject();
        }
        return freeObject;
    }


    T getFreeObject(){
        T freeObject = findFreeObject();
        if (freeObject == null) {
            createObjects(INCREMENT_SIZE);
            T freeObject1 = findFreeObject();
            if (freeObject1 == null) {
                return null;
            }
        }


        return freeObject;
    }

    /**
     *
     * @return null if no free object
     */
    T findFreeObject() {
        Enumeration<PooledObject<T>> elements = objects.elements();
        while (elements.hasMoreElements()) {
            PooledObject<T> pooledObject = elements.nextElement();
            if (!pooledObject.isBusy()) {
                pooledObject.setBusy(true);
                return pooledObject.getObject();
            }
        }

        return null;
    }



    void createObjects(int increment){
        if (increment + objects.size() > maxCapacity) {
//            throw new IllegalArgumentException("exceed maxSize");
            return;
        }
        for (int i = 0; i < increment; i++) {
            objects.add(create());

        }
    }

    abstract PooledObject<T> create();

    void returnObject(T obj){
        //
        for (PooledObject<T> object : objects) {
            if (Objects.equals(obj, object.getObject())) {
                object.setBusy(false);
            }
        }
    }

    // todo difference between two methods
    void closeObjectPool(){
        for (PooledObject<T> object : objects) {
            object = null;
        }


        Enumeration<PooledObject<T>> elements = objects.elements();
        while (elements.hasMoreElements()) {
            PooledObject<T> pooledObject = elements.nextElement();
            pooledObject = null;
        }
        objects = null;
    }

    public static void main(String[] args) {
        StringObjectPool stringObjectPool = new StringObjectPool();
        stringObjectPool.createPool(3,5);
        for (int i = 0; i < 4; i++) {
            String object = stringObjectPool.getObject();
            System.out.println(object);
        }
    }
}


class StringObjectPool extends ObjectPool<String> {

    @Override
    PooledObject<String> create() {
        PooledObject<String> pooledObject = new PooledObject<>();
        pooledObject.setObject("demus");
        return pooledObject;
    }
}

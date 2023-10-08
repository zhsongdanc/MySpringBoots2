package org.example.others;

/*
 * @Author: demussong
 * @Description: ref: https://www.jianshu.com/p/38c5bccf892f
 * @Date: 2023/10/8 19:13
 */
public class PooledObject<T> {



    private T object;
    private boolean busy;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}

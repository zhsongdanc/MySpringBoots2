package org.example.diy;

import java.io.Closeable;
import java.io.IOException;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/10/8 17:38
 */
public class MyObj implements Closeable {

    // 一个线程用完后关闭，其他线程可能读取
    private volatile boolean  closed = true;

    public MyObj() {
        this.closed = false;
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}

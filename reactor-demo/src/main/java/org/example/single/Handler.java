package org.example.single;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/10/7 10:49
 */
public class Handler implements Runnable {

    Selector selector;
    SocketChannel socket;
    SelectionKey sk;

    ByteBuffer input = ByteBuffer.allocate(1024);
    ByteBuffer output = ByteBuffer.allocate(1024);
    static final int READING = 0, SENDING = 1;
    int state = READING;


    public Handler(Selector selector, SocketChannel socket) throws IOException {
        this.selector = selector;
        this.socket = socket;

        this.socket.configureBlocking(false);
        sk = this.socket.register(selector,0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
//        selector.wakeup();
    }

    @Override
    public void run() {
        try{
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void read() throws IOException {
        socket.read(input);
        if (inputIsComplete()) {
            // 执行业务逻辑代码
            process();
            state = SENDING;
            // Normally also do first write now
            sk.interestOps(SelectionKey.OP_WRITE);
        }
    }

    private void send() throws IOException {
        socket.write(output);
        socket.close();
        if (outputIsComplete()) sk.cancel();
    }

    boolean inputIsComplete() { return true;}

    boolean outputIsComplete() {return true;}
    // 处理非IO操作(业务逻辑代码)
    void process(){
        String msg = new String(input.array());
        System.out.println("读取内容：" + msg);
        output.put(msg.getBytes());
        output.flip();
    }
}

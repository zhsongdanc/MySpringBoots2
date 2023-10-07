package org.example.multithread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * @Author: demussong
 * @Description: 但是所有IO操作都在Reactor单线程中完成的。在高负载、高并发场景下，也会成为瓶颈
 *  因此，主从reactor模型中，主reactor只复制网络连接，每个从reactor负责自己的读写
 * @Date: 2023/10/7 10:55
 */
public class Reactor implements Runnable {

    int port;
    Selector selector;
    ServerSocketChannel serverSocket;


    public Reactor(int port) throws IOException {
        this.port = port;

        // 创建serverSocket对象
        serverSocket = ServerSocketChannel.open();
        // 绑定端口
        serverSocket.socket().bind(new InetSocketAddress(port));
        // 配置非阻塞
        serverSocket.configureBlocking(false);

        // 创建selector对象
        selector = Selector.open();
        // serversocket注册到selector上，帮忙监听accpet事件
        serverSocket.register(selector, SelectionKey.OP_ACCEPT, new Acceptor("Acceptor",serverSocket,selector));

        /** 还可以使用 SPI provider，来创建selector和serversocket对象
         SelectorProvider p = SelectorProvider.provider();
         selector = p.openSelector();
         serverSocket = p.openServerSocketChannel();
         */
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println("start select event...");
                selector.select();
                Set selectedKeys = selector.selectedKeys();
                Iterator it = selectedKeys.iterator();
                while (it.hasNext()) {
                    dispatch((SelectionKey)it.next());
                }
                selectedKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey key) {
        SelfRunable r = (SelfRunable) key.attachment();
        if (r != null) {
            System.out.println("dispatch to " + r.getName() + "====");
            r.run();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        Thread thread = new Thread(new Reactor(2021));
        thread.start();

        synchronized (Reactor.class) {
            Reactor.class.wait();
        }


    }
}

 class Acceptor implements SelfRunable {
    ServerSocketChannel serverSocket;
    Selector selector;
    String name;
    public Acceptor(String name, ServerSocketChannel serverSocket,Selector selector) {
        this.name = name;
        this.serverSocket = serverSocket;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            SocketChannel socket = this.serverSocket.accept();
            if (socket != null) {
                new Handler("handler_" + ((InetSocketAddress)socket.getLocalAddress()).getPort(), selector,socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }
}

 interface SelfRunable extends Runnable {
    public String getName();
 }

 class Handler implements SelfRunable {
    String name;
    Selector selector;
    SocketChannel socket;
    SelectionKey sk;
    ByteBuffer input = ByteBuffer.allocate(1024);
    ByteBuffer output = ByteBuffer.allocate(1024);
    static final int READING = 0, SENDING = 1,  PROCESSING = 3;
    volatile int state = READING;
    static ExecutorService poolExecutor = Executors.newFixedThreadPool(5);

    public Handler(String name, Selector selector, SocketChannel socket) throws IOException {
        this.selector = selector;
        this.socket = socket;
        this.name = name;

        this.socket.configureBlocking(false);
        sk = this.socket.register(selector,0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        try{
            System.out.println("state:" + state);
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    synchronized void read() throws IOException {
        socket.read(input);
        if (inputIsComplete()) {
            state = PROCESSING;
            poolExecutor.execute(new Processer());
        }
    }

    synchronized void processAndHandOff() {
        System.out.println("processAndHandOff=========");
        process();
        state = SENDING; // or rebind attachment
        sk.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
        System.out.println("processAndHandOff finish ! =========");
    }

    private void send() throws IOException {
        System.out.println("start send ...");
        socket.write(output);
        socket.close();
        System.out.println("start send finish!");
        if (outputIsComplete()) sk.cancel();
    }

    boolean inputIsComplete() { return true;}

    boolean outputIsComplete() {return true;}

    void process(){
        String msg = new String(input.array());
        System.out.println("读取内容：" + msg);
        output.put(msg.getBytes());
        output.flip();
    }

    @Override
    public String getName() {
        return this.name;
    }

    class Processer implements Runnable {
        public void run() { processAndHandOff(); }
    }
}

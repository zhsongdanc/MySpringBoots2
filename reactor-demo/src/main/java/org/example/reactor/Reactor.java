package org.example.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/10/7 11:00
 */
public class Reactor implements Runnable {

    int port;
    Selector selector;
    ServerSocketChannel serverSocket;
    int SUBREACTOR_SIZE = 1;
    SubReactor[] subReactorPool = new SubReactor[SUBREACTOR_SIZE];


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
        serverSocket.register(selector, SelectionKey.OP_ACCEPT, new Acceptor("Acceptor",serverSocket,subReactorPool));

        // 初始化subreactor pool
        initSubReactorPool();


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
                System.out.println("mainReactor start select event...");
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

    void initSubReactorPool() {
        try {
            for (int i = 0; i < SUBREACTOR_SIZE; i++) {
                subReactorPool[i] = new SubReactor("SubReactor" + i);
            }
        } catch (IOException ex) { /* ... */ }
    }

    private void dispatch(SelectionKey key) {
        SelfRunable r = (SelfRunable) key.attachment();
        if (r != null) {
            System.out.println("mainReactor dispatch to " + r.getName() + "====");
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

interface SelfRunable extends Runnable {
    public String getName();
}

 class SubReactor implements SelfRunable {

    private Selector selector;
    private String name;
    private List<SelfRunable> task = new ArrayList<SelfRunable>();

    public SubReactor(String name) throws IOException {
        this.name = name;
        selector = Selector.open();
        new Thread(this).start();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println("subReactor start select event...");
                selector.select(5000);
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
            System.out.println("subReactor dispatch to " + r.getName() + "====");
            r.run();
        }
    }

    public Selector getSelector(){
        return this.selector;
    }

    public void submit(SelfRunable runnable) {
        task.add(runnable);
    }

}

 class Acceptor implements SelfRunable {

    int next = 0;
    String name;
    SubReactor[] subReactorPool;
    ServerSocketChannel serverSocket;

    public Acceptor(String name, ServerSocketChannel serverSocket,SubReactor[] subReactorPool) {
        this.name = name;
        this.serverSocket = serverSocket;
        this.subReactorPool = subReactorPool;
    }

    @Override
    public void run() {
        try {
            SocketChannel socket = this.serverSocket.accept();
            if (socket != null) {
                new Handler("handler", subReactorPool[next].getSelector(),socket);
            }
            if (++next == subReactorPool.length) {next=0;}

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }
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
        sk = this.socket.register(this.selector,0);
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

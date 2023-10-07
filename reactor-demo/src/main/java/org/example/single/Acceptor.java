package org.example.single;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/10/7 10:49
 */
// acceptor调度器
public class Acceptor implements Runnable {

    ServerSocketChannel serverSocket;
    Selector selector;

    public Acceptor(ServerSocketChannel serverSocket,Selector selector) {
        this.serverSocket = serverSocket;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            SocketChannel socket = this.serverSocket.accept();
            if (socket != null) {
                new Handler(selector,socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

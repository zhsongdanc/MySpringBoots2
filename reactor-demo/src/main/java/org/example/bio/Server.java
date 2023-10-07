package org.example.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * @Author: demussong
 * @Description:
 * @Date: 2023/10/7 13:41
 */
public class Server implements Runnable {

    int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                System.out.println("等待新连接...");
                new Thread(new Handler(serverSocket.accept())).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Handler implements Runnable{

        private Socket socket;

        public Handler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                byte[] input = new byte[1024];

                this.socket.getInputStream().read(input);
                byte[] output = process(input);
                this.socket.getOutputStream().write(output);
                this.socket.getOutputStream().flush();
                this.socket.close();
                System.out.println("响应完成！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private byte[] process(byte[] input) {
            System.out.println("读取内容：" + new String(input));
            return input;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Server(2021));
        thread.setDaemon(true);
        thread.start();

        synchronized (Server.class) {
            Server.class.wait();
        }
    }
}

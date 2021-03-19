package com.example.tcpclient;

import com.example.tcpclient.Message;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

// reads and writes messages on socket
public class Session {
    private final Thread threadRead;
    private final Socket socket;
    private ConcurrentLinkedQueue<Message> messageQueue;

    private boolean debug = true;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private boolean isRunning;

    private boolean isObserver;

    public Session(Socket socket) {
        this.socket = socket;

        threadRead = new Thread(this::readFromSocket);
        messageQueue = new ConcurrentLinkedQueue<>();


        // init streams to send/receive data
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            if (is != null && os != null) {
                outputStream = new ObjectOutputStream(os);
                inputStream = new ObjectInputStream(is);
            }
        } catch (IOException e) {
            if (debug) {
                e.printStackTrace();
            }
            isRunning = false;
        }
    }

    public ConcurrentLinkedQueue<Message> getMessages() {
        return messageQueue;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isObserver() {
        return isObserver;
    }

    public void setObserver(boolean observer) {
        isObserver = observer;
    }

    // start thread
    public void run() {
        isRunning = true;
        threadRead.start();
    }

    public boolean send(Object object) {
        if (outputStream == null || object == null) {
            return false;
        }
        try {
            outputStream.writeObject(object);
            outputStream.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // stop thread and close socket
    public void stop() {
        isRunning = false;
        threadRead.interrupt();
        outputStream = null;
        inputStream = null;
        try {
            socket.close();
        } catch (IOException e) {
            if (debug) {
                e.printStackTrace();
            }
            isRunning = false;
        }
    }

    // read message from socket
    private void readFromSocket() {
        while (isRunning) {
            try {
                if (inputStream == null) {
                    isRunning = false;
                    break;
                }
                // Read message from input stream
                Object obj = inputStream.readObject();
                // add to queue to prevent cross thread conflicts
                Message message = (Message) obj;
                messageQueue.offer(message);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e);
                isRunning = false;
            }
            Core.Utility.sleep();
        }
    }
}

package com.example.tcpclient;

import android.util.Log;

import com.example.sensor.IPredictionListener;

import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Client extends Thread implements IPredictionListener {
    private Queue<ConnectionDetails> connectQueue;
    private Queue<float[]> valueQueue;
    private Session session;
    private Socket socket;


    public Client() {
        super();
        connectQueue = new LinkedList<>();
        valueQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            while (!connectQueue.isEmpty()) {
                ConnectionDetails c = connectQueue.poll();
                try {
                    socket = new Socket(c.getIp(), c.getPort());
                    session = new Session(socket);
                    session.run();

                    Log.d("Foo", "connected");

                } catch (Exception e) {
                    Log.d("Foo", e.toString());
                }
            }
            while (!valueQueue.isEmpty()){
                float[] values = valueQueue.poll();
                session.send(values);
                Log.d("Foo", "sent: "+ Arrays.toString(values));
            }
            Core.Utility.sleep();
        }
    }

    public void connect(String ip, int port) {
        ConnectionDetails c = new ConnectionDetails(ip, port);
        connectQueue.offer(c);
    }

    @Override
    public void onPrediction(float[] result) {
        if (session == null || !socket.isConnected()){
            return;
        }
        valueQueue.offer(result);
    }
}

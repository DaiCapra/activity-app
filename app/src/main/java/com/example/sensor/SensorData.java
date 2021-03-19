package com.example.sensor;

public class SensorData {
    private long timestamp;
    private Vector vector;

    public SensorData(long timestamp, Vector value) {
        this.timestamp = timestamp;
        this.vector = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Vector getVector() {
        return vector;
    }
}

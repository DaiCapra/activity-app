package com.example.sensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

public class SensorService extends Activity implements SensorEventListener {

    private final Sensor sensorAccelerometer;
    private final Settings settings;
    private List<SensorData> list;
    private List<ISensorListener> listeners;

    public SensorService(Settings settings, SensorManager sensorManager) {
        this.settings = settings;
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        sensorManager.registerListener(this, sensorAccelerometer, settings.getSampleRate());
        list = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public void registerListener(ISensorListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == sensorAccelerometer) {
            addAccelerometerEvent(event);
        }
    }

    public void addAccelerometerEvent(SensorEvent event) {

        Vector v = new Vector(event.values[0],event.values[1],event.values[2]);
        SensorData d = new SensorData(event.timestamp, v);
        list.add(d);

        if (list.size() >= settings.getNumberOfDataPoints()) {
            for (ISensorListener listener : listeners){
                listener.onSensorGatherComplete(list);
            }
            list.clear();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

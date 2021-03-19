package com.example.sensor;

import java.util.List;

public interface ISensorListener {
    void onSensorGatherComplete(List<SensorData> list);
}

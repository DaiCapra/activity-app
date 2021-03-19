package com.example.sensor;

import android.content.res.AssetManager;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PredictionService implements ISensorListener{
    private final long[] inputSize;

    private final Settings settings;

    private TensorFlowInferenceInterface inferenceInterface;

    private List<IPredictionListener> listeners;

    public PredictionService(Settings settings, AssetManager assets) {
        this.settings = settings;
        listeners = new ArrayList<>();
        inferenceInterface = new TensorFlowInferenceInterface(assets, settings.getPathModel());

        inputSize = new long[]{1, settings.getNumberOfDataPoints(), 3};

    }

    public void registerListener(IPredictionListener listener){
        listeners.add(listener);
    }

    private List<Vector> loadData(AssetManager assets) {
        List<Vector> list = new ArrayList<Vector>();
        try {
            InputStream in = assets.open("testData.txt");
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            in.close();
            String s = new String(buffer);

            String lines[] = s.split("\\r?\\n");

            for (String line : lines) {
                String[] values = line.split(",");
                float x = Float.parseFloat(values[0]);
                float y = Float.parseFloat(values[1]);
                float z = Float.parseFloat(values[2]);
                Vector v = new Vector(x, y, z);
                list.add(v);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private float[] predict(List<Vector> list){
        ArrayList<Float> values = new ArrayList<>();
        for (Vector v :list){
            values.add(v.getX());
        }
        for (Vector v :list){
            values.add(v.getY());
        }
        for (Vector v :list){
            values.add(v.getZ());
        }
        float[] v = new float[values.size()];
        for (int i = 0; i < values.size();i++){
            v[i] = values.get(i);
        }

        String[] outputNodes = {settings.getModelNameOutput()};
        float[] result = new float[4];

        inferenceInterface.feed(settings.getModelNameInput(), v, inputSize);
        inferenceInterface.run(outputNodes);
        inferenceInterface.fetch(settings.getModelNameOutput(), result);

        return result;
    }

    @Override
    public void onSensorGatherComplete(List<SensorData> list) {
        List<Vector> v = new ArrayList<>();
        for (SensorData s : list){
            v.add(s.getVector());
        }

        Log.d("Main", "list: " +v.size());

        float[] result = predict(v);

        Log.d("Main", "V: " + Arrays.toString(result));
        for (IPredictionListener listener : listeners){
            listener.onPrediction(result);
        }
    }
}

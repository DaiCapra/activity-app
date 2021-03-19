package com.example.sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.tcpclient.Client;

public class MainActivity extends AppCompatActivity implements IPredictionListener {

    private PredictionService predictionService;
    private SensorService sensorService;
    private TextView textJogging;
    private TextView textSitting;
    private TextView textStanding;
    private TextView textWalking;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Main", "Application started");

        client = new Client();
        client.start();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
        wakeLock.acquire();

        textJogging = findViewById(R.id.textJogging);
        textSitting = findViewById(R.id.textSitting);
        textStanding = findViewById(R.id.textStanding);
        textWalking = findViewById(R.id.textWalking);

        Settings settings = new Settings();
        AssetManager assets = getAssets();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorService = new SensorService(settings, sensorManager);
        predictionService = new PredictionService(settings, assets);

        sensorService.registerListener(predictionService);
        predictionService.registerListener(this);
        predictionService.registerListener(client);
        onPrediction(new float[]{0, 0, 0, 0});


        client.connect("127.0.0.1", 4242);
    }

    @Override
    public void onPrediction(float[] result) {
        textJogging.setText("Jogging: " + result[0]);
        textSitting.setText("Sitting: " + result[1]);
        textStanding.setText("Standing: " + result[2]);
        textWalking.setText("Walking: " + result[3]);
    }
}

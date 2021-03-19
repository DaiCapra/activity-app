package com.example.sensor;

public class Settings {
    private int numberOfDataPoints;
    private int sampleRate;
    private String pathModel;

    private String modelNameInput;
    private String modelNameOutput;

    public Settings(){
        numberOfDataPoints = 100;
        sampleRate = 50;

        pathModel = "file:///android_asset/model-100.pb";
        modelNameInput = "lstm_1_input";
        modelNameOutput = "dense_1/Sigmoid";
    }

    public int getNumberOfDataPoints() {
        return numberOfDataPoints;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public String getPathModel() {
        return pathModel;
    }

    public String getModelNameInput() {
        return modelNameInput;
    }

    public String getModelNameOutput() {
        return modelNameOutput;
    }
}

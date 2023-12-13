package com.example.afinal.model;
import com.google.gson.annotations.SerializedName;
public class PointWeather {
    @SerializedName("x")
    private long timestamp;

    @SerializedName("y")
    private float value;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}

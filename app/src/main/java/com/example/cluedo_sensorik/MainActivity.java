package com.example.cluedo_sensorik;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accel;
    private ShakeDetector shakeDetector;

    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tvShake);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); //initialize Shake Detector
        assert sensorManager != null;
        accel = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector();
        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                textView.setText(R.string.ShakeEventDetected);
            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(shakeDetector, accel,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        sensorManager.unregisterListener(shakeDetector);
        super.onPause();
    }
}

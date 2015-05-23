package com.example.nihal.parkinsons;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WalkService extends Service implements SensorEventListener2 {

    public WalkService() {
    }

    private int accelStep = 0;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        accelStep = 0;
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {
        super.onCreate();


        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            float g = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
            if(g>3.2) {
                Log.d("Accel Step", "AStep!");
                accelStep++;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        senSensorManager.unregisterListener(this);
        Toast.makeText(getApplicationContext(), "Accel Step:  "+ accelStep, Toast.LENGTH_LONG).show();
    }
}

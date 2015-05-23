package com.example.nihal.parkinsons;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

import java.net.URLEncoder;
import java.util.List;

public class TremorMonitor extends Service  implements SensorEventListener2 {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 10;

    double startTime;
    double tremorCount = 0;

    public TremorMonitor() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        startTime = 0;
        tremorCount = 0;
        startTime = System.currentTimeMillis();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startTime = System.currentTimeMillis() - startTime;

        //abhinavAPI("7", "10,11,12,13");
        senSensorManager.unregisterListener(this);
        Log.d("Average Frequency", ""+calcFrequency());
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    void  abhinavAPI(String id, String values){
    String timeStamp= "";
    
	Time now = new Time();
    now.setToNow();
    String sTime = now.format("%d-%m-%Y-%H-%M-%S");
    timeStamp = sTime;
		String reqParam = id+";"+timeStamp+";"+values;
        try{
            reqParam = URLEncoder.encode(reqParam, "utf-8");
        }catch(Exception e){}

		new RequestTask().execute("http://10.14.121.236:3000/insert/"+reqParam);    
    }
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
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


            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    Log.d("Sensor", "X: "+x+"  Y:  "+y+"  Z :"+z);
                    tremorCount++;
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }

        }

    }

    double calcFrequency(){
        return (tremorCount/startTime)*1000;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}

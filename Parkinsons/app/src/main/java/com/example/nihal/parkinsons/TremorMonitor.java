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
    private static final int SHAKE_THRESHOLD = 600;


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

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        abhinavAPI("7", "10,11,12,13");
        senSensorManager.unregisterListener(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    void  abhinavAPI(String id, String values){
    String timeStamp= "";
    
	Time now = new Time();
    now.setToNow();
    String sTime = now.format("%Y_%m_%d_%H_%M_%S");
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
            Log.d("Sensor", "X: "+x+"  Y:  "+y+"  Z :"+z);
            if(isCallActive(getApplicationContext())){
                Log.d("Call State", "Call Active");
            }
            else
                Log.d("Call State", "Call Inactive");
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public static boolean isCallActive(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getMode()==AudioManager.MODE_IN_CALL)
            return true;
        return false;
    }
}

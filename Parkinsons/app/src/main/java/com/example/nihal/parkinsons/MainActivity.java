package com.example.nihal.parkinsons;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;



public class MainActivity extends ActionBarActivity {

    boolean serviceSwitch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(!serviceSwitch && checkCallState()){
                        serviceSwitch = true;
                        Log.d("Call State", "Call Active");
                        startService(new Intent(getApplicationContext(), TremorMonitor.class));
                    }
                    else if(serviceSwitch && !checkCallState()) {
                        serviceSwitch = false;
                        Log.d("Call State", "Call Inactive");
                        stopService(new Intent(getApplicationContext(), TremorMonitor.class));
                    }
                }
            }
        });

        thread.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean checkCallState(){
        if(isCallActive(getApplicationContext())) {
//            Log.d("Call State", "Call Active");
            return true;
        }
        else {
//            Log.d("Call State", "Call Inactive");
            return false;
        }
    }





    public static boolean isCallActive(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getMode()==AudioManager.MODE_IN_CALL)
            return true;
        return false;
    }

    public void startService(View view) {
        startService(new Intent(this, TremorMonitor.class));
    }

    // Method to stop the service
    public void stopService(View view) {
        stopService(new Intent(this, TremorMonitor.class));
    }

}

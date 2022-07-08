package com.counter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyService extends Service implements SensorEventListener, StepListener {

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
//    private int numSteps;
    private static SharedPreferences mPrefs;

    Context context;

    public MyService() {
        context = this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        println("onCreate");
        getDate();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//        numSteps = 0;
        defaultInitStepCounter();
        runStepCounter();
    }

    public void runStepCounter(){
        println("runStepCounter");
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void defaultInitStepCounter(){
        println("defaultInitStepCounter");
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initializeNotification();
        return START_STICKY;
    }

    public void initializeNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "3");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setOngoing(true);
        builder.setWhen(0);
        builder.setShowWhen(false);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        builder.setContent(remoteViews);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("3", "포그라운드 서비스", NotificationManager.IMPORTANCE_NONE));
        }
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void step(long timeNs) {
//        numSteps++;
        int spSteps = mPrefs.getInt(getDate(),0);

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(getDate(),++spSteps);
//        editor.putInt("Steps",numSteps
        editor.apply();
        println(Integer.toString(spSteps));
    }

    /**
     * 서비스 종료
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyService", "onDestory");
    }

    public String getDate() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return format.format(currentTime);
    }

    public void println(String message){
        Log.d("MyService", message);
    }

}

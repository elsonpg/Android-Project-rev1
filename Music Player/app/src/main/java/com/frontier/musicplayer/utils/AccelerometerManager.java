package com.frontier.musicplayer.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.frontier.musicplayer.MusicPlayer;
import com.frontier.musicplayer.MusicService;

import java.util.List;

/**
 * Created by Elson on 6/26/2016.
 */
public final class AccelerometerManager extends Service implements SensorEventListener
        {

    private static Context aContext=null;


    /** Accuracy configuration */
    private static float threshold  = 15.0f;
    private static int interval     = 200;

    public static String mShakeAction;

    public static int mShakeThreshold;

    public static boolean mEnableShake;
    /**
    * Magnitude of last sensed acceleration.
    */
    private double mAccelLast;
    /**
    Filtered acceleration used for shake detection.
    */
    private double mAccelFiltered;

    /**
    * Elapsed realtime of last shake action.
    */
    private long mLastShakeTime;

    /**
    * Minimum time in milliseconds between shake actions.
    */
    private static final int MIN_SHAKE_PERIOD = 500;


    private static Sensor sensor;
    private static SensorManager mSensorManager;


            @Override
            public void onCreate() {


                mShakeAction = PreferencesUtility.getShakeAction();

                mShakeThreshold = PreferencesUtility.getShakeThreshold();

                mEnableShake=PreferencesUtility.shakeEnabled();
                if (mEnableShake) {

                    mAccelFiltered = 0.0f;
                    mAccelLast = SensorManager.GRAVITY_EARTH;
                    setupSensor();



                }
            }

            /**
             * Setup the accelerometer.
             */
            private void setupSensor()
            {
                if (mShakeAction =="nothing") {
                    if (mSensorManager != null)
                        mSensorManager.unregisterListener(this);
                } else {
                    if (mSensorManager == null)
                        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
                    mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
                }
            }

            private static void startService(Context context, String command) {
                final Intent i = new Intent(context, MusicService.class);
                i.setAction(MusicService.SERVICECMD);
                i.putExtra(MusicService.CMDNAME, command);
                i.putExtra(MusicService.FROM_MEDIA_BUTTON, true);

            }

            @Override
            public void onSensorChanged(SensorEvent se)
            {
                double x = se.values[0];
                double y = se.values[1];
                double z = se.values[2];

                double accel = Math.sqrt(x*x + y*y + z*z);
                double delta = accel - mAccelLast;
                mAccelLast = accel;

                double filtered = mAccelFiltered * 0.9f + delta;
                mAccelFiltered = filtered;

                if (filtered > mShakeThreshold) {
                    long now = SystemClock.elapsedRealtime();
                    if (now - mLastShakeTime > MIN_SHAKE_PERIOD) {
                        mLastShakeTime = now;

                        MusicPlayer.playOrPause();


                        }

                    }
                }
            
            @Nullable
            @Override
            public IBinder onBind(Intent intent) {
                return null;
            }



            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }




        }
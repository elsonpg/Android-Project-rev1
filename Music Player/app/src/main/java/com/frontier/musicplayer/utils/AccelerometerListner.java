package com.frontier.musicplayer.utils;

/**
 * Created by Elson on 6/26/2016.
 */
public interface AccelerometerListner {
    public void onAccelerationChanged(float x, float y, float z);

    public void onShake(float force);
}

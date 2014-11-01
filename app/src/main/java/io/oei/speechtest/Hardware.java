package io.oei.speechtest;

import android.app.Application;
import android.content.Context;
import android.content.pm.FeatureGroupInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by c on 10/31/14.
 */
public class Hardware {

    public static Camera camera = null;
    public static final String log_tag = "hardware";
    public static LocationManager locationManager = null;

    public static boolean hasSystemFeature(String f) {
        return MyApplication.applicationContext.getPackageManager().hasSystemFeature(f);
    }

    public static void getLocation() {
        if (locationManager == null) {
            locationManager = (LocationManager) MyApplication.applicationContext.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestSingleUpdate(new Criteria(), new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.i(log_tag, location.toString());
                    Brains.say("Your latitude is " + location.getLatitude() + " and your longitude is " + location.getLongitude());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            }, null);
        }

    }

    public static void toggleFlashlight() {
        if (!hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Log.w(log_tag, "No flashlight");
            Brains.say("I do not have a flashlight on me.");
            return;
        } else {
            Log.d(log_tag, "Flashlight found");
        }
        try {
            if (camera == null) {
                camera = Camera.open();
                Camera.Parameters p = camera.getParameters();
                if (!p.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                    Brains.say("This device cannot keep the flash on.");
                    return;
                }
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                camera.startPreview();
                Brains.say("Turning on flashlight.");
            } else {
                Camera.Parameters p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(p);
                camera.stopPreview();
                camera.release();
                camera = null;
                Brains.say("Turning flashlight off.");
            }
        } catch(Exception e) {
            Log.e(log_tag, e.toString());
            Brains.say("Something went wrong.");
        }
    }
}

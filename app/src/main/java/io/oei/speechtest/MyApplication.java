package io.oei.speechtest;

import android.app.Application;
import android.content.Context;


/**
 * Created by c on 10/31/14.
 */
public class MyApplication extends Application {

    public static Context applicationContext;
    
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
    }

}

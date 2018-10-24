package com.sawatruck.driver;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by royal on 3/13/2018.
 * http://steveliles.github.io/is_my_android_app_currently_foreground_or_background.html
 */

public class Foreground
        implements Application.ActivityLifecycleCallbacks {

    private boolean foreground = false, paused = true;
    private Handler handler = new Handler();
    private Runnable check;
    private int CHECK_DELAY = 500;
    private static Foreground instance;

    public static void init(Application app){
        if (instance == null){
            instance = new Foreground();
            app.registerActivityLifecycleCallbacks(instance);
        }
    }

    public interface Listener {
        void onBecameForeground();
        void onBecameBackground();
    }

    private List listeners =
            new CopyOnWriteArrayList();

    public void addListener(Listener listener){
        listeners.add(listener);
    }

    public void removeListener(Listener listener){
        listeners.remove(listener);
    }

    public static Foreground get(){
        return instance;
    }


    public boolean isForeground(){
        return foreground;
    }

    public boolean isBackground(){
        return !foreground;
    }

    private Foreground(){}

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;

        if (check != null)
            handler.removeCallbacks(check);

        if (wasBackground){
            for(int i=0 ; i<listeners.size(); i++) {
                try{
                    Listener l = (Listener)listeners.get(i);
                    l.onBecameForeground();
                }catch (Exception e) {

                }
            }
        } else {

        }
    }


    @Override
    public void onActivityPaused(Activity activity) {
        paused = true;

        if (check != null)
            handler.removeCallbacks(check);

        handler.postDelayed(check = new Runnable(){
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;
                    for(int i=0 ; i<listeners.size(); i++) {
                        try{
                            Listener l = (Listener)listeners.get(i);
                            l.onBecameForeground();
                        }catch (Exception e) {

                        }
                    }
                } else {

                }
            }
        }, CHECK_DELAY);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    // TODO: implement the lifecycle callback methods!

}
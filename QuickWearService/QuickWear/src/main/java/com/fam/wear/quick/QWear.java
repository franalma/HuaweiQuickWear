package com.fam.wear.quick;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import utils.Utils;

public class QWear {
    private static String SERVICE_NAME = "WearCustomService";
    IWear iWear;

    public interface QWearInitListener {
        void onInit(boolean result);
    }

    public void init(Context context, QWearInitListener listener) {
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                iWear = IWear.Stub.asInterface(binder);
                listener.onInit(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                listener.onInit(false);
            }
        };
        Intent intent = new Intent(SERVICE_NAME);
        Intent explicit = Utils.convertImplicitIntentToExplicitIntent(intent, context);
        context.bindService(explicit, connection, Context.BIND_AUTO_CREATE);
    }

    public void setWearConfig(String watchAppPackageName, String watchAppFingerPrint) {
        try {
            iWear.setInternaConfiguration(watchAppPackageName, watchAppFingerPrint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadWearConfigFromUrl() {

    }

    public void startCommunication() {
        try {
            iWear.startCommunication();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(String data){
        try{
            iWear.sendData(data);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}

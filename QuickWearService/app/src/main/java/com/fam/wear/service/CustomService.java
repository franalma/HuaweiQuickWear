package com.fam.wear.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.fam.wear.service.stubs.Wear;
import com.huawei.wearengine.HiWear;
import com.huawei.wearengine.device.DeviceClient;

public class CustomService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("----service binding");
        Wear wear = new Wear(getBaseContext());
        return  wear.asBinder();
    }
}

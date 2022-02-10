package com.unisoc.ccsa.permission.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.unisoc.ccsa.Log;
import com.unisoc.ccsa.permission.service.DataUpdateService;


public class BootReceiver extends BroadcastReceiver{
    private String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG,"BootReceiver action=" + action);
        if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent startIntent = new Intent(context, DataUpdateService.class);
            context.startService(startIntent);
        }
    }
}

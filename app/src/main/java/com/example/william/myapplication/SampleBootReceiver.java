package com.example.william.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SampleBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // to start service on boot complete
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            Intent svc = new Intent(context, MainService.class);
            context.startService(svc);
        }
    }
}

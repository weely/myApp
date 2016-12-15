package com.lw.myapp.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Lw on 2016/12/13.
 */

public class UserBroadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            Log.i("weely", "---" + Intent.ACTION_USER_PRESENT);
        }
    }
}

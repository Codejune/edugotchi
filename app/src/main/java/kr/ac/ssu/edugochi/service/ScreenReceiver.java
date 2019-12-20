package kr.ac.ssu.edugochi.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import kr.ac.ssu.edugochi.activity.MeasureActivity;

public class ScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Intent i = new Intent(context, MeasureActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}


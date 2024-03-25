package org.tensorflow.lite.examples.detection.Reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String text = bundle.getString("event");
        String date = bundle.getString("date") + " " + bundle.getString("time");
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra("message",text);
        context.startService(intentService);

    }
}

package cl.bermanngatecontrol.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastBootService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, SyncChoferes.class);
        context.startService(myIntent);

    }
}
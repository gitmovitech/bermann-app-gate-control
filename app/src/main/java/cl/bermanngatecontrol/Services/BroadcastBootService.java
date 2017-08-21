package cl.bermanngatecontrol.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastBootService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent IntentChoferes = new Intent(context, SyncChoferes.class);
        context.startService(IntentChoferes);

        Intent IntentGaritas = new Intent(context, SyncGaritas.class);
        context.startService(IntentGaritas);

        Intent IntentEscaneos = new Intent(context, SyncEscaneos.class);
        context.startService(IntentEscaneos);

    }
}

package cl.bermanngatecontrol.Services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import cl.bermanngatecontrol.Libraries.CallbackSync;
import cl.bermanngatecontrol.Libraries.SyncUtilities;
import cl.bermanngatecontrol.R;

public class SyncChoferes extends Service {

    Integer Processing = 0;

    NotificationCompat.Builder builder;
    Context context;
    SyncUtilities sync_utilities;
    Integer timeSleep = 1000*60*60;

    public SyncChoferes() {
    }

    @Override
    public void onCreate() {

        context = getApplicationContext();
        sync_utilities = new SyncUtilities(context);


        builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.sync)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.bermanngps))
                .setContentTitle("Sincronizando")
                .setContentText("Descargando imágenes");

        sync_utilities.setNotificacion(new CallbackSync(){
            @Override
            public void success() {
                super.success();

                ContentValues values = getValues();
                int completed = values.getAsInteger("completed");
                int total = values.getAsInteger("total");
                builder.setProgress(total, completed, false);
                startForeground(1, builder.build());

                if(completed == total){
                    stopForeground(true);
                }
            }
        });


        new Thread(new Runnable(){
            public void run() {
                int m = 0;
                while(true)
                {

                    try {
                        Thread.sleep(timeSleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (Processing == 0) {
                        Processing = 1;
                        if(sync_utilities.detectInternet()){
                            sync_utilities.getChoferes();
                            Processing = 0;
                        }
                    }


                }

            }
        }).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

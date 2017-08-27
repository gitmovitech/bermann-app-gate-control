package cl.bermanngatecontrol.Services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import cl.bermanngatecontrol.Libraries.CallbackSync;
import cl.bermanngatecontrol.Libraries.SyncUtilities;
import cl.bermanngatecontrol.R;

public class SyncChoferes extends Service {

    NotificationCompat.Builder builder;
    SyncUtilities Sync;
    Integer timeSleep = 20000;
    SharedPreferences config;
    Thread thread;

    public SyncChoferes() {
    }

    public void WakeUp(){
        thread.interrupt();
    }

    @Override
    public void onCreate() {

        config = getApplicationContext().getSharedPreferences("AppGateControl", Context.MODE_PRIVATE);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true){
                    Log.d("EJECUTANDO SERVICIO", "SyncChoferes");
                    if(!config.getString("db_id", "").isEmpty()){

                    } else {
                        try {
                            thread.sleep(timeSleep);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
        thread.start();


        /*builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.sync)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.bermanngps))
                .setContentTitle("Sincronizando")
                .setContentText("Sincronizando choferes");
        startForeground(1, builder.build());

        Sync = new SyncUtilities(getApplicationContext());
        Sync();



        Sync.setNotificacion(new CallbackSync(){
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
        });*/


        /*new Thread(new Runnable(){
            public void run() {
                while(true)
                {
                    try {
                        Thread.sleep(timeSleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(Sync.detectInternet()){
                        Sync.getChoferes();
                    }

                }

            }
        }).start();*/

    }

    public void Sync(){
        //Sync.getChoferesCallback();
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

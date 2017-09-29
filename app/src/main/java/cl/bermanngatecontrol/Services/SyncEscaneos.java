package cl.bermanngatecontrol.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import cl.bermanngatecontrol.Libraries.SyncUtilities;

public class SyncEscaneos extends Service {

    Integer Processing = 0;


    Context context;
    SyncUtilities sync_utilities;
    Integer timeSleep = 1000*60*60;

    public SyncEscaneos() {
    }

    @Override
    public void onCreate() {


        context = getApplicationContext();
        sync_utilities = new SyncUtilities(context);


        new Thread(new Runnable(){
            public void run() {
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
                            try {
                                sync_utilities.postEscaneos();
                                Processing = 0;
                            } catch(Exception e){

                            }
                        }
                    }


                }

            }
        });
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

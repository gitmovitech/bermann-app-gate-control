package cl.bermanngatecontrol.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import cl.bermanngatecontrol.Libraries.SyncUtilities;
import cl.bermanngatecontrol.SQLite.DbChoferesHelper;

public class SyncChoferes extends Service {

    Integer Processing = 0;


    Context context;
    SyncUtilities sync_utilities;
    Integer timeSleep = 1000*60;

    public SyncChoferes() {
    }

    @Override
    public void onCreate() {

        context = getApplicationContext();
        sync_utilities = new SyncUtilities(context);


        new Thread(new Runnable(){
            public void run() {
                while(true)
                {

                    DbChoferesHelper Choferes = new DbChoferesHelper(getApplicationContext());
                    Cursor c = Choferes.getAll();
                    if(c.getCount() == 0){
                        timeSleep = 1000;
                    }
                    c.close();
                    Choferes.close();


                    try {
                        Thread.sleep(timeSleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    if (Processing == 0) {
                        Processing = 1;
                        if(sync_utilities.detectInternet()){
                            sync_utilities.getChoferes();
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
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

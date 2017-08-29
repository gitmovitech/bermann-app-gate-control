package cl.bermanngatecontrol.Services;

import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cl.bermanngatecontrol.Activities.InitActivity;
import cl.bermanngatecontrol.Libraries.CallbackSync;
import cl.bermanngatecontrol.Libraries.ImageDownload;
import cl.bermanngatecontrol.Libraries.RESTService;
import cl.bermanngatecontrol.Libraries.SyncUtilities;
import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesHelper;
import cl.bermanngatecontrol.SQLite.DbChoferesProjection;

import static java.lang.System.in;

public class SyncChoferes extends Service {

    public static String BROADCAST_ACTION = "cl.bermanngatecontrol";
    NotificationCompat.Builder builder;
    Integer timeSleep = 1000*60*60;
    SharedPreferences config;
    Thread thread;
    IBinder mBinder = new LocalBinder();

    public SyncChoferes() {
    }

    public void WakeUp(){
        Log.d("WAKE", "UP");
        thread.interrupt();
    }

    @Override
    public void onCreate() {

        config = getApplicationContext().getSharedPreferences("AppGateControl", Context.MODE_PRIVATE);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true){
                    if(!config.getString("db_id", "").isEmpty()){

                        if(detectInternet()) {

                            builder = new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.sync)
                                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.bermanngps))
                                    .setContentTitle("Sincronizando")
                                    .setContentText("Sincronizando choferes");
                            startForeground(1, builder.build());

                            new RESTService(getApplicationContext()).getJSONArray(getResources().getString(R.string.url_choferes) + "&id=" + config.getString("db_id", ""), new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {

                                    DbChoferesHelper Choferes = new DbChoferesHelper(getApplicationContext());
                                    Choferes.deleteAll();
                                    ContentValues Values;
                                    ArrayList<String> url_fotos = new ArrayList<String>();
                                    String[] foto = null;
                                    try {
                                        for (int n = 0; n < response.length(); n++) {

                                            JSONObject item = response.getJSONObject(n);

                                            Values = new ContentValues();
                                            Values.put(DbChoferesProjection.Entry.NOMBRE, item.getString("nombre"));
                                            Values.put(DbChoferesProjection.Entry.APELLIDO_PATERNO, item.getString("apellido_paterno"));
                                            Values.put(DbChoferesProjection.Entry.ESTADO, item.getString("estado"));
                                            Values.put(DbChoferesProjection.Entry.RUT, item.getString("rut"));
                                            foto = item.getString("foto_chofer").split("/");
                                            Values.put(DbChoferesProjection.Entry.FOTO, foto[foto.length-1]);
                                            Choferes.insert(Values);
                                            
                                            url_fotos.add(item.getString("foto_chofer"));

                                        }
                                    } catch (JSONException e) {

                                    }
                                    Choferes.close();

                                    config.edit().putString("LAST_SYNC_DATE", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).commit();

                                    Download(url_fotos, 0, new CallbackSync(){
                                        @Override
                                        public void success() {
                                            super.success();

                                            sendBroadcast(new Intent(BROADCAST_ACTION));

                                            config.edit().putString("LAST_SYNC_DATE", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).commit();
                                        }
                                    });

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("ERROR SYNC", error.toString());
                                }
                            });

                            try {
                                thread.sleep(timeSleep);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

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


    }


    private void Download(final ArrayList<String> fotos, final int n, final CallbackSync cb){
        try{
            String[] foto =  fotos.get(n).split("/");
            new ImageDownload(getApplicationContext(), fotos.get(n), foto[foto.length-1], new CallbackSync(){
                @Override
                public void success() {
                    super.success();
                    builder.setContentText("Descargando imagen "+(n+1)+" de "+fotos.size());
                    builder.setProgress(n+1, fotos.size(), false);
                    startForeground(1, builder.build());
                    Download(fotos, n+1, cb);
                }
            });
        } catch (Exception e){
            stopForeground(true);
            cb.success();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public SyncChoferes getServerInstance() {
            return SyncChoferes.this;
        }
    }

    /**
     * DETECCIÓN DE CONEXIÓN A INTERNET
     * @return
     */
    public boolean detectInternet(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null){
            return activeNetwork.isConnected();
        } else{
            return false;
        }
    }


}

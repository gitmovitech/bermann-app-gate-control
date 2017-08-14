package cl.bermanngatecontrol.Services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cl.bermanngatecontrol.Libraries.RESTService;
import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesHelper;
import cl.bermanngatecontrol.SQLite.DbChoferesProjection;

public class SyncChoferes extends Service {

    Integer Processing = 0;
    RESTService REST;
    String url_choferes;
    Context context;

    public SyncChoferes() {
    }

    @Override
    public void onCreate() {

        REST = new RESTService(getApplicationContext());
        context = getApplicationContext();

        url_choferes = getResources().getString(R.string.url_choferes);

        new Thread(new Runnable(){
            public void run() {
                while(true)
                {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    if (Processing == 0) {
                        Processing = 1;

                        if(detectInternet()){

                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("accept", "application/json");
                            REST.get(url_choferes, new Response.Listener<JSONArray>() {
                                public void onResponse(JSONArray response) {
                                    Log.d("RESPONSE", response.toString());
                                    setToDatabase(response);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("ERROR", error.toString());
                                }
                            }, headers);

                        }

                    }

                }

            }
        }).start();

    }

    private void setToDatabase(JSONArray data){

        JSONObject item;
        ContentValues values;

        DbChoferesHelper Choferes = new DbChoferesHelper(getApplicationContext());
        Choferes.deleteAll();

        for(int n = 0; n < data.length(); n++){
            try {
                item = (JSONObject) data.get(n);
                values = new ContentValues();
                values.put(DbChoferesProjection.Entry.NOMBRE, item.getString("nombre"));
                values.put(DbChoferesProjection.Entry.APELLIDO_PATERNO, item.getString("apellido_paterno"));
                values.put(DbChoferesProjection.Entry.RUT, item.getString("rut"));
                values.put(DbChoferesProjection.Entry.ESTADO, item.getString("estado"));
                values.put(DbChoferesProjection.Entry.FOTO, item.getString("foto_chofer"));
                Choferes.insert(values);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        Choferes.close();
    }

    /**
     * DETECCIÓN DE CONEXIÓN A INTERNET
     * @return
     */
    private boolean detectInternet(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null){
            return activeNetwork.isConnected();
        } else{
            Processing = 0;
            return false;
        }
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

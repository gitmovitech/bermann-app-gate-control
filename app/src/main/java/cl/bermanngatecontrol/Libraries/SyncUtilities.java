package cl.bermanngatecontrol.Libraries;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
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
import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesHelper;
import cl.bermanngatecontrol.SQLite.DbChoferesProjection;
import cl.bermanngatecontrol.SQLite.DbEscaneosHelper;
import cl.bermanngatecontrol.SQLite.DbEscaneosProjection;
import cl.bermanngatecontrol.SQLite.DbGaritasHelper;
import cl.bermanngatecontrol.SQLite.DbGaritasProjection;

public class SyncUtilities {

    Context context;
    RESTService REST;
    String url_choferes;
    String url_garitas;
    String url_escaneos;
    SharedPreferences config;
    CallbackSync emitter = null;
    CallbackSync cbImagenes = null;
    TelephonyManager telephonyManager;

    public SyncUtilities(Context context){
        this.context = context;
        config = context.getSharedPreferences("AppGateControl", Context.MODE_PRIVATE);
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public SyncUtilities(Context context, CallbackSync emitter){
        this.context = context;
        this.emitter = emitter;
        config = context.getSharedPreferences("AppGateControl", Context.MODE_PRIVATE);
    }

    public void setNotificacion(CallbackSync cbImagenes){
        this.cbImagenes = cbImagenes;
    }


    /**
     * OBTENER TODOS LOS CHOFERES DESDE EL SERVICIO REST
     */
    public void getChoferes(){

        url_choferes = context.getResources().getString(R.string.url_choferes);
        REST = new RESTService(context);

        REST.getJSONArray(url_choferes, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                setChoferesToDatabase(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
            }
        });
    }


    /**
     * ENVIA REGISTROS ESCANEADOS AL SERVIDOR
     */
    @SuppressLint("MissingPermission")
    public void postEscaneos(){
        url_escaneos = context.getResources().getString(R.string.url_escaneos);
        url_choferes += "&id="+config.getString("db_id", "");
        url_choferes += "&comentarios=TODO";
        url_escaneos += "&id_device=" + telephonyManager.getDeviceId();
        url_escaneos += "&garita="+config.getString(DbGaritasProjection.Entry.ID, "");
        String url = url_escaneos;

        DbEscaneosHelper Escaneos = new DbEscaneosHelper(context);
        DbChoferesHelper Choferes = new DbChoferesHelper(context);
        Cursor cho;
        Cursor c = Escaneos.getAllNotSync();
        while (c.moveToNext()){

            cho = Choferes.getByRut(c.getString(c.getColumnIndexOrThrow(DbEscaneosProjection.Entry.RUT)));
            cho.moveToFirst();
            url_escaneos += "&id_chofer=" + cho.getString(cho.getColumnIndexOrThrow(DbChoferesProjection.Entry.ID));
            cho.close();

            if(c.getString(c.getColumnIndexOrThrow(DbEscaneosProjection.Entry.ESTADO)).equals("1")){
                url_escaneos += "&estado_chofer=TRUE";
            } else {
                url_escaneos += "&estado_chofer=FALSE";
            }
            if(c.getString(c.getColumnIndexOrThrow(DbEscaneosProjection.Entry.ENTRADA)).equals("1")){
                url_escaneos += "&en_sa=TRUE";
            } else {
                url_escaneos += "&en_sa=FALSE";
            }
            url_escaneos += "&fecha_envio=" + c.getString(c.getColumnIndexOrThrow(DbEscaneosProjection.Entry.FECHA));
            url_escaneos += "&hora_envio=" + c.getString(c.getColumnIndexOrThrow(DbEscaneosProjection.Entry.HORA));

            syncEscaneos(url_escaneos, c.getInt(c.getColumnIndexOrThrow(DbEscaneosProjection.Entry.ORDEN)));

            url_escaneos = url;
        }
        c.close();
        Escaneos.close();
        Choferes.close();
    }

    private void syncEscaneos(String url, final int id){
        Log.d("SYNC", url);
        REST = new RESTService(context);
        REST.get(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DbEscaneosHelper Escaneos = new DbEscaneosHelper(context);
                Escaneos.setSync(id);
                Escaneos.close();
                Log.d("SYNC", "OK:"+id);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }


    /**
     * OBTENER TODOS LOS CHOFERES DESDE EL SERVICIO REST CON CALLBACK
     * @param cb
     */
    public void getChoferesCallback(final CallbackSync cb){
        url_choferes = context.getResources().getString(R.string.url_choferes)+"&id="+config.getString("db_id", null);
        new RESTService(context).getJSONArray(url_choferes, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray data) {

                JSONObject item;
                ContentValues values;

                DbChoferesHelper Choferes = new DbChoferesHelper(context);
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

                config.edit().putString("LAST_SYNC_DATE",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).commit();

                Choferes.close();
                cb.success();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
            }
        });
    }


    /**
     * GUARDA CHOFERES EN LA BASE DE DATOS
     * @param data
     */
    public void setChoferesToDatabase(JSONArray data){

        JSONObject item;
        ContentValues values;

        DbChoferesHelper Choferes = new DbChoferesHelper(context);
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

        config.edit().putString("LAST_SYNC_DATE",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).commit();

        Choferes.close();

        if(emitter == null) {
            /*getChoferImages(new CallbackSync() {
                @Override
                public void success() {
                    super.success();
                    Log.d("SYNC", "Imagenes sincronizadas en background");
                }
            });*/
        }
    }



    public void getChoferImagesEmitter(CallbackSync cb){

        ArrayList<String> ImageList = new ArrayList<>();
        DbChoferesHelper Choferes = new DbChoferesHelper(context);
        Cursor c = Choferes.getAll();
        while(c.moveToNext()){
            if(!c.getString(c.getColumnIndexOrThrow(DbChoferesProjection.Entry.FOTO)).equals("null")){
                ImageList.add(c.getString(c.getColumnIndexOrThrow(DbChoferesProjection.Entry.FOTO)));
            }
        }
        c.close();
        Choferes.close();
        if(ImageList.size() > 0){
            if(emitter != null) {
                ContentValues values = new ContentValues();
                values.put("completed", 0);
                values.put("total", ImageList.size());
                emitter.setValues(values);
                emitter.success();
            }
            downloadChoferImages(ImageList, 0, cb);
        }
    }



    public void downloadChoferImages(final ArrayList<String> ImageList, final int n, final CallbackSync cb){
        if(ImageList.size() > n) {

            new ImageDownload(context, context.getResources().getString(R.string.url_foto_chofer) + ImageList.get(n).toString(), ImageList.get(n).toString(), new CallbackSync() {
                @Override
                public void success() {

                    if(emitter != null) {
                        ContentValues values = new ContentValues();
                        values.put("completed", n + 1);
                        values.put("total", ImageList.size());
                        emitter.setValues(values);
                        emitter.success();
                    } else if(cbImagenes != null) {
                        ContentValues values = new ContentValues();
                        values.put("completed", n + 1);
                        values.put("total", ImageList.size());
                        cbImagenes.setValues(values);
                        cbImagenes.success();
                    }

                    downloadChoferImages(ImageList, n + 1, cb);
                }
            });
        }
        else {
            cb.success();
        }
    }


    /**
     * GUARDA CHOFERES EN LA BASE DE DATOS CON CALLBACK
     * @param data
     * @param cb
     */
    public void setChoferesToDatabase(JSONArray data, CallbackSync cb){
        JSONObject item;
        ContentValues values;

        DbChoferesHelper Choferes = new DbChoferesHelper(context);
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

        config.edit().putString("LAST_SYNC_DATE",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).commit();

        Choferes.close();

        if(emitter == null) {
            //getChoferImages(cb);
        }
    }


    /**
     * OBTENER TODAS LAS GARITAS DESDE EL SERVICIO REST CON CALLBACK
     * @param cb
     */
    public void getGaritasCallback(String qrcode, final CallbackSync cb){

        url_garitas = context.getResources().getString(R.string.url_garitas)+"&id="+qrcode;
        new RESTService(context).getJSONObject(url_garitas, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("ok").equals("1")){
                        ContentValues values = new ContentValues();
                        values.put(DbGaritasProjection.Entry.ID, response.getString("id"));
                        values.put(DbGaritasProjection.Entry.NOMBRE, response.getString("nombre"));
                        cb.setValues(values);
                        cb.success();
                    } else {
                        cb.error();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
            }
        });
    }


    /**
     * ENVIA DATOS DEL DISPOSITIVO MOVIL
     * @param url
     */
    public void setMobileDevice(String url){
        REST = new RESTService(context);
        Log.d("DEVICE", url);
        REST.get(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }


    /**
     * DETECCIÓN DE CONEXIÓN A INTERNET
     * @return
     */
    public boolean detectInternet(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null){
            return activeNetwork.isConnected();
        } else{
            return false;
        }
    }

}

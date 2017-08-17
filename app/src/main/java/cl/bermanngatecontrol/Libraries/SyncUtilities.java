package cl.bermanngatecontrol.Libraries;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cl.bermanngatecontrol.Activities.MainActivity;
import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesHelper;
import cl.bermanngatecontrol.SQLite.DbChoferesProjection;
import cl.bermanngatecontrol.SQLite.DbGaritasHelper;
import cl.bermanngatecontrol.SQLite.DbGaritasProjection;

public class SyncUtilities {

    Context context;
    RESTService REST;
    String url_choferes;
    String url_garitas;
    SharedPreferences config;


    public SyncUtilities(Context context){
        this.context = context;
        config = context.getSharedPreferences("AppGateControl", Context.MODE_PRIVATE);
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
     * OBTENER TODOS LOS CHOFERES DESDE EL SERVICIO REST CON CALLBACK
     * @param cb
     */
    public void getChoferesCallback(final CallbackSync cb){

        url_choferes = context.getResources().getString(R.string.url_choferes);
        REST = new RESTService(context);

        REST.getJSONArray(url_choferes, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                setChoferesToDatabase(response, cb);
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

        getChoferImages(data, new CallbackSync(){
            @Override
            public void success() {
                super.success();
            }
        });
    }



    public void getChoferImages(JSONArray data, CallbackSync cb){
        ArrayList<String> ImageList = new ArrayList<>();
        JSONObject item;
        for(int n = 0; n < data.length(); n++){
            try {
                item = (JSONObject) data.get(n);
                if(!item.getString("foto_chofer").equals("null")){
                    ImageList.add(item.getString("foto_chofer"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(ImageList.size() > 0){
            downloadChoferImages(ImageList, 0, cb);
        }
    }



    public void downloadChoferImages(final ArrayList<String> ImageList, final int n, final CallbackSync cb){
        if(ImageList.size() > n) {
            new ImageDownload(context, context.getResources().getString(R.string.url_foto_chofer) + ImageList.get(n).toString(), ImageList.get(n).toString(), new CallbackSync() {
                @Override
                public void success() {
                    downloadChoferImages(ImageList, n + 1, cb);
                }
            });
        } else {
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

        getChoferImages(data, cb);
    }


    /**
     * OBTENER TODAS LAS GARITAS DESDE EL SERVICIO REST
     */
    public void getGaritas(){

        //@todo Probar sincronizacion de garitas

        url_garitas = context.getResources().getString(R.string.url_garitas);
        REST = new RESTService(context);

        REST.getJSONArray(url_garitas, new Response.Listener<JSONArray>() {
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
     * OBTENER TODAS LAS GARITAS DESDE EL SERVICIO REST CON CALLBACK
     * @param cb
     */
    public void getGaritasCallback(final CallbackSync cb){

        url_garitas = context.getResources().getString(R.string.url_garitas);
        REST = new RESTService(context);

        REST.getJSONArray(url_garitas, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                setGaritasToDatabase(response, cb);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
            }
        });
    }


    /**
     * GUARDA GARITAS EN LA BASE DE DATOS
     * @param data
     */
    public void setGaritasToDatabase(JSONArray data){

        JSONObject item;
        ContentValues values;

        DbGaritasHelper Garitas = new DbGaritasHelper(context);
        Garitas.deleteAll();

        for(int n = 0; n < data.length(); n++){
            try {
                item = (JSONObject) data.get(n);
                values = new ContentValues();
                values.put(DbGaritasProjection.Entry.ID, item.getString("id"));
                values.put(DbGaritasProjection.Entry.NOMBRE, item.getString("nombre"));
                values.put(DbGaritasProjection.Entry.CLIENTE, item.getString("cliente"));
                Garitas.insert(values);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        Garitas.close();
    }


    /**
     * GUARDA CHOFERES EN LA BASE DE DATOS CON CALLBACK
     * @param data
     * @param cb
     */
    public void setGaritasToDatabase(JSONArray data, CallbackSync cb){
        setGaritasToDatabase(data);
        cb.success();
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

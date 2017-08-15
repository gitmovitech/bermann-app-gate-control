package cl.bermanngatecontrol.Libraries;


import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesHelper;
import cl.bermanngatecontrol.SQLite.DbChoferesProjection;

public class SyncUtilities {

    Context context;
    RESTService REST;
    String url_choferes;
    String url_garitas;

    public SyncUtilities(Context context){
        this.context = context;
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

        Choferes.close();
    }


    /**
     * GUARDA CHOFERES EN LA BASE DE DATOS CON CALLBACK
     * @param data
     * @param cb
     */
    public void setChoferesToDatabase(JSONArray data, CallbackSync cb){
        setChoferesToDatabase(data);
        cb.success();
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

        Choferes.close();
    }


    /**
     * GUARDA CHOFERES EN LA BASE DE DATOS CON CALLBACK
     * @param data
     * @param cb
     */
    public void setGaritasToDatabase(JSONArray data, CallbackSync cb){
        setChoferesToDatabase(data);
        cb.success();
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

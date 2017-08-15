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

    public SyncUtilities(Context context){
        this.context = context;
    }

    public void getChoferes(){

        url_choferes = context.getResources().getString(R.string.url_choferes);
        REST = new RESTService(context);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        REST.get(url_choferes, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                setToDatabase(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
            }
        }, headers);
    }

    public void getChoferesCallback(final CallbackSync cb){

        url_choferes = context.getResources().getString(R.string.url_choferes);
        REST = new RESTService(context);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        REST.get(url_choferes, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                setToDatabase(response, cb);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
            }
        }, headers);
    }

    public void setToDatabase(JSONArray data){

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

    public void setToDatabase(JSONArray data, CallbackSync cb){
        setToDatabase(data);
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

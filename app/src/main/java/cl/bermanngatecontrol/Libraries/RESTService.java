package cl.bermanngatecontrol.Libraries;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RESTService {

    private static final String TAG = RESTService.class.getSimpleName();

    private final Context contexto;

    public RESTService(Context contexto) {
        this.contexto = contexto;
    }

    /**
     * Servicio GET JSON ARRAY
     * @param uri
     * @param jsonListener
     * @param errorListener
     */
    public void getJSONArray(String uri, Response.Listener<JSONArray> jsonListener, Response.ErrorListener errorListener) {
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        JsonArrayRequest peticion = new JsonArrayRequest(uri,jsonListener,errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        VolleySingleton.getInstance(contexto).addToRequestQueue(peticion);
    }


    /**
     * Servicio GET JSON OBJECT
     * @param uri
     * @param jsonListener
     * @param errorListener
     */
    public void getJSONObject(String uri, Response.Listener<JSONObject> jsonListener, Response.ErrorListener errorListener) {
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        JsonObjectRequest peticion = new JsonObjectRequest(uri,jsonListener,errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        VolleySingleton.getInstance(contexto).addToRequestQueue(peticion);
    }

    /**
     * Servicio GET STRING
     * @param uri
     * @param listener
     * @param errorListener
     */
    public void get(String uri, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        StringRequest peticion = new StringRequest(uri,listener,errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        VolleySingleton.getInstance(contexto).addToRequestQueue(peticion);
    }


    /**
     * Servicio POST
     * @param uri
     * @param params
     * @param jsonListener
     * @param errorListener
     */
    public void post(String uri, JSONObject params, Response.Listener<JSONObject> jsonListener, Response.ErrorListener errorListener){
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, uri, params, jsonListener,errorListener);
        RequestQueue requestQueue = Volley.newRequestQueue(contexto);
        requestQueue.add(stringRequest);
    }


}

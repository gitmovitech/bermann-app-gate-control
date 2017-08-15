package cl.bermanngatecontrol.Activities;

import android.app.Activity;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import cl.bermanngatecontrol.Libraries.CallbackSync;
import cl.bermanngatecontrol.Libraries.RESTService;
import cl.bermanngatecontrol.Libraries.SyncUtilities;
import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesHelper;
import cl.bermanngatecontrol.SQLite.DbChoferesProjection;
import cl.bermanngatecontrol.SQLite.DbGaritasHelper;
import cl.bermanngatecontrol.SQLite.DbGaritasProjection;
import cl.bermanngatecontrol.Services.SyncChoferes;
import cl.bermanngatecontrol.Services.SyncGaritas;

public class MainActivity extends AppCompatActivity {

    IntentIntegrator integrator;
    Intent intent;
    SyncUtilities sync_utilities;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        integrator = new IntentIntegrator(this);
        intent = new Intent(this, QrScannerActivity.class);

        Button btnQrScan = (Button) findViewById(R.id.btnQrScan);
        btnQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                integrator.setPrompt(getResources().getString(R.string.qr_garita_scan_message));
                integrator.initiateScan();
            }
        });

        dialog = ProgressDialog.show(MainActivity.this, "", getResources().getString(R.string.syncing_first_time), false);
        SyncChoferes();
    }



    /**
     * SINCRONIZACION DE CHOFERES
     */
    protected void SyncChoferes(){
        new Thread() {
            @Override
            public void run() {
                DbChoferesHelper Choferes = new DbChoferesHelper(getApplicationContext());
                Cursor c = Choferes.getAll();
                final Intent intent = new Intent(getApplicationContext(), SyncChoferes.class);
                if(c.getCount() == 0){

                    sync_utilities = new SyncUtilities(getApplicationContext());
                    if(sync_utilities.detectInternet()){
                        sync_utilities.getChoferesCallback(new CallbackSync(){
                            @Override
                            public void success() {
                                SyncGaritas();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.hide();
                            }
                        });
                        AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
                        alert.setTitle(getResources().getString(R.string.error));
                        alert.setMessage(getResources().getString(R.string.connection_error_message));
                        alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                            }
                        });
                        alert.create();
                        alert.show();
                    }

                } else {
                    startService(intent);
                    SyncGaritas();
                }
                c.close();
                Choferes.close();
            }
        }.start();

    }



    /**
     * SINCRONIZACION DE GARITAS
     */
    protected void SyncGaritas(){
        new Thread() {
            @Override
            public void run() {
                DbGaritasHelper Garitas = new DbGaritasHelper(getApplicationContext());
                Cursor c = Garitas.getAll();
                final Intent intent = new Intent(getApplicationContext(), SyncGaritas.class);
                if(c.getCount() == 0){

                    //PARA TEST INSERCION POR PRIMERA VEZ
                    ContentValues values = new ContentValues();
                    values.put(DbGaritasProjection.Entry.ID, "2");
                    values.put(DbGaritasProjection.Entry.NOMBRE, "GARITA NORTE");
                    values.put(DbGaritasProjection.Entry.CLIENTE, "1");
                    Garitas.insert(values);
                    startService(intent);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.hide();
                        }
                    });

                    //@todo Sincronizar garitas por primera vez
            /*sync_utilities = new SyncUtilities(this);
            if(sync_utilities.detectInternet()){
                final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", getResources().getString(R.string.syncing_first_time), false);
                sync_utilities.getGaritasCallback(new CallbackSync(){
                    @Override
                    public void success() {
                        dialog.hide();
                        startService(intent);
                    }
                });
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(getResources().getString(R.string.error));
                alert.setMessage(getResources().getString(R.string.connection_error_message));
                alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
                alert.create();
                alert.show();
            }*/

                } else {
                    startService(intent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.hide();
                        }
                    });
                }
                c.close();
                Garitas.close();
            }
        }.start();

    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, getResources().getString(R.string.qr_scan_cancel), Toast.LENGTH_LONG).show();
            } else {
                String qrcode = result.getContents();
                validateCode(qrcode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * VALIDAR GARITA
     * @param code
     */
    protected void validateCode(String code){
        DbGaritasHelper Garitas = new DbGaritasHelper(getApplicationContext());
        Cursor c = Garitas.getById(code);
        if(c.getCount() > 0){
            c.moveToFirst();
            intent.putExtra(DbGaritasProjection.Entry.ID, c.getString(c.getColumnIndexOrThrow(DbGaritasProjection.Entry.ID)));
            intent.putExtra(DbGaritasProjection.Entry.NOMBRE, c.getString(c.getColumnIndexOrThrow(DbGaritasProjection.Entry.NOMBRE)));
            intent.putExtra(DbGaritasProjection.Entry.CLIENTE, c.getString(c.getColumnIndexOrThrow(DbGaritasProjection.Entry.CLIENTE)));
            startActivity(intent);
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getResources().getString(R.string.error));
            alert.setMessage(getResources().getString(R.string.garita_not_found));
            alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alert.create();
            alert.show();
        }
    }


}

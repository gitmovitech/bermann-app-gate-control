package cl.bermanngatecontrol.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import cl.bermanngatecontrol.Libraries.CallbackSync;
import cl.bermanngatecontrol.Libraries.SyncUtilities;
import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbGaritasHelper;
import cl.bermanngatecontrol.SQLite.DbGaritasProjection;
import cl.bermanngatecontrol.Services.SyncChoferes;
import cl.bermanngatecontrol.Services.SyncEscaneos;

public class MainActivity extends AppCompatActivity {

    IntentIntegrator integrator;
    Intent intent;
    SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = new Intent(this, InitActivity.class);

        config = getSharedPreferences("AppGateControl", Context.MODE_PRIVATE);

        startService(new Intent(getApplicationContext(), SyncEscaneos.class));
        startService(new Intent(getApplicationContext(), SyncChoferes.class));

        /**
         * COMPRUEBA INICIO DE SESION ANTERIOR
         */
        if(!config.getString(DbGaritasProjection.Entry.ID,"").isEmpty() && !config.getString(DbGaritasProjection.Entry.NOMBRE,"").isEmpty()){

            intent.putExtra(DbGaritasProjection.Entry.ID, config.getString(DbGaritasProjection.Entry.ID,""));
            intent.putExtra(DbGaritasProjection.Entry.NOMBRE, config.getString(DbGaritasProjection.Entry.NOMBRE,""));
            startActivity(intent);
            finish();

        } else {

            setContentView(R.layout.activity_main);

            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            }

            integrator = new IntentIntegrator(this);

            Button btnQrScan = (Button) findViewById(R.id.btnQrScan);
            btnQrScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    integrator.setPrompt(getResources().getString(R.string.qr_garita_scan_message));
                    integrator.initiateScan();
                }
            });

        }

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


    protected void validateCode(final String qrcode){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        if(qrcode.contains("|")){

            SyncUtilities sync = new SyncUtilities(this);
            if(sync.detectInternet()){

                sync.getGaritasCallback(qrcode, new CallbackSync(){
                    @Override
                    public void success() {
                        super.success();
                        String arr[] = qrcode.split("\\|");
                        String id = getValues().getAsString(DbGaritasProjection.Entry.ID);
                        String nombre = getValues().getAsString(DbGaritasProjection.Entry.NOMBRE);
                        intent.putExtra(DbGaritasProjection.Entry.ID, id);
                        intent.putExtra(DbGaritasProjection.Entry.NOMBRE, nombre);
                        config.edit().putString("db_id", arr[0]).putString(DbGaritasProjection.Entry.ID, id).putString(DbGaritasProjection.Entry.NOMBRE, nombre).commit();
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void error() {
                        super.error();

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
                });

            } else {

                alert.setTitle(getResources().getString(R.string.error));
                alert.setMessage(getResources().getString(R.string.connection_error_message));
                alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.create();
                alert.show();
            }

        } else {
            alert.setTitle(getResources().getString(R.string.error));
            alert.setMessage(getResources().getString(R.string.qr_scan_invalid));
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

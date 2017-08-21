package cl.bermanngatecontrol.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import cl.bermanngatecontrol.Libraries.CallbackSync;
import cl.bermanngatecontrol.Libraries.SyncUtilities;
import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesHelper;
import cl.bermanngatecontrol.SQLite.DbGaritasHelper;
import cl.bermanngatecontrol.Services.SyncChoferes;
import cl.bermanngatecontrol.Services.SyncGaritas;

public class InitActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0x11;
    SyncUtilities sync_utilities;
    SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DbGaritasHelper Garitas = new DbGaritasHelper(getApplicationContext());
        Cursor cgaritas = Garitas.getAll();
        DbChoferesHelper Choferes = new DbChoferesHelper(getApplicationContext());
        Cursor cchoferes = Choferes.getAll();

        if(cgaritas.getCount() > 0 && cchoferes.getCount() > 0){

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            startService(new Intent(getApplicationContext(), SyncGaritas.class));
            startService(new Intent(getApplicationContext(), SyncChoferes.class));

        } else {

            setContentView(R.layout.activity_init);
            getSupportActionBar().hide();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            }

            config = getSharedPreferences("AppGateControl", Context.MODE_PRIVATE);
            askForWriteExternalStorage();

        }

        cgaritas.close();
        cchoferes.close();

        Garitas.close();
        Choferes.close();

    }

    protected void askForWriteExternalStorage(){
        String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};
        ActivityCompat.requestPermissions(this, permissions, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SyncGaritas();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.perms_write_required), Toast.LENGTH_SHORT).show();
                askForWriteExternalStorage();
            }
        }
    }


    /**
     * SINCRONIZACION DE GARITAS
     */
    protected void SyncGaritas(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DbGaritasHelper Garitas = new DbGaritasHelper(getApplicationContext());
                Cursor c = Garitas.getAll();
                final Intent intent = new Intent(getApplicationContext(), SyncGaritas.class);
                if(c.getCount() == 0){

                    sync_utilities = new SyncUtilities(getApplicationContext());
                    if(sync_utilities.detectInternet()){
                        sync_utilities.getGaritasCallback(new CallbackSync(){
                            @Override
                            public void success() {
                                startService(intent);
                                SyncChoferes();
                            }
                        });
                    } else {
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
                }
                c.close();
                Garitas.close();
            }
        }).start();
    }


    /**
     * SINCRONIZACION DE CHOFERES
     */
    protected void SyncChoferes(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DbChoferesHelper Choferes = new DbChoferesHelper(getApplicationContext());
                Cursor c = Choferes.getAll();
                final Intent intent = new Intent(getApplicationContext(), SyncChoferes.class);
                if(c.getCount() == 0){

                    final TextView syncing_imagenes = (TextView) findViewById(R.id.txtSyncing);
                    final TextView syncing_imagenes_progress = (TextView) findViewById(R.id.syncing_imagenes_progress);
                    CallbackSync emitter = new CallbackSync(){
                        @Override
                        public void success() {
                            super.success();
                            if(getValues() != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ContentValues values = getValues();
                                        syncing_imagenes_progress.setText(values.get("completed") + " de "+values.get("total"));
                                    }
                                });
                            }
                        }
                    };

                    sync_utilities = new SyncUtilities(getApplicationContext(), emitter);
                    if(sync_utilities.detectInternet()){
                        sync_utilities.getChoferesCallback(new CallbackSync(){
                            @Override
                            public void success() {
                                startService(intent);
                                syncing_imagenes.setText(getResources().getString(R.string.syncing_imagenes));
                                sync_utilities.getChoferImages(new CallbackSync(){
                                    @Override
                                    public void success() {
                                        super.success();
                                        config.edit().putString("LAST_SYNC_DATE",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).commit();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }
                                });
                            }
                        });
                    } else {
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
                }
                c.close();
                Choferes.close();
            }
        }).start();
    }




}

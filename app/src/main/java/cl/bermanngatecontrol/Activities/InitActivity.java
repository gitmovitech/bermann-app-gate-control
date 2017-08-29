package cl.bermanngatecontrol.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import cl.bermanngatecontrol.Services.SyncEscaneos;
import cl.bermanngatecontrol.Services.SyncGaritas;

public class InitActivity extends AppCompatActivity {

    SyncUtilities sync_utilities;
    SharedPreferences config;
    TextView syncing_imagenes;
    TextView syncing_imagenes_progress;
    AlertDialog.Builder alert;
    //CallbackSync emitter;
    Intent intent;
    SyncChoferes SyncChoferes;
    boolean mBounded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

        config = getSharedPreferences("AppGateControl", Context.MODE_PRIVATE);
        syncing_imagenes = (TextView) findViewById(R.id.txtSyncing);
        syncing_imagenes_progress = (TextView) findViewById(R.id.syncing_imagenes_progress);
        sync_utilities = new SyncUtilities(this);

        alert = new AlertDialog.Builder(this);

        intent = new Intent(this, QrScannerActivity.class);
        intent.putExtras(getIntent().getExtras());

        Intent mIntent = new Intent(this, SyncChoferes.class);
        try {
            bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        } catch (Exception e){

        }

        DbChoferesHelper Choferes = new DbChoferesHelper(getApplicationContext());
        Cursor c = Choferes.getAll();

        if(c.getCount() > 0){

            goToNextPage();

        } else {

            setContentView(R.layout.activity_init);
            getSupportActionBar().hide();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            }

        }

        c.close();
        Choferes.close();


        registerReceiver(sync_choferes_finished, new IntentFilter(SyncChoferes.BROADCAST_ACTION));


        /*emitter = new CallbackSync(){
            @Override
            public void success() {
                super.success();
                if(getValues() != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ContentValues values = getValues();
                            syncing_imagenes_progress.setText(values.get("completed") + " de " + values.get("total"));
                        }
                    });
                }
            }
        };*/

    }

    private BroadcastReceiver sync_choferes_finished = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent inte) {
            goToNextPage();
        }
    };

    public void goToNextPage(){
        startActivity(intent);
        finish();
    }

    /**
     * INTERRUMPIR PAUSA EN SERVICIO DE SINCRONIZACION
     */
    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            SyncChoferes = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            cl.bermanngatecontrol.Services.SyncChoferes.LocalBinder mLocalBinder = (cl.bermanngatecontrol.Services.SyncChoferes.LocalBinder)service;
            SyncChoferes = mLocalBinder.getServerInstance();

            SyncChoferes.WakeUp();
        }
    };



}

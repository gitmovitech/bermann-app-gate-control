package cl.bermanngatecontrol.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    CallbackSync emitter;
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
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);

        DbChoferesHelper Choferes = new DbChoferesHelper(getApplicationContext());
        Cursor c = Choferes.getAll();

        if(c.getCount() > 0){

            startActivity(intent);
            finish();

        } else {

            setContentView(R.layout.activity_init);
            getSupportActionBar().hide();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            }

        }

        c.close();
        Choferes.close();

        emitter = new CallbackSync(){
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

    }

    /**
     * SINCRONIZACION DE CHOFERES
     * @param cb
     */
    private void SyncChoferes(final CallbackSync cb){

        if(sync_utilities.detectInternet()){
            sync_utilities.getChoferesCallback(new CallbackSync(){
                @Override
                public void success() {

                    config.edit().putString("LAST_SYNC_DATE",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).commit();

                    syncing_imagenes.setText(getResources().getString(R.string.syncing_imagenes));

                    /*sync_utilities.getChoferImagesEmitter(new CallbackSync(){
                        @Override
                        public void success() {
                            super.success();

                            config.edit().putString("LAST_SYNC_DATE",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).commit();

                            cb.success();
                        }
                    }, emitter);*/
                }
            });
        } else {
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
    }

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

package cl.bermanngatecontrol.Activities;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.app.Service;
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

import com.google.zxing.integration.android.IntentIntegrator;

import cl.bermanngatecontrol.Libraries.CallbackSync;
import cl.bermanngatecontrol.Libraries.SyncUtilities;
import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesHelper;
import cl.bermanngatecontrol.Services.SyncChoferes;

public class MainActivity extends AppCompatActivity {

    IntentIntegrator integrator;
    Intent intent;
    SyncUtilities sync_utilities;

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


        /**
         * SINCRONIZACION DE CHOFERES
         */
        DbChoferesHelper Choferes = new DbChoferesHelper(this);
        Cursor c = Choferes.getAll();
        final Intent intent = new Intent(this, SyncChoferes.class);
        if(c.getCount() == 0){

            sync_utilities = new SyncUtilities(this);
            if(sync_utilities.detectInternet()){
                final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", getResources().getString(R.string.syncing_first_time), false);
                sync_utilities.getChoferesCallback(new CallbackSync(){
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
            }

        } else {
            startService(intent);
        }
        c.close();
        Choferes.close();


    }

}

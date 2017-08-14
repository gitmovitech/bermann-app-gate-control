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
                startActivity(intent);
                /*integrator.setPrompt("Visualice el código QR de la credencial a través de esta cámara para iniciar el escaneo");
                integrator.initiateScan();*/
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
                final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", "Sincronizando por primera vez, por favor espere...", false);
                sync_utilities.getChoferesCallback(new CallbackSync(){
                    @Override
                    public void success() {
                        dialog.hide();
                        startService(intent);
                    }
                });
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Error de conexión");
                alert.setMessage("Ha habido un error de conexión al servidor.\n\nCompruebe que posee una conexión a Internet activa.\n\nSi el problema persiste, puede que los servicios se encuentren desactivados. En este caso contáctenos para notificarnos sobre este problema.");
                alert.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
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

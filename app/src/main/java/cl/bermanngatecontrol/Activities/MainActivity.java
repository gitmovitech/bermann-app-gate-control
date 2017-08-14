package cl.bermanngatecontrol.Activities;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;

import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.Services.SyncChoferes;

public class MainActivity extends AppCompatActivity {

    IntentIntegrator integrator;
    Intent intent;

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

        Intent intent = new Intent(this, SyncChoferes.class);
        startService(intent);
    }

}

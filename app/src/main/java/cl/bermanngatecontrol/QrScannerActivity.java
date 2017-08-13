package cl.bermanngatecontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QrScannerActivity extends AppCompatActivity {

    IntentIntegrator integrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        integrator = new IntentIntegrator(this);

        Button btnQrScan = (Button) findViewById(R.id.btnQrScan);
        btnQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                integrator.setPrompt("Visualice el código QR de la credencial a través de esta cámara para iniciar el escaneo");
                integrator.initiateScan();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Ha cancelado el escaneo de código", Toast.LENGTH_LONG).show();
            } else {
                String qrcode = result.getContents();
                Log.d("QR", qrcode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

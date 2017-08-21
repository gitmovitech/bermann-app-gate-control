package cl.bermanngatecontrol.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbGaritasHelper;
import cl.bermanngatecontrol.SQLite.DbGaritasProjection;

public class MainActivity extends AppCompatActivity {

    IntentIntegrator integrator;
    Intent intent;
    SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = new Intent(this, QrScannerActivity.class);

        config = getSharedPreferences("AppGateControl", Context.MODE_PRIVATE);
        if(!config.getString(DbGaritasProjection.Entry.ID,"").isEmpty()
                && !config.getString(DbGaritasProjection.Entry.NOMBRE,"").isEmpty()
                && !config.getString(DbGaritasProjection.Entry.CLIENTE,"").isEmpty()){
            intent.putExtra(DbGaritasProjection.Entry.ID, config.getString(DbGaritasProjection.Entry.ID,""));
            intent.putExtra(DbGaritasProjection.Entry.NOMBRE, config.getString(DbGaritasProjection.Entry.NOMBRE,""));
            intent.putExtra(DbGaritasProjection.Entry.CLIENTE, config.getString(DbGaritasProjection.Entry.CLIENTE,""));
            startActivity(intent);
            finish();
        }

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

            new Thread(){
                @Override
                public void run(){
                    try{
                        this.sleep(500);
                    } catch (Exception e){}

                    SharedPreferences.Editor editData = config.edit();
                    editData.putString(DbGaritasProjection.Entry.ID, intent.getStringExtra(DbGaritasProjection.Entry.ID));
                    editData.putString(DbGaritasProjection.Entry.NOMBRE, intent.getStringExtra(DbGaritasProjection.Entry.NOMBRE));
                    editData.putString(DbGaritasProjection.Entry.CLIENTE, intent.getStringExtra(DbGaritasProjection.Entry.CLIENTE));
                    editData.commit();

                    startActivity(intent);
                    finish();
                }
            }.start();

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

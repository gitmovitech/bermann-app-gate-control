package cl.bermanngatecontrol.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesHelper;
import cl.bermanngatecontrol.SQLite.DbChoferesProjection;

public class QrScannerActivity extends AppCompatActivity {

    IntentIntegrator integrator;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        integrator = new IntentIntegrator(this);
        intent = new Intent(this, ResultActivity.class);

        Button btnQrScan = (Button) findViewById(R.id.btnQrScan);
        btnQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                integrator.setPrompt(getResources().getString(R.string.qr_credencial_scan_message));
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

                DbChoferesHelper Choferes = new DbChoferesHelper(getApplicationContext());
                Cursor c = Choferes.getByRut(qrcode);
                if(c.getCount() > 0){
                    c.moveToFirst();
                    intent.putExtra(DbChoferesProjection.Entry.ID, c.getString(c.getColumnIndexOrThrow(DbChoferesProjection.Entry.ID)));
                    intent.putExtra(DbChoferesProjection.Entry.NOMBRE, c.getString(c.getColumnIndexOrThrow(DbChoferesProjection.Entry.NOMBRE)));
                    intent.putExtra(DbChoferesProjection.Entry.APELLIDO_PATERNO, c.getString(c.getColumnIndexOrThrow(DbChoferesProjection.Entry.APELLIDO_PATERNO)));
                    intent.putExtra(DbChoferesProjection.Entry.RUT, c.getString(c.getColumnIndexOrThrow(DbChoferesProjection.Entry.RUT)));
                    intent.putExtra(DbChoferesProjection.Entry.ESTADO, c.getString(c.getColumnIndexOrThrow(DbChoferesProjection.Entry.ESTADO)));
                    intent.putExtra(DbChoferesProjection.Entry.FOTO, c.getString(c.getColumnIndexOrThrow(DbChoferesProjection.Entry.FOTO)));
                    startActivity(intent);

                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle(getResources().getString(R.string.error));
                    alert.setMessage(getResources().getString(R.string.id_not_found));
                    alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alert.create();
                    alert.show();
                }
                c.close();
                Choferes.close();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.qrcredencial_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                //@todo trabajar en logout
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
    }

}

package cl.bermanngatecontrol.Activities;

import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import cl.bermanngatecontrol.Libraries.SyncUtilities;
import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesHelper;
import cl.bermanngatecontrol.SQLite.DbChoferesProjection;
import cl.bermanngatecontrol.SQLite.DbEscaneosHelper;
import cl.bermanngatecontrol.SQLite.DbEscaneosProjection;
import cl.bermanngatecontrol.SQLite.DbGaritasProjection;
import cl.bermanngatecontrol.Services.SyncChoferes;

public class QrScannerActivity extends AppCompatActivity {

    IntentIntegrator integrator;
    Intent intent;
    String NombreGarita;
    SharedPreferences config;
    String uuid;
    SyncChoferes SyncChoferes;
    boolean mBounded;
    Intent mIntent;
    Boolean ScanIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        this.setTitle(this.getTitle() + " " + getResources().getString(R.string.version));

        mIntent = new Intent(this, SyncChoferes.class);

        config = getSharedPreferences("AppGateControl", Context.MODE_PRIVATE);


        TextView last_sync_date = (TextView) findViewById(R.id.last_sync_date);
        Date dateFormat;
        try {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(config.getString("LAST_SYNC_DATE", ""));
            last_sync_date.setText("Ultima sincronización: " + new SimpleDateFormat("EEEE").format(dateFormat) + " " + dateFormat.toLocaleString());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            phoneStatePerms();
        }


        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        integrator = new IntentIntegrator(this);
        intent = new Intent(this, ChoferStatusActivity.class);
        intent.putExtras(getIntent().getExtras());

        NombreGarita = getIntent().getStringExtra(DbGaritasProjection.Entry.NOMBRE);
        TextView txtNombreGarita = (TextView) findViewById(R.id.txtNombreGarita);
        txtNombreGarita.setText(NombreGarita);

        Button btnQrScan = (Button) findViewById(R.id.btnQrScan);
        btnQrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanIn = true;
                integrator.setPrompt(getResources().getString(R.string.qr_credencial_scan_message));
                integrator.initiateScan();
            }
        });

        Button btnQrScanOut = (Button) findViewById(R.id.btnQrScanOut);
        btnQrScanOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanIn = false;
                integrator.setPrompt(getResources().getString(R.string.qr_credencial_scan_message));
                integrator.initiateScan();
            }
        });
    }

    public void phoneStatePerms() {
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        uuid = tManager.getDeviceId();
        SyncUtilities sync_utilities = new SyncUtilities(this);
        String url_set_mobile_device = getResources().getString(R.string.url_set_mobile_device);
        url_set_mobile_device += "&nombre=" + uuid;
        url_set_mobile_device += "&app=android";
        url_set_mobile_device += "&version_app="+getResources().getString(R.string.version);
        url_set_mobile_device += "&so="+Build.VERSION.RELEASE;
        url_set_mobile_device += "&id="+config.getString("db_id", null);
        url_set_mobile_device += "&garita="+getIntent().getStringExtra(DbGaritasProjection.Entry.ID);
        sync_utilities.setMobileDevice(url_set_mobile_device);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    phoneStatePerms();
                }
                break;

            default:
                break;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, getResources().getString(R.string.qr_scan_cancel), Toast.LENGTH_LONG).show();
            } else {
                String qrcode = result.getContents().trim();

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
                    intent.putExtra(DbChoferesProjection.Entry.CELULAR, c.getString(c.getColumnIndexOrThrow(DbChoferesProjection.Entry.CELULAR)));
                    intent.putExtra(DbChoferesProjection.Entry.ID_GARITA, c.getString(c.getColumnIndexOrThrow(DbChoferesProjection.Entry.ID_GARITA)));


                    DbEscaneosHelper Escaneos = new DbEscaneosHelper(getApplicationContext());
                    ContentValues values = new ContentValues();
                    values.put(DbEscaneosProjection.Entry.CLIENTE, config.getString(DbGaritasProjection.Entry.CLIENTE,""));
                    values.put(DbEscaneosProjection.Entry.ENTRADA, ScanIn);
                    values.put(DbEscaneosProjection.Entry.ESTADO, c.getString(c.getColumnIndexOrThrow(DbChoferesProjection.Entry.ESTADO)));
                    values.put(DbEscaneosProjection.Entry.SYNC, "0");
                    values.put(DbEscaneosProjection.Entry.GARITA, config.getString(DbGaritasProjection.Entry.ID,""));
                    values.put(DbEscaneosProjection.Entry.RUT, c.getString(c.getColumnIndexOrThrow(DbChoferesProjection.Entry.RUT)));
                    Date date = new Date();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    values.put(DbEscaneosProjection.Entry.FECHA, dateFormat.format(date));
                    dateFormat = new SimpleDateFormat("HH:mm:ss");
                    values.put(DbEscaneosProjection.Entry.HORA, dateFormat.format(date));
                    Escaneos.insert(values);
                    Escaneos.close();

                } else {
                    intent.putExtra(DbChoferesProjection.Entry.ESTADO, "0");
                }
                c.close();
                Choferes.close();

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            this.sleep(500);
                        } catch (Exception e) {
                        }
                        startActivity(intent);
                    }
                }.start();

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
            case R.id.sync:
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
                try {
                    bindService(mIntent, mConnection, BIND_IMPORTANT);
                } catch (Exception e){

                }
                Toast.makeText(getApplicationContext(),R.string.start_sync,Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout:
                AlertDialog.Builder alert = new AlertDialog.Builder(QrScannerActivity.this);
                alert.setTitle(getResources().getString(R.string.exit));
                alert.setMessage(getResources().getString(R.string.exit_message_question));
                alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();

                        SharedPreferences.Editor editData = config.edit();
                        editData.putString(DbGaritasProjection.Entry.ID, "");
                        editData.putString(DbGaritasProjection.Entry.NOMBRE, "");
                        editData.putString(DbGaritasProjection.Entry.CLIENTE, "");
                        editData.commit();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.create();
                alert.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

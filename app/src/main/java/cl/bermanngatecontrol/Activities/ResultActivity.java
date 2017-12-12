package cl.bermanngatecontrol.Activities;

import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;

import cl.bermanngatecontrol.Adapters.AdapterEscaneos;
import cl.bermanngatecontrol.Adapters.ModelEscaneos;
import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesProjection;
import cl.bermanngatecontrol.SQLite.DbEscaneosHelper;
import cl.bermanngatecontrol.SQLite.DbEscaneosProjection;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);

        ListView Registros = (ListView) findViewById(R.id.ListViewRegistros);
        ArrayList<ModelEscaneos> ArrayEscaneos = new ArrayList<>();

        DbEscaneosHelper Escaneos = new DbEscaneosHelper(getApplicationContext());
        Cursor c = Escaneos.getAll();
        while(c.moveToNext()){
            ArrayEscaneos.add(new ModelEscaneos(
                    c.getString(c.getColumnIndexOrThrow(DbEscaneosProjection.Entry.FECHA)),
                    c.getString(c.getColumnIndexOrThrow(DbEscaneosProjection.Entry.HORA)),
                    c.getString(c.getColumnIndexOrThrow(DbEscaneosProjection.Entry.ENTRADA)),
                    c.getString(c.getColumnIndexOrThrow(DbEscaneosProjection.Entry.ESTADO))
            ));
        }
        c.close();
        Escaneos.close();
        AdapterEscaneos Listado = new AdapterEscaneos(this, ArrayEscaneos, getIntent());
        Registros.setAdapter(Listado);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
    }
}

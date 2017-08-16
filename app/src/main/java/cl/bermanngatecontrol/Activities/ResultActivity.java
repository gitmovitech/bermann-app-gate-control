package cl.bermanngatecontrol.Activities;

import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


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

        ImageView foto_chofer = (ImageView) findViewById(R.id.foto_chofer);
        TextView txtRut = (TextView) findViewById(R.id.txtRut);
        TextView txtNombres = (TextView) findViewById(R.id.txtNombres);
        TextView txtApellidos = (TextView) findViewById(R.id.txtApellidos);

        txtRut.setText(getIntent().getStringExtra(DbChoferesProjection.Entry.RUT));
        txtNombres.setText(getIntent().getStringExtra(DbChoferesProjection.Entry.NOMBRE));
        txtApellidos.setText(getIntent().getStringExtra(DbChoferesProjection.Entry.APELLIDO_PATERNO));


        ListView Registros = (ListView) findViewById(R.id.ListViewRegistros);
        ArrayAdapter<String> Listado = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);


        DbEscaneosHelper Escaneos = new DbEscaneosHelper(getApplicationContext());
        Cursor c = Escaneos.getAll();
        while(c.moveToNext()){
            Listado.add(c.getString(c.getColumnIndexOrThrow(DbEscaneosProjection.Entry.FECHA)));
        }
        c.close();
        Escaneos.close();
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

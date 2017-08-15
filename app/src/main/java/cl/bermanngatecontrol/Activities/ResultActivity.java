package cl.bermanngatecontrol.Activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesProjection;

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
        Listado.add("Registro 1");
        Listado.add("Registro 2");
        Listado.add("Registro 3");
        Listado.add("Registro 4");
        Listado.add("Registro 5");
        Listado.add("Registro 6");
        Listado.add("Registro 7");
        Listado.add("Registro 8");
        Listado.add("Registro 9");
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

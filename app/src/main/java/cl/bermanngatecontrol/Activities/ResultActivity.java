package cl.bermanngatecontrol.Activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cl.bermanngatecontrol.R;

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

        ListView Registros = (ListView) findViewById(R.id.ListViewRegistros);
        ArrayAdapter<String> Listado = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        Listado.add("Registro 1");
        Listado.add("Registro 2");
        Listado.add("Registro 3");
        Listado.add("Registro 4");
        Listado.add("Registro 5");
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

package cl.bermanngatecontrol.Activities;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesProjection;
import cl.bermanngatecontrol.SQLite.DbGaritasProjection;

public class ChoferStatusActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chofer_status);

        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView txtAcceso = (TextView) findViewById(R.id.txtAcceso);
        TextView txtRut = (TextView) findViewById(R.id.txtRut);
        Button btnDetalle = (Button) findViewById(R.id.btnDetalle);

        intent = new Intent(this, ResultActivity.class);
        intent.putExtras(getIntent().getExtras());

        btnDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });


        String Estado = getIntent().getStringExtra(DbChoferesProjection.Entry.ESTADO);
        int Color = 0;
        if(Estado.equals("1")){
            Color = R.color.colorAprobado;
            txtAcceso.setText(getResources().getString(R.string.acceso_aprobado));
            btnDetalle.setVisibility(View.VISIBLE);
        } else {
            Color = R.color.colorRechazado;
            txtAcceso.setText(getResources().getString(R.string.acceso_rechazado));
            btnDetalle.setVisibility(View.GONE);
        }

        RelativeLayout rlColor = (RelativeLayout) findViewById(R.id.rlColor);
        rlColor.setBackgroundColor(getResources().getColor(Color));

        txtRut.setText(getIntent().getStringExtra(DbChoferesProjection.Entry.RUT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(Color));
        }

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

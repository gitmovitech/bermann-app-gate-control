package cl.bermanngatecontrol.Activities;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesProjection;

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
        ImageView stopImage = (ImageView) findViewById(R.id.stopImage);

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
            stopImage.setVisibility(View.GONE);
            txtRut.setText(getIntent().getStringExtra(DbChoferesProjection.Entry.RUT));
        } else {
            Color = R.color.colorRechazado;
            txtAcceso.setText(getResources().getString(R.string.id_not_found));
            btnDetalle.setVisibility(View.GONE);
            stopImage.setVisibility(View.VISIBLE);
        }

        RelativeLayout rlColor = (RelativeLayout) findViewById(R.id.rlColor);
        rlColor.setBackgroundColor(getResources().getColor(Color));


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

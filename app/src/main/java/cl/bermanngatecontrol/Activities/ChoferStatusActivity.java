package cl.bermanngatecontrol.Activities;

import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import cl.bermanngatecontrol.Adapters.AdapterEscaneos;
import cl.bermanngatecontrol.Adapters.ModelEscaneos;
import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbChoferesProjection;
import cl.bermanngatecontrol.SQLite.DbEscaneosHelper;
import cl.bermanngatecontrol.SQLite.DbEscaneosProjection;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class ChoferStatusActivity extends AppCompatActivity {

    //Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chofer_status);

        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Button btnDetalle = (Button) findViewById(R.id.btnDetalle);
        btnDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
            }
        });


        LinearLayout layout_aprobado = (LinearLayout) findViewById(R.id.layout_aprobado);
        LinearLayout layout_rechazado = (LinearLayout) findViewById(R.id.layout_rechazado);
        layout_aprobado.setVisibility(View.GONE);
        layout_rechazado.setVisibility(View.GONE);

        String Estado = getIntent().getStringExtra(DbChoferesProjection.Entry.ESTADO);
        int Color = 0;

        if(Estado.equals("1")){
            layout_aprobado.setVisibility(View.VISIBLE);
            Color = R.color.colorAprobado;
            final ImageButton foto_chofer = (ImageButton) findViewById(R.id.foto_chofer);
            TextView txtRut = (TextView) findViewById(R.id.txtRut);
            TextView txtNombres = (TextView) findViewById(R.id.txtNombres);
            TextView txtApellidos = (TextView) findViewById(R.id.txtApellidos);
            TextView txtCelular = (TextView) findViewById(R.id.txtCelular);

            String foto = getIntent().getStringExtra(DbChoferesProjection.Entry.FOTO);
            if(!foto.isEmpty()) {
                ContextWrapper c = new ContextWrapper(this);
                String filesdir = c.getFilesDir() + "/";
                final File imagen = new File(filesdir+foto);
                if(imagen.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagen.getAbsolutePath());
                    foto_chofer.setImageBitmap(bitmap);
                    foto_chofer.setAdjustViewBounds(true);
                    foto_chofer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ImageDetail.class);
                            intent.putExtra("imagepath", imagen.getAbsolutePath());
                            startActivity(intent);
                        }
                    });
                }
            }
            txtRut.setText(getIntent().getStringExtra(DbChoferesProjection.Entry.RUT));
            txtNombres.setText(getIntent().getStringExtra(DbChoferesProjection.Entry.NOMBRE));
            txtApellidos.setText(getIntent().getStringExtra(DbChoferesProjection.Entry.APELLIDO_PATERNO));
            txtCelular.setText(getIntent().getStringExtra(DbChoferesProjection.Entry.CELULAR));

        } else {

            layout_rechazado.setVisibility(View.VISIBLE);
            Color = R.color.colorRechazado;

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

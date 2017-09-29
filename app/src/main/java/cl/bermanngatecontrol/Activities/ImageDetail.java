package cl.bermanngatecontrol.Activities;

import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import cl.bermanngatecontrol.R;

public class ImageDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_image_detail);
        getSupportActionBar().hide();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);


        ImageView image = (ImageView) findViewById(R.id.image);
        image.setImageURI(Uri.parse(getIntent().getStringExtra("imagepath")));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
    }
}

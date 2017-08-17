package cl.bermanngatecontrol.Libraries;


import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageDownload {

    public ImageDownload(final Context ctx, final String url, final String filename, final CallbackSync cb){
        new Thread(new Runnable() {
            @Override
            public void run() {

                ContextWrapper c = new ContextWrapper(ctx);
                String filesdir = c.getFilesDir() + "/";
                File file = new File(filesdir);
                file.mkdirs();
                file = new File(filesdir + filename);

                if (!file.exists()) {
                    try {
                        Bitmap bitmap = Picasso.with(ctx).load(url).get();
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                        ostream.flush();
                        ostream.close();
                        Log.d("Imagen guardada: ", filesdir + filename);
                    } catch (Exception e) {

                    }
                }
                cb.success();

            }
        }).start();
    }
}

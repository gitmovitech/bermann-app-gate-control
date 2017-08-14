package cl.bermanngatecontrol.Libraries;

import android.util.Log;

public class CallbackSync{
    public void success(){
        Log.d("CALLBACK", "SUCCESS");
    }
    public void error(){
        Log.e("CALLBACK", "ERROR");
    }
}
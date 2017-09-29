package cl.bermanngatecontrol.Libraries;

import android.content.ContentValues;
import android.util.Log;

public class CallbackSync{
    ContentValues values = null;
    public void setValues(ContentValues values){
        this.values = values;
    }
    public ContentValues getValues(){
        return values;
    }
    public void success(){
        //Log.d("CALLBACK", "SUCCESS");
    }
    public void error(){
        //Log.e("CALLBACK", "ERROR");
    }
}
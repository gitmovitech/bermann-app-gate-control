package cl.bermanngatecontrol.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cl.bermanngatecontrol.R;
import cl.bermanngatecontrol.SQLite.DbGaritasHelper;
import cl.bermanngatecontrol.SQLite.DbGaritasProjection;

public class AdapterEscaneos extends BaseAdapter {
    private Context context;
    private ArrayList<ModelEscaneos> escaneos;
    private Intent intent;

    public AdapterEscaneos(Context context, ArrayList<ModelEscaneos> equipos, Intent intent) {
        this.context = context;
        this.escaneos = equipos;
        this.intent = intent;
    }

    @Override
    public int getCount() {
        return escaneos.size();
    }

    @Override
    public Object getItem(int position) {
        return escaneos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View layoutitem;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutitem = inflater.inflate(R.layout.item_escaneos, null);
        } else {
            layoutitem = convertView;
        }

        TextView garita = (TextView) layoutitem.findViewById(R.id.garita);
        TextView acceso = (TextView) layoutitem.findViewById(R.id.acceso);
        TextView fecha = (TextView) layoutitem.findViewById(R.id.fecha);

        String estado = "Acceso Aprobado";
        if(!escaneos.get(position).ESTADO.equals("1")){
            estado = "Acceso Rechazado";
        }

        /*DbGaritasHelper Garitas = new DbGaritasHelper(context);
        Cursor c = Garitas.getById(escaneos.get(position).GARITA);
        c.moveToFirst();
        String nombre_garita = c.getString(c.getColumnIndexOrThrow(DbGaritasProjection.Entry.NOMBRE));
        c.close();
        Garitas.close();*/
        String nombre_garita = intent.getStringExtra(DbGaritasProjection.Entry.NOMBRE);

        Date dateFormat;
        try {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(escaneos.get(position).FECHA+ " " +escaneos.get(position).HORA);
            fecha.setText(new SimpleDateFormat("EEEE").format(dateFormat) +" "+ dateFormat.toLocaleString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        garita.setVisibility(View.GONE); //OCULTA NOMBRE, NOSE SI SE USARÀ
        garita.setText(nombre_garita);
        acceso.setText(estado);

        return layoutitem;
    }

}

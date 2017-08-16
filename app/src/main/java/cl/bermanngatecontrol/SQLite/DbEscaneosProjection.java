package cl.bermanngatecontrol.SQLite;


import android.provider.BaseColumns;

public class DbEscaneosProjection {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="escaneos";

        public static final String ID = "ID";
        public static final String FECHA = "FECHA";
        public static final String HORA = "HORA";
        public static final String GARITA = "GARITA";
        public static final String RUT = "RUT";
        public static final String ESTADO = "ESTADO";
        public static final String CLIENTE = "CLIENTE";
        public static final String SYNC = "SYNC";
    }
}

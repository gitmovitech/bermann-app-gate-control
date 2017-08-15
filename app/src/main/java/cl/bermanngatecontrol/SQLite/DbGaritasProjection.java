package cl.bermanngatecontrol.SQLite;


import android.provider.BaseColumns;

public class DbGaritasProjection {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="garitas";

        public static final String ID = "ID";
        public static final String NOMBRE = "NOMBRE";
        public static final String CLIENTE = "CLIENTE";
    }
}

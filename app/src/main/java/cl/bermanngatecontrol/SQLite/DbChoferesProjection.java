package cl.bermanngatecontrol.SQLite;


import android.provider.BaseColumns;

public class DbChoferesProjection {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="choferes";

        public static final String ID = "ID";
        public static final String RUT = "RUT";
        public static final String NOMBRE = "NOMBRE";
        public static final String APELLIDO_PATERNO = "APELLIDO_PATERNO";
        public static final String ESTADO = "ESTADO";
        public static final String FOTO = "FOTO";
    }
}

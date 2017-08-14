package cl.bermanngatecontrol;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbChoferesHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = DbChoferesProjection.Entry.TABLE_NAME+".db";

    public DbChoferesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ DbChoferesProjection.Entry.TABLE_NAME;
        query += " ("+ DbChoferesProjection.Entry.ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        query += DbChoferesProjection.Entry.RUT+" INTEGER NOT NULL,";
        query += DbChoferesProjection.Entry.NOMBRE+" TEXT NOT NULL,";
        query += DbChoferesProjection.Entry.APELLIDO_PATERNO+" TEXT NOT NULL,";
        query += DbChoferesProjection.Entry.ESTADO+" INTEGER NOT NULL,";
        query += DbChoferesProjection.Entry.FOTO+" TEXT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

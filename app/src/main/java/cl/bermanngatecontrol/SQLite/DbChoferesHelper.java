package cl.bermanngatecontrol.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbChoferesHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = DbChoferesProjection.Entry.TABLE_NAME+".db";

    String[] projection = {
        DbChoferesProjection.Entry.ID,
        DbChoferesProjection.Entry.RUT,
        DbChoferesProjection.Entry.NOMBRE,
        DbChoferesProjection.Entry.APELLIDO_PATERNO,
        DbChoferesProjection.Entry.ESTADO,
        DbChoferesProjection.Entry.FOTO,
        DbChoferesProjection.Entry.CELULAR,
        DbChoferesProjection.Entry.ID_GARITA
    };;

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
        query += DbChoferesProjection.Entry.ESTADO+" TEXT NOT NULL,";
        query += DbChoferesProjection.Entry.FOTO+" TEXT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE "+DbChoferesProjection.Entry.TABLE_NAME+" ADD COLUMN "+DbChoferesProjection.Entry.CELULAR+" TEXT NULL");
            db.execSQL("ALTER TABLE "+DbChoferesProjection.Entry.TABLE_NAME+" ADD COLUMN "+DbChoferesProjection.Entry.ID_GARITA+" TEXT NULL");
        }
    }

    public void deleteAll(){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(DbChoferesProjection.Entry.TABLE_NAME, null, null);
        db.close();
    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        db.insert(DbChoferesProjection.Entry.TABLE_NAME, null, values);
    }

    public Cursor getByRut(String rut){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DbChoferesProjection.Entry.TABLE_NAME, projection, DbChoferesProjection.Entry.RUT + " = ?", new String[]{ String.valueOf(rut) }, null, null, null);
        return cursor;
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DbChoferesProjection.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }
}

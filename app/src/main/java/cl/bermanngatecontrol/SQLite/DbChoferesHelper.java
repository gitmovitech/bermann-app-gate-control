package cl.bermanngatecontrol.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
        query += DbChoferesProjection.Entry.ESTADO+" TEXT NOT NULL,";
        query += DbChoferesProjection.Entry.FOTO+" TEXT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

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
        String[] projection = {
                DbChoferesProjection.Entry.ID,
                DbChoferesProjection.Entry.RUT,
                DbChoferesProjection.Entry.NOMBRE,
                DbChoferesProjection.Entry.APELLIDO_PATERNO,
                DbChoferesProjection.Entry.ESTADO,
                DbChoferesProjection.Entry.FOTO
        };
        Cursor cursor = db.query(DbChoferesProjection.Entry.TABLE_NAME, projection, DbChoferesProjection.Entry.RUT+"=?", new String[]{String.valueOf(rut)}, null, null, null);
        return cursor;
    }
}

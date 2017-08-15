package cl.bermanngatecontrol.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbGaritasHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = DbGaritasProjection.Entry.TABLE_NAME+".db";

    String[] projection = {
        DbGaritasProjection.Entry.ID,
        DbGaritasProjection.Entry.NOMBRE,
        DbGaritasProjection.Entry.CLIENTE
    };;

    public DbGaritasHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ DbGaritasProjection.Entry.TABLE_NAME;
        query += " ("+ DbGaritasProjection.Entry.ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        query += DbGaritasProjection.Entry.NOMBRE+" TEXT NOT NULL,";
        query += DbGaritasProjection.Entry.CLIENTE+" TEXT NOT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void deleteAll(){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(DbGaritasProjection.Entry.TABLE_NAME, null, null);
        db.close();
    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        db.insert(DbGaritasProjection.Entry.TABLE_NAME, null, values);
    }

    public Cursor getById(String id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DbGaritasProjection.Entry.TABLE_NAME, projection, DbGaritasProjection.Entry.ID + " = ?", new String[]{ String.valueOf(id) }, null, null, null);
        return cursor;
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DbGaritasProjection.Entry.TABLE_NAME, projection, null, null, null, null, null);
        return cursor;
    }
}

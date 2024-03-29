package cl.bermanngatecontrol.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbEscaneosHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = DbEscaneosProjection.Entry.TABLE_NAME+".db";

    String[] projection = {
        DbEscaneosProjection.Entry.ID,
        DbEscaneosProjection.Entry.FECHA,
        DbEscaneosProjection.Entry.HORA,
        DbEscaneosProjection.Entry.GARITA,
        DbEscaneosProjection.Entry.RUT,
        DbEscaneosProjection.Entry.ESTADO,
        DbEscaneosProjection.Entry.SYNC,
        DbEscaneosProjection.Entry.CLIENTE,
        DbEscaneosProjection.Entry.ENTRADA,
        DbEscaneosProjection.Entry.ORDEN
    };;

    public DbEscaneosHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+ DbEscaneosProjection.Entry.TABLE_NAME;
        query += " ("+ DbEscaneosProjection.Entry.ID+" INTEGER NULL,";
        query += DbEscaneosProjection.Entry.FECHA+" TEXT NOT NULL,";
        query += DbEscaneosProjection.Entry.HORA+" TEXT NOT NULL,";
        query += DbEscaneosProjection.Entry.GARITA+" TEXT NOT NULL,";
        query += DbEscaneosProjection.Entry.RUT+" TEXT NOT NULL,";
        query += DbEscaneosProjection.Entry.ESTADO+" TEXT NOT NULL,";
        query += DbEscaneosProjection.Entry.SYNC+" TEXT NOT NULL,";
        query += DbEscaneosProjection.Entry.ENTRADA+" TEXT NOT NULL,";
        query += DbEscaneosProjection.Entry.ORDEN+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        query += DbEscaneosProjection.Entry.CLIENTE+" TEXT NOT NULL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE "+DbEscaneosProjection.Entry.TABLE_NAME+" ADD COLUMN "+DbEscaneosProjection.Entry.ENTRADA+" TEXT NULL");
        }
    }

    public void setSync(int id){
        ContentValues values = new ContentValues();
        values.put(DbEscaneosProjection.Entry.SYNC, "0");
        SQLiteDatabase db = getReadableDatabase();
        db.update(DbEscaneosProjection.Entry.TABLE_NAME, values, DbEscaneosProjection.Entry.ORDEN + " = ?", new String[]{ String.valueOf(id) });
        db.close();
    }

    public void deleteAll(){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(DbEscaneosProjection.Entry.TABLE_NAME, null, null);
        db.close();
    }

    public void insert(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();
        db.insert(DbEscaneosProjection.Entry.TABLE_NAME, null, values);
    }

    public Cursor getById(String id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DbEscaneosProjection.Entry.TABLE_NAME, projection, DbEscaneosProjection.Entry.ID + " = ?", new String[]{ String.valueOf(id) }, null, null, null);
        return cursor;
    }

    public Cursor getAllNotSync(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DbEscaneosProjection.Entry.TABLE_NAME, projection, DbEscaneosProjection.Entry.SYNC + " = ?", new String[]{ "0" }, null, null, null);
        return cursor;
    }

    public Cursor getAll(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DbEscaneosProjection.Entry.TABLE_NAME, projection, null, null, null, null, DbEscaneosProjection.Entry.ORDEN + " DESC");
        return cursor;
    }
}

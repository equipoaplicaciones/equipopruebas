package com.example.git_practica;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//BACK por Valverde
public class DBHelper extends SQLiteOpenHelper {

    // Nombre de la base de datos y tabla
    private static final String DATABASE_NAME = "LoginApp.db";
    private static final String TABLE_USERS = "users";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla para usuarios
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si existe la tabla, eliminarla y crearla de nuevo
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Método para insertar un usuario
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLE_USERS + " (username, password) VALUES (?, ?)", new Object[]{username, password});
        return true;
    }

    // Método para validar el login
    public boolean validateLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username = ? AND password = ?", new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }
}


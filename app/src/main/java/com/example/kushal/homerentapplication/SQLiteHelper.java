package com.example.kushal.homerentapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {


    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql)
    {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    // Insert data
    public void insertData(String name, String address, String number, String room, String price, byte[] image)
    {
        SQLiteDatabase database = getWritableDatabase();

        // Query to insert data
        String sql = "INSERT INTO RECORD VALUES (NULL, ?, ?, ?, ?, ?, ?)"; // RECORD is table name

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);
        statement.bindString(2, address);
        statement.bindString(3, number);
        statement.bindString(4, room);
        statement.bindString(5, price);
        statement.bindBlob(6, image);

        statement.executeInsert();
    }

    // Update Data
    public void updateData(String name, String address, String number, String room, String price, byte[] image ,int id)
    {
        SQLiteDatabase database = getWritableDatabase();

        // Query to update record
        String sql = "UPDATE RECORD SET name=?, address=?, number=?, room=?, price=?, image=? WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, name);
        statement.bindString(2, address);
        statement.bindString(3, number);
        statement.bindString(4, room);
        statement.bindString(5, price);
        statement.bindBlob(6, image);
        statement.bindDouble(7, id);

        statement.execute();
        database.close();
    }

    // Delete data
    public void deleteData(int id)
    {
        SQLiteDatabase database = getWritableDatabase();

        // Query to delete data
        String sql = "DELETE FROM RECORD WHERE id=?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, id);

        statement.execute();
        database.close();
    }

    public Cursor getData(String sql)
    {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

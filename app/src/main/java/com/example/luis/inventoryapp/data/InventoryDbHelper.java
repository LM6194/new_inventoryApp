package com.example.luis.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.luis.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Luis on 11/24/2017.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "inventory.db";

    /**
     * Database version. if I change the database schema, I must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE TABLE engagement_ring(id INTEGER PRIMARY KEY, stock_id INTEGER,supplier TEXT
        // description TEXT, quantity INTEGER, cost INTEGER, price INTEGER):
        // Create a String that contains the SQL statement to Create the engagement_ring TABLE.
        String SQL_CREATE_ENGAGEMENT_RING_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + "("
                +InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +InventoryEntry.COLUMN_STOCK_ID + " INTEGER NOT NULL, "
                +InventoryEntry.COLUMN_SUPPLIER + " TEXT, "
                +InventoryEntry.COLUMN_DETAILS + " TEXT, "
                +InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                +InventoryEntry.COLUMN_COST + " INTEGER NOT NULL, "
                +InventoryEntry.COLUMN_PRICE + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_ENGAGEMENT_RING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // I don't do any thing yet
    }
}

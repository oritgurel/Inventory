package com.oritmalki.inventory.products.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductsDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ProductsDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public ProductsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + ProductsContract.ProductEntry.TABLE_NAME + " ("
                + ProductsContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE + " FLOAT, "
                + ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME + " INTEGER NOT NULL DEFAULT 0, "
                + ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT);";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

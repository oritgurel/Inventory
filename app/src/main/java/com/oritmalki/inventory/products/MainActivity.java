package com.oritmalki.inventory.products;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.oritmalki.inventory.products.data.ProductsContract;


public class MainActivity extends AppCompatActivity {

    private TextView mDisplayTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDisplayTv = findViewById(R.id.display_tv);
        insertProduct();
        showDbInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showDbInfo();
    }

    private void showDbInfo() {
        String[] projection = {
                ProductsContract.ProductEntry._ID,
                ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        Cursor cursor = getContentResolver().query(
                ProductsContract.ProductEntry.CONTENT_URI, projection, null, null, null);


        try {

            mDisplayTv.setText("The products table contains " + cursor.getCount() + " products.\n\n");
            mDisplayTv.append(ProductsContract.ProductEntry._ID + " - " +
                    ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME + " - " +
                    ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE + " - " +
                    ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                    ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME + " - " +
                    ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                Float currentPrice = cursor.getFloat(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                mDisplayTv.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplier + " - " +
                        currentSupplierPhone));
            }
        } finally {

            cursor.close();
        }
    }

    private void insertProduct() {
        ContentValues values = new ContentValues();
        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME, "Mystic Shampoo");
        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE, 29.90f);
        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, 3);
        values.put(ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME, ProductsContract.ProductEntry.SUPPLIER_NAME_LOREAL);
        values.put(ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "0525890478");

        Uri newUri = getContentResolver().insert(ProductsContract.ProductEntry.CONTENT_URI, values);
    }

}




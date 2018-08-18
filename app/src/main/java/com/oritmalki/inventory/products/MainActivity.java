package com.oritmalki.inventory.products;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.oritmalki.inventory.products.data.ProductsContract;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER_ID = 0;
    ProductsCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO init fab

        ListView productListView = findViewById(R.id.activity_main_listview);
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);
        mCursorAdapter = new ProductsCursorAdapter(this, null, true);
        productListView.setAdapter(mCursorAdapter);
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditProductActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductsContract.ProductEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);

    }



    private void insertProduct() {
        ContentValues values = new ContentValues();
        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME, "Mystic Shampoo");
        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE, 29.90f);
        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, 3);
        values.put(ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME, ProductsContract.ProductEntry.SUPPLIER_NAME_LOREAL);
        values.put(ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "0525890478");

        Uri newUri = getContentResolver().insert(ProductsContract.ProductEntry.CONTENT_URI, values);
        getSupportLoaderManager().restartLoader(PRODUCT_LOADER_ID, null, this);


    }

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductsContract.ProductEntry.CONTENT_URI, null, null);
        getSupportLoaderManager().restartLoader(PRODUCT_LOADER_ID, null, this);
        Log.v("MainActivity", rowsDeleted + " rows deleted from product database");
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                ProductsContract.ProductEntry._ID,
                ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this,
                ProductsContract.ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            mCursorAdapter.swapCursor(null);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_insert_product:
                insertProduct();
                return true;
            case R.id.menu_main_delete_all_enteries:
                deleteAllProducts();
                mCursorAdapter.notifyDataSetChanged();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    //    private void showDbInfo() {
//        String[] projection = {
//                ProductsContract.ProductEntry._ID,
//                ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME,
//                ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE,
//                ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
//                ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME,
//                ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};
//
//        Cursor cursor = getContentResolver().query(
//                ProductsContract.ProductEntry.CONTENT_URI, projection, null, null, null);
//
//
//        try {
//
//            mDisplayTv.setText("The products table contains " + cursor.getCount() + " products.\n\n");
//            mDisplayTv.append(ProductsContract.ProductEntry._ID + " - " +
//                    ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME + " - " +
//                    ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE + " - " +
//                    ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " - " +
//                    ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME + " - " +
//                    ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");
//
//            // Figure out the index of each column
//            int idColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry._ID);
//            int nameColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME);
//            int priceColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE);
//            int quantityColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
//            int supplierColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME);
//            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
//
//            while (cursor.moveToNext()) {
//                int currentID = cursor.getInt(idColumnIndex);
//                String currentName = cursor.getString(nameColumnIndex);
//                Float currentPrice = cursor.getFloat(priceColumnIndex);
//                int currentQuantity = cursor.getInt(quantityColumnIndex);
//                String currentSupplier = cursor.getString(supplierColumnIndex);
//                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);
//
//                mDisplayTv.append(("\n" + currentID + " - " +
//                        currentName + " - " +
//                        currentPrice + " - " +
//                        currentQuantity + " - " +
//                        currentSupplier + " - " +
//                        currentSupplierPhone));
//            }
//        } finally {
//
//            cursor.close();
//        }
//    }
}




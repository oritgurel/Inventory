package com.oritmalki.inventory.products.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.regex.Pattern;

public class ProductProvider extends ContentProvider {

    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY, ProductsContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY, ProductsContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    private static final String PHONE_REGEX = "^[0][1-9]\\d{8}$|^[1-9]\\d{8}$";

    private ProductsDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new ProductsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductsContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductsContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductsContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        String name = values.getAsString(ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer quantity = values.getAsInteger(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        Float price = values.getAsFloat(ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product requires valid price");
        }

        String supplierPhoneNumber = values.getAsString(ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (!Pattern.matches(PHONE_REGEX, supplierPhoneNumber)) {
            throw new IllegalArgumentException("Product requires 10 digit supplier phone number");
        }


        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ProductsContract.ProductEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductsContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        if (values.containsKey(ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Float price = values.getAsFloat(ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        if (values.containsKey(ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumber = values.getAsString(ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (!Pattern.matches(PHONE_REGEX, supplierPhoneNumber)) {
                throw new IllegalArgumentException("Product requires 10 digit supplier phone number");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ProductsContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(ProductsContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductsContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductsContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductsContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductsContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }
}

package com.oritmalki.inventory.products.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ProductsContract {

    public ProductsContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.oritmalki.inventory.products";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    public static final class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public final static String _ID = BaseColumns._ID;
        public final static String TABLE_NAME = "products";
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

        public final static int SUPPLIER_CODE_NIVEA = 1;
        public final static int SUPPLIER_CODE_LOREAL = 2;
        public final static int SUPPLIER_CODE_REVLON = 3;

        public static boolean isValidSupplierCode(int supplierCode) {
            if (supplierCode == SUPPLIER_CODE_NIVEA || supplierCode == SUPPLIER_CODE_LOREAL || supplierCode == SUPPLIER_CODE_REVLON) {
                return true;
            }
            return false;
        }


    }
}

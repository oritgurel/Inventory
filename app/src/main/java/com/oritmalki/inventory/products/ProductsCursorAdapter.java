package com.oritmalki.inventory.products;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.oritmalki.inventory.products.data.ProductsContract;

public class ProductsCursorAdapter extends CursorAdapter {

    public ProductsCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_main_product_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTv = view.findViewById(R.id.product_list_item_name_tv);
        TextView priceTv = view.findViewById(R.id.product_list_item_price_tv);
        TextView quantityTv = view.findViewById(R.id.product_list_item_quantity_tv);
        TextView supplierNameTv = view.findViewById(R.id.product_list_item_supplier_tv);

        int nameColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNmaeColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME);

        String name = cursor.getString(nameColumnIndex);
        Float price = cursor.getFloat(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        String supplierName = cursor.getString(supplierNmaeColumnIndex);

        nameTv.setText(name);
        priceTv.setText(String.valueOf(price));
        quantityTv.setText(String.valueOf(quantity));
        supplierNameTv.setText(supplierName);

    }
}

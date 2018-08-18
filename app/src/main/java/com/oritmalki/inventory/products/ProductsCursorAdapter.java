package com.oritmalki.inventory.products;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oritmalki.inventory.products.data.ProductProvider;
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
    public void bindView(final View view, final Context context, final Cursor cursor) {

        TextView nameTv = view.findViewById(R.id.product_list_item_name_tv);
        TextView priceTv = view.findViewById(R.id.product_list_item_price_tv);
        final TextView quantityTv = view.findViewById(R.id.product_list_item_quantity_tv);
        ImageButton saleBtn = view.findViewById(R.id.product_list_sale_btn);


        int nameColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        final int idColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry._ID);

        String name = cursor.getString(nameColumnIndex);
        Float price = cursor.getFloat(priceColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        final int itemId = cursor.getInt(idColumnIndex);

        nameTv.setText(name);
        priceTv.setText(String.valueOf(price));
        quantityTv.setText(String.valueOf(quantity));
        saleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO not working
                int position = cursor.getPosition();
                cursor.moveToPosition(position);
                ContentValues decrementQuantity = new ContentValues();
                decrementQuantity.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);
                String selection = ProductsContract.ProductEntry._ID + "=?";
                String[] itemIdArgs = {String.valueOf(itemId)};
                context.getContentResolver().update(ProductsContract.ProductEntry.CONTENT_URI, decrementQuantity, selection, itemIdArgs);
                notifyDataSetChanged();
            }
        });

    }
}

package com.oritmalki.inventory.products;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.oritmalki.inventory.products.data.ProductsContract;

public class EditProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentProductUri;

    private EditText mNameEt;
    private EditText mQuantityEt;
    private EditText mPriceEt;
    private EditText mSupplierPhoneEt;
    private Spinner mSupplierNameSpinner;
    private Button mOrderFromSupplerBtn;

    private String mSupplierName = "";

    private boolean mProductHasChanged = false;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        mCurrentProductUri = getIntent().getData();
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.activity_title_add_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.activity_title_edit_product));
            getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        initViews();

    }

    private void initViews() {
        mNameEt = findViewById(R.id.product_name_et);
        mQuantityEt = findViewById(R.id.product_quantity_et);
        mPriceEt = findViewById(R.id.product_price_et);
        mSupplierPhoneEt = findViewById(R.id.supplier_phone_number);
        mSupplierNameSpinner = findViewById(R.id.supplier_name_spinner);
        mOrderFromSupplerBtn = findViewById(R.id.call_supplier_btn);
        mOrderFromSupplerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mSupplierPhoneEt.getText().toString().trim()));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);

            }
        });

        mNameEt.setOnTouchListener(mOnTouchListener);
        mQuantityEt.setOnTouchListener(mOnTouchListener);
        mPriceEt.setOnTouchListener(mOnTouchListener);
        mSupplierPhoneEt.setOnTouchListener(mOnTouchListener);
        mSupplierNameSpinner.setOnTouchListener(mOnTouchListener);
        initSpinner();

    }

    private void initSpinner() {
        ArrayAdapter supplierNameSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.product_supplier_name_options, android.R.layout.simple_spinner_item);
        supplierNameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSupplierNameSpinner.setAdapter(supplierNameSpinnerAdapter);
        mSupplierNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    mSupplierName = selection;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplierName = "";
            }
        });
    }

    private void saveProduct() {
        String name = mNameEt.getText().toString().trim();
        String quantity = mQuantityEt.getText().toString().trim();
        String price = mPriceEt.getText().toString().trim();
        String supplierPhone = mSupplierPhoneEt.getText().toString().trim();

        if (mCurrentProductUri == null && TextUtils.isEmpty(name) || TextUtils.isEmpty(quantity) || TextUtils.isEmpty(price)
                || mSupplierName == "") {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME, name);
        int quantityInt = 0;
        if (!TextUtils.isEmpty(quantity)) {
            quantityInt = Integer.parseInt(quantity);
            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityInt);
        }
        float priceFloat = 0.0f;
        if (!TextUtils.isEmpty(price)) {
            priceFloat = Float.parseFloat(price);
            values.put(ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE, priceFloat);
        }

        values.put(ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME, mSupplierName);
        values.put(ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductsContract.ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.toast_error_insert_product), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_success_insert_product), Toast.LENGTH_LONG).show();
            }
        } else {
            // existing product, edit mode
            int rowsChanged = getContentResolver().update(ProductsContract.ProductEntry.CONTENT_URI, values, null, null);

            if (rowsChanged == 0) {
                Toast.makeText(this, getString(R.string.toast_error_update_product), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_update_success), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_product_menu, menu);
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        super.onPrepareOptionsPanel(view, menu);
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                break;
            case R.id.action_delete:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                ProductsContract.ProductEntry._ID,
                ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            if (cursor == null || cursor.getCount() < 1) { return; }
            if (cursor.moveToFirst()) {
                int nameColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NAME);
                int quantityColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
                int priceColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_PRICE);
                int idColumnIndex = cursor.getColumnIndex(ProductsContract.ProductEntry._ID);
                int supplierNameColIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_SUPPLIER_NAME);
                int supplierPhoneColIndex = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);


                String name = cursor.getString(nameColumnIndex);
                Float price = cursor.getFloat(priceColumnIndex);
                int quantity = cursor.getInt(quantityColumnIndex);
                int itemId = cursor.getInt(idColumnIndex);
                String supplierName = cursor.getString(supplierNameColIndex);
                String supplierPhone = cursor.getString(supplierPhoneColIndex);

                mNameEt.setText(name);
                mPriceEt.setText(String.valueOf(price));
                mQuantityEt.setText(String.valueOf(quantity));
                mSupplierPhoneEt.setText(supplierPhone);
                switch (supplierName) {
                    case ProductsContract.ProductEntry.SUPPLIER_NAME_LOREAL:
                        mSupplierNameSpinner.setSelection(0);
                        break;
                    case ProductsContract.ProductEntry.SUPPLIER_NAME_REVLON:
                        mSupplierNameSpinner.setSelection(1);
                        break;
                    case ProductsContract.ProductEntry.SUPPLIER_NAME_NIVEA:
                        mSupplierNameSpinner.setSelection(2);
                        break;
                    default:
                        mSupplierNameSpinner.setSelection(0);
                        break;
                }

            }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mNameEt.setText("");
        mPriceEt.setText("");
        mQuantityEt.setText("");
        mSupplierPhoneEt.setText("");
        mSupplierNameSpinner.setSelection(0);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discadrButtonOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message_unsaved_changes)
                .setPositiveButton(getString(R.string.dialog_discard_btn), discadrButtonOnClickListener);
                builder.setNegativeButton(getString(R.string.dialog_btn_keep_editing), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
    }
}

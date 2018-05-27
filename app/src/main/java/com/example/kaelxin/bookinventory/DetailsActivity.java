package com.example.kaelxin.bookinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.kaelxin.bookinventory.data.BookContract;
import com.example.kaelxin.bookinventory.data.MyQueryHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.edit_bookname)
    EditText editTextBookName;
    @BindView(R.id.edit_book_price)
    EditText editTextBookPrice;
    @BindView(R.id.edit_quantity)
    EditText editTextQuantity;
    @BindView(R.id.edit_supp_name)
    EditText editTextSuppName;
    @BindView(R.id.edit_supp_phone_id)
    EditText editTextSuppPhone;

    private Uri currentUri;
    private static final int MAGIC_ZERO = 0;
    private static final int BOOK_LOADER = 0;
    private boolean mBookHasChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBookHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        currentUri = intent.getData();
        //this means that we have a new book to insert
        if (currentUri == null) {
            setTitle(getString(R.string.new_book));
            //invalidateOptionsMenu() is used to say Android, that contents of menu have changed, and menu should be redrawn
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.existing_book));
            getLoaderManager().initLoader(BOOK_LOADER, null, this);
        }

        editTextBookName.setOnTouchListener(touchListener);
        editTextQuantity.setOnTouchListener(touchListener);
        editTextBookPrice.setOnTouchListener(touchListener);
        editTextSuppName.setOnTouchListener(touchListener);
        editTextSuppPhone.setOnTouchListener(touchListener);

    }

    //this creates the menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }

    // after creating the menu options with onprepare you can choose which items you want to show or hide .
    //Changing Menu Items at Run Time
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (currentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_book);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                insertBookEntry();
                finish();
                return true;
            case R.id.delete_book:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChanges(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!mBookHasChanged) {
            NavUtils.navigateUpFromSameTask(DetailsActivity.this);
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChanges(discardButtonClickListener);

    }

    private void insertBookEntry() {

        String bookName = editTextBookName.getText().toString().trim();
        String bookPrice = editTextBookPrice.getText().toString().trim();
        String bookQuantity = editTextQuantity.getText().toString().trim();
        String supplierName = editTextSuppName.getText().toString().trim();
        String supplierPhone = editTextSuppPhone.getText().toString().trim();

        // if nothing no field has any input then just return.
        if (currentUri == null && TextUtils.isEmpty(bookName) && TextUtils.isEmpty(bookPrice)
                && TextUtils.isEmpty(bookQuantity) && TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierPhone)
                ) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        ContentValues values = new ContentValues();

        values.put(BookContract.BookEntry.COL_BOOK_NAME, bookName);
        values.put(BookContract.BookEntry.COL_BOOK_PRICE, bookPrice);
        values.put(BookContract.BookEntry.COL_BOOK_QUANTITY, bookQuantity);
        values.put(BookContract.BookEntry.COL_SUPPLIER_NAME, supplierName);
        values.put(BookContract.BookEntry.COL_SUPPLIER_PHONE, supplierPhone);
        MyQueryHandler handler = new MyQueryHandler(getContentResolver());

        // this is a new pet that we are gonna add to the database
        if (currentUri == null) {


            handler.startInsert(MAGIC_ZERO, null, BookContract.BookEntry.CONTENT_URI, values);
        } else {
            //this pet already exists so we will update it

            handler.startUpdate(MAGIC_ZERO, null, BookContract.BookEntry.CONTENT_URI, values, null, null);
        }
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Are you sure you want to delete that entry?");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (currentUri != null) {
                    MyQueryHandler handler = new MyQueryHandler(getContentResolver());
                    handler.startDelete(MAGIC_ZERO, null, currentUri, null, null);
                    finish();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void showUnsavedChanges(DialogInterface.OnClickListener discardButtonClickListener) {

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.discard_or_edit);
        alertBuilder.setPositiveButton(getString(R.string.discard), discardButtonClickListener);
        alertBuilder.setNegativeButton(getString(R.string.keep_edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COL_BOOK_NAME,
                BookContract.BookEntry.COL_BOOK_QUANTITY,
                BookContract.BookEntry.COL_BOOK_PRICE,
                BookContract.BookEntry.COL_SUPPLIER_NAME,
                BookContract.BookEntry.COL_SUPPLIER_PHONE
        };

        return new CursorLoader(this,
                BookContract.BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {

            int bookNameCOLindex = cursor.getColumnIndex(BookContract.BookEntry.COL_BOOK_NAME);
            int bookQuantityCOLindex = cursor.getColumnIndex(BookContract.BookEntry.COL_BOOK_QUANTITY);
            int bookPriceCOLindex = cursor.getColumnIndex(BookContract.BookEntry.COL_BOOK_PRICE);
            int supplierNameCOLindex = cursor.getColumnIndex(BookContract.BookEntry.COL_SUPPLIER_NAME);
            int supplierPhoneCOLindex = cursor.getColumnIndex(BookContract.BookEntry.COL_SUPPLIER_PHONE);

            String bookName = cursor.getString(bookNameCOLindex);
            Integer bookQuantity = cursor.getInt(bookQuantityCOLindex);
            Double bookPrice = cursor.getDouble(bookPriceCOLindex);
            String supplierName = cursor.getString(supplierNameCOLindex);
            String supplierPhone = cursor.getString(supplierPhoneCOLindex);

            editTextBookName.setText(bookName);
            editTextQuantity.setText(String.valueOf(bookQuantity));
            editTextBookPrice.setText(String.valueOf(bookPrice));
            editTextSuppName.setText(supplierName);
            editTextSuppPhone.setText(supplierPhone);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        editTextBookName.getText().clear();
        editTextBookPrice.getText().clear();
        editTextQuantity.getText().clear();
        editTextSuppName.getText().clear();
        editTextSuppPhone.getText().clear();


    }
}

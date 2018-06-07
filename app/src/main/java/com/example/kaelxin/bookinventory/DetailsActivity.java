package com.example.kaelxin.bookinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kaelxin.bookinventory.data.BookContract;
import com.example.kaelxin.bookinventory.data.MyQueryHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

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
    @BindView(R.id.plusButtonID)
    ImageButton plusButton;
    @BindView(R.id.minusButtonID)
    ImageButton minusButton;
    @BindView(R.id.addimagebutton)
    ImageButton addImageButton;
    @BindView(R.id.imageView)
    ImageView bookImage;

    private Uri currentUri;
    private static final int MAGIC_ZERO = 0;
    private static final int BOOK_LOADER = 0;
    private static final int REQUEST_IMAGE_CODE = 1;
    private boolean mBookHasChanged = false;
    private int quantity_it = 0;
    private Uri imageUri;

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

        final Intent intent = getIntent();
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

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentUri != null) {
                    Integer quantity = Integer.parseInt(editTextQuantity.getText().toString());
                    quantity++;
                    editTextQuantity.setText(String.valueOf(quantity));
                    mBookHasChanged = true;
                } else {
                    quantity_it++;
                    editTextQuantity.setText(String.valueOf(quantity_it));
                }
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUri != null) {
                    Integer quantity = Integer.parseInt(editTextQuantity.getText().toString());
                    if (quantity > 0) {
                        quantity--;
                        editTextQuantity.setText(String.valueOf(quantity));
                        mBookHasChanged = true;
                    } else {
                        Toast.makeText(DetailsActivity.this, R.string.cannot_negativequant, Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (quantity_it > 0) {
                        quantity_it--;
                        editTextQuantity.setText(String.valueOf(quantity_it));
                    } else {
                        Toast.makeText(DetailsActivity.this, R.string.cannot_negativequant, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imageIntent;

                if (Build.VERSION.SDK_INT < 19) {
                    imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    imageIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    imageIntent.addCategory(Intent.CATEGORY_OPENABLE);
                }

                imageIntent.setType("image/*");
                startActivityForResult(imageIntent, REQUEST_IMAGE_CODE);
                mBookHasChanged = true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);

        if (requestCode == REQUEST_IMAGE_CODE && resultCode == RESULT_OK && imageData != null) {
            imageUri = imageData.getData();
            bookImage.setImageBitmap(getBitMapFromUri(imageUri));
        }
    }

    private Bitmap getBitMapFromUri(Uri imageUri) {

        if (imageUri == null || TextUtils.isEmpty(imageUri.toString())) {
            return null;
        }
        //get dimensions of the image
        int imageWidth = bookImage.getWidth();
        int imageHeight = bookImage.getHeight();

        InputStream inputStream = null;
        Bitmap bp = null;

        try {
            inputStream = getContentResolver().openInputStream(imageUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            if (inputStream != null) {
                inputStream.close();
            }

            int photoWidht = options.outWidth;
            int photoHeight = options.outHeight;

            int scaleFactor = 1;

            if (imageWidth > photoWidht || imageHeight > photoHeight) {

                float heightScale = photoHeight / imageHeight;
                float widthScale = photoWidht / imageWidth;
                scaleFactor = Math.round(heightScale > widthScale ? heightScale : widthScale);

            }

            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;
            options.inPurgeable = true;

            inputStream = this.getContentResolver().openInputStream(imageUri);
            bp = BitmapFactory.decodeStream(inputStream, null, options);
            if (inputStream != null) {
                inputStream.close();
            }
            return bp;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bp;
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
            MenuItem menuItem1 = menu.findItem(R.id.call);
            menuItem1.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                if (insertBookEntry()) {
                    finish();
                }
                return true;
            case R.id.delete_book:
                showDeleteConfirmationDialog();
                return true;
            case R.id.call:
                String calling_number = editTextSuppPhone.getText().toString();
                if (isPhoneValid(calling_number)) {
                    Uri call = Uri.parse("tel:" + calling_number);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, call);
                    startActivity(callIntent);
                }
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

    private boolean insertBookEntry() {

        String bookName = editTextBookName.getText().toString().trim();
        String bookPrice = editTextBookPrice.getText().toString().trim();
        String bookQuantity = editTextQuantity.getText().toString().trim();
        String supplierName = editTextSuppName.getText().toString().trim();
        String supplierPhone = editTextSuppPhone.getText().toString().trim();
        String imageUriBook;
        if (imageUri == null) {
            imageUriBook = null;
        } else {
            imageUriBook = imageUri.toString();
        }
        // if nothing no field has any input then just return.
        if (currentUri == null && TextUtils.isEmpty(bookName) && TextUtils.isEmpty(bookPrice)
                && TextUtils.isEmpty(bookQuantity) && TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierPhone)
                && TextUtils.isEmpty(imageUriBook)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return true;
        }

        ContentValues values = new ContentValues();

        values.put(BookContract.BookEntry.COL_BOOK_IMAGE, imageUriBook);


        if (bookName.equals("")) {
            editTextBookName.setError(getString(R.string.error_nedname));
            return false;
        } else {
            values.put(BookContract.BookEntry.COL_BOOK_NAME, bookName);
        }
        values.put(BookContract.BookEntry.COL_BOOK_PRICE, bookPrice);
        values.put(BookContract.BookEntry.COL_BOOK_QUANTITY, bookQuantity);
        if (supplierName.equals("")) {
            editTextSuppName.setError(getString(R.string.errsupname));
            return false;
        } else {
            values.put(BookContract.BookEntry.COL_SUPPLIER_NAME, supplierName);
        }
        if (supplierPhone.equals("")) {
            editTextSuppPhone.setError(getString(R.string.errsupphone));
            return false;
        } else {
            if (isPhoneValid(supplierPhone)) {
                values.put(BookContract.BookEntry.COL_SUPPLIER_PHONE, supplierPhone);
            } else {
                editTextSuppPhone.setError(getString(R.string.invalphone));
                return false;
            }
        }

        MyQueryHandler handler = new MyQueryHandler(getContentResolver());

        // this is a new pet that we are gonna add to the database
        if (currentUri == null) {
            handler.startInsert(MAGIC_ZERO, null, BookContract.BookEntry.CONTENT_URI, values);
            return true;
        } else {
            //this pet already exists so we will update it
            handler.startUpdate(MAGIC_ZERO, null, currentUri, values, null, null);
            return true;
        }

    }

    private boolean isPhoneValid(String phoneNumber) {

        boolean check;
        if (!Pattern.matches("[a-zA-Z]+", phoneNumber)) {
            check = phoneNumber.length() >= 6 && phoneNumber.length() <= 13;
        } else {
            check = false;
        }
        return check;
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.sure_todo);
        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (currentUri != null) {
                    MyQueryHandler handler = new MyQueryHandler(getContentResolver());
                    handler.startDelete(MAGIC_ZERO, null, currentUri, null, null);
                    finish();
                }
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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
                BookContract.BookEntry.COL_SUPPLIER_PHONE,
                BookContract.BookEntry.COL_BOOK_IMAGE
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
            int bookImageCOLindex = cursor.getColumnIndex(BookContract.BookEntry.COL_BOOK_IMAGE);

            String bookName = cursor.getString(bookNameCOLindex);
            Integer bookQuantity = cursor.getInt(bookQuantityCOLindex);
            Double bookPrice = cursor.getDouble(bookPriceCOLindex);
            String supplierName = cursor.getString(supplierNameCOLindex);
            String supplierPhone = cursor.getString(supplierPhoneCOLindex);
            String bookyImage = cursor.getString(bookImageCOLindex);
            if (bookyImage == null) {
                bookImage.setImageResource(R.drawable.defaultimage);
            } else {
                Uri bookImageUri = Uri.parse(bookyImage);
                imageUri = bookImageUri;
                bookImage.setImageBitmap(getBitMapFromUri(bookImageUri));
            }
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
        bookImage.setImageResource(0);

    }
}

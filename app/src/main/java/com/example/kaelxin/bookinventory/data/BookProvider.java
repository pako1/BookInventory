package com.example.kaelxin.bookinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BookProvider extends ContentProvider {

    private BookDbHelper bookDbHelper;

    private static final UriMatcher sUrimatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUrimatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOK, BookContract.BOOKS);

        sUrimatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOK + "/#", BookContract.BOOK_ID);

    }

    @Override
    public boolean onCreate() {
        bookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = bookDbHelper.getReadableDatabase();
        int match = sUrimatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case BookContract.BOOKS:
                cursor = db.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BookContract.BOOK_ID:
                selection = BookContract.BookEntry.COL_BOOK_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("couldn't query URI" + uri);
        }

        // ayto koitaei twra sto sigkekrimeno cursor. an ayto to cursor exei diaforetika pragmata apo to prohgoymeno cursor
        // tote ayto simainei oti prepei na kanei update thn lista.

        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }


    /*
     *  The purpose of this method is to return a String that describes the type of the data stored at the input Uri.
     *  This String is known as the MIME type, which can also be referred to as content type.
     * The Android system will check the MIME type of that URI to determine which app component on the device can best handle
     * your request.(If the URI happens to be a content URI, then the system will check with the corresponding ContentProvider
     * to ask for the MIME type using the getType() method.)
     */

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = sUrimatcher.match(uri);

        switch (match) {
            case BookContract.BOOKS:
                //this determines that you have one list as a uri
                return BookContract.BookEntry.BOOK_LIST_TYPE;
            case BookContract.BOOK_ID:
                //and this just a single item
                return BookContract.BookEntry.BOOK_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("wrong MIME type" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        int match = sUrimatcher.match(uri);
        switch (match) {
            case BookContract.BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("couldn't insert book with uri" + uri);
        }

    }

    private Uri insertBook(Uri uri, ContentValues values) {

        SQLiteDatabase db = bookDbHelper.getWritableDatabase();

        long inserted_row = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, inserted_row);


    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match = sUrimatcher.match(uri);

        SQLiteDatabase db = bookDbHelper.getWritableDatabase();
        int rows_deteled;
        switch (match) {

            case BookContract.BOOKS:
                rows_deteled = db.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BookContract.BOOK_ID:
                selection = BookContract.BookEntry.COL_BOOK_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows_deteled = db.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("deletion is not supported for" + uri);
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows_deteled;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match = sUrimatcher.match(uri);

        switch (match) {
            case BookContract.BOOKS:
                if (values != null) {
                    return updateBook(uri, values, selection, selectionArgs);
                }

            case BookContract.BOOK_ID:
                selection = BookContract.BookEntry.COL_BOOK_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                if (values != null) {
                    return updateBook(uri, values, selection, selectionArgs);
                }
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = bookDbHelper.getWritableDatabase();


        // if there is an empty content values then nothing would change so just return that 0 rows have been updated.
        if (values.size() == 0) {
            return 0;
        }

        if (values.containsKey(BookContract.BookEntry.COL_BOOK_IMAGE)) {
            String bookImage = values.getAsString(BookContract.BookEntry.COL_BOOK_IMAGE);
            if (bookImage == null) {
                throw new IllegalArgumentException("book requires an image" + uri);
            }
        }

        //if you update a entry it could be that you dont update every single column but only a few.
        //Therefore you have to check if the specific entry is being updated .
        // This will be done when you call containsKey in the contentvalues.
        //if the column exists in the contentvalue then you can take the input.
        //if not then the user has let the data as it is.
        if (values.containsKey(BookContract.BookEntry.COL_BOOK_NAME)) {
            String bookName = values.getAsString(BookContract.BookEntry.COL_BOOK_NAME);
            if (bookName == null) {
                throw new IllegalArgumentException("Book requires a name" + uri);
            }
        }
        if (values.containsKey(BookContract.BookEntry.COL_BOOK_QUANTITY)) {
            Integer bookQuantity = values.getAsInteger(BookContract.BookEntry.COL_BOOK_QUANTITY);
            if (bookQuantity == null) {
                throw new IllegalArgumentException("quantity is needed" + uri);
            }
        }
        if (values.containsKey(BookContract.BookEntry.COL_BOOK_PRICE)) {
            Double bookPrice = values.getAsDouble(BookContract.BookEntry.COL_BOOK_PRICE);
            if (bookPrice == null) {
                throw new IllegalArgumentException("price label needs a price" + uri);
            }
        }
        if (values.containsKey(BookContract.BookEntry.COL_SUPPLIER_PHONE)) {
            String supplierName = values.getAsString(BookContract.BookEntry.COL_BOOK_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("supplier needs a name" + uri);
            }

        }
        if (values.containsKey(BookContract.BookEntry.COL_SUPPLIER_PHONE)) {
            String supplierPhone = values.getAsString(BookContract.BookEntry.COL_SUPPLIER_PHONE);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("suppliers phone is necessary" + uri);
            }
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return db.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);


    }
}

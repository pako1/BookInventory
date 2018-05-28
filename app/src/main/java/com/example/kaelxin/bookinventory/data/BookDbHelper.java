package com.example.kaelxin.bookinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;


    BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQLITE_CREATE_BOOK_TABLE = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " ("
                + BookContract.BookEntry.COL_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookContract.BookEntry.COL_BOOK_NAME + " TEXT NOT NULL, "
                + BookContract.BookEntry.COL_BOOK_PRICE + " REAL NOT NULL DEFAULT 0, "
                + BookContract.BookEntry.COL_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookContract.BookEntry.COL_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BookContract.BookEntry.COL_SUPPLIER_PHONE + " TEXT NOT NULL );";

        db.execSQL(SQLITE_CREATE_BOOK_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

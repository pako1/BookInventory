package com.example.kaelxin.bookinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BookContract {


    private BookContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.kaelxin.bookinventory";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOK = "books";

    public static final int BOOKS = 100;

    public static final int BOOK_ID = 101;

    public static final class BookEntry implements BaseColumns {

        public static final String BOOK_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOK;

        public static final String BOOK_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + " / " + CONTENT_AUTHORITY + " / " + PATH_BOOK;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOK);

        public static final String TABLE_NAME = "books";

        public static final String COL_BOOK_ID = BaseColumns._ID;
        public static final String COL_BOOK_NAME = "name";
        public static final String COL_BOOK_QUANTITY = "quantity";
        public static final String COL_BOOK_PRICE = "price";
        public static final String COL_SUPPLIER_NAME = "supplier";
        public static final String COL_SUPPLIER_PHONE = "phone";

    }


}

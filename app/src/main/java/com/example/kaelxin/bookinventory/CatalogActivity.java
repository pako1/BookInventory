package com.example.kaelxin.bookinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.kaelxin.bookinventory.data.BookContract;
import com.example.kaelxin.bookinventory.data.MyQueryHandler;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.book_ListView)
    ListView listView;

    @BindView(R.id.empty_view)
    View emptyView;

    @BindView(R.id.floating_button)
    FloatingActionButton fab;

    private CursorBookAdapter cursorBookAdapter;

    private static final int BOOK_LOADER = 0;

    private MyQueryHandler myQueryHandler;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_activity);
        ButterKnife.bind(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailsIntent = new Intent(CatalogActivity.this, DetailsActivity.class);
                startActivity(detailsIntent);
            }
        });
        listView.setEmptyView(emptyView);
        cursorBookAdapter = new CursorBookAdapter(this, null);
        listView.setAdapter(cursorBookAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent bookDetailsIntent = new Intent(CatalogActivity.this, DetailsActivity.class);
                bookDetailsIntent.setData(ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id));
                startActivity(bookDetailsIntent);
            }
        });

        getLoaderManager().initLoader(BOOK_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.catalog_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dummy_insert:
                insertBook();
                return true;
            case R.id.delete_all:
                deleteAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAll() {
        myQueryHandler = new MyQueryHandler(getContentResolver());
        myQueryHandler.startDelete(0, null, BookContract.BookEntry.CONTENT_URI, null, null);
    }

    private void insertBook() {
        ContentValues values = new ContentValues();
        final Double[] prices = new Double[]{32.2, 30.2, 12.2, 38.2, 22.2};
        final String[] names = new String[]{"Marios alven", "helen dido", "fil katz", "Bill kotslo", "Kiko livo"};
        final String[] books = new String[]{"The end of time", "hey lady", "food & love", "android for dummies", "Fif livo"};
        final String[] phones = new String[]{"6946537855", "6946537855", "6946537322", "6943537833", "6946000854"};
        Random rand = new Random();

        String name = names[rand.nextInt(names.length)];
        Double price = prices[rand.nextInt(prices.length)];
        String book = books[rand.nextInt(books.length)];
        String phone = phones[rand.nextInt(phones.length)];
        int quantity = rand.nextInt(10) + 1;

        values.put(BookContract.BookEntry.COL_BOOK_NAME, book);
        values.put(BookContract.BookEntry.COL_BOOK_QUANTITY, quantity);
        values.put(BookContract.BookEntry.COL_BOOK_PRICE, price);
        values.put(BookContract.BookEntry.COL_SUPPLIER_NAME, name);
        values.put(BookContract.BookEntry.COL_SUPPLIER_PHONE, phone);
        myQueryHandler = new MyQueryHandler(getContentResolver());

        myQueryHandler.startInsert(0, null, BookContract.BookEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {
                BookContract.BookEntry.COL_BOOK_ID,
                BookContract.BookEntry.COL_BOOK_NAME,
                BookContract.BookEntry.COL_BOOK_QUANTITY,
                BookContract.BookEntry.COL_BOOK_PRICE,
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
        cursorBookAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorBookAdapter.swapCursor(null);
    }
}

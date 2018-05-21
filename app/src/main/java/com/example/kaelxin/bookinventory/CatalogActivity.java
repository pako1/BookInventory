package com.example.kaelxin.bookinventory;

import android.app.LoaderManager;
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
import android.widget.ListView;

import com.example.kaelxin.bookinventory.data.BookContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.book_ListView)
    ListView listView;

    @BindView(R.id.empty_view)
    View emptyView;

    private CursorBookAdapter cursorBookAdapter;

    private static final int PET_LOADER = 0;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_activity);
        ButterKnife.bind(this);
        FloatingActionButton fab = new FloatingActionButton(this);
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

        getLoaderManager().initLoader(PET_LOADER, null, this);

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
        getContentResolver().delete(BookContract.BookEntry.CONTENT_URI, null, null);
    }

    private void insertBook() {

        ContentValues values = new ContentValues();

        values.put(BookContract.BookEntry.COL_BOOK_NAME, "big blue");
        values.put(BookContract.BookEntry.COL_BOOK_QUANTITY, 13);
        values.put(BookContract.BookEntry.COL_BOOK_PRICE, 32.2);
        values.put(BookContract.BookEntry.COL_SUPPLIER_NAME, "Xiao mao");
        values.put(BookContract.BookEntry.COL_SUPPLIER_PHONE, "6946537855");

        getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String projection[] = {
                BookContract.BookEntry.COL_BOOK_ID,
                BookContract.BookEntry.COL_BOOK_NAME,
                BookContract.BookEntry.COL_BOOK_QUANTITY,
                BookContract.BookEntry.COL_BOOK_PRICE
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

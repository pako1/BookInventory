package com.example.kaelxin.bookinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaelxin.bookinventory.data.BookContract;
import com.example.kaelxin.bookinventory.data.MyQueryHandler;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CursorBookAdapter extends CursorAdapter {

    private static final int MAGIC_ZERO = 0;

    CursorBookAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.book_cardview, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        ButterKnife.bind(this, view);
        ViewHolder holder = new ViewHolder(view);

        int nameColIndex = cursor.getColumnIndex(BookContract.BookEntry.COL_BOOK_NAME);
        int priceColIndex = cursor.getColumnIndex(BookContract.BookEntry.COL_BOOK_PRICE);
        int quantityColIndex = cursor.getColumnIndex(BookContract.BookEntry.COL_BOOK_QUANTITY);

        holder.bookNameView.setText(cursor.getString(nameColIndex));
        Double price = cursor.getDouble(priceColIndex);
        String finalprice = String.valueOf(price) + context.getString(R.string.money);
        holder.bookPriceView.setText(finalprice);
        final Integer quantity = cursor.getInt(quantityColIndex);
        holder.bookQuantityView.setText(String.valueOf(quantity));
        final Uri currentUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry._ID)));
        holder.buyButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    int newQuantity = quantity - 1;
                    ContentValues values = new ContentValues();

                    values.put(BookContract.BookEntry.COL_BOOK_QUANTITY, newQuantity);

                    MyQueryHandler myQueryHandler = new MyQueryHandler(context.getContentResolver());
                    myQueryHandler.startUpdate(MAGIC_ZERO, null, currentUri, values, null, null);
                    context.getContentResolver().notifyChange(currentUri, null);

                } else {
                    Toast.makeText(context, R.string.outofstock, Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    static class ViewHolder {
        @BindView(R.id.cv_book_name)
        TextView bookNameView;
        @BindView(R.id.cv_book_price)
        TextView bookPriceView;
        @BindView(R.id.cv_book_quantity)
        TextView bookQuantityView;
        /*@BindView(R.id.cv_book_image)
        ImageView bookImageView;*/
        @BindView(R.id.buyButton)
        Button buyButtonView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}



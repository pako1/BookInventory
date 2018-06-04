package com.example.kaelxin.bookinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaelxin.bookinventory.data.BookContract;
import com.example.kaelxin.bookinventory.data.MyQueryHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
        int bookImageCOLIndex = cursor.getColumnIndex(BookContract.BookEntry.COL_BOOK_IMAGE);
        String imageString = cursor.getString(bookImageCOLIndex);

        Uri imageUri = Uri.parse(imageString);
        //get dimensions of the image
        int imageWidth = 54;
        int imageHeight = 54;

        holder.bookImageView.setImageBitmap(getBitMapFromUri(context, imageUri,imageWidth,imageHeight));
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

    private Bitmap getBitMapFromUri(Context context, Uri imageUri,int imageWidth,int imageHeight) {

        if (imageUri == null || TextUtils.isEmpty(imageUri.toString())) {
            return null;
        }

        InputStream inputStream = null;
        Bitmap bp = null;

        try {
            inputStream = context.getContentResolver().openInputStream(imageUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            int photoWidht = options.outWidth;
            int photoHeight = options.outHeight;

            int scaleFactor = Math.min(photoWidht / imageWidth, photoHeight / imageHeight);

            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;
            options.inPurgeable = true;

            inputStream = context.getContentResolver().openInputStream(imageUri);
            bp = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
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

    static class ViewHolder {
        @BindView(R.id.cv_book_name)
        TextView bookNameView;
        @BindView(R.id.cv_book_price)
        TextView bookPriceView;
        @BindView(R.id.cv_book_quantity)
        TextView bookQuantityView;
        @BindView(R.id.cv_book_image)
        ImageView bookImageView;
        @BindView(R.id.buyButton)
        Button buyButtonView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}



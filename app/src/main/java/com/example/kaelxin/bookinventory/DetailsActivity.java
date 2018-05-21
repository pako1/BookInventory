package com.example.kaelxin.bookinventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import butterknife.BindView;

public class DetailsActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


    }
}

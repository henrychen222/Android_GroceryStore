package com.example.weichen.grocery2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewCouponActivity extends AppCompatActivity {

    Button btnBack;
    TextView txtDiscount;
    LinearLayout lin;
    MyDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_info);

        //receive the coupon data from ListCouponsActivity
        Intent intent = getIntent();
        int id = intent.getIntExtra("coupon_id",-1);

        btnBack = (Button)findViewById(R.id.btnBack_ViewCoupon);
        txtDiscount = (TextView)findViewById(R.id.txtDiscount_view);
        lin = (LinearLayout)findViewById(R.id.linProductList_AddCoupon);

        btnBack.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        Intent i = new Intent( ViewCouponActivity.this, ListCouponsActivity.class);
                        startActivity(i);
                    }
                }
        );

        dbHelper = new MyDBHelper(this);

        if(id > 0) {
            Coupon coupon = dbHelper.selectCouponById(id);
            if(coupon == null){
                Toast.makeText(this,"Unable to find Coupon with id "+id,Toast.LENGTH_LONG);
            }
            else {
                txtDiscount.setText(String.format("%.2f",coupon.getDiscount()));
                ArrayList<Product> products = coupon.getProducts();
                for(int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);
                    LinearLayout layout = new LinearLayout(getApplicationContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);

                    TextView name = new TextView(getApplicationContext());
                    name.setText(product.getName());
                    name.setTextColor(Color.BLACK);
                    name.setTextSize(17);
                    TextView price = new TextView(getApplicationContext());
                    price.setText(String.format("%.2f",product.getPrice()));
                    price.setTextColor(Color.BLACK);
                    name.setTextSize(17);
                    price.setPadding(15,0,0,0);   //have distance from the name

                    layout.addView(name, 0);
                    layout.addView(price, 1);

                    lin.addView(layout);

                    ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                    LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    nameParams.weight = 1;
                    name.setLayoutParams(nameParams);

                    LinearLayout.LayoutParams priceParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    priceParams.weight = 0;
                    price.setLayoutParams(priceParams);
                }
            }
        }
    }
}
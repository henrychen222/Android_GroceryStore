// The largest discount: 20%
package com.example.weichen.grocery2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ShoppingListLargestDiscountActivity extends AppCompatActivity implements View.OnClickListener{

    EditText txtBudget;
    Button btnGenerate, btnBack;
    MyDBHelper dbHelper;
    LinearLayout lin;
    private final static int offColor = Color.argb(255,220,220,220);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.largest_discount);

        txtBudget = (EditText)findViewById(R.id.txtBudget);
        btnGenerate = (Button)findViewById(R.id.btnGenerate);
        btnBack = (Button) findViewById(R.id.btnBack_GenLarDis);
        lin = (LinearLayout) findViewById(R.id.linItemList);

        dbHelper = new MyDBHelper(this);

        btnGenerate.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack_GenLarDis:
                Intent intent = new Intent(ShoppingListLargestDiscountActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btnGenerate:
                //make sure the Budget input shouldn't be null
                if(txtBudget.getText().toString().isEmpty()) {
                    Toast.makeText(this,"Enter a budget",Toast.LENGTH_LONG).show();
                    return;
                }
                lin.removeAllViews();
                double budget = Double.parseDouble(txtBudget.getText().toString());

                //generate discount
                ArrayList<Coupon> coupons = dbHelper.getAllCoupons();
                Coupon.DiscountResult result = Coupon.generateLargestDiscount(coupons,budget);

                //display results
                TextView txtGenCost = new TextView(getApplicationContext());
                txtGenCost.setText("Total Expense: "+String.format("%.2f",result.cost));
                txtGenCost.setTextColor(Color.BLUE);
                lin.addView(txtGenCost);

                TextView txtGenDiscount = new TextView(getApplicationContext());
                txtGenDiscount.setText("Largest Discount: "+String.format("%.2f",result.discount));
                txtGenDiscount.setTextColor(Color.BLUE);
                lin.addView(txtGenDiscount);

                //find the coupons
                for (int i = 0; i < result.coupons.size(); i++) {
                    Coupon coupon = result.coupons.get(i);

                    //parent Layout contains sub-layout and coupon discount textview
                    LinearLayout layout = new LinearLayout(getApplicationContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.setGravity(Gravity.CENTER_VERTICAL);


                    //sub-layout for product name
                    LinearLayout linItems = new LinearLayout(getApplicationContext());
                    linItems.setOrientation(LinearLayout.VERTICAL);

                    //coupon discount Textview
                    TextView txtCouponDiscount = new TextView(getApplicationContext());
                    txtCouponDiscount.setText(String.format("%.2f",coupon.getDiscount()));
                    txtCouponDiscount.setTextColor(Color.RED);

                    if(i % 2 == 0){
                        layout.setBackgroundColor(offColor);
                    }

                    //list all product name
                    ArrayList<Product> items = coupon.getProducts();
                    for (int j = 0; j < items.size(); j++) {
                        TextView txtItem = new TextView(getApplicationContext());
                        txtItem.setTextColor(Color.RED);
                        txtItem.setText(items.get(j).getName());
                        linItems.addView(txtItem);
                    }

                    //put the sub-layout of the product name and coupon discount Textview into the parent layout
                    layout.addView(linItems,0);
                    layout.addView(txtCouponDiscount,1);
                    //put the parent layout into the whole screen LinearLayout
                    lin.addView(layout);

                    //LinearLayout.LayoutParams(int width, int height)
                    LinearLayout.LayoutParams linItemsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    linItemsParams.weight = 1;
                    LinearLayout.LayoutParams couponDiscountParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    couponDiscountParams.weight = 0;
//                   LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//                   layoutParams.setMargins(0,15,0,0);

                    //set width and height for sub-layout and coupon discount Textview
                    linItems.setLayoutParams(linItemsParams);
                    txtCouponDiscount.setLayoutParams(couponDiscountParams);
//                   layout.setLayoutParams(layoutParams);
                }

                    break;

        }
    }
}
package com.example.weichen.grocery2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class AddCouponActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnBack, btnAdd;
    ArrayList<CheckBox> checkBoxes = new ArrayList<>();    //creat an arraylist to save data with checkbox type
    EditText txtDiscount;
    LinearLayout lin;
    MyDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_coupons);

        btnBack = (Button) findViewById(R.id.btnBack_ViewCoupon);
        btnAdd = (Button) findViewById(R.id.btnAddCoupon);
        txtDiscount = (EditText) findViewById(R.id.txtDiscount);
        lin = (LinearLayout) findViewById(R.id.linProductList_AddCoupon);

        btnBack.setOnClickListener(this);
        btnAdd.setOnClickListener(this);


        dbHelper = new MyDBHelper(this);
        ArrayList<Product> products = dbHelper.getAllProducts();
        if (products == null) {
            Toast.makeText(this, "Unable to retrieve products", Toast.LENGTH_SHORT).show();
        } else {
            if (products.size() == 0) {
                Toast.makeText(this, "No products to list", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);
                    LinearLayout layout = new LinearLayout(getApplicationContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);

                    CheckBox checkBox = new CheckBox(getApplicationContext());
                    checkBox.setText(product.getName());
                    checkBox.setTextColor(Color.BLACK);
                    checkBox.setTag(product);

                    layout.addView(checkBox, 0);

                    checkBoxes.add(checkBox);
                    lin.addView(layout);

                    ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                    LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    checkBox.setLayoutParams(checkBoxParams);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack_ViewCoupon:
                Intent intent = new Intent(AddCouponActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btnAddCoupon:
                //assert we have a discount amount greater than 0
                double discount = Double.parseDouble(txtDiscount.getText().toString());    //covert the discount number to double
                ArrayList<Product> products = new ArrayList<>();                    //create an arraylist to save Product data

                //add the item to products ArrayList if the checkbox being selected
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isChecked()) {
                        products.add((Product) checkBoxes.get(i).getTag());    //
                    }
                }
                //discount should be greater than 0 and should at least pick up on item for adding coupon
                if (discount <= 0)
                    Toast.makeText(this, "Discount cannot be lower than 0", Toast.LENGTH_SHORT).show();
                else if (products.size() == 0)
                    Toast.makeText(this, "select the product for coupon", Toast.LENGTH_SHORT).show();
                else {
                    //
                    if (dbHelper.insertCoupon(new Coupon(discount, products))) {
                        Toast.makeText(this, "Coupon has been successfully added", Toast.LENGTH_SHORT).show();
                        //after add coupons set the discount to be empty and all checkboxes unchecked
                        txtDiscount.setText("");
                        for (int i = 0; i < checkBoxes.size(); i++) {
                            checkBoxes.get(i).setChecked(false);
                        }
                    }
                }
                break;
        }
    }
}
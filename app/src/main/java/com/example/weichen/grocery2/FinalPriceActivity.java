//The final price: 20%

package com.example.weichen.grocery2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FinalPriceActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnBack, btnFindDiscount;
    TextView txtResult;
    LinearLayout lin;
    MyDBHelper dbHelper;
    ArrayList<CheckBox> checkBoxes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.low_best_discount);
        btnBack = (Button) findViewById(R.id.btnBack_UserList);
        btnFindDiscount = (Button)findViewById(R.id.btnFindDiscount);
        txtResult = (TextView)findViewById(R.id.txtDiscount_UserList);
        lin = (LinearLayout) findViewById(R.id.linProductList_UserList);

        btnBack.setOnClickListener(this);
        btnFindDiscount.setOnClickListener(this);


        dbHelper = new MyDBHelper(this);
        ArrayList<Product> products = dbHelper.getAllProducts();
        if (products == null){
            Toast.makeText(this,"Unable to retrieve products",Toast.LENGTH_SHORT).show();
        }
        else {
            if(products.size() == 0) {
                Toast.makeText(this,"No products to list",Toast.LENGTH_SHORT).show();
            }
            else {
                for(int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);
                    LinearLayout layout = new LinearLayout(getApplicationContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);

                    CheckBox checkBox = new CheckBox(getApplicationContext());
                    checkBox.setText(product.getName());
                    checkBox.setTextColor(Color.BLACK);
                    checkBox.setTag(product);

                    TextView price = new TextView(getApplicationContext());
                    price.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    price.setText(String.format("%.2f",product.getPrice()));
                    price.setTextColor(Color.BLACK);
                    price.setPadding(15,0,0,0);   //have distance from the name

                    layout.addView(checkBox,0);
                    layout.addView(price,1);

                    checkBoxes.add(checkBox);
                    lin.addView(layout);

                    ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                    LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    checkBoxParams.weight = 1;

                    LinearLayout.LayoutParams priceParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    priceParams.weight = 0;

                    checkBox.setLayoutParams(checkBoxParams);
                    price.setLayoutParams(priceParams);
                }
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack_UserList:
                Intent intent = new Intent(FinalPriceActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btnFindDiscount:
                ArrayList<Product> products = new ArrayList<>();
                double originalCost = 0.00;
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if(checkBoxes.get(i).isChecked()){
                        Product product = (Product)checkBoxes.get(i).getTag();
                        products.add(product);
                        originalCost += product.getPrice();
                    }
                }
                ArrayList<Coupon> coupons = dbHelper.getAllCoupons();
                for (int i = coupons.size() - 1; i >= 0; i--) {
                    boolean bad = false;
                    for (int j = 0; j < coupons.get(i).getProducts().size(); j++) {
                        if(!products.contains(coupons.get(i).getProducts().get(j))){ //if we haven't checked a product the coupon contains, remove it
                            bad = true;
                            break;
                        }
                    }
                    if(bad)
                        coupons.remove(i);
                }
                Coupon.DiscountResult result = Coupon.generateLargestDiscount(coupons);
                txtResult.setText("" + String.format("%.2f",originalCost - result.discount) + " after " + String.format("%.2f",result.discount) + " off");
//                txtResult.setText("" + String.format("%.2f",originalCost - result.discount) + " after a discount of " + String.format("%.2f",result.discount));

                break;

        }
    }
}
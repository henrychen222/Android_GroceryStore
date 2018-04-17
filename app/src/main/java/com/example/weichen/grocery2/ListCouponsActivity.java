package com.example.weichen.grocery2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListCouponsActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnBack, btnDelete;
    LinearLayout lin;
    MyDBHelper dbHelper;
    ArrayList<RadioButton> radioButtons = new ArrayList<>();
    ArrayList<TextView> textViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_all_coupons);

        btnBack = (Button)findViewById(R.id.btnBack_ListCoupons);
        btnDelete = (Button)findViewById(R.id.btnDeleteCoupons);
        lin = (LinearLayout)findViewById(R.id.linCouponList_ListCoupons);

        btnBack.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        dbHelper = new MyDBHelper(this);

        refreshList();
    }

    private void refreshList() {
        lin.removeAllViews();
        ArrayList<Coupon> coupons = dbHelper.getAllCouponsWOutProducts();
        if (coupons == null){
            Toast.makeText(this,"Unable to retrieve coupons",Toast.LENGTH_SHORT).show();
        }
        else {
            if(coupons.size() == 0) {
                Toast.makeText(this,"No coupons to list",Toast.LENGTH_SHORT).show();
            }
            else {
                for(int i = 0; i < coupons.size(); i++) {
                    final Coupon coupon = coupons.get(i);
                    LinearLayout layout = new LinearLayout(getApplicationContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);

                    RadioButton radio = new RadioButton(getApplicationContext());
                    radio.setTag(coupon);
                    radio.setOnClickListener(this);
                    radioButtons.add(radio);

                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(coupon.getId().toString());
                    textView.setTag(coupon.getId());
                    textView.setOnClickListener(this);
                    textView.setTextColor(Color.BLUE);
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    textViews.add(textView);

                    layout.addView(radio,0);
                    layout.addView(textView,1);
                    lin.addView(layout);

                    ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                    LinearLayout.LayoutParams radioParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    radioParams.weight = 0;
                    radio.setLayoutParams(radioParams);

                    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    textParams.weight = 1;
                    textView.setLayoutParams(textParams);
                }
            }
        }
    }

    @Override
    public void onClick(View view){
        if(view.getId() == btnBack.getId()){
            Intent i = new Intent( ListCouponsActivity.this, MainActivity.class);
            startActivity(i);
        }
        else if(view.getId() == btnDelete.getId()){
            boolean found = false;
            for (int i = 0; i < radioButtons.size(); i++) {
                found |= radioButtons.get(i).isChecked();
                if(radioButtons.get(i).isChecked()){
                    dbHelper.deleteCouponById(((Coupon)radioButtons.get(i).getTag()).getId());
                }
            }
            if(found)
                refreshList();
            else
                Toast.makeText(this,"No items were selected for deletion",Toast.LENGTH_SHORT).show();
        }
        else {
            if(view instanceof RadioButton) {
                for (int i = 0; i < radioButtons.size(); i++) {
                    radioButtons.get(i).setChecked(false);
                }
                ((RadioButton)view).setChecked(true);
            } else if(view instanceof TextView) {
                TextView textView = (TextView) view;
                Intent i = new Intent(ListCouponsActivity.this, ViewCouponActivity.class);
                //transfer coupon data to ViewCouponActivity based on "coupon_id"
                i.putExtra("coupon_id", (int) textView.getTag());
                startActivity(i);
            }
        }

    }
}
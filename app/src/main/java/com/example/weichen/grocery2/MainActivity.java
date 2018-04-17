package com.example.weichen.grocery2;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btnAddProduct = (Button)findViewById(R.id.btnAddProduct);
        final Button btnAddCoupon = (Button)findViewById(R.id.btnAddCoupon);
        final Button btnListCoupons = (Button)findViewById(R.id.btnListCoupons);
        final Button btnListProducts = (Button)findViewById(R.id.btnListProducts);

        final Button btnAppLargestDiscount = (Button)findViewById(R.id.btnAppLargestDiscount);
        final Button btnBestDisList = (Button)findViewById(R.id.btnBestDisList);

        btnAddProduct.setOnClickListener(this);
        btnListProducts.setOnClickListener(this);
        btnAddCoupon.setOnClickListener(this);
        btnAppLargestDiscount.setOnClickListener(this);
        btnListCoupons.setOnClickListener(this);
        btnBestDisList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnAddProduct:
                Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
                startActivity(intent);
                break;
            case R.id.btnListProducts:
                Intent intent1= new Intent(MainActivity.this, ListProductsActivity.class);
                startActivity(intent1);
                break;
            case R.id.btnAddCoupon:
                Intent intent2= new Intent(MainActivity.this, AddCouponActivity.class);
                startActivity(intent2);
                break;
            case R.id.btnAppLargestDiscount:
                Intent intent3= new Intent(MainActivity.this, ShoppingListLargestDiscountActivity.class);
                startActivity(intent3);
                break;
            case R.id.btnListCoupons:
                Intent intent4= new Intent(MainActivity.this, ListCouponsActivity.class);
                startActivity(intent4);
                break;
            case R.id.btnBestDisList:
                Intent intent5= new Intent(MainActivity.this, FinalPriceActivity.class);
                startActivity(intent5);
                break;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reset_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Reset Grocery system");
        alertDialog.setMessage("Confirm to reset the system");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                cw.deleteDatabase(MyDBHelper.DB_NAME);
                Toast.makeText(getApplicationContext(),"Grocery system successfully being reset",Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
        return true;
    }
}
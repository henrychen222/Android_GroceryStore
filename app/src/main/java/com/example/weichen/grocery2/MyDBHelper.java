package com.example.weichen.grocery2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;


 public class MyDBHelper extends SQLiteOpenHelper {
    private static final String DB_PATH = "/data/data/com.example.weichen.grocery2/databases/";
    public static final String DB_NAME = "shopping_db";
    private static String DB_ABS_LOC = MyDBHelper.DB_PATH + MyDBHelper.DB_NAME;
    private final Context context;

    public static final String PRODUCT_TABLE_NAME = "products";
    public static final String PRODUCT_COLUMN_ID = "_id";
    public static final String PRODUCT_COLUMN_NAME = "name";
    public static final String PRODUCT_COLUMN_PRICE = "price";

    public static final String COUPON_TABLE_NAME = "coupons";
    public static final String COUPON_COLUMN_ID = "_id";
    public static final String COUPON_COLUMN_DISCOUNT = "discount";

    public static final String COUPON_PRODUCTS_TABLE_NAME = "coupon_products";
    public static final String COUPON_PRODUCTS_COLUMN_COUPON = "coupon";
    public static final String COUPON_PRODUCTS_COLUMN_PRODUCT = "product";

    public MyDBHelper(Context context) {
        super(context, MyDBHelper.DB_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create product table
        db.execSQL("CREATE TABLE " + PRODUCT_TABLE_NAME + "(" +
                PRODUCT_COLUMN_ID + " INTEGER PRIMARY KEY," +
                PRODUCT_COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                PRODUCT_COLUMN_PRICE + " REAL NOT NULL" +
                ")");

        //create coupon table
        db.execSQL("CREATE TABLE " + COUPON_TABLE_NAME + "(" +
                COUPON_COLUMN_ID +" INTEGER PRIMARY KEY," +
                COUPON_COLUMN_DISCOUNT +" REAL NOT NULL" +
                ")");

        //create coupon_products join table
        db.execSQL("CREATE TABLE " + COUPON_PRODUCTS_TABLE_NAME + "(" +
                COUPON_PRODUCTS_COLUMN_COUPON + " INTEGER," +
                COUPON_PRODUCTS_COLUMN_PRODUCT + " INTEGER," +
                "PRIMARY KEY("+COUPON_PRODUCTS_COLUMN_COUPON + "," + COUPON_PRODUCTS_COLUMN_PRODUCT + ")," +
                "FOREIGN KEY(" + COUPON_PRODUCTS_COLUMN_COUPON + ") REFERENCES " + COUPON_TABLE_NAME + "(" + COUPON_COLUMN_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COUPON_PRODUCTS_COLUMN_PRODUCT + ") REFERENCES " + PRODUCT_TABLE_NAME + "(" + PRODUCT_COLUMN_ID + ") ON DELETE CASCADE" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+PRODUCT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+COUPON_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+COUPON_PRODUCTS_TABLE_NAME);
        onCreate(db);
    }

     //Add products
    public boolean insertProduct(Product product) {
        if(product == null)
            return false;
        //getWritableDatabase(): Create and/or open a database that will be used for reading and writing
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+PRODUCT_COLUMN_ID+" FROM "+PRODUCT_TABLE_NAME+" WHERE "+PRODUCT_COLUMN_NAME+" = ?",new String[]{product.getName()});
        if(cursor.moveToNext()){
            Toast.makeText(context,"Item with name \""+product.getName()+"\" already exists",Toast.LENGTH_LONG).show();
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(PRODUCT_COLUMN_NAME,product.getName());
        values.put(PRODUCT_COLUMN_PRICE,product.getPrice());
        long id = db.insert(PRODUCT_TABLE_NAME,null,values);
        if(id < 0)
            Toast.makeText(context,"Unable to make product",Toast.LENGTH_SHORT).show();
        db.close();
        return id != -1;
    }

    public boolean updateProduct(Product product) {
        if(product == null)
            return false;
        ArrayList<Product> arrayList = new ArrayList<>();
        arrayList.add(product);
        return updateProducts(arrayList);
    }

     //Update the prices of several products at the same time
    public boolean updateProducts(ArrayList<Product> products){
        if(products == null)
            return false;
        SQLiteDatabase db = getWritableDatabase();
        boolean ret = false;
        try {
            db.beginTransaction();
            for (int i = 0; i < products.size(); i++) {
                if(products.get(i).getId() < 1)
                    throw new Exception("Product is missing ID");
                ContentValues values = new ContentValues();
                values.put(PRODUCT_COLUMN_NAME,products.get(i).getName());
                values.put(PRODUCT_COLUMN_PRICE,products.get(i).getPrice());
                db.update(PRODUCT_TABLE_NAME,values,PRODUCT_COLUMN_ID + " = ? ", new String[]{products.get(i).getId().toString()});
            }
            db.setTransactionSuccessful();
            ret = true;
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
            db.close();
        }

        return ret;
    }

    public boolean insertOrUpdateProduct(Product product) {
        if(product == null)
            return false;
        return product.getId() < 0 ? insertProduct(product) : updateProduct(product);
    }

     //Add coupons
    public boolean insertCoupon(Coupon coupon) {
        boolean ret = false;
        if(coupon == null || !coupon.valid())
            return ret;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COUPON_COLUMN_DISCOUNT, coupon.getDiscount());
        db.beginTransaction();
        try {
            long coupon_id = db.insert(COUPON_TABLE_NAME, null, values);

            if (coupon_id < 0) {
                throw new Exception("Unable to make coupon");
            }

            for (Product product : coupon.getProducts()) {
                values.clear();
                values.put(COUPON_PRODUCTS_COLUMN_COUPON, coupon_id);
                values.put(COUPON_PRODUCTS_COLUMN_PRODUCT, product.getId());
                if (db.insert(COUPON_PRODUCTS_TABLE_NAME, null, values) < 0) {
                    throw new Exception("Unable to add product to coupon");
                }
            }
            db.setTransactionSuccessful();
            ret = true;
        } catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return ret;
    }

    public boolean updateCoupon(Coupon coupon) {
        boolean ret = false;
        if(coupon == null || !coupon.valid(true))
            return ret;

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        String coupon_id = coupon.getId().toString();
        //update the coupon
        try {
            ContentValues values = new ContentValues();
            values.put(COUPON_COLUMN_DISCOUNT, coupon.getDiscount());
            db.update(COUPON_TABLE_NAME,values,COUPON_COLUMN_ID + " = ? ", new String[]{coupon_id});

            //update all the coupon_products
            ArrayList<Product> newProducts = (ArrayList<Product>)coupon.getProducts().clone();
            Cursor cursor = db.rawQuery("SELECT ALL "+COUPON_PRODUCTS_COLUMN_PRODUCT+" FROM "+COUPON_PRODUCTS_TABLE_NAME+" WHERE "+COUPON_PRODUCTS_COLUMN_COUPON+" = ?",new String[]{coupon.getId().toString()});
            while(cursor.moveToNext()) {
                Integer product_id = cursor.getInt(0);
                boolean found = false;
                for(int i = 0; i < newProducts.size(); i++) {
                    if(newProducts.get(i).getId() == product_id) { //if found, remove it from new products, as it is not new
                        found = true;
                        newProducts.remove(i);
                        break;
                    }
                }
                if(!found){ // if it cannot be found, that means it is no longer on the coupon, so delete from db
                    if(0 > db.delete(COUPON_PRODUCTS_TABLE_NAME,COUPON_PRODUCTS_COLUMN_COUPON+" = ? AND "+COUPON_PRODUCTS_COLUMN_PRODUCT+" = ?",new String[]{coupon_id,product_id.toString()})) {
                        throw new Exception("Unable to delete product from coupon");
                    }
                }
            }

            //whatever is left in newProducts is new to the coupon and must be added to db
            for(Product product : newProducts) {
                values.clear();
                values.put(COUPON_PRODUCTS_COLUMN_COUPON,coupon_id);
                values.put(COUPON_PRODUCTS_COLUMN_PRODUCT, product.getId());
                if (db.insert(COUPON_PRODUCTS_TABLE_NAME, null, values) < 0) {
                    throw new Exception("Unable to add product to coupon");
                }
            }

            db.setTransactionSuccessful();
            ret = true;
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
            db.close();
        }
        return ret;
    }

    public ArrayList<Product> getAllProducts(){
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT " + PRODUCT_COLUMN_ID + ", " + PRODUCT_COLUMN_NAME + ", " + PRODUCT_COLUMN_PRICE +" FROM " + PRODUCT_TABLE_NAME +" ORDER BY " + PRODUCT_COLUMN_NAME + " ASC",null);
            while(cursor.moveToNext()) {
                products.add(new Product(cursor.getInt(0),cursor.getString(1),cursor.getDouble(2)));
            }
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            db.close();
            return null;
        }
        db.close();
        return products;
    }

    public ArrayList<Coupon> getAllCouponsWOutProducts(){
        ArrayList<Coupon> coupons = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT "+COUPON_COLUMN_ID+", "+COUPON_COLUMN_DISCOUNT +
                            " FROM "+COUPON_TABLE_NAME+" ORDER BY "+COUPON_COLUMN_ID+" ASC",
                    null);
            while(cursor.moveToNext()){
                coupons.add(new Coupon(cursor.getInt(0),cursor.getDouble(1), new ArrayList<Product>()));
            }

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(),Toast.LENGTH_LONG).show();
            db.close();
            return null;
        }
        db.close();
        return coupons;
    }

    public ArrayList<Coupon> getAllCoupons(){
        LinkedHashMap<Integer,Coupon> couponMap = new LinkedHashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT c."+COUPON_COLUMN_ID+", c."+COUPON_COLUMN_DISCOUNT+", p."+PRODUCT_COLUMN_ID + ", p."+PRODUCT_COLUMN_NAME +", p."+PRODUCT_COLUMN_PRICE +
                            " FROM "+COUPON_TABLE_NAME+" c JOIN "+COUPON_PRODUCTS_TABLE_NAME+" cp ON c."+COUPON_COLUMN_ID+" = cp."+COUPON_PRODUCTS_COLUMN_COUPON+
                            " JOIN "+PRODUCT_TABLE_NAME+" p ON p."+PRODUCT_COLUMN_ID+" = cp."+COUPON_PRODUCTS_COLUMN_PRODUCT+" ORDER BY c."+COUPON_COLUMN_ID+" ASC",
                    null);
            while(cursor.moveToNext()){
                int cid = cursor.getInt(0);
                Coupon coupon = couponMap.get(cid);
                if(coupon == null) {
                    coupon = new Coupon(cid,cursor.getDouble(1),new ArrayList<Product>());
                    couponMap.put(cid,coupon);
                }
                coupon.addProduct(new Product(cursor.getInt(2),cursor.getString(3),cursor.getDouble(4)));
            }

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(),Toast.LENGTH_LONG).show();
            db.close();
            return null;
        }
        db.close();
        return new ArrayList<>(couponMap.values());
    }

    public Coupon selectCouponById(Integer id) {
        SQLiteDatabase db = getReadableDatabase();
        Coupon coupon = null;
        try {
            Cursor cursor = db.rawQuery("SELECT c." + COUPON_COLUMN_ID + ", c." + COUPON_COLUMN_DISCOUNT + ", p." + PRODUCT_COLUMN_ID + ", p." + PRODUCT_COLUMN_NAME + ", p." + PRODUCT_COLUMN_PRICE +
                            " FROM " + COUPON_TABLE_NAME + " c JOIN " + COUPON_PRODUCTS_TABLE_NAME + " cp ON c." + COUPON_COLUMN_ID + " = cp." + COUPON_PRODUCTS_COLUMN_COUPON +
                            " JOIN " + PRODUCT_TABLE_NAME + " p ON p." + PRODUCT_COLUMN_ID + " = cp." + COUPON_PRODUCTS_COLUMN_PRODUCT +
                            " WHERE c." + COUPON_COLUMN_ID + " = ?",
                    new String[]{id.toString()});
            while (cursor.moveToNext()) {
                if (coupon == null) {
                    coupon = new Coupon(cursor.getInt(0), cursor.getDouble(1), new ArrayList<Product>());
                }
                coupon.addProduct(new Product(cursor.getInt(2), cursor.getString(3), cursor.getDouble(4)));
            }

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            db.close();
            return null;
        }
        db.close();
        return coupon;
    }

    public void deleteCouponById(Integer id){
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(COUPON_TABLE_NAME, COUPON_COLUMN_ID + " = ?", new String[]{id.toString()});
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG);
        }
        db.close();
    }

    public ArrayList<Coupon> getCouponsWithProducts(ArrayList<Product> products){
        LinkedHashMap<Integer,Coupon> couponMap = new LinkedHashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] binds = null;
        try {
            String sql = "SELECT c."+COUPON_COLUMN_ID+", c."+COUPON_COLUMN_DISCOUNT+", p."+PRODUCT_COLUMN_ID + ", p."+PRODUCT_COLUMN_NAME +", p."+PRODUCT_COLUMN_PRICE +
                    " FROM "+COUPON_TABLE_NAME+" c JOIN "+COUPON_PRODUCTS_TABLE_NAME+" cp ON c."+COUPON_COLUMN_ID+" = cp."+COUPON_PRODUCTS_COLUMN_COUPON+
                    " JOIN "+PRODUCT_TABLE_NAME+" p ON p."+PRODUCT_COLUMN_ID+" = cp."+COUPON_PRODUCTS_COLUMN_PRODUCT;
            if(products.size() > 0) {
                binds = new String[products.size()];
                sql += " WHERE (p." + PRODUCT_COLUMN_ID + " = ?";
                binds[0] = products.get(0).getId().toString();
                for (int i = 1; i < products.size(); i++) {
                    sql += " OR p."+PRODUCT_COLUMN_ID+" = ?";
                    binds[i] = products.get(i).getId().toString();
                }
                sql += ")";
            }
            sql += " ORDER BY c."+COUPON_COLUMN_ID+" ASC";
            Cursor cursor = db.rawQuery(sql,
                    binds);
            while(cursor.moveToNext()){
                int cid = cursor.getInt(0);
                Coupon coupon = couponMap.get(cid);
                if(coupon == null) {
                    coupon = new Coupon(cid,cursor.getDouble(1),new ArrayList<Product>());
                    couponMap.put(cid,coupon);
                }
                coupon.addProduct(new Product(cursor.getInt(2),cursor.getString(3),cursor.getDouble(4)));
            }

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(),Toast.LENGTH_LONG).show();
            db.close();
            return null;
        }
        db.close();
        return new ArrayList<>(couponMap.values());
    }
}
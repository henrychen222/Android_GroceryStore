/**
 * Created by connorbowley on 11/8/17.
 */

package com.example.weichen.grocery2;


public class Product {
    private int id = -1;
    private String name;
    private double price;

    public Product(){}
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Double getPrice(){
        return price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
//        Log.d("product class","ran equals");
        if(obj != null && obj instanceof Product) {
            Product other  = (Product)obj;
            return this.id == other.id;
        }
        return false;
    }
}
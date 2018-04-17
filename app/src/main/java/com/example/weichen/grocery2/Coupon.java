package com.example.weichen.grocery2;

import java.util.ArrayList;

/**
 * Created by connorbowley on 11/8/17.
 */


public class Coupon {

    public static class DiscountResult{
        public double discount = 0;
        public double cost = 0;
        public double preCost = 0;
        public ArrayList<Coupon> coupons = new ArrayList<>();
    }

    private int id = -1;
    private double discount;
    private ArrayList<Product> products = new ArrayList<>();

    public Coupon(int id, double discount, ArrayList<Product> products){
        this.id = id;
        this.discount = discount;
        this.products = (ArrayList<Product>) products.clone();
    }

    public Coupon(double discount, ArrayList<Product> products){
        this.discount = discount;
        this.products = (ArrayList<Product>) products.clone();
    }

    public void setDiscount(double discount) {
        if(discount >= 0.0)
            this.discount = discount;
    }
    public Integer getId() {
        return id;
    }

    public Double getDiscount() {
        return discount;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        if(!products.contains(product)) {
            products.add(product);
        }
    }

    public void addProducts(ArrayList<Product> products){
        for (int i = 0; i < products.size(); i++) {
            addProduct(products.get(i));
        }
    }

    public boolean conflicts(Coupon other) {
        for (Product product : other.products) {
            if(this.products.contains(product)){
                return true;
            }
        }
        return false;
    }
    public boolean valid() {
        return valid(false);
    }

    public boolean valid(boolean checkId) {
        if(checkId && id < 0)
            return false;
        for (Product product : products) {
            if (product.getId() < 0) {
                return false;
            }
        }
        return discount >= 0.00;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof Coupon) {
            return this.id == ((Coupon)obj).id;
        }
        return false;
    }

    public static DiscountResult generateLargestDiscount(ArrayList<Coupon> coupons){
        return generateLargestDiscount(coupons, -1);
    }

    public static DiscountResult generateLargestDiscount(ArrayList<Coupon> coupons, double budget) {
        DiscountResult result = new DiscountResult();
        int[] curset = new int[coupons.size()];
        int[] bestset = new int[coupons.size()];
        for (int i = 0; i < curset.length; i++) {
            curset[i] = 0;
            bestset[i] = 0;
        }

        //run algorithm
        double bestDis = 0.00;
        double bestCost = 0.00;
        boolean done = false;
        while(updateFrom(curset,0)){
            int conflictIndex = getConflictsOn(curset,coupons);
//            Log.d("curset",join(curset));
            while(conflictIndex >= 0) {
                if (!updateFrom(curset, conflictIndex)) {
                    done = true;
                    break;
                }
                conflictIndex = getConflictsOn(curset,coupons);
            }
            if(done)
                break;
            double[] costAndDis = getCostAndDiscount(curset,coupons);
            double cost   = costAndDis[0];
            double curDis = costAndDis[1];
            if((cost <= budget || budget < 0) && curDis > bestDis) {
                bestDis = curDis;
                bestCost = cost;
                for (int i = 0; i < curset.length; i++) {
                    bestset[i] = curset[i];
                }
            }
        }

        result.cost = bestCost;
        result.discount = bestDis;
        result.preCost = bestCost + bestDis;
        for (int i = 0; i < bestset.length; i++) {
            if(bestset[i] == 1)
                result.coupons.add(coupons.get(i));
        }
        return result;
    }

    private static boolean updateFrom(int[] arr, int index) {
        if(arr.length <= index)
            return false;
        while(arr[index] == 1){
            arr[index++] = 0;
            if(index >= arr.length)
                return false;
        }
        arr[index] = 1;
        return true;
    }

    private static int getConflictsOn(int[] set, ArrayList<Coupon> coupons){
        Coupon conflicter = new Coupon(0.0,new ArrayList<Product>());
        for(int i = set.length - 1; i >= 0; i--){
            if(set[i] == 1) {
                if (conflicter.conflicts(coupons.get(i))) {
//                    Toast.makeText(this,"confliction!",Toast.LENGTH_SHORT).show();
//                    Log.d("conflict on",join(set) + " " +String.valueOf(i));
                    return i;
                }
                else
                    conflicter.addProducts(coupons.get(i).getProducts());
            }
        }
        return -1;
    }

    private static double[] getCostAndDiscount(int[] set, ArrayList<Coupon> coupons){
        double cost = 0.00;
        double dis = 0.00;
        for (int i = 0; i < set.length; i++) {
            if(set[i] == 1) {
                dis += coupons.get(i).getDiscount();
                for (int j = 0; j < coupons.get(i).getProducts().size(); j++) {
                    cost += coupons.get(i).getProducts().get(j).getPrice();
                }
            }
        }
        return new double[]{cost - dis, dis};
    }

    private static String join(int[] set){
        StringBuilder out = new StringBuilder();
        for (int i = set.length - 1; i >= 0; i--) {
            out.append(set[i]);
        }
        return out.toString();
    }


}
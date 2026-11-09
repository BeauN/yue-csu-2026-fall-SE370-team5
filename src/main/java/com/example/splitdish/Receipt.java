package com.example.splitdish;
import java.util.ArrayList;

public class Receipt {
    private String restaurantName;
    private ArrayList<ReceiptItem> items;
    private double tax;
    private double total;
    
    public Receipt(String restaurantName){
        this.restaurantName = restaurantName;
        this.items = new ArrayList<>();
    }

    public String getRestaurantName(){
        return this.restaurantName;
    }

    public void setRestaurantName(String name){
        this.restaurantName = name;
    }

    public ArrayList<ReceiptItem> getItems() {
        return items;
    }

    public void addItem(ReceiptItem item){
        items.add(item);
    }

    public void removeItem(ReceiptItem item){
        items.remove(item);
    }

    public double getSubtotal(){
        double runningSubTotal = 0;
        for(int i = 0; i < items.size(); i++){
            runningSubTotal += items.get(i).getPrice();
        }
        return runningSubTotal;
    }

    public double getTax(){
        return this.tax;
    }

    public void setTax(double tax){
        this.tax = tax;
        
    }

    public double getTotal(){
        return this.total;
        
    }

    public void setTotal(double total){
        this.total = total;
    }
}

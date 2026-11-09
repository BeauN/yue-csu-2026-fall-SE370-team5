package com.example.splitdish;
import java.util.ArrayList;

public class Bill{
    private Receipt receipt;
    private ArrayList<Diner> diners;
    private ArrayList<OrderShare> orderShares;
    private double tipPercentage;
    
    public Bill(Receipt receipt) {
        this.receipt = receipt;
        this.diners = new ArrayList<>();
        this.orderShares = new ArrayList<>();
    }
    public Receipt getReceipt() {
        return this.receipt;
    }

    public ArrayList<Diner> getDiners() {
        return this.diners;


    }

    public void addDiner(Diner diner) {
        this.diners.add(diner);
    }

    public void removeDiner(Diner diner) {
        // Loop backward to avoid skipping elements when removals shift indices.
        // You can avoid this behavior with ArrayList if you use an Iterator
        for (int i = orderShares.size() - 1; i >= 0; i--) {
            if (orderShares.get(i).getDiner() == diner) {
                orderShares.remove(i);
            }
        }
        diners.remove(diner);
    }

    public ArrayList<OrderShare> getOrderShares() {
        return orderShares;
    }

    public void addOrderShare(OrderShare share) {
        this.orderShares.add(share);
        
    }

    public void removeOrderShare(OrderShare share) {
        orderShares.remove(share);
    }

    public double getTipPercentage() {
        return this.tipPercentage;
    }

    public void setTipPercentage(double tipPercentage) {
        this.tipPercentage = tipPercentage;
    }

    public ArrayList<OrderShare> getSharesForDiner(Diner diner) {
        ArrayList<OrderShare> tempOrderShares = new ArrayList<>();
        for(int i = 0; i < orderShares.size(); i++) {
            if (diner == orderShares.get(i).getDiner()) {
                tempOrderShares.add(orderShares.get(i));
            }
        }
        return tempOrderShares;
    }

    public ArrayList<OrderShare> getSharesForItem(ReceiptItem item) {
        ArrayList<OrderShare> tempOrderShares = new ArrayList<>();
        for(int i = 0; i < orderShares.size(); i++){
            if(item == orderShares.get(i).getItem()) {
                tempOrderShares.add(orderShares.get(i));
            }
        }
        return tempOrderShares;
    }

}

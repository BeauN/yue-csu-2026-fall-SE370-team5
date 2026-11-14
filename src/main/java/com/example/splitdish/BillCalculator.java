package com.example.splitdish;

import java.util.ArrayList;

public class BillCalculator {
    // Calculates the diner's share of the food subtotal. A percentage of 1.0 means the diner has the full item; 0.5 means the diner has half.
    public double calculateDinerSubtotal(Diner diner, Bill bill) {
        double subtotal = 0.0;

        for (OrderShare share : bill.getSharesForDiner(diner)) {
            subtotal += share.getItem().getPrice() * share.getPercentage();
        }

        return subtotal;
    }

    // Allocates the receipt's tax proportionally to the diner's food subtotal.
    public double calculateTaxShare(Diner diner, Bill bill) {
        double receiptSubtotal = bill.getReceipt().getSubtotal();

        if (receiptSubtotal == 0.0) {
            return 0.0;
        }

        double tax = bill.getReceipt().getTax();
        double dinerSubtotal = calculateDinerSubtotal(diner, bill);

        return tax * dinerSubtotal / receiptSubtotal;
    }

    // Allocates the tip proportionally to the diner's food subtotal.
    // Assumes tipPercentage is stored as a fraction (e.g. 0.20 for 20%).
    public double calculateTipShare(Diner diner, Bill bill) {
        return calculateDinerSubtotal(diner, bill) * bill.getTipPercentage();
    }

    // Calculates the diner's subtotal, tax share, and tip share.
    public double calculateDinerTotal(Diner diner, Bill bill) {
        return calculateDinerSubtotal(diner, bill) + calculateTaxShare(diner, bill) + calculateTipShare(diner, bill);
    }

    // Returns receipt items that have no assigned shares.
    public ArrayList<ReceiptItem> getUnassignedItems(Bill bill) {
        ArrayList<ReceiptItem> unassigned = new ArrayList<>();

        for (ReceiptItem item : bill.getReceipt().getItems()) {
            if (bill.getSharesForItem(item).isEmpty()) {
                unassigned.add(item);
            }
        }

        return unassigned;
    }

    // Returns true if every item is fully assigned.
    // For shared items, the assigned percentages must sum to 1.0.
    public boolean allItemsFullyAssigned(Bill bill) {
        final double epsilon = 1e-9;

        for (ReceiptItem item : bill.getReceipt().getItems()) {
            double assignedPercentage = 0.0;

            for (OrderShare share : bill.getSharesForItem(item)) {
                assignedPercentage += share.getPercentage();
            }

            if (Math.abs(assignedPercentage - 1.0) > epsilon) {
                return false;
            }
        }

        return true;
    }
}
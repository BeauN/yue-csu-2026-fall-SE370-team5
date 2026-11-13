package com.example.splitdish;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import java.io.IOException;

public class AddItemController {

    private Receipt receipt;
    private Bill bill;

    @FXML
    private TextField itemNameField;

    @FXML
    private TextField itemPriceField;

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
        this.receipt = bill.getReceipt();
    }

    private void returnToReview() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("receipt-review-view.fxml"));
            Scene scene = new Scene(loader.load(), 390, 700);
            ReceiptReviewController controller = loader.getController();

            controller.setBill(bill);

            Stage stage = (Stage) itemNameField.getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Navigation Error", "Unable to return to Receipt Review");

        }
    }

    @FXML
    private void onSaveClick() {
        if(receipt == null) {
            AlertUtils.showError("Missing Receipt", "No receipt is available");
            return;
        }

        String name = itemNameField.getText().trim();
        if(name.isEmpty()) {
            AlertUtils.showError("Invalid Name", "Please enter an item name.");
            return;
        }

        double price;
        try{
            price = Double.parseDouble(itemPriceField.getText().trim());
            if(!Double.isFinite(price) || price < 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            AlertUtils.showError("Invalid Price", "Please enter a valid, non-negative price.");
            return;
        }

        ReceiptItem item = new ReceiptItem(name, price);
        receipt.addItem(item);

        returnToReview();
    }


    @FXML
    private void onCancelClick() {
        returnToReview();

    }

}
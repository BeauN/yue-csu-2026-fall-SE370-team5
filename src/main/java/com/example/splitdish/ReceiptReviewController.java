package com.example.splitdish;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ReceiptReviewController {
    private Receipt receipt;
    private Bill bill;

    @FXML
    private TextField restaurantNameField;

    @FXML
    private TextField taxField;

    @FXML
    private TextField receiptTotalField;

    @FXML
    private TextField tipPercentageField;

    @FXML
    private Button continueButton;

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
        displayReceipt();
    }

    public void displayReceipt() {
        if(receipt == null) {
            return;
        }
        restaurantNameField.setText(receipt.getRestaurantName());
        taxField.setText(String.format("%.2f", receipt.getTax()));
        receiptTotalField.setText(String.format("%.2f", receipt.getTotal()));

        if(bill == null) {
            tipPercentageField.setText("0.00");
        } else {
            tipPercentageField.setText(String.format(java.util.Locale.US, "%.2f", bill.getTipPercentage()));
        }
    }

    public void setBill(Bill bill) {
        this.bill = bill;
        this.receipt = bill.getReceipt();
        displayReceipt();
    }

    //Purpose: Validate the restaurant name, tax, tip, and total together. If anything is invalid
    //show an error and return false. Otherwise, update Receipt and return true.
    private boolean saveReviewFields() {
        if(receipt == null) {
            AlertUtils.showError("Missing Receipt", "No receipt has been loaded.");
            return false;
        }
        String name = restaurantNameField.getText().trim();
        if(name.isEmpty()) {
            AlertUtils.showError("Invalid Restaurant Name", "Restaurant name cannot be empty.");
            return false;
        }
        double tax;
        double total;
        double tip;

        try{
            tax = Double.parseDouble(taxField.getText().trim());
            total = Double.parseDouble(receiptTotalField.getText().trim());
            tip = Double.parseDouble(tipPercentageField.getText().trim());

            if(!Double.isFinite(tax) || !Double.isFinite(total) || !Double.isFinite(tip) ||
                    tax < 0 || total < 0 || tip < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            AlertUtils.showError("Invalid Amount", "Tax, total, and tip must be valid, non-negative numbers.");
            return false;
        }

        //we validate all four values before setting them, so we don't save a partially invalid receipt
        receipt.setRestaurantName(name);
        receipt.setTax(tax);
        receipt.setTotal(total);
        bill.setTipPercentage(tip);

        return true;
    }

    @FXML
    private void onNextButtonClick() {
        if (!saveReviewFields()) {
            return;
        }

        //load the Diner window
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("diner-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 390, 700);
            DinerController dinerController = loader.getController();
            dinerController.setBill(bill);
            Stage stage = (Stage)continueButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Navigation Error", "Unable to open the Add Diners screen.");
        }
    }

    @FXML
    private void onAddItemClick() {
        if(!saveReviewFields()) {
            return;
        }
        if(receipt == null) {
            AlertUtils.showError("Missing Reciept", "No receipt has been loaded");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-item-view.fxml"));
            Scene scene = new Scene(loader.load(), 390,700);

            AddItemController controller = loader.getController();
            controller.setBill(bill);

            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Navigation Error", "Unable to open Add Item screen.");
        }

    }

    @FXML
    private void onEditItemClick() {

    }

    @FXML
    private void onDeleteItemClick(){

    }

    @FXML
    private void onTaxChanged() {
        if(receipt == null) {
            return;
        }

        try{
            double tax = Double.parseDouble(taxField.getText());

            //This checks for values like NaN or infinite which are valid double types but not valid here
            if(!Double.isFinite(tax) || tax < 0) { //what this
                throw new NumberFormatException();
            }

            receipt.setTax(tax);
            taxField.setText(String.format("%.2f", tax));

        } catch (NumberFormatException e) {
            //restore the valid number before the user entered a wrong one
            taxField.setText(String.format("%.2f", receipt.getTax()));
            //display error
            AlertUtils.showError("Invalid Tax", "Please entre a valid, non-negative tax amount");
        }
    }

    @FXML
    private void onReceiptTotalChanged() {
        if (receipt == null) {
            return;
        }

        try {
            double total = Double.parseDouble(receiptTotalField.getText());

            if(!Double.isFinite(total) || total < 0) {
                throw new NumberFormatException();
            }

            receipt.setTotal(total);
            receiptTotalField.setText(String.format("%.2f", total));

        } catch (NumberFormatException e) {
            receiptTotalField.setText(String.format("%.2f", receipt.getTotal()));
            AlertUtils.showError("Invalid Total", "Please enter a valid, non-negative receipt total");
        }
    }

    //Purpose: Validate the percentage entered by the user and save it in Bill
    @FXML
    private void onTipChanged() {
        if(bill == null) {
            return;
        }
        try {
            double tip = Double.parseDouble(tipPercentageField.getText());
            if(!Double.isFinite(tip) || tip < 0) {
                throw new NumberFormatException();
            }
            bill.setTipPercentage(tip);
            tipPercentageField.setText(String.format(java.util.Locale.US, "%.2f", tip));

        } catch (NumberFormatException e) {
            tipPercentageField.setText(String.format(java.util.Locale.US, "%.2f", bill.getTipPercentage()));
            AlertUtils.showError("Invalid Tip", "Please enter a valid, non-negative tip percentage.");
        }

    }
    @FXML void onRestaurantNameChanged() {
        if(receipt == null) {
            return;
        }

        String name = restaurantNameField.getText().trim();

        if (name.isEmpty()) {
            restaurantNameField.setText(receipt.getRestaurantName());
            AlertUtils.showError("Invalid Restaurant Name", "Restaurant name cannot be empty");
            return;
        }

        receipt.setRestaurantName(name);
    }
}

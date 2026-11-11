package com.example.splitdish;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Alert;

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
    }

    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setBill(Bill bill) {
        this.bill = bill;
        this.receipt = bill.getReceipt();
        displayReceipt();

        tipPercentageField.setText(String.format(java.util.Locale.US,"%.2f", bill.getTipPercentage()));
    }

    @FXML
    private void onNextButtonClick() {
        if (receipt == null) {
            return;
        }
        if (bill == null) {
            bill = new Bill(receipt);
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

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Unable to open Add Diners");
            alert.setContentText("An error occurred while loading the next screen.");
            alert.showAndWait();
        }
    }

    @FXML
    private void onAddItemClick() {

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
            showError("Invalid Tax", "Please entre a valid, non-negative tax amount");
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
            showError("Invalid Total", "Please enter a valid, non-negative receipt total");
        }
    }

    //Purpose: Validate the percentage entered by the user and save it in Bill
    @FXML
    private void onTipChanged() {
        try {
            double tip = Double.parseDouble(tipPercentageField.getText());
            if(!Double.isFinite(tip) || tip < 0) {
                throw new NumberFormatException();
            }
            bill.setTipPercentage(tip);
            tipPercentageField.setText(String.format(java.util.Locale.US, "%.2f", tip));

        } catch (NumberFormatException e) {
            tipPercentageField.setText(String.format(java.util.Locale.US, "%.2f", bill.getTipPercentage()));
            showError("Invalid Tip", "Please enter a valid, non-negative tip percentage.");
        }

    }
    @FXML void onRestaurantNameChanged() {
        if(receipt == null) {
            return;
        }

        String name = restaurantNameField.getText().trim();

        if (name.isEmpty()) {
            restaurantNameField.setText(receipt.getRestaurantName());
            showError("Invalid Restaurant Name", "Restaurant name cannot be empty");
            return;
        }

        receipt.setRestaurantName(name);
    }
}

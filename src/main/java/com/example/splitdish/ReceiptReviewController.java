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
    //private Receipt receipt;
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

    @FXML
    private void onNextButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("diner-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 390, 700);
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

    }

    @FXML
    private void onReceiptTotalChanged() {

    }

    @FXML
    private void onTipChanged() {

    }
}

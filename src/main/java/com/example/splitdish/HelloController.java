package com.example.splitdish;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        /*
        * Create a loader pointed at receipt-source-view.fxml.
        * Load that FXML and make a new Scene from it.
        * Find the Stage (window) we're currently in.
        * Replace that Stage's current Scene with the new Scene.
        */
        try {
            FXMLLoader loader = new FXMLLoader( HelloApplication.class.getResource("receipt-source-view.fxml"));
            Scene scene = new Scene(loader.load(), 390, 700);
            Stage stage = (Stage) welcomeText.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException | RuntimeException e) {
            //prints the exception to the console
            e.printStackTrace();
            //alerts the user there was a problem
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not open the receipt upload screen.");
            alert.setContentText("Please try again.");

            alert.showAndWait();
        }
    }
    @FXML
    private void onManualEntryClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    HelloApplication.class.getResource("receipt-review-view.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 390, 700);
            ReceiptReviewController controller = fxmlLoader.getController();
            Receipt receipt = new Receipt("");
            Bill bill = new Bill(receipt);
            controller.setBill(bill);
            Stage stage = (Stage) welcomeText.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
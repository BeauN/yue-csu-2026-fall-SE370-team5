package com.example.splitdish;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.io.IOException;

public class DinerController {
    private Bill bill;

    // A list that can notify the UI when diners are added or removed.
    private final ObservableList<Diner> diners =
            FXCollections.observableArrayList();

    public void setBill(Bill bill) {
        this.bill = bill;
        diners.clear();
        if(bill != null) {
            //ObservableList has methods add, addAll, remove, and clear
            diners.addAll(bill.getDiners());
        }
    }
    @FXML
    private void initialize() {
        dinerListView.setItems(diners);

    }

    @FXML
    private TextField dinerNameField;

    @FXML
    private ListView<Diner> dinerListView;

    @FXML
    private void onAddClick() {
        if(bill == null) {
            return;
        }
        String name = dinerNameField.getText().trim();

        if(name.isEmpty()) {
            return;
        }

        Diner diner = new Diner(name);
        bill.addDiner(diner);
        diners.add(diner);
        dinerNameField.clear();
    }

    @FXML
    private void onRemoveClick() {
        if (bill == null) {
            return;
        }
        //dinerListView contains diner objects
        //getSelectionModel gets JavaFX object responsible for tracking which diner was selected
        //getSelectedItem gets actual diner object selected
        Diner selectedDiner = dinerListView.getSelectionModel().getSelectedItem();
        if(selectedDiner == null) {
            return;
        }
        bill.removeDiner(selectedDiner);
        diners.remove(selectedDiner);

    }

    @FXML
    private void onContinueClick() {
        if (bill == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("item-assignment-view.fxml"));
            Scene scene = new Scene(loader.load(), 390, 700);
            ItemAssignmentController controller = loader.getController();
            controller.setBill(bill);
            Stage stage = (Stage) dinerListView.getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Unable to open Assign Items");
            alert.setContentText("An error occurred while loading the next screen.");
            alert.showAndWait();
        }


    }
}

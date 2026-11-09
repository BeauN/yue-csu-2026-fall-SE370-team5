package com.example.splitdish;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DinerController {
    // A list that can notify the UI when diners are added or removed.
    private final ObservableList<String> dinerNames =
            FXCollections.observableArrayList();
    @FXML
    private void initialize() {
        dinerListView.setItems(dinerNames);
    }

    @FXML
    private TextField dinerNameField;

    @FXML
    private ListView<String> dinerListView;

    @FXML
    private void onAddClick() {
        String name = dinerNameField.getText().trim();
        if (name.isEmpty()) {
            return;
        }
        dinerNames.add(name);
    }

    @FXML
    private void onRemoveClick() {

    }

    @FXML
    private void onContinueClick() {

    }
}

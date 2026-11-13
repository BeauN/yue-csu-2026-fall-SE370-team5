package com.example.splitdish;

import javafx.scene.control.Alert;

public final class AlertUtils {
    private AlertUtils() {

    }

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

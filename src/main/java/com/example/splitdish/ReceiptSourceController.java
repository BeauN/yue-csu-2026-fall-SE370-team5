package com.example.splitdish;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

public class ReceiptSourceController {
    private File selectedFile;
    @FXML
    private ImageView receiptImageView;

    @FXML
    private Button continueButton;

    @FXML
    protected void onUploadClick() {
        FileChooser fileChooser = new FileChooser();

        //restricting the file extension types
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Image Files",
                "*.png", "*.jpg", "*.jpeg"
            )
        );

        Stage stage = (Stage) receiptImageView.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if(selectedFile != null) {
            System.out.println(selectedFile.getAbsolutePath());

            //converting the file to something setImage understands
            Image image = new Image(selectedFile.toURI().toString());
            //setting the image
            receiptImageView.setImage(image);

            //allow the user to continue if the image selected is correct
            continueButton.setDisable(false);
        }
    }

    @FXML
    protected void onContinueClick() {
        if(selectedFile == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("receipt-review-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 390, 700);

            Stage stage = (Stage)continueButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

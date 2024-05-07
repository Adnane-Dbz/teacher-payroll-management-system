package org.example.max;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class help {

    @FXML
    public ImageView imageView;


    @FXML
    public void initialize() {


        try {
            Image image = new Image(getClass().getResourceAsStream("/org/example/max/image/001.png"));
            imageView.setImage(image);
        } catch (NullPointerException e) {
            System.err.println("Image file not found or could not be loaded.");
        }

    }
}


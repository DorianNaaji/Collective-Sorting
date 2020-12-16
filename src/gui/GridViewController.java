package gui;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class GridViewController
{
    @FXML
    private Pane mainPane;

    @FXML
    public void initialize()
    {
        System.out.println("Grid View is set");
    }
}

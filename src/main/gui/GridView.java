package gui;

import gui.generic.GenericView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GridView extends GenericView
{
    public GridView(Stage parent, int width, int height) throws IOException
    {
        super(parent, "Collective sorting", "GridView.fxml", width, height, Modality.NONE);
    }

    public GridViewController getController()
    {
        return  (GridViewController)this.controller;
    }
}

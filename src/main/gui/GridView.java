package main.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import main.gui.generic.GenericView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.model.Grid;

import java.io.IOException;

public class GridView extends GenericView
{
    private static final int gridViewWidthHeight = 15;

    /**
     * Default gridview constructor.
     * @param parent the main stage.
     * @param gridDimensions the model grid dimensions. each cell will be 15px long/large.
     * @throws IOException
     */
    public GridView(Stage parent, int gridDimensions) throws IOException
    {
        super(parent, "Collective sorting", "GridView.fxml", gridDimensions*gridViewWidthHeight, gridDimensions*gridViewWidthHeight, Modality.NONE);
        this.setOnHiding(onCloseHandler);
    }

    public GridViewController getController()
    {
        return  (GridViewController)this.controller;
    }

    public static int getGridViewWidthHeight()
    {
        return gridViewWidthHeight;
    }

    /**
     * Event handler that will stop the business logic thread when exit button is pressed.
     */
    private static final EventHandler<WindowEvent> onCloseHandler = new EventHandler<WindowEvent>()
    {
        @Override
        public void handle(WindowEvent event)
        {
            Grid.setIsThreadRunning(false);
            Platform.exit();
            System.exit(0);
        }
    };
}

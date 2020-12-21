package main;

import javafx.stage.Stage;
import main.customexceptions.ParamsNotSetException;
import main.customexceptions.UnexpectedRandomGenerationException;
import main.gui.GridView;
import main.model.Grid;

import java.io.IOException;

/**
 * Wraps the GUI and the model into one class, so that it can be accessible from the Main class.
 */
public class GridWrapper
{
    private GridView gridView;
    private Grid gridModel;

    public GridWrapper(Stage primaryStage, int gridDimensions, int nbAgents, int nbItems) throws IOException, UnexpectedRandomGenerationException, ParamsNotSetException
    {
        this.gridView = new GridView(primaryStage, gridDimensions);
        this.gridView.getController().initializeGridModel(gridDimensions, nbAgents, nbItems);
        this.gridView.getController().initializeGridView(GridView.getGridViewWidthHeight());
        this.gridModel = gridView.getController().getGridModel();
    }

    public GridView getGridView()
    {
        return gridView;
    }

    public Grid getGridModel()
    {
        return gridModel;
    }
}

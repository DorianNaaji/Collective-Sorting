package main.gui;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import main.customexceptions.PaneNotFoundException;
import main.customexceptions.ParamsNotSetException;
import main.customexceptions.UnexpectedRandomGenerationException;
import main.model.*;

import java.awt.event.ActionEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 * UI operations on the grid view. It is composed a a Grid Pane, composed itself with panes, that each have their
 * background color changing according to the model grid state.
 */
public class GridViewController
{
    /**
     * the model.
     */
    private Grid gridModel = null;

    private Pane[][] gridPaneArray = null;

    @FXML
    private GridPane mainPane;

    public void initializeGridModel(int gridDimensions, int nbAgents, int nbItems) throws UnexpectedRandomGenerationException, ParamsNotSetException
    {
        if(this.gridModel == null)
        {
            this.gridModel = new Grid(gridDimensions, gridDimensions, nbAgents, nbItems);
        }
    }

    /**
     * Initializes the grid view : creates the panes instances.
     * @param gridViewWidthHeight
     */
    public void initializeGridView(int gridViewWidthHeight)
    {
        for(int y = 0; y < this.gridModel.getLines(); y++)
        {
            for(int x = 0; x < this.gridModel.getColumns(); x++)
            {
                Pane p = new Pane();
                p.setPrefSize(gridViewWidthHeight, gridViewWidthHeight);
                //p.setStyle("-fx-border-color: lightgrey");
                p.setStyle("-fx-background-color: #ffffff");
                GridPane.setRowIndex(p, y);
                GridPane.setColumnIndex(p, x);
                this.mainPane.getChildren().add(p);
            }
        }
        this.initializeGridPaneArray();
    }

    private void initializeGridPaneArray()
    {
        this.gridPaneArray = new Pane[this.gridModel.getLines()][this.gridModel.getColumns()];
        for(Node node : this.mainPane.getChildren())
        {
            this.gridPaneArray[GridPane.getRowIndex(node)][GridPane.getColumnIndex(node)] = (Pane)node;
        }
    }

    /**
     * displays the grid as seen by the user, for each new state. It is called when the model is refreshed, in the Main class.
     */
    public void refresh()
    {
        for(int line = 0; line < this.gridModel.getLines(); line++)
        {
            for(int column = 0; column < this.gridModel.getColumns(); column++)
            {
                CellContent current = (this.gridModel.getCells()[line][column].hasContent()) ? this.gridModel.getCells()[line][column].getCellContent() : null;
                // if the cell is empty, it will be white
                Pane p = this.gridPaneArray[line][column];
                if(current == null)
                {
                    p.setStyle("-fx-background-color: #ffffff");
                }
                // if it contains an agent
                else if(current.getClass().equals(Agent.class))
                {
                    p.setStyle("-fx-background-color: #ff0004");
                }
                // if it contains an item
                else if(current.getClass().equals(Item.class))
                {
                    Item item = (Item)current;
                    if(item.getItemType().equals(ItemType.A))
                    {
                        p.setStyle("-fx-background-color: #c7ffee");
                    }
                    else if(item.getItemType().equals(ItemType.B))
                    {
                        p.setStyle("-fx-background-color: #f2dfff");
                    }
                }

                // if it contains an agent on top of the item, it will be displayed on top prior.
                if(current != null && this.gridModel.getCells()[line][column].hasAgentOnTop())
                {
                    p.setStyle("-fx-background-color: #ff0004");
                }
            }
        }
    }

//    /**
//     *
//     * @param column
//     * @param row
//     * @return the pane at the specified column & row. exception otherwise.
//     * @throws PaneNotFoundException when the pane is not found...
//     */
//    private Pane getPaneFromGridPane(int column, int row) throws PaneNotFoundException
//    {
//        for(Node node : this.mainPane.getChildren())
//        {
//            if(GridPane.getColumnIndex(node) == column
//            && GridPane.getRowIndex(node)    == row)
//            {
//                return (Pane)node;
//            }
//        }
//
//
//        throw new PaneNotFoundException("Pane at column " + column + " and row " + row + " could not be found.");
//    }

    public Grid getGridModel()
    {
        return gridModel;
    }
}

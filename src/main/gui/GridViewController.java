package main.gui;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import main.customexceptions.PaneNotFoundException;
import main.customexceptions.ParamsNotSetException;
import main.customexceptions.UnexpectedRandomGenerationException;
import main.model.*;

import java.awt.event.ActionEvent;
import java.util.Timer;
import java.util.TimerTask;

public class GridViewController
{
    private Grid gridModel = null;

    @FXML
    private GridPane mainPane;

    public void initializeGridModel(int gridDimensions, int nbAgents, int nbItems) throws UnexpectedRandomGenerationException, ParamsNotSetException
    {
        if(this.gridModel == null)
        {
            this.gridModel = new Grid(gridDimensions, gridDimensions, nbAgents, nbItems);
        }
    }

    public void initializeGridView(int gridViewWidthHeight)
    {
        for(int y = 0; y < this.gridModel.getLines(); y++)
        {
            for(int x = 0; x < this.gridModel.getColumns(); x++)
            {
                Pane p = new Pane();
                p.setPrefSize(gridViewWidthHeight, gridViewWidthHeight);
                p.setStyle("-fx-border-color: lightgrey");
                GridPane.setRowIndex(p, y);
                GridPane.setColumnIndex(p, x);
                this.mainPane.getChildren().add(p);
            }
        }
    }

    //todo : make agents appear on top of everything.

    public void refresh() throws PaneNotFoundException
    {
        for(int y = 0; y < this.gridModel.getLines(); y++)
        {
            for(int x = 0; x < this.gridModel.getColumns(); x++)
            {
                CellContent current = (this.gridModel.getCells()[y][x] != null) ? this.gridModel.getCells()[y][x].getCellContent() : null;
                if(current == null)
                {
                    this.getPaneFromGridPane(x, y).setStyle("-fx-background-color: white");
                }
                else if(current.getClass().equals(Agent.class))
                {
                    this.getPaneFromGridPane(x, y).setStyle("-fx-background-color: #ff0004");
                }
                else if(current.getClass().equals(Item.class))
                {
                    Item item = (Item)current;
                    if(item.getItemType().equals(ItemType.A))
                    {
                        this.getPaneFromGridPane(x, y).setStyle("-fx-background-color: #e5fffd");

                    }
                    else if(item.getItemType().equals(ItemType.B))
                    {
                        this.getPaneFromGridPane(x, y).setStyle("-fx-background-color: #f2dfff");
                    }
                }
            }
        }
    }


    private Pane getPaneFromGridPane(int column, int row) throws PaneNotFoundException
    {
        for(Node node : this.mainPane.getChildren())
        {
            if(GridPane.getColumnIndex(node) == column
            && GridPane.getRowIndex(node)    == row)
            {
                return (Pane)node;
            }
        }

        throw new PaneNotFoundException("Pane at column " + column + " and row " + row + " could not be found.");
    }

    public Grid getGridModel()
    {
        return gridModel;
    }
}

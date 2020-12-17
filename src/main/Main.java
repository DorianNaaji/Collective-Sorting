package main;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import main.customexceptions.PaneNotFoundException;
import main.gui.GridView;
import javafx.application.Application;
import javafx.stage.Stage;
import main.model.Agent;
import main.model.Grid;

import java.util.Observable;
import java.util.Observer;

public class Main extends Application
{
    /* parameters */
    private static final int GRID_DIM = 50;
    private static final int AGENTS = 20;
    private static final int ITEMS = 400;
    private static final double K_MINUS = 0.3;
    private static final double K_PLUS = 0.1;
    private static final int NB_DEPLACEMENTS = 2; // i
    private static final int AGENT_MEMORY_SIZE = 10; // t characters
    private static final int GUI_REFRESH_TIME = 1000;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception
    {
        Agent.SET_PARAMS(NB_DEPLACEMENTS, AGENT_MEMORY_SIZE);
        Grid.SET_PARAMS(K_MINUS, K_PLUS, GUI_REFRESH_TIME);
        GridWrapper wrapper = new GridWrapper(primaryStage, GRID_DIM, AGENTS, ITEMS);
        wrapper.getGridView().show();

        Observer o = new Observer()
        {
            @Override
            public void update(Observable o, Object arg)
            {
                try
                {
                    wrapper.getGridView().getController().refresh();
                }
                catch (PaneNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        };

        wrapper.getGridModel().addObserver(o);
        new Thread(wrapper.getGridModel()).start();
    }
}

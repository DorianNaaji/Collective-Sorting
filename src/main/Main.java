package main;

import javafx.application.Platform;
import main.customexceptions.PaneNotFoundException;
import javafx.application.Application;
import javafx.stage.Stage;
import main.model.Agent;
import main.model.Grid;

import java.util.Observable;
import java.util.Observer;

/**
 * Application entry point.
 */
public class Main extends Application
{
    /* parameters. Set them here in order to change the application behavior. */
    private static final int GRID_DIM = 50;
    private static final int AGENTS = 20;
    private static final int ITEMS = 400;
    private static final double K_MINUS = 0.3;
    private static final double K_PLUS = 0.1;
    private static final int NB_MOVES = 1; // i
    private static final int AGENT_MEMORY_SIZE = 10; // t characters
    private static final int GUI_REFRESH_TIME = 5; // refresh rate in milliseconds

    public static void main(String[] args)
    {
        launch(args);
    }

    /**
     * Sets the params, initialize a new grid (model and view) and runs the gui on a separate thread.
     * View is refreshed as soon as the model changes thanks to the observer/observed design pattern.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(final Stage primaryStage) throws Exception
    {
        Agent.SET_PARAMS(NB_MOVES, AGENT_MEMORY_SIZE);
        Grid.SET_PARAMS(K_MINUS, K_PLUS, GUI_REFRESH_TIME);
        GridWrapper wrapper = new GridWrapper(primaryStage, GRID_DIM, AGENTS, ITEMS);
        wrapper.getGridView().show();

        Observer o = new Observer()
        {
            @Override
            public void update(Observable o, Object arg)
            {
                Platform.runLater( () ->
                {
                    wrapper.getGridView().getController().refresh();
                });
            }
        };

        wrapper.getGridModel().addObserver(o);
        new Thread(wrapper.getGridModel()).start();
    }
}

import gui.GridView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application
{

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception
    {
        GridView gui = new GridView(primaryStage, 500, 500);
        gui.show();
    }
}

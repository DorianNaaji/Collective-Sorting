package main.model;

import javafx.application.Platform;
import main.Main;
import main.customexceptions.ParamsNotSetException;
import main.customexceptions.UnexpectedRandomGenerationException;
import main.customexceptions.WrongParametersException;

import java.util.*;


/**
 * The grid. It is the main class of our program.
 * Its content is displayed on the gui each {@link #GUI_REFRESH_RATE_IN_MS} millis.
 * It can be started as a thread so that it doesnt interfere with the UI thread.
 * It is observable so that we can notify the UI as soon as changes are made.
 * It handles all the operations that are described in the Collective Sorting Article
 * ("THE DYNAMICS OF COLLECTIVE SORTING, ROBOT-LIKE ANTS AND ANT-LIKE ROBOTS")
 */
public class Grid extends Observable implements  Runnable
{
    // modify this if you need more dispersion of the items in the grid.
    private static int INITIALIZATION_RANDOM_BOUND = 100;
    private static int ITERATIONS = 0;
    private static int MAX_ITER = 0;
    private static boolean STOP_AFTER_MAX_ITER = false;

    private static double K_MINUS = 0;
    private static double K_PLUS = 0;
    private static int GUI_REFRESH_RATE_IN_MS = 0;
    private static boolean ARE_PARAMS_SET = false;

    private static boolean isThreadRunning = false;

    private Cell[][] cells;
    private int columns;
    private int lines;

    private Agent[] agents;
    private Item[] items;


    private Random random;

    public Grid(int lines, int columns, int nbAgents, int nbItems) throws UnexpectedRandomGenerationException, ParamsNotSetException
    {
        if(!ARE_PARAMS_SET)
        {
            throw new ParamsNotSetException("Please set the params (SET_PARAMS method) before creating a new instance.");
        }
        this.columns = columns;
        this.lines = lines;
        this.cells = new Cell[columns][lines];

        this.agents = new Agent[nbAgents];
        this.items = new Item[nbItems];

        this.random = new Random();
        this.initializeGridState();
    }

    /**
     * Initializes the grid at a random state :
     * Places all the agents and all the items at a random position on the grid.
     * It places two kinds of items : A and B items.
     */
    private void initializeGridState() throws UnexpectedRandomGenerationException, ParamsNotSetException
    {
        int remainingItemsA = this.items.length / 2;
        int remainingItemsB = remainingItemsA;
        int remainingAgents = this.agents.length;

        int itemArrayIndex = 0;
        int agentArrayIndex = 0;

        while(Arrays.stream(this.agents).anyMatch(Objects::isNull) || Arrays.stream(this.items).anyMatch(Objects::isNull))
        {
            for(int i = 0; i < this.lines; i++)
            {
                for(int j = 0; j < this.columns; j++)
                {
                    int prob = this.random.nextInt(INITIALIZATION_RANDOM_BOUND);
                    switch(prob)
                    {
                        //do not place anything
                        case 0:
                            break;
                        // place an agent if remaining, otherwise place an object.
                        case 1:
                            if(remainingAgents > 0)
                            {
                                if(this.placeAnAgent(i, j, agentArrayIndex))
                                {
                                    agentArrayIndex++;
                                    remainingAgents--;
                                }
                            }
                            break;
                        case 2:
                            //places randomly an A or B item (according to remaining items)
                            if(remainingItemsA > 0 && remainingItemsB > 0)
                            {
                                int aOrB = this.random.nextInt(2);
                                ItemType type = (aOrB == 0) ? ItemType.A : ItemType.B;
                                if(this.placeAnItem(i, j, itemArrayIndex, type))
                                {
                                    itemArrayIndex++;
                                    if(type == ItemType.A)
                                    {
                                        remainingItemsA--;
                                    }
                                    else
                                    {
                                        remainingItemsB--;
                                    }
                                }
                            }
                            // or A or B if the other type items are already all placed
                            else if(remainingItemsA > 0)
                            {
                                if(this.placeAnItem(i, j, itemArrayIndex, ItemType.A))
                                {
                                    itemArrayIndex++;
                                    remainingItemsA--;
                                }
                            }
                            else if(remainingItemsB > 0)
                            {
                                if(this.placeAnItem(i, j, itemArrayIndex, ItemType.B))
                                {
                                    itemArrayIndex++;
                                    remainingItemsB--;
                                }
                            }
                            break;
                        default:
                            break;
                            //throw new UnexpectedRandomGenerationException("Value " + prob + " is not supported by this method.");
                    }
                }
            }
        }
        this.initializeRemainingCellsWithEmptyContent();
    }

    /**
     * Private helper for the {@link #initializeGridState()} method.
     */
    private boolean placeAnAgent(int line, int column, int arrayIndex) throws ParamsNotSetException
    {
        Agent a = new Agent(this, line, column);
        Cell c = new Cell(a);
        this.agents[arrayIndex] = a;
        return this.placeSomethingOnTheGrid(line, column, c);
    }

    /**
     * Private helper for the {@link #initializeGridState()} method.
     */
    private boolean placeAnItem(int line, int column, int arrayIndex, ItemType itemType)
    {
        Item i = new Item(itemType);
        Cell c = new Cell(i);
        this.items[arrayIndex] = i;
        return this.placeSomethingOnTheGrid(line, column, c);
    }

    /**
     * Private helper for both the {@link #placeAnAgent(int, int, int)} and {@link #placeAnItem(int, int, int, ItemType)}
     * methods.
     */
    private boolean placeSomethingOnTheGrid(int line, int column, Cell cell)
    {
        if(this.cells[line][column] == null)
        {
            this.cells[line][column] = cell;
            return true;
        }
        else
        {
            return false;
        }
    }

    private void initializeRemainingCellsWithEmptyContent()
    {
        for(int i = 0; i < this.lines; i++)
        {
            for (int j = 0; j < this.columns; j++)
            {
                if(this.getCells()[i][j] == null)
                {
                    this.getCells()[i][j] = new Cell();
                }
            }
        }
    }

    /**
     * Runs the Grid Model behavior as a thread and notifies the UI thread as soon as there are changes.
     * It then sleeps for {@link #GUI_REFRESH_RATE_IN_MS} milliseconds
     */
    @Override
    public void run()
    {
        isThreadRunning = true;

        while(isThreadRunning)
        {
            try
            {
                this.step();
                setChanged();
                notifyObservers();
                Grid.ITERATIONS++;
                if(Grid.ITERATIONS%10000 == 0)
                {
                    System.out.println("Itérations : " + Grid.ITERATIONS);
                }
            }
            catch (WrongParametersException e)
            {
                e.printStackTrace();
            }

            // waits before refreshing, so that the user can see what's going on
            if(GUI_REFRESH_RATE_IN_MS == 0)
            {
                if(Grid.ITERATIONS%100 == 0)
                {
                    Grid.sleep(5);
                }
                else
                {
                    Grid.sleep(GUI_REFRESH_RATE_IN_MS);
                }
            }
            else
            {
                Grid.sleep(GUI_REFRESH_RATE_IN_MS);
            }

            if(Grid.STOP_AFTER_MAX_ITER && Grid.ITERATIONS >= MAX_ITER)
            {
                Main.printExecutionTime();
                // freeze when max iter is reached. other behavior can be done but for now, that will be fine
                while(true){ }
            }
        }
    }

    private static final void sleep(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * makes one iteration of the algorithm, i.e. moves the agent and makes them do their decisions.
     */
    private void step() throws WrongParametersException
    {
        for(Agent agent : this.agents)
        {
            agent.moveRandomly();
            agent.behave();
        }
    }

    /**
     * gets x nearby cells at pos (line, column) in four cardinal directions.
     *
     * i.e returns all the cells in a cross located around (line, column) :
     *
     *                |
     *                |
     *                |
     *                |
     *            ----o----
     *                |
     *                |
     *                |
     *                |
     *
     *      (o being the (line, column) pos)
     *
     * @param x
     * @param line
     * @param column
     */
    public List<Cell> getxNearbyCells(int x, int line, int column)
    {
        List<Cell>  nearbyCells = new ArrayList<>();
        Direction[] directions = Direction.values();

        for(Direction dir : directions)
        {
            for(int i = 1; i <= x; i++)
            {
                int newLine = line;
                int newColumn = column;
                switch(dir)
                {
                    case NORTH:
                        newLine-=i;
                        break;
                    case EAST:
                        newColumn+=i;
                        break;
                    case SOUTH:
                        newLine+=i;
                        break;
                    case WEST:
                        newColumn-=i;
                        break;
                }
                if(this.isLineInBounds(newLine) && this.isColumnInBounds(newColumn))
                {
                    nearbyCells.add(this.cells[newLine][newColumn]);
                }
            }
        }
        return nearbyCells;
    }

    private boolean isColumnInBounds(int column)
    {
        return  (column >= 0 && column < this.getColumns());
    }

    private boolean isLineInBounds(int line)
    {
        return (line >= 0 && line < this.getLines());
    }


    public int getColumns()
    {
        return columns;
    }

    public int getLines()
    {
        return lines;
    }

    public Agent[] getAgents()
    {
        return agents;
    }

    public Item[] getItems()
    {
        return items;
    }

    public Cell[][] getCells()
    {
        return cells;
    }

    public static double getkMinus()
    {
        return K_MINUS;
    }

    public static double getkPlus()
    {
        return K_PLUS;
    }

    public static void setIsThreadRunning(boolean isThreadRunning)
    {
        Grid.isThreadRunning = isThreadRunning;
    }

    public static void SET_PARAMS(double kMinus, double kPlus, int guiRefreshTime, int maxIter, boolean stopAfterMaxIter)
    {
        if(!ARE_PARAMS_SET)
        {
            K_MINUS = kMinus;
            K_PLUS = kPlus;
            GUI_REFRESH_RATE_IN_MS = guiRefreshTime;
            MAX_ITER = maxIter;
            STOP_AFTER_MAX_ITER = stopAfterMaxIter;
            ARE_PARAMS_SET = true;
        }
    }
}

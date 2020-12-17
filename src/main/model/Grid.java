package main.model;

import main.customexceptions.ParamsNotSetException;
import main.customexceptions.UnexpectedRandomGenerationException;

import java.util.*;

public class Grid extends Observable implements  Runnable
{
    // modify this if you need more dispersion of the items in the grid.
    private static int INITIALIZATION_RANDOM_BOUND = 100;

    private static double K_MINUS = 0;
    private static double K_PLUS = 0;
    private static int GUI_REFRESH_TIME;
    private static boolean ARE_PARAMS_SET = false;

    private static boolean isThreadRunning = false;

    private Cell[][] cells;
    private int columns;
    private int lines;

    private Agent[] agents;
    private Item[] items;


    private Random random;

    //todo doc
    public Grid(int lines, int columns, int nbAgents, int nbItems) throws UnexpectedRandomGenerationException, ParamsNotSetException
    {
        if(!ARE_PARAMS_SET)
        {
            throw new ParamsNotSetException("Please set the params (SET_PARAMS method) before creating a new instance.");
        }
        this.columns = columns;
        this.lines = lines;
        this.cells = new Cell[lines][columns];

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
    }

    private boolean placeAnAgent(int line, int column, int arrayIndex) throws ParamsNotSetException
    {
        Agent a = new Agent();
        Cell c = new Cell(a);
        this.agents[arrayIndex] = a;
        return this.placeSomethingOnTheGrid(line, column, c);
    }

    private boolean placeAnItem(int line, int column, int arrayIndex, ItemType itemType)
    {
        Item i = new Item(itemType);
        Cell c = new Cell(i);
        this.items[arrayIndex] = i;
        return this.placeSomethingOnTheGrid(line, column, c);
    }

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

    public static void setIsThreadRunning(boolean isThreadRunning)
    {
        Grid.isThreadRunning = isThreadRunning;
    }

    public static void SET_PARAMS(double kMinus, double kPlus, int guiRefreshTime)
    {
        if(!ARE_PARAMS_SET)
        {
            K_MINUS = kMinus;
            K_PLUS = kPlus;
            GUI_REFRESH_TIME = guiRefreshTime;
            ARE_PARAMS_SET = true;
        }
    }

    /**
     * Runs the Grid Model behavior as a thread and notifies the UI thread as soon as there are changes.
     * It then sleeps for {@link #GUI_REFRESH_TIME} milliseconds
     */
    @Override
    public void run()
    {
        isThreadRunning = true;

        while(isThreadRunning)
        {
            //do anything (do an iteration of the grid model)
            setChanged();
            notifyObservers();
            // waits before refreshing, so that the user can see what's going on
            try
            {
                Thread.sleep(GUI_REFRESH_TIME);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}

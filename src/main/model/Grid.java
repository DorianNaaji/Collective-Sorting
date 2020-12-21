package main.model;

import main.customexceptions.ParamsNotSetException;
import main.customexceptions.UnexpectedRandomGenerationException;

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
            this.step();
            setChanged();
            notifyObservers();
            // waits before refreshing, so that the user can see what's going on
            try
            {
                Thread.sleep(GUI_REFRESH_RATE_IN_MS);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * makes one iteration of the algorithm, i.e. moves the agent and makes them do their decisions.
     */
    private void step()
    {
        // FOR EACH AGENT

            // moves the agent randomly up to NB_MOVES further, NB_MOVES being a constant in the Agent class. Its value is set in the Main method.
            // agents can only move if there are no other agents on the nearby cells. It tries to go in the 4 availables directions and to
            // make i steps forward, 1 =< i < NB_MOVES. It tries that let's say 100 times (we will/can define a count var for this)
            // and if it cannot move (because all attempts would have moved the agent on a cell where an agent is already is),
            // the agent stays still.

            // IF IT HAS MOVED
                // after that the agent has moved, the current method refreshes the agent memory.
            // END IF

            // then, computes wether or not the agent should drop an item (if it holds an item) or pick up an item.
            // (and this, for each item type)


            // IF AGENT IS ON TOP OF AN ITEM
                // pickUp = (kPlus / (kPlus + f))²
                // drop   = (f / (kMinus + f))²
                //f being a ratio : nearbyCellsThatContainItemOfTypeX/nearbyCells.
                // a cell is "nearby" when it can be met by the agent in less than NB_MOVES. So, each cell around the agent
                // by 1 to NB_MOVES distance is a nearby cell.

                // IF AGENT SHOULD DROP
                    // DROP (if possible)
                    // OTHERWISE, keep the item and wait for the next step. (do nothing)
                // END IF

                // IF AGENT SHOULD PICK UP
                    // PICK UP ONLY IF NOT ALREADY CARRYING AN ITEM
                // END IF
            // END IF

        // END FOR EACH

        for(Agent agent : this.agents)
        {
            agent.moveRandomly();
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
            GUI_REFRESH_RATE_IN_MS = guiRefreshTime;
            ARE_PARAMS_SET = true;
        }
    }
}

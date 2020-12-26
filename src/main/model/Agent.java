package main.model;

import main.utils.Utils;
import main.customexceptions.ParamsNotSetException;
import main.customexceptions.WrongParametersException;

import java.util.List;
import java.util.Random;

/**
 * An agent. Its goal is to sort out items on the grid.
 */
public class Agent extends CellContent
{
    private static int NB_MOVES = 0;
    private static int AGENT_MEMORY_SIZE = 0;
    private static boolean ARE_PARAMS_SET = false;

    private Grid environment;
    private Character[] memory;
    private Random random;
    // the agents coordinates
    private int line;
    private int column;
    private boolean isHoldingAnItem;
    private Item holding;


    public Agent(Grid environment, int line, int column) throws ParamsNotSetException
    {
        if(!ARE_PARAMS_SET)
        {
            throw new ParamsNotSetException("Please set the params (SET_PARAMS method) before creating a new instance.");
        }
        this.environment = environment;
        this.memory = new Character[AGENT_MEMORY_SIZE];
        this.line = line;
        this.column = column;
        this.isHoldingAnItem = false;
        this.holding = null;
        this.random = new Random();
    }

    public static void SET_PARAMS(int nbDeplacements, int agentMemorySize)
    {
        if(!ARE_PARAMS_SET)
        {
            NB_MOVES = nbDeplacements;
            AGENT_MEMORY_SIZE = agentMemorySize;
            ARE_PARAMS_SET = true;
        }
    }

    public void moveRandomly() throws WrongParametersException
    {
        // random step
        int i = this.random.nextInt(Agent.NB_MOVES + 1);
        // random direction
        Direction direction = Direction.values()[this.random.nextInt(Direction.values().length)];

        // agent will try to move i steps further in the given direction. If i steps further is out of bound,
        // the agent will try again by decreasing one to i
        int newLine = this.line;
        int newColumn = this.column;
        boolean flag = true;
        boolean canMove = false;
        boolean wontBeAbleToMoveInGivenDirection = false;

        while(flag)
        {
            switch(direction)
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

            // if isIn bounds -> canMove = true;
            if(newLine >= 0 && newLine < this.environment.getLines() && newColumn >= 0 && newColumn < this.environment.getColumns())
            {
                canMove = true;
                flag = false;
            }
            else
            {
                if(i> 0)
                {
                    i--;
                }
                else if(i == 0)
                {
                    wontBeAbleToMoveInGivenDirection = true;
                    flag = false;
                }
            }
        }

        // try another random direction. agent can atleast move in 2 directions in the grid (if it is in one of the grid angles).
        // so this recursive call won't force the app to stick in here.
        // edit : well actually the app can be stuck in that recursive calls in some cases (too much agents arounds)
        // todo : recursion has to be fixed, otherwise it may cause a stack over flow error (if no move is possible)
//        if(wontBeAbleToMoveInGivenDirection)
//        {
//         //   this.moveRandomly();
//        }
        /*else*/ if(canMove)
        {
            this.makeMove(newLine, newColumn);
        }
    }

    private void makeMove(int newLine, int newColumn) throws WrongParametersException
    {
        // the new cell is empty. we just move onto it.
        if(!this.environment.getCells()[newLine][newColumn].hasContent())
        {
            this.removeFromOldPosition();
            this.line = newLine;
            this.column = newColumn;
            this.refreshMemory();
            this.environment.getCells()[newLine][newColumn].setCellContent(this);
        }
        // the new cell contains an item. we must place the agent on top.
        else if(this.environment.getCells()[newLine][newColumn].isCellContentAnItem())
        {
            // todo : recursion has to be fixed, otherwise it may cause a stack over flow error (if no move is possible)
            // if cell does already have an agent on top, we try again.
//            if(this.environment.getCells()[newLine][newColumn].hasAgentOnTop())
//            {
////                this.moveRandomly();
//            }
            //else
            if(!this.environment.getCells()[newLine][newColumn].hasAgentOnTop())
            {
                // moves
                this.removeFromOldPosition();
                this.line = newLine;
                this.column = newColumn;
                this.refreshMemory();
                this.environment.getCells()[newLine][newColumn].placeAgentOnTop(this);
            }
        }
        // an agent is located on newLine and newColumn. We try again another position.
        // todo : recursion has to be fixed, otherwise it may cause a stack over flow error (if no move is possible)
//        else if(this.environment.getCells()[newLine][newColumn].isCellContentAnAgent())
//        {
//            //this.moveRandomly();
//        }
    }

    private void removeFromOldPosition()
    {
        Cell currentCell = this.environment.getCells()[this.line][this.column];
        if(currentCell.getCellContent() == null)
        {
            System.out.println();
        }
        if(currentCell.isCellContentAnAgent())
        {
            currentCell.removeContent();
        }
        else if(currentCell.isCellContentAnItem())
        {
            currentCell.removeAgentFromTop();
        }
    }

    private void refreshMemory() throws WrongParametersException
    {
        Cell currentCell = this.environment.getCells()[this.line][this.column];
        char toPush = '0';
        if(currentCell.hasContent())
        {
            if(currentCell.isCellContentAnItem())
            {
                Item content = (Item)currentCell.getCellContent();
                toPush = content.getItemType().equals(ItemType.A) ? 'A' : 'B';
            }
        }
        Utils.push(this.memory, toPush);
    }

    public void behave()
    {
        Cell current = this.environment.getCells()[this.line][this.column];
        //drop
        if(this.isHoldingAnItem)
        {
            double probaDrop = this.compute_f(this.holding.getItemType(), false);
            if(this.random.nextDouble() <= probaDrop)
            {
                if(current.hasNoItemPlacedOntoIt())
                {
                    current.placeAgentOnTop(this);
                    current.setCellContent(this.holding);
                    this.isHoldingAnItem = false;
                }
            }
        }
        //pickup
        else if(current.isCellContentAnItem())
        {
            double probaPick = this.compute_f(((Item)current.getCellContent()).getItemType(), true);
            if(this.random.nextDouble() <= probaPick)
            {
                this.holding = (Item)current.getCellContent();
                current.setCellContent(this);
                current.removeAgentFromTop();
                this.isHoldingAnItem = true;
            }
        }
    }

    private double compute_f(ItemType itemType, boolean isPickUp)
    {
        List<Cell> nearbyCells = this.environment.getxNearbyCells(Agent.NB_MOVES, this.line, this.column);
        double cellsThatContainItemsOfGivenType = 0;

        for(Cell c : nearbyCells)
        {
            if(c.hasContent() && c.isCellContentAnItem() && ((Item)c.getCellContent()).getItemType().equals(itemType))
            {
                cellsThatContainItemsOfGivenType++;
            }
        }
        double f = cellsThatContainItemsOfGivenType/nearbyCells.size();
        if(isPickUp)
        {
            return Math.pow( Grid.getkPlus()/(Grid.getkPlus() + f), 2);
        }
        else
        {
            return Math.pow(f/(Grid.getkMinus() + f), 2);
        }
    }
}

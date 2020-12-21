package main.model;

import main.customexceptions.ParamsNotSetException;

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
    private Item[] memory;
    private Random random;
    // the agents coordinates
    private int line;
    private int column;


    public Agent(Grid environment, int line, int column) throws ParamsNotSetException
    {
        if(!ARE_PARAMS_SET)
        {
            throw new ParamsNotSetException("Please set the params (SET_PARAMS method) before creating a new instance.");
        }
        this.environment = environment;
        this.memory = new Item[AGENT_MEMORY_SIZE];
        this.line = line;
        this.column = column;
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

    public void moveRandomly()
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
        if(wontBeAbleToMoveInGivenDirection)
        {
            this.moveRandomly();
        }
        else if(canMove)
        {
            this.makeMove(newLine, newColumn);
        }
    }

    private void makeMove(int newLine, int newColumn)
    {
        // the new cell is empty. we just move onto it.
        if(!this.environment.getCells()[newLine][newColumn].hasContent())
        {
            this.removeFromOldPosition();
            this.line = newLine;
            this.column = newColumn;
            this.environment.getCells()[newLine][newColumn].setCellContent(this);
        }
        // the new cell contains an item. we must place the agent on top.
        else if(this.environment.getCells()[newLine][newColumn].getCellContent().getClass().equals(Item.class))
        {
            // if cell does already have an agent on top, we try again.
            if(this.environment.getCells()[newLine][newColumn].hasAgentOnTop())
            {
                this.moveRandomly();
            }
            else
            {
                // moves
                this.removeFromOldPosition();
                this.line = newLine;
                this.column = newColumn;
                this.environment.getCells()[newLine][newColumn].placeAgentOnTop(this);
            }
        }
        // an agent is located on newLine and newColumn. We try again another position.
        else
        {
            this.moveRandomly();
        }
    }

    private void removeFromOldPosition()
    {
        Cell currentCell = this.environment.getCells()[this.line][this.column];
        if(currentCell.getCellContent() == null)
        {
            System.out.println();
        }
        if(currentCell.getCellContent().getClass().equals(Agent.class))
        {
            currentCell.removeContent();
        }
        else if(currentCell.getCellContent().getClass().equals(Item.class))
        {
            currentCell.removeAgentFromTop();
        }
    }
}

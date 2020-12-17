package main.model;

import main.customexceptions.ParamsNotSetException;

public class Agent extends CellContent
{
    private static int NB_DEPLACEMENTS = 0;
    private static int AGENT_MEMORY_SIZE = 0;
    private static boolean ARE_PARAMS_SET = false;

    private Item[] memory;


    public Agent() throws ParamsNotSetException
    {
        if(!ARE_PARAMS_SET)
        {
            throw new ParamsNotSetException("Please set the params (SET_PARAMS method) before creating a new instance.");
        }
        this.memory = new Item[AGENT_MEMORY_SIZE];

    }


    public static void SET_PARAMS(int nbDeplacements, int agentMemorySize)
    {
        if(!ARE_PARAMS_SET)
        {
            NB_DEPLACEMENTS = nbDeplacements;
            AGENT_MEMORY_SIZE = agentMemorySize;
            ARE_PARAMS_SET = true;
        }
    }
}

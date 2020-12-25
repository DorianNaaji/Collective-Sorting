package main.model;

import main.customexceptions.ParamsNotSetException;
import main.customexceptions.UnexpectedRandomGenerationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GridTest
{

    @Test
    void getxNearbyCells() throws ParamsNotSetException, UnexpectedRandomGenerationException
    {
        Grid.SET_PARAMS(0.3, 0.5, 60);
        Grid g = new Grid(50, 50, 0, 0);
        List<Cell> nearbyCells = g.getxNearbyCells(5, 25, 25);
        assertEquals(5*4, nearbyCells.size());

        for(int i = 0; i < g.getLines(); i++)
        {
            for(int j = 0; j < g.getColumns(); j++)
            {
                for(Cell nearby : nearbyCells)
                {
                    if(nearby.equals(g.getCells()[i][j]))
                    {
                        System.out.println("(" + i + ", " + j + ")");
                    }
                }
            }
        }
    }
}
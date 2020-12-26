package main;

import main.customexceptions.WrongParametersException;
import main.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest
{

    @Test
    void push() throws WrongParametersException
    {
        Integer[] array = new Integer[10];
        array[0] = 5;
        array[1] = 7;
        array[2] = 42;

        Utils.push(array, 5);
        assertEquals(5, array[3]);
        Utils.push(array, 12);
        assertEquals(12, array[4]);

        array[5] = 65;
        array[6] = 45;
        array[7] = 85;
        array[8] = 2;
        array[9] = 78;

        Utils.push(array, 266);
        assertNotEquals(5, array[0]);
        assertEquals(7, array[0]);
        assertEquals(266, array[9]);


        Utils.push(array, 20);

        assertEquals(42, array[0]);
        assertEquals(266, array[8]);
        assertEquals(20, array[9]);






    }
}
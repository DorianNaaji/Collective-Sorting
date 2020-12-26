package main.utils;


import main.customexceptions.WrongParametersException;

public final class Utils
{
    /**
     * Pushes an item at the end of the array, and removes the first object if the array is already full.
     * @param array
     * @param toAdd
     */
    public static void push(Object[] array, Object toAdd) throws WrongParametersException
    {
        if(toAdd == null || array == null)
        {
            throw new WrongParametersException("Could not proceed with the given argumennts.");
        }

        boolean added = false;

        for(int i = 0; i < array.length; i++)
        {
            if(array[i] == null)
            {
                array[i] = toAdd;
                added = true;
                break;
            }
        }

        if(!added)
        {
            array[0] = null;
            for(int i = 1; i < array.length; i++)
            {
                array[i - 1] = array[i];
            }
            array[array.length - 1] = toAdd;
        }
    }
}

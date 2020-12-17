package main.customexceptions;

public class ParamsNotSetException extends Exception
{
    public ParamsNotSetException(String message)
    {
        super(message);
    }
}

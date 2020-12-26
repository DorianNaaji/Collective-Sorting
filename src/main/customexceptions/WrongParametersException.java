package main.customexceptions;

public class WrongParametersException extends Exception
{
    public WrongParametersException(String message)
    {
        super(message);
    }
}

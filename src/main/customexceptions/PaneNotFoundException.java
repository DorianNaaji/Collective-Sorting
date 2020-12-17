package main.customexceptions;

public class PaneNotFoundException extends Exception
{
    public PaneNotFoundException(String message)
    {
        super(message);
    }
}

package main.customexceptions;

public class UnexpectedRandomGenerationException extends Exception
{
    public UnexpectedRandomGenerationException(String message)
    {
        super(message);
    }
}

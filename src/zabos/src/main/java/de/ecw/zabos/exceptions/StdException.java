package de.ecw.zabos.exceptions;

/**
 * Generische Exceptionklasse. Wird benutzt um die Problematik des Java
 * Exception-Zoos zu vereinfachen.
 * 
 * @author bsp
 * 
 */
public class StdException extends Exception
{

    private static final long serialVersionUID = 5186896760759422016L;

    private Throwable inner_exception;

    /**
     * Konstruiert eine einfache Exception
     * 
     * @param _message
     */
    public StdException(String _message)
    {
        super(_message);
    }

    /**
     * Konstruiert eine Exception mit einer "Huckepack" Exception
     * 
     * @param _message
     * @param _innerException
     */
    public StdException(String _message, Throwable _innerException)
    {
        super(_message);
        inner_exception = _innerException;
    }

    /**
     * Konstruiert eine Exception mit einer "Huckepack" Exception und ohne
     * message text
     * 
     * @param _innerException
     */
    public StdException(Throwable _innerException)
    {
        super();
        inner_exception = _innerException;
    }

    public String toString()
    {
        String s = super.toString();
        if (inner_exception != null)
        {
            s = s + inner_exception.toString();
        }
        return s;
    }

}

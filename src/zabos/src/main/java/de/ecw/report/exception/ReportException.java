package de.ecw.report.exception;

/**
 * Exception class for BIRT reporting subsystem
 */
public class ReportException extends Exception
{

    private static final long serialVersionUID = 5186896760759422016L;

    private Throwable inner_exception;

    /**
     * New exception
     * 
     * @param _message
     */
    public ReportException(String _message)
    {
        super(_message);
    }

    /**
     * Creates a new inner exception
     * 
     * @param _message
     * @param _innerException
     */
    public ReportException(String _message, Throwable _innerException)
    {
        super(_message);
        inner_exception = _innerException;
    }

    /**
     * New inner exception without message
     * 
     * @param _innerException
     */
    public ReportException(Throwable _innerException)
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

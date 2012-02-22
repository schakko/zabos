package de.ecw.zabos;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

/**
 * "Main" für Start aus der Entwicklungsumgebung
 * 
 * @author bsp
 * 
 */
public class Main
{
    private final static Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args)
    {
        globalMain();
    }

    private static void globalMain()
    {
        try
        {
            Globals.init(null);
        }
        catch (ServletException e)
        {
            log.error(e);
        }

        try
        {
            Thread.sleep(1000 * 10800); // 3 Stunden
        }
        catch (InterruptedException e)
        {
            log.error(e);
        }

        Globals.destroy();
    }
}

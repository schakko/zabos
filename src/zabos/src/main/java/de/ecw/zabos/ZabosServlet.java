package de.ecw.zabos;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * System/Daemon Initialisierung / Shutdown innerhalb der Tomcat Umgebung
 * 
 * @author bsp
 * 
 */
public class ZabosServlet extends HttpServlet
{

    private static boolean bInitialized = false;

    private static boolean bDestroyed = false;

    private static final long serialVersionUID = 9020938426748602576L;

    public void init(ServletConfig _servletConfig) throws ServletException
    {
        synchronized (ZabosServlet.class)
        {
            if (!bInitialized)
            {
                bInitialized = true;

                super.init(_servletConfig);

                Globals.init(_servletConfig);
            }
        }
    }

    public void destroy()
    {
        synchronized (ZabosServlet.class)
        {
            if (!bDestroyed)
            {
                bDestroyed = true;

                Globals.destroy();
            }
        }
    }

}

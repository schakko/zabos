package de.ecw.report.service;

import javax.servlet.ServletContext;

import org.eclipse.birt.core.framework.PlatformServletContext;
import org.springframework.web.context.ServletContextAware;

/**
 * This class must be used if running BIRT subsystem in a servlet container like
 * Tomcat. <br />
 * This class is Springified and depends on spring*.jar
 * 
 * @author ckl
 * 
 */
public class WebReportService extends ReportServiceAdapter implements
                ServletContextAware
{
    private ServletContext servletContext = null;

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.report.service.ReportServiceAdapter#initPlatformContext()
     */
    protected void initPlatformContext()
    {
        if (servletContext != null)
        {
            setPlatformContext(new PlatformServletContext(servletContext));
        }
        else
        {
            super.initPlatformContext();
        }

    }

    /**
     * Initalizes pathes; will set the absolute path to
     * {@link #setEngineHome(String)} and {@link #setReportDesignDir(String)}.
     */
    protected void initPathes()
    {
        if (!isUseAbsolutePathForBirt() && (servletContext != null))
        {
            setEngineHome(servletContext.getRealPath(getEngineHome()));
            setReportDesignDir(servletContext.getRealPath(getReportDesignDir()));
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.web.context.ServletContextAware#setServletContext
     * (javax.servlet.ServletContext)
     */
    public void setServletContext(ServletContext _sc)
    {
        servletContext = _sc;
    }
}

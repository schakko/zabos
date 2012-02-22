package de.ecw.report.service;

import java.io.File;
import java.sql.Connection;

import org.apache.log4j.Logger;
import org.eclipse.birt.core.framework.IPlatformConfig;
import org.eclipse.birt.core.framework.IPlatformContext;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformFileContext;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;

import de.ecw.report.IReportService;
import de.ecw.report.IReportTypeTask;
import de.ecw.report.ReportTaskFactory;
import de.ecw.report.exception.ReportException;
import de.ecw.report.types.IReportModel;

/**
 * Adapter class which capsulates the interaction between BIRT and the rest of
 * the world
 * 
 * @author ckl
 * 
 */
public class ReportServiceAdapter implements IReportService
{
    private final static Logger log = Logger
                    .getLogger(ReportServiceAdapter.class);

    /**
     * Home directory of BIRT
     */
    private String engineHome;

    /**
     * Home directory of report design files (
     */
    private String reportDesignDir;

    /**
     * Home directory where new reports wilbe stored
     */
    private String dataDir;

    /**
     * JDBC connection
     */
    private Connection connection;

    /**
     * Use absolute path for BIRT
     */
    private boolean useAbsolutePathForBirt = false;

    /**
     * BIRT engine configuration
     */
    private final EngineConfig config = new EngineConfig();

    /**
     * BIRT engine
     */
    private IReportEngine birtEngine = null;

    /**
     * platform context
     */
    private IPlatformContext platformContext = null;

    /**
     * Logging directory
     */
    private String logDir;

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.report.IReportService#init()
     */
    public void init() throws ReportException
    {
        try
        {
            log.debug("ReportService [engineHome]: " + engineHome);
            log.debug("ReportService [designDir]: " + reportDesignDir);
            log.debug("ReportService [dataDir]: " + dataDir);
            log.debug("ReportService [logDir]: " + getLogDir());

            if (!new File(dataDir).exists())
            {
                throw new ReportException(
                                "ReportService [dataDir]: directory does not exist");
            }

            log.debug("ReportService [use absolute path]: "
                            + useAbsolutePathForBirt);

            initPathes();

            log.debug("ReportService [engineHome]: " + engineHome);

            if (getLogDir() != null)
            {
                config.setLogConfig(getLogDir(), java.util.logging.Level.ALL);
            }

            if (!new File(engineHome).exists())
            {
                throw new ReportException(
                                "ReportService [engineHome]: directory does not exist");
            }

            log.debug("ReportService [designDir]: " + reportDesignDir);

            if (!(new File(reportDesignDir).exists()))
            {
                throw new ReportException(
                                "ReportService [designDir]: directory does not exist");
            }

            initPlatformContext();

            config.setPlatformContext(getPlatformContext());
            Platform.startup(config);
            final IReportEngineFactory factory = (IReportEngineFactory) Platform
                            .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);

            if (factory == null)
            {
                throw new ReportException(
                                "ReportEngine: could not create factory");
            }

            birtEngine = factory.createReportEngine(config);
        }
        catch (Exception e)
        {
            throw new ReportException("ReportEngine: " + e.getMessage());
        }

        log.info("ReportService started");
    }

    /**
     * Initialzes platform configuration
     */
    protected void initPlatformContext()
    {
        System.setProperty(IPlatformConfig.BIRT_HOME, engineHome);
        setPlatformContext(new PlatformFileContext());
    }

    /**
     * initalizes path
     */
    protected void initPathes()
    {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.report.IReportService#getTargetPathOfReport(de.ecw.report.types
     * .IReportModel)
     */
    public String getTargetPathOfReport(IReportModel _report)
    {
        return getDataDir() + "/" + _report.getReportUid() + "."
                        + _report.getReportFormat().getExtension();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.report.IReportService#setEngineHome(java.lang.String)
     */
    public void setEngineHome(String engineHome)
    {
        this.engineHome = engineHome;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.report.IReportService#getEngineHome()
     */
    public String getEngineHome()
    {
        return engineHome;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.report.IReportService#setDesignDir(java.lang.String)
     */
    public void setReportDesignDir(String designDir)
    {
        this.reportDesignDir = designDir;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.report.IReportService#getDesignDir()
     */
    public String getReportDesignDir()
    {
        return reportDesignDir;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.report.IReportService#setDataDir(java.lang.String)
     */
    public void setDataDir(String dataDir)
    {
        this.dataDir = dataDir;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.report.IReportService#getDataDir()
     */
    public String getDataDir()
    {
        return dataDir;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.report.IReportService#setUseAbsolutePathForBirt(boolean)
     */
    public void setUseAbsolutePathForBirt(boolean useAbsolutePathForBirt)
    {
        this.useAbsolutePathForBirt = useAbsolutePathForBirt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.report.IReportService#destroy()
     */
    public synchronized void destroy()
    {
        birtEngine.destroy();
        Platform.shutdown();

        birtEngine = null;

        log.debug("ReportService stopped");
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.report.IReportService#isUseAbsolutePathForBirt()
     */
    public boolean isUseAbsolutePathForBirt()
    {
        return useAbsolutePathForBirt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.report.IReportService#create(de.ecw.report.types.IReportModel)
     */
    public void create(IReportModel _report) throws ReportException
    {
        // UID muss gesetzt werden, da dar�ber der Report-Name definiert wird
        if (_report.getReportUid() == null || _report.getReportUid().equals(""))
        {
            throw new ReportException("no UID for report available");
        }

        try
        {
            log.debug("Creating new report: " + _report.toString());

            IReportTypeTask task = ReportTaskFactory.create(birtEngine,
                            _report, getTargetPathOfReport(_report),
                            resolveReportDesignPath(_report), getConnection());
            task.run();

            _report.setIsCreated(true);

            log.debug("report '" + _report.toString() + "' was created");
        }
        catch (ReportException e)
        {
            _report.setIsCreated(false);
            throw e;
        }
    }

    /**
     * Set platform context
     * 
     * @param platformContext
     */
    public void setPlatformContext(IPlatformContext platformContext)
    {
        this.platformContext = platformContext;
    }

    /**
     * Get platform context
     * 
     * @return
     */
    public IPlatformContext getPlatformContext()
    {
        return platformContext;
    }

    /**
     * Set JDBC connection. The connection will be injected into the report
     * templates.
     */
    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    /**
     * Get JDBC connection
     * 
     * @return
     */
    public Connection getConnection()
    {
        return connection;
    }

    /**
     * Resolves the complete path of a {@link IReportModel} object
     * 
     * @param the
     *            reportModel
     */
    public String resolveReportDesignPath(IReportModel _report)
    {
        return getReportDesignDir() + "/" + _report.getReportType().getHash()
                        + RPTDESIGN_EXTENSION;
    }

    /**
     * Set logging directory
     * 
     * @param logDir
     */
    public void setLogDir(String logDir)
    {
        this.logDir = logDir;
    }

    /**
     * Get logging directory
     * 
     * @return
     */
    public String getLogDir()
    {
        return logDir;
    }
}

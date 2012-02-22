package de.ecw.report;

import java.sql.Connection;

import de.ecw.report.exception.ReportException;
import de.ecw.report.types.IReportModel;

/**
 * Interface for BIRT reporting subsystem
 * 
 * @author ckl
 * 
 */
public interface IReportService
{
    /**
     * Extension for RPTDESIGN files used as templates
     */
    public final static String RPTDESIGN_EXTENSION = ".rptdesign";

    /**
     * Initializes the BIRT environment
     * 
     * @throws ReportException
     */
    public void init() throws ReportException;

    /**
     * Returns the path where the generated report will be saved
     * 
     * @param _report
     * @return
     */
    public String getTargetPathOfReport(IReportModel _report);

    /**
     * Returns the (absolute) path where the template for the given report can
     * be found
     * 
     * @param _report
     * @return
     */
    public String resolveReportDesignPath(IReportModel _report);

    /**
     * Set BIRT engine home
     * 
     * @param engineHome
     */
    public void setEngineHome(String engineHome);

    /**
     * Set report design dir which includes the templates
     * 
     * @param designDir
     */
    public void setReportDesignDir(String designDir);

    /**
     * Sets the data dir where generated reports will be stored
     * 
     * @param dataDir
     */
    public void setDataDir(String dataDir);

    /**
     * Define that absolute pathes are used for BIRT
     * 
     * @param useAbsolutePathForBirt
     */
    public void setUseAbsolutePathForBirt(boolean useAbsolutePathForBirt);

    /**
     * Sets JDBC connection if available
     * 
     * @param _con
     */
    public void setConnection(Connection _con);

    /**
     * Does the execution of report generation
     * 
     * @param _report
     * @throws ReportException
     */
    public void create(IReportModel _report) throws ReportException;

    /**
     * Will be called on shutdown
     */
    public void destroy();
}
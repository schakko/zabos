package de.ecw.report;

import java.sql.Connection;

import org.eclipse.birt.report.engine.api.IReportEngine;

import de.ecw.report.exception.ReportException;
import de.ecw.report.types.IReportModel;

/**
 * Interface for report creation tasks
 * 
 * @author ckl
 * 
 */
public interface IReportTypeTask
{
    /**
     * Executes the report creation
     * 
     * @throws ReportException
     */
    public void run() throws ReportException;

    /**
     * Sets the BIRT engine
     * 
     * @param _reportEngine
     */
    public void setReportEngine(IReportEngine _reportEngine);

    /**
     * Sets the specific report model
     * 
     * @param _report
     */
    public void setReportModel(IReportModel _report);

    /**
     * Sets the (absolute) path under which the model is stored
     * 
     * @param _targetReportPath
     */
    public void setTargetReportPath(String _targetReportPath);

    /**
     * Sets the (absolute) report template path. This file will be used as
     * template for the report.
     * 
     * @param _reportDesignPath
     */
    public void setReportDesignPath(String _reportDesignPath);

    /**
     * This JDBC connection is passed to the BIRT template
     * 
     * @param _conn
     */
    public void setConnection(Connection _conn);
}

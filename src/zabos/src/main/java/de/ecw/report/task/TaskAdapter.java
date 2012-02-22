package de.ecw.report.task;

import java.sql.Connection;

import org.eclipse.birt.report.engine.api.IReportEngine;

import de.ecw.report.IReportTypeTask;
import de.ecw.report.exception.ReportException;
import de.ecw.report.types.IReportModel;

/**
 * Task adapter
 * 
 * @author ckl
 */
abstract public class TaskAdapter implements IReportTypeTask
{
    /**
     * report engine reference
     */
    protected IReportEngine reportEngine;

    /**
     * report model reference
     */
    protected IReportModel report;

    /**
     * target path of report
     */
    protected String targetReportPath;

    /**
     * source path of report template
     */
    protected String reportDesignPath;

    /**
     * JDBC connection
     */
    protected Connection connection;

    abstract public void run() throws ReportException;

    public void setReportEngine(IReportEngine _reportEngine)
    {
        reportEngine = _reportEngine;
    }

    public void setReportModel(IReportModel _report)
    {
        report = _report;
    }

    public void setTargetReportPath(String _targetReportPath)
    {
        targetReportPath = _targetReportPath;
    }

    public void setReportDesignPath(String _reportDesignPath)
    {
        reportDesignPath = _reportDesignPath;
    }

    public void setConnection(Connection _conn)
    {
        connection = _conn;
    }
}

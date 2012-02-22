package de.ecw.report;

import java.sql.Connection;

import org.eclipse.birt.report.engine.api.IReportEngine;

import de.ecw.report.task.CsvTask;
import de.ecw.report.task.PdfTask;
import de.ecw.report.types.IReportModel;
import de.ecw.report.types.ReportFormat;

/**
 * Factory for creating a new report task
 * 
 * @author ckl
 */
public class ReportTaskFactory
{
    /**
     * @param _reportEngine
     * @param _report
     * @param _targetReportPath
     * @param _reportDesignPath
     */
    public static IReportTypeTask create(IReportEngine _reportEngine,
                    IReportModel _report, String _targetReportPath,
                    String _reportDesignPath, Connection _conn)
    {
        IReportTypeTask task = null;

        if (_report.getReportFormat().equals(ReportFormat.CSV))
        {
            task = new CsvTask();
        }

        if (_report.getReportFormat().equals(ReportFormat.PDF))
        {
            task = new PdfTask();
        }

        if (task != null)
        {
            task.setReportDesignPath(_reportDesignPath);
            task.setConnection(_conn);
            task.setReportEngine(_reportEngine);
            task.setReportModel(_report);
            task.setTargetReportPath(_targetReportPath);
        }

        return task;
    }
}

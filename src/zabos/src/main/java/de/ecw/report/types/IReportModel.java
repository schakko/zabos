package de.ecw.report.types;

import java.util.Map;

/**
 * Interface for models from which a report will be generated
 * 
 * @author ckl
 * 
 */
public interface IReportModel
{
    /**
     * Return report format (CSV, PDF) for report
     * 
     * @return
     */
    public ReportFormat getReportFormat();

    /**
     * Return type of report (e.g. bilancing, statistic)
     * 
     * @return
     */
    public ReportType getReportType();

    /**
     * Return specific options which will be passed to the report
     * 
     * @return
     */
    public Map<String, String> getOptions();

    /**
     * Returns an UID under which the report can be unique identified
     * 
     * @return
     */
    public String getReportUid();

    /**
     * Sets whether the report is already created or not
     * 
     * @param _isCreated
     */
    public void setIsCreated(boolean _isCreated);

}

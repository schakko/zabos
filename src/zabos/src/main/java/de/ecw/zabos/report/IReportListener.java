package de.ecw.zabos.report;

import de.ecw.report.types.IReportModel;

/**
 * Listener, der für die Report-Erstellung benutzt werden kann
 * 
 * @author ckl
 * 
 */
public interface IReportListener
{
    /**
     * Der Listener wird mit dem {@link IReportModel} aufgerufen.
     * 
     * @param _reportModel
     */
    public void onExecute(IReportModel _reportModel);
}

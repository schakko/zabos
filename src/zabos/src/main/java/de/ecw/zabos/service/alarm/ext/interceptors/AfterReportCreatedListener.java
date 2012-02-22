package de.ecw.zabos.service.alarm.ext.interceptors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.interceptors.executor.CommandLineExecutorAdapter;
import de.ecw.report.types.IReportModel;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.report.IReportListener;
import de.ecw.zabos.sql.vo.SchleifeVO;

/**
 * Dieser Listener wird aufgerufen, sobald ein Report durch das BIRT-Framework
 * fertig erstellt worden ist.
 * 
 * @author ckl
 * 
 */
public class AfterReportCreatedListener extends CommandLineExecutorAdapter
                implements IReportListener
{
    private final static Logger log = Logger
                    .getLogger(AfterReportCreatedListener.class);

    private String druckerKuerzel = "";

    private SchleifeVO schleifeVO = null;

    private IReportPrintingInterceptor reportPrintingInteceptor = null;

    /**
     * @param _reportPrintingInterceptor
     *            Interceptor, der den Druck in Auftrag gegeben hat.
     * @param _druckerKuerzel
     *            Drucker, auf dem der Report gedruckt werden soll
     * @param _schleifeVO
     *            Schleife, die zu dem Drucker gehört. Kann null sein.
     * @throws StdException
     *             Wenn _reportPrintingInterceptor null ist
     */
    public AfterReportCreatedListener(
                    IReportPrintingInterceptor _reportPrintingInterceptor,
                    String _druckerKuerzel, SchleifeVO _schleifeVO)
                    throws StdException
    {
        if (_reportPrintingInterceptor == null)
        {
            throw new StdException(
                            "Es muss ein ReportPrintingInteceptor uebergeben werden.");
        }
        setReportPrintingInteceptor(_reportPrintingInterceptor);
        druckerKuerzel = _druckerKuerzel;
        schleifeVO = _schleifeVO;
    }

    public void onExecute(IReportModel _reportModel)
    {
        File path = getReportPrintingInteceptor().getReportCreationService()
                        .findReport(_reportModel);

        if (path == null)
        {
            log.error("Die Report-Datei konnte nicht erstellt werden");
        }

        log.info("Der Report [" + _reportModel.getReportUid()
                        + "] wird auf dem Drucker [" + druckerKuerzel
                        + "] gedruckt");

        if (schleifeVO != null)
        {
            log.info("Der Report [" + _reportModel.getReportUid()
                            + "] wird dort gedruckt, weil die Schleife ["
                            + schleifeVO.getDisplayName()
                            + "] diesen Drucker benutzt");
        }

        List<String> listCommands = buildCommandList(getCommandArguments(),
                        _reportModel, path, druckerKuerzel);

        buildExecutor(listCommands, getEnvironmentVariables()).run();
    }

    /**
     * Erstellt aus dem {@link IReportModel} und dem {@link File} eine Liste mit
     * den Parametern, die an das auszuführende Shell-Kommando übergeben werden
     * sollen.<br />
     * Ersetzt wird:
     * <ul>
     * <li>{@link IReportPrintingInterceptor#getOptionUid()} durch
     * {@link IReportModel#getReportUid()}</li>
     * <li> {@link IReportPrintingInterceptor#getOptionAbsolutePath()} durch
     * {@link File#getAbsolutePath()}</li>
     * <li> {@link IReportPrintingInterceptor#getOptionDruckerKuerzel()} durch
     * _druckerKuerzel</li>
     * </ul>
     * 
     * @param _commandArguments
     *            Liste mit Kommandozeilenargumenten, in denen gesucht und
     *            ersetzt wird
     * @param _reportModel
     * @param _path
     * @param _druckerKuerzel
     * @return
     */
    public List<String> buildCommandList(List<String> _commandArguments,
                    IReportModel _reportModel, File _path,
                    String _druckerKuerzel)
    {
        List<String> r = new ArrayList<String>();

        if (_commandArguments != null)
        {
            for (String command : _commandArguments)
            {
                command = command.replace(getReportPrintingInteceptor()
                                .getOptionUid(), _reportModel.getReportUid());
                command = command.replace(getReportPrintingInteceptor()
                                .getOptionAbsolutePath(), _path
                                .getAbsolutePath());
                command = command.replace(getReportPrintingInteceptor()
                                .getOptionDruckerKuerzel(), _druckerKuerzel);
                r.add(command);
            }
        }

        return r;
    }

    public void setReportPrintingInteceptor(
                    IReportPrintingInterceptor reportPrintingInteceptor)
    {
        this.reportPrintingInteceptor = reportPrintingInteceptor;
    }

    public IReportPrintingInterceptor getReportPrintingInteceptor()
    {
        return reportPrintingInteceptor;
    }
}
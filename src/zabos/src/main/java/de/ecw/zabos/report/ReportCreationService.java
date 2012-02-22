package de.ecw.zabos.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.report.IReportService;
import de.ecw.report.exception.ReportException;
import de.ecw.report.types.IReportModel;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * Führt die Erstellung der einzelnen Reports durch und stellt das Bindeglied
 * zwischen de.ecw.zabos und de.ecw.report dar
 * 
 * @author ckl
 * 
 */
public class ReportCreationService
{
    /**
     * Logger-Instanz
     */
    private final static Logger log = Logger
                    .getLogger(ReportCreationService.class);

    /**
     * Enthält die Liste der Reporte, die momentan in der Erstellung sind
     */
    private List<String> listReportQueue = new ArrayList<String>();

    /**
     * Reporting-Instanz
     */
    private IReportService reportService = null;

    /**
     * Datenbank-Verbindung
     */
    private DBResource dbResource = null;

    /**
     * Konstruktor
     * 
     * @param _dbResource
     */
    public ReportCreationService(DBResource _dbResource)
    {
        dbResource = _dbResource;
    }

    /**
     * Konstruktor
     * 
     * @param _dbResource
     * @param _irs
     */
    public ReportCreationService(final DBResource _dbResource,
                    final IReportService _irs)
    {
        dbResource = _dbResource;
        setReportService(_irs);
    }

    /**
     * Setzt den Reporting-Service
     * 
     * @param _irs
     */
    final public void setReportService(IReportService _irs)
    {
        reportService = _irs;
    }

    /**
     * Findet den Alarm der zu dem {@link IReportModel} gehört
     * 
     * @param _reportModel
     * @return
     */
    protected AlarmVO findAlarmByReportModel(IReportModel _reportModel)
    {
        String alarmIdAsString = _reportModel.getOptions().get(
                        ReportModel.KEY_ALARM_ID);
        AlarmVO alarmVO = null;

        if (alarmIdAsString != null)
        {
            try
            {
                AlarmId alarmId = new AlarmId(Long.valueOf(alarmIdAsString));
                alarmVO = dbResource.getDaoFactory().getAlarmDAO()
                                .findAlarmById(alarmId);
            }
            catch (Exception e)
            {
                log.error("Konnte aus dem String [" + alarmIdAsString
                                + "] keine Alarm-ID erzeugen");
            }
        }

        return alarmVO;
    }

    public enum REPORT_CREATION_FLAG
    {
        IST_ERSTELLBAR, ALARM_NICHT_IN_DATENBANK_GEFUNDEN, ALARM_NOCH_AKTIV, REPORT_WIRD_GERADE_ERSTELLT
    }

    /**
     * Liefert zurück, ob aus einem bestehenden Alarm ein Report erstellt werden
     * kann. Dies ist der Fall wenn
     * <ul>
     * <li>der Alarm existiert: {@link #findAlarmByReportModel(IReportModel)}
     * ein gültiges {@link AlarmVO} zurückliefert</li>
     * <li>der Report existiert</li>
     * <li>und der Report <strong>nicht</strong> mehr aktiv ist</li>
     * </ul>
     * 
     * @param _reportModel
     * @return
     * @throws StdException
     */
    public REPORT_CREATION_FLAG isReportOfAlarmCreatable(
                    IReportModel _reportModel)
    {
        AlarmVO alarmVO = findAlarmByReportModel(_reportModel);

        if (alarmVO == null)
        {
            return REPORT_CREATION_FLAG.ALARM_NICHT_IN_DATENBANK_GEFUNDEN;
        }

        if (alarmVO.getAktiv())
        {
            return REPORT_CREATION_FLAG.ALARM_NOCH_AKTIV;
        }

        if (isUidInReportQueue(_reportModel.getReportUid()))
        {
            return REPORT_CREATION_FLAG.REPORT_WIRD_GERADE_ERSTELLT;
        }

        return REPORT_CREATION_FLAG.IST_ERSTELLBAR;
    }

    /**
     * Erstellt für einen Alarm ein neues ReportModel
     * 
     * 
     * @param _ids
     *            Es können beliebige IDs übergeben werden. Allerdings werden
     *            nur die Typen {@link AlarmId} und {@link SchleifeId}
     *            berücksichtigt. Sollten von einem Typ mehr als eine Id
     *            übergeben werden, wird die letzte ID benutzt. aus (AlarmId(1),
     *            SchleifeId(2), AlarmId(3), SchleifeId(4)) wird dann "3_4";
     * @return
     */
    public IReportModel createReportModel(Object... _ids)
    {
        IReportModel r = null;

        AlarmId alarmId = null;
        SchleifeId schleifeId = null;

        if (_ids != null)
        {
            for (Object id : _ids)
            {
                if (id instanceof AlarmId)
                {
                    alarmId = (AlarmId) id;
                }

                if (id instanceof SchleifeId)
                {
                    schleifeId = (SchleifeId) id;
                }
            }
        }

        try
        {
            r = new ReportModel(alarmId, schleifeId);
        }
        catch (StdException e)
        {
            log.error("Konnte kein neues ReportModel erstellen: "
                            + e.getMessage());
        }

        return r;
    }

    /**
     * Erstellt aus übergebenen IDs ein UIDs. Delegiert
     * {@link #createReportModel(Object...)} und liefert
     * {@link IReportModel#getReportUid()} zurück.
     * 
     * @param _ids
     * @return
     */
    public String createUid(Object... _ids)
    {
        IReportModel r = createReportModel(_ids);

        if (r == null)
        {
            return "";
        }

        return r.getReportUid();
    }

    /**
     * Liefert die Datei zurück, in der der Report gespeichert ist. Wenn die
     * Datei nicht gefunden wird, wird null zurückgeliefert
     * 
     * @param _reportModel
     * @return
     */
    public File findReport(IReportModel _reportModel)
    {
        String path = reportService.getTargetPathOfReport(_reportModel);

        File file = new File(path);

        if (!file.exists())
        {
            return null;
        }

        return file;
    }

    /**
     * Fügt die UID zur Reporting-Queue hinzu. <br />
     * <strong>thread-safe</strong>
     * 
     * @param _uid
     */
    protected synchronized void addReportUidToQueue(String _uid)
    {
        if (!listReportQueue.contains(_uid))
        {
            listReportQueue.add(_uid);
        }

    }

    /**
     * Liefert zurück, ob der Alarm in der Queue existiert. <br />
     * <strong>thread-safe</strong>
     * 
     * @param _uid
     * @return
     */
    protected synchronized boolean isUidInReportQueue(String _uid)
    {
        return listReportQueue.contains(_uid);
    }

    /**
     * Delegiert an {@link #isUidInReportQueue(String)}
     * 
     * @param _ids
     * @return
     */
    public boolean isAlarmInReportQueue(Object... _ids)
    {
        String uid = createUid(_ids);
        return isUidInReportQueue(uid);
    }

    /**
     * Entfernt den Alarm aus der Queue. <br />
     * <strong>thread-safe</strong>
     * 
     * @param _alarmId
     */
    protected synchronized void removeAlarmFromReportQueue(String _alarmId)
    {
        listReportQueue.remove(_alarmId);
    }

    /**
     * Thread zum Erstellen der einzelnen Reporte.<br />
     * <ul>
     * <li>Hinzufügen des Alarms in die Queue</li>
     * <li>Generierung des Reports</li>
     * <li>Entfernen des Alarms aus der Queue</li>
     * <li>Wenn ein Listener existiert, diesen ausführen</li>
     * </ul
     * 
     * @author ckl
     */
    protected class ReportCreationThread extends Thread
    {
        private IReportModel reportModel = null;

        private List<IReportListener> reportListener = null;

        public ReportCreationThread(IReportModel _reportModel)
        {
            setReportModel(_reportModel);
        }

        public void run()
        {
            try
            {
                // Report zur Erstellungs-Queue hinzufügen
                addReportUidToQueue(getReportModel().getReportUid());

                // Blockiert
                create(getReportModel());

                if (getReportListener() != null)
                {
                    for (IReportListener listener : getReportListener())
                    {
                        listener.onExecute(getReportModel());
                    }
                }
            }
            catch (ReportException e)
            {
                log.error(e);
            }
            finally
            {
                // Alarm auf jeden Fall aus der Queue kicken
                removeAlarmFromReportQueue(getReportModel().getReportUid());
            }
        }

        /**
         * Delegiert an {@link IReportService#create(IReportModel)}
         * 
         * @param _reportModel
         * @throws ReportException
         */
        protected void create(IReportModel _reportModel) throws ReportException
        {
            reportService.create(_reportModel);
        }

        final public void setReportModel(IReportModel reportModel)
        {
            this.reportModel = reportModel;
        }

        public IReportModel getReportModel()
        {
            return reportModel;
        }

        public void setReportListener(List<IReportListener> reportListener)
        {
            this.reportListener = reportListener;
        }

        public List<IReportListener> getReportListener()
        {
            return reportListener;
        }

    }

    /**
     * Führt die Erstellung eines Reports durch.<br />
     * <strong>thread-safe</strong><br />
     * 
     * Die Erstellung des Reports geschieht über einen eigenen Thread. Der
     * {@link IReportListener} wird nach der Erstellung des Reports ausgeführt.
     * 
     * @param _reportModel
     * @param _afterReportCreated
     *            Liste mit Listenern, der nach dem Erstellen des Reports
     *            ausgeführt werden sollen. Sie werden nur bei erfolgreicher
     *            Erstellung des Reports ausgeführt.
     */
    public void startReportCreation(IReportModel _reportModel,
                    List<IReportListener> _afterReportCreated)
    {
        if (_reportModel == null)
        {
            log
                            .error("Es wurde kein ReportModel zum Erstellen eines neuen Reports uebergeben");
            return;
        }

        // make sure, report can be created
        REPORT_CREATION_FLAG creationFlag = isReportOfAlarmCreatable(_reportModel);

        if (REPORT_CREATION_FLAG.IST_ERSTELLBAR != creationFlag)
        {
            log.error("Der Report ist nicht erstellbar [momentaner Status: "
                            + creationFlag.toString() + "]");
            return;
        }

        ReportCreationThread thread = createNewReportCreationThread(
                        _reportModel, _afterReportCreated,
                        "ReportCreationThread");

        thread.start();
    }

    /**
     * Erstellt ein neues Threading-Objekt
     * 
     * @param _reportModel
     * @param _afterReportCreated
     * @param _threadName
     * @return
     */
    protected ReportCreationThread createNewReportCreationThread(
                    IReportModel _reportModel,
                    List<IReportListener> _afterReportCreated,
                    String _threadName)
    {
        ReportCreationThread thread = new ReportCreationThread(_reportModel);
        thread.setReportListener(_afterReportCreated);
        thread.setName(_threadName);

        return thread;

    }
}

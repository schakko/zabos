package de.ecw.zabos.service.alarm.ext.interceptors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.report.types.IReportModel;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.report.IReportListener;
import de.ecw.zabos.report.ReportCreationService;
import de.ecw.zabos.service.alarm.ext.AlarmInterceptorActionType;
import de.ecw.zabos.service.alarm.ext.IAlarmInterceptor;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.SchleifeVO;

/**
 * Interceptor, der zum Erstellen und automatischen Drucken von Reports benutzt
 * werden muss.
 * 
 * @author ckl
 * 
 */
public class ReportPrintingInterceptor extends ReportPrintingInterceptorAdapter
                implements IAlarmInterceptor
{
    private DBResource dbResource;

    public ReportPrintingInterceptor(final ReportCreationService _rcs,
                    final DBResource _dbResource)
    {
        setReportCreationService(_rcs);
        setDbResource(_dbResource);
    }

    /**
     * Diese Seperatoren können benutzt werden, um mehrere Drucker zu
     * hinterlegen
     */
    public final static String MULTIPLE_DRUCKER_SEPERATOR = "[\\;|\\,]";

    /**
     * Logger-Instanz
     */
    private final static Logger log = Logger
                    .getLogger(ReportPrintingInterceptor.class);

    /**
     * Führt bei der Aktion
     * {@link AlarmInterceptorActionType#AFTER_ALARM_DEAKTIVIEREN} ein
     * Shell-Script aus.<br />
     * Im Shell-Script werden die Parameter %uid%, %drucker_kuerzel% und
     * %abs_path% ersetzt.<br />
     * <ul>
     * <li> {@link #OPTION_UID} ist der Name des Reports OHNE Erweiterung</li>
     * <li> {@link #OPTION_DRUCKER_KUERZEL} ist der Name des Druckers der
     * <strong>ersten</strong> ausgelösten Schleife des Alarms</li>
     * <li> {@link #OPTION_ABSOLUTE_PATH} ist der absolute Pfad zu dem Report
     * inkl. der Dateierweiterung</li>
     * </ul>
     * Die Listener werden an
     * {@link ReportCreationService#startReportCreation(de.ecw.zabos.types.id.AlarmId, de.ecw.zabos.types.id.SchleifeId, List)}
     * übergeben. <br />
     * Das Erstellen des Reports geschieht über einen eigenen Thread, d.h. die
     * Methode {@link #intercept(AlarmInterceptorActionType, AlarmVO)} blockiert
     * nicht.
     * 
     * @see de.ecw.zabos.service.alarm.ext.IAlarmInterceptor#intercept(de.ecw.zabos.service.alarm.ext.AlarmInterceptorActionType,
     *      de.ecw.zabos.sql.vo.AlarmVO)
     */
    public void intercept(AlarmInterceptorActionType type, AlarmVO alarmVO)
    {
        log.debug("intercepting action for report printing");

        if (!(type.equals(AlarmInterceptorActionType.AFTER_ALARM_DEAKTIVIEREN) || type
                        .equals(AlarmInterceptorActionType.AFTER_ALARM_ENTWARNEN)))
        {
            log.debug("intercepting canceled, not listen for given action");
            return;
        }

        if (getReportCreationService() == null)
        {
            log
                            .error("could not intercept report printing: no reportCreationService set");
            return;
        }

        if (alarmVO == null)
        {
            log.error("AlarmVO was null");
            return;
        }

        try
        {

            SchleifeVO[] schleifen = dbResource.getDaoFactory()
                            .getSchleifenDAO().findSchleifenByAlarmId(
                                            alarmVO.getAlarmId());

            List<IReportListener> listeners = createAfterReportCreatedListeners(
                            alarmVO, schleifen);

            IReportModel reportModel = getReportCreationService()
                            .createReportModel(alarmVO.getAlarmId());

            getReportCreationService().startReportCreation(reportModel,
                            listeners);
        }
        catch (StdException e)
        {
            log.error(e);
        }
    }

    /**
     * Erstellt einen neuen {@link AfterReportCreatedListener}
     * 
     * @param _druckerKuerzel
     * @param _schleifeVO
     * @return
     */
    public AfterReportCreatedListener createAfterReportCreatedListener(
                    String _druckerKuerzel, SchleifeVO _schleifeVO)
    {
        try
        {
            return new AfterReportCreatedListener(this, _druckerKuerzel,
                            _schleifeVO);
        }
        catch (StdException e)
        {
            log.error("Fehler beim Erstellen des AfterReportCreatedListeners: "
                            + e.getMessage());
        }

        return null;
    }

    /**
     * Erstellt die Listeners, die nach Erstellung des Reports ausgeführt werden
     * Für jede Schleife wird überprüft, ob
     * {@link SchleifeVO#getDruckerKuerzel()} nicht null ist. Wenn dies der Fall
     * ist, wird ein neuer {@link AfterReportCreatedListener} erzeugt. <br />
     * 
     * @param _alarmVO
     *            Wenn null, wird eine leere Liste zurückgegeben
     * @param _schleifen
     * @return
     */
    public List<IReportListener> createAfterReportCreatedListeners(
                    AlarmVO _alarmVO, SchleifeVO[] _schleifen)
    {
        List<IReportListener> r = new ArrayList<IReportListener>();

        if (_alarmVO == null)
        {
            return r;
        }

        if (isUseAlwayDefaultPrinter())
        {
            log
                            .debug("Fuege Standard-Drucker hinzu. Dieser wird nach Erzeugung des Reports benutzt");
            r.add(createAfterReportCreatedListener("", null));
        }

        if (_schleifen != null && _schleifen.length > 0)
        {
            for (SchleifeVO schleife : _schleifen)
            {
                String druckerKuerzelDerSchleife = schleife.getDruckerKuerzel();

                if (druckerKuerzelDerSchleife != null
                                && !("".equals(druckerKuerzelDerSchleife)))
                {
                    String[] mehrereDruckerKuerzel = druckerKuerzelDerSchleife
                                    .split(MULTIPLE_DRUCKER_SEPERATOR);

                    if (mehrereDruckerKuerzel == null
                                    || mehrereDruckerKuerzel.length == 0)
                    {
                        mehrereDruckerKuerzel = new String[]
                        { druckerKuerzelDerSchleife };
                    }

                    for (String druckerKuerzel : mehrereDruckerKuerzel)
                    {
                        // Lerzeichen entfernen
                        druckerKuerzel = druckerKuerzel.trim();
                        
                        log
                                        .debug("Report fuer Alarm ["
                                                        + _alarmVO.getAlarmId()
                                                        + "] wird nach Abschluss der Erstellung auf Drucker ["
                                                        + druckerKuerzel
                                                        + "] gedruckt");

                        r.add(createAfterReportCreatedListener(druckerKuerzel,
                                        schleife));
                    }
                }
            }
        }

        return r;
    }

    final public void setDbResource(DBResource dbResource)
    {
        this.dbResource = dbResource;
    }

    final public DBResource getDbResource()
    {
        return dbResource;
    }

}

package de.ecw.zabos.frontend.controllers;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.zabos.alarm.ZwischenStatistik;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.FormObject;
import de.ecw.zabos.frontend.FormValidator;
import de.ecw.zabos.frontend.Navigation;
import de.ecw.zabos.frontend.Parameters;
import de.ecw.zabos.frontend.objects.BaseControllerAdapter;
import de.ecw.zabos.frontend.objects.fassade.AlarmiertePersonFassade;
import de.ecw.zabos.frontend.objects.fassade.ZwischenStatistikFassade;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.frontend.utils.DateUtils;
import de.ecw.zabos.service.alarm.IAlarmService;
import de.ecw.zabos.sql.dao.AlarmDAO;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.PersonInAlarmDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.dao.SystemKonfigurationDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.RechtId;

/**
 * Controller für die Reporte
 * 
 * @author ckl
 */
public class ReportController extends BaseControllerAdapter
{
    public static final String DO_STOP_ALARM = "doStopAlarm";

    public static final String DO_SET_HISTORY_SETTINGS = "doSetHistorySettings";

    // Serial
    final static long serialVersionUID = 1209312049;

    // Logger-Instanz
    private final static Logger log = Logger.getLogger(ReportController.class);

    protected OrganisationDAO daoOrganisation = null;

    protected OrganisationsEinheitDAO daoOrganisationsEinheit = null;

    protected BenutzerVerwaltungTAO taoBV = null;

    protected RolleDAO daoRolle = null;

    protected PersonDAO daoPerson = null;

    protected SchleifenDAO daoSchleife = null;

    protected AlarmDAO daoAlarm = null;

    protected PersonInAlarmDAO daoPersonInAlarm = null;

    protected IAlarmService alarmService = null;

    protected SystemKonfigurationDAO daoSystemkonfiguration = null;

    /**
     * JSP-Datei, die für die Anzeige der Details eines Alarms zuständig ist
     */
    private String jspFileReportDetail = "object.jsp";

    public ReportController(final DBResource dbResource)
    {
        super(dbResource);

        // Verzeichnis mit den Views setzen
        this.setActionDir(Navigation.ACTION_DIR_REPORT);

        // TAO/DAO-Factory initalisieren
        daoOrganisation = dbResource.getDaoFactory().getOrganisationDAO();
        daoOrganisationsEinheit = dbResource.getDaoFactory()
                        .getOrganisationsEinheitDAO();
        taoBV = dbResource.getTaoFactory().getBenutzerVerwaltungTAO();
        daoRolle = dbResource.getDaoFactory().getRolleDAO();
        daoPerson = dbResource.getDaoFactory().getPersonDAO();
        daoSchleife = dbResource.getDaoFactory().getSchleifenDAO();
        daoAlarm = dbResource.getDaoFactory().getAlarmDAO();
        daoPersonInAlarm = dbResource.getDaoFactory().getPersonInAlarmDAO();
        daoSystemkonfiguration = dbResource.getDaoFactory()
                        .getSystemKonfigurationDAO();
    }

    /**
     * Alarm-Service über Setter setzen. Zum Zeitpunkt des Erstellens über den
     * Konstruktur ist der Spring-Kontext nocht nicht geladen.
     * 
     * @param _alarmTAO
     */
    public void setAlarmService(IAlarmService _alarmService)
    {
        alarmService = _alarmService;
    }

    /**
     * Liefert den AlarmService
     * 
     * @return
     */
    public IAlarmService getAlarmService()
    {
        return alarmService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.BaseControllerAdapter#setRequestIds(de.
     * ecw.zabos.frontend.ressources.RequestResources)
     */
    @Override
    public void setRequestIds(final RequestResources req)
    {
        if (req.getServletRequest().getParameter(Parameters.ALARM_ID) != null)
        {
            req.setId(Parameters.ALARM_ID, req
                            .getLongForParam(Parameters.ALARM_ID));
        }
        else
        {
            req.setId(Parameters.ALARM_ID, 0);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.BaseControllerAdapter#setViewData(de.ecw
     * .zabos.frontend.ressources.RequestResources)
     */
    @Override
    public void setViewData(final RequestResources req)
    {
        final List<ZwischenStatistikFassade> statistikenVO = new ArrayList<ZwischenStatistikFassade>();
        AlarmVO[] alarmeVO = null;
        PersonInAlarmVO[] voAlarmiertePersonen = null;
        AlarmiertePersonFassade fasAlarmiertePerson = null;
        ZwischenStatistikFassade fasStatistik = null;
        PersonVO voPerson = null;
        AlarmVO alarmVO = null;
        ZwischenStatistik zs = null;

        req
                        .setData(Parameters.JSP_FILE_REPORT_OBJECT,
                                        getJspFileReportDetail());

        try
        {
            if (req.getTab().equals(Navigation.TAB_HISTORY))
            {
                // Spezielles Datum wurde nicht festgelegt => Datum von heute
                // benutzen
                if (req.getStringForAttribute(Parameters.TEXT_DATUM).length() == 0)
                {
                    req.setData(Parameters.TEXT_DATUM, DateUtils
                                    .getDateAsString(new java.util.Date()));
                }

                // Defaults setzen:
                // Standardmäßig wird "Tag" als Einheit ausgew�hlt
                // Der Zeitraum beträgt dabei "1" (1 Tag)
                if (req.getStringForAttribute(
                                Parameters.TEXT_DATUM_RAHMEN_TYPE).length() == 0)
                {
                    req.setData(Parameters.TEXT_DATUM_RAHMEN_TYPE, "day");
                }

                if (req.getStringForAttribute(
                                Parameters.TEXT_DATUM_RAHMEN_RANGE).length() == 0)
                {
                    req.setData(Parameters.TEXT_DATUM_RAHMEN_RANGE, "1");
                }

                int calAttributeType = GregorianCalendar.DAY_OF_MONTH;

                if (req.getStringForAttribute(
                                Parameters.TEXT_DATUM_RAHMEN_TYPE).length() != 0)
                {
                    if (req.getStringForAttribute(
                                    Parameters.TEXT_DATUM_RAHMEN_TYPE).equals(
                                    "day"))
                    {
                        calAttributeType = GregorianCalendar.DAY_OF_MONTH; // !!!!
                    }
                    else if (req.getStringForAttribute(
                                    Parameters.TEXT_DATUM_RAHMEN_TYPE).equals(
                                    "week"))
                    {
                        calAttributeType = GregorianCalendar.WEEK_OF_YEAR;
                    }
                    else if (req.getStringForAttribute(
                                    Parameters.TEXT_DATUM_RAHMEN_TYPE).equals(
                                    "month"))
                    {
                        calAttributeType = GregorianCalendar.MONTH;
                    }
                }

                int scrollType = DateUtils.SCROLL_NONE;

                // Vor-/Zurück-Blättern
                if (req.getStringForAttribute(Parameters.SELECT_STEP).length() != 0)
                {
                    if (req.getStringForAttribute(Parameters.SELECT_STEP)
                                    .equals("future"))
                    {
                        scrollType = DateUtils.SCROLL_FORWARD;// !!!!
                    }
                    else
                    {
                        scrollType = DateUtils.SCROLL_BACKWARD;
                    }
                }

                GregorianCalendar[] calTimespan = DateUtils
                                .getTimespanOfDate(
                                                req
                                                                .getStringForAttribute(Parameters.TEXT_DATUM),
                                                "dd.MM.yyyy",
                                                calAttributeType,
                                                Integer
                                                                .valueOf(
                                                                                req
                                                                                                .getStringForAttribute(Parameters.TEXT_DATUM_RAHMEN_RANGE))
                                                                .intValue(),
                                                scrollType);

                if (calTimespan != null)
                {
                    final GregorianCalendar calLower = calTimespan[0];
                    final GregorianCalendar calHigher = calTimespan[1];
                    final GregorianCalendar calUseStep = calHigher;

                    if (scrollType != DateUtils.SCROLL_NONE)
                    {// !!!!
                        req
                                        .setData(
                                                        Parameters.TEXT_DATUM,
                                                        DateUtils
                                                                        .fillTwoSigns(calUseStep
                                                                                        .get(GregorianCalendar.DAY_OF_MONTH))
                                                                        + "."
                                                                        + DateUtils
                                                                                        .fillTwoSigns((calUseStep
                                                                                                        .get(GregorianCalendar.MONTH) + 1))
                                                                        + "."
                                                                        + calUseStep
                                                                                        .get(GregorianCalendar.YEAR));
                    }

                    req.setData(Parameters.TS_DATUM_START, String
                                    .valueOf(+calLower.getTimeInMillis()));
                    req.setData(Parameters.TS_DATUM_END, String
                                    .valueOf(+calHigher.getTimeInMillis()));

                    alarmeVO = daoAlarm.findAlarmeByZeitfenster(new UnixTime(
                                    calLower.getTimeInMillis()), new UnixTime(
                                    calHigher.getTimeInMillis()));
                }
            }

            if (alarmeVO != null)
            {
                for (int i = 0, m = alarmeVO.length; i < m; i++)
                {
                    SchleifeVO[] voSchleifenInAlarm = daoSchleife
                                    .findSchleifenByAlarmId(alarmeVO[i]
                                                    .getAlarmId());

                    for (int j = 0, n = voSchleifenInAlarm.length; j < n; j++)
                    {
                        // Person darf hat mindestens in einer Schleife das
                        // Recht die Details zu sehen
                        if (daoPerson.hatPersonRechtInSchleife(req
                                        .getUserBean().getPerson()
                                        .getPersonId(),
                                        RechtId.ALARMHISTORIE_DETAILS_SEHEN,
                                        voSchleifenInAlarm[j].getSchleifeId()))
                        {
                            voPerson = daoPerson.findPersonById(alarmeVO[i]
                                            .getAlarmPersonId());
                            fasStatistik = new ZwischenStatistikFassade();
                            zs = getAlarmService().zwischenStatistik(
                                            alarmeVO[i]);
                            fasStatistik.setAlarm(alarmeVO[i]);
                            fasStatistik.setZwischenStatisitik(zs);
                            fasStatistik.setPerson(voPerson);

                            // Statistik hinzufügen
                            statistikenVO.add(fasStatistik);
                            break;
                        }
                    }
                }
            }
        }
        catch (StdException e)
        {
            req.getErrorBean().addMessage(e);
        }

        // Aktueller Alarm wurde festgelegt
        if (req.getTab().equals(Navigation.TAB_OBJECT))
        {
            if (req.getId(Parameters.ALARM_ID) > 0)
            {
                try
                {
                    alarmVO = daoAlarm.findAlarmById(new AlarmId(req
                                    .getId(Parameters.ALARM_ID)));

                    if (alarmVO != null)
                    {
                        voPerson = daoPerson.findPersonById(alarmVO
                                        .getAlarmPersonId());
                        voAlarmiertePersonen = daoPersonInAlarm
                                        .findPersonenInAlarmByAlarmId(alarmVO
                                                        .getAlarmId());

                        fasStatistik = new ZwischenStatistikFassade();
                        zs = alarmService.zwischenStatistik(alarmVO);
                        fasStatistik.setAlarm(alarmVO);
                        fasStatistik.setPerson(voPerson);
                        fasStatistik.setZwischenStatisitik(zs);

                        // Durch die alarmierten Personen iterieren
                        if (voAlarmiertePersonen != null)
                        {
                            for (int i = 0, m = voAlarmiertePersonen.length; i < m; i++)
                            {
                                fasAlarmiertePerson = new AlarmiertePersonFassade();
                                voPerson = daoPerson
                                                .findPersonById(voAlarmiertePersonen[i]
                                                                .getPersonId());

                                if (voPerson != null)
                                {
                                    fasAlarmiertePerson.setPerson(voPerson);
                                    fasAlarmiertePerson
                                                    .setPersonInAlarm(voAlarmiertePersonen[i]);
                                    fasStatistik
                                                    .setAlarmiertePerson(fasAlarmiertePerson);
                                }
                            }
                        }
                    }
                }
                catch (StdException e)
                {
                    log.error(e);
                }
            }

            req.setData(Parameters.OBJ_STATISTIK, fasStatistik);
        }

        req.setData(Parameters.ARR_ALARME_STATISTIKEN, statistikenVO);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.BaseControllerAdapter#run(de.ecw.zabos.
     * frontend.ressources.RequestResources)
     */
    @Override
    public void run(final RequestResources req)
    {
        if (req.isValidSubmit())
        {
            if (req.getRequestDo().equals(DO_SET_HISTORY_SETTINGS))
            {
                doSetHistorySettings(req);
            }
            else if (req.getRequestDo().equals(DO_STOP_ALARM))
            {
                doStopAlarm(req);
            }
            else
            {
                log.error("Do " + req.getRequestDo()
                                + " wurde noch nicht in der Methode "
                                + this.getClass().getName()
                                + "::dispatchExplicitSubmit definiert");
            }
        }
    }

    /**
     * Wird aufgerufen, wenn der der Zeitraum der Alarme geändert werden soll
     * 
     * @author ckl
     */
    protected void doSetHistorySettings(final RequestResources req)
    {
        FormValidator formValidator = req.buildFormValidator();
        FormObject foDatum = new FormObject("textDatum", "Datum");
        foDatum.setFlag(FormObject.VALID_DATE_FORMAT);
        foDatum.setFlag(FormObject.VALID_DATE);
        formValidator.add(foDatum);
        formValidator.run();

        if (formValidator.getTotalErrors() == 0)
        {
            // Wir brauchen nur den Datums-Wert weiterleten, den Rest muss die
            // View
            // besorgen
            req.setData(Parameters.TEXT_DATUM, req
                            .getStringForParam(Parameters.TEXT_DATUM));
            // Zeitraum parsen
            String datumRahmen = "";
            String[] paramsDatum = null;

            if ((datumRahmen = req
                            .getStringForParam(Parameters.SELECT_DATUM_RAHMEN))
                            .length() != 0)
            {
                paramsDatum = datumRahmen.split("_");

                if (paramsDatum != null)
                {
                    req.setData(Parameters.TEXT_DATUM_RAHMEN_TYPE,
                                    paramsDatum[0]);
                    log.debug("Parameter TEXT_DATUM_RAHMEN_RANGE resolves to " + Parameters.TEXT_DATUM_RAHMEN_RANGE);
                    req.setData(Parameters.TEXT_DATUM_RAHMEN_RANGE,
                                    paramsDatum[1]);
                }
            }

            if ((req.getStringForParam(Parameters.SELECT_STEP).length() != 0)
                            && ((req.getStringForParam(Parameters.SELECT_STEP)
                                            .equals("past") || req
                                            .getStringForParam(
                                                            Parameters.SELECT_STEP)
                                            .equals("future"))))
            {
                req.setData(Parameters.SELECT_STEP, req
                                .getStringForParam(Parameters.SELECT_STEP));
            }
        }
    }

    /**
     * Beendet einen Alarm
     * 
     * @since 2006-06-06 CKL
     * @author ckl
     */
    protected void doStopAlarm(RequestResources req)
    {
        long alarmId = 0;
        SchleifeVO[] schleifenVO = null;
        AlarmVO alarmVO = null;

        if ((alarmId = req.getLongForParam(Parameters.ALARM_ID)) != 0)
        {
            // Alle Schleifen laden
            try
            {
                if (req.getUserBean().getPerson() != null)
                {
                    alarmVO = daoAlarm.findAlarmById(new AlarmId(alarmId));

                    if (alarmVO != null)
                    {
                        schleifenVO = daoSchleife
                                        .findSchleifenByAlarmId(alarmVO
                                                        .getAlarmId());

                        boolean bHatRechtAlarmZuEntwarnen = true;

                        // überprüfen, ob Person wirklich überall das
                        // Recht hat, den Alarm entwarnen zu dürfen
                        for (int i = 0, m = schleifenVO.length; i < m; i++)
                        {
                            if (!daoPerson.hatPersonRechtInSchleife(req
                                            .getUserBean().getPerson()
                                            .getPersonId(),
                                            RechtId.ALARM_AUSLOESEN,
                                            schleifenVO[i].getSchleifeId()))
                            {
                                bHatRechtAlarmZuEntwarnen = false;
                            }
                        }

                        // Person darf Alarm entwarnen
                        if (bHatRechtAlarmZuEntwarnen)
                        {
                            PersonInAlarmVO[] voPersonenInAlarm = daoPersonInAlarm
                                            .findPersonenInAlarmByAlarmId(alarmVO
                                                            .getAlarmId());
                            getAlarmService().alarmEntwarnung(alarmVO,
                                            voPersonenInAlarm);
                        }
                        else
                        {
                            req
                                            .getErrorBean()
                                            .addMessage(
                                                            "Sie besitzen nicht das Recht, den Alarm zu entwarnen.");
                        }
                    }
                    else
                    {
                        req
                                        .getErrorBean()
                                        .addMessage(
                                                        "Der Alarm mit der angegebenen Id konnte nicht gefunden werden.");
                    }
                }
                else
                {
                    req.getErrorBean().addMessage("Sie sind nicht eingeloggt.");
                }
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(
                                "Der Alarm konnte nicht entwarnt werden.");
                log.error(buildLogMessage(req, e.getMessage()));
            }
        }
    }

    /**
     * Legt den Namen der JSP-Datei fest, die die Details zu einem spezifischen
     * Alarm anzeigt
     * 
     * @param jspFileReportDetail
     */
    public void setJspFileReportDetail(String jspFileReportDetail)
    {
        this.jspFileReportDetail = jspFileReportDetail;
    }

    /**
     * Liefert den Namen der JSP-Datei zurueck, die die Details zu einem
     * spezifischen Alarm liefert
     * 
     * @return
     */
    public String getJspFileReportDetail()
    {
        return jspFileReportDetail;
    }
}

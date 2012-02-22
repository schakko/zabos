package de.ecw.zabos.frontend.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.zabos.broadcast.sms.SmsContent;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.FormObject;
import de.ecw.zabos.frontend.FormValidator;
import de.ecw.zabos.frontend.Navigation;
import de.ecw.zabos.frontend.Parameters;
import de.ecw.zabos.frontend.objects.BaseControllerAdapter;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.service.alarm.IAlarmService;
import de.ecw.zabos.sql.dao.AlarmDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.AlarmQuelleId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * Controller für die Alarmierung
 * 
 * @author ckl
 */
public class AlarmierungController extends BaseControllerAdapter
{
    public static final String DO_ALARM = "doAlarm";

    // Serial
    final static long serialVersionUID = 1209312049;

    // Logger-Instanz
    private final static Logger log = Logger
                    .getLogger(AlarmierungController.class);

    private IAlarmService alarmService;

    private SmsContent smsContent;

    private boolean isGpsEnabled = false;

    /**
     * Konstruktor
     */
    public AlarmierungController(final DBResource dbResource)
    {
        super(dbResource);

        // Verzeichnis mit den Templates setzen
        this.setActionDir(Navigation.ACTION_DIR_ALARMIERUNG);
    }

    /**
     * Wird aufgerufen, wenn die Seite abgeschickt wurde
     */
    @Override
    public void run(final RequestResources req)
    {
        if (req.isValidSubmit())
        {
            if (req.getRequestDo().equals(DO_ALARM))
            {
                doAlarm(req);
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

    public int getMaxLengthForNachricht()
    {
        if (getSmsContent() != null)
        {
            String gps = (isGpsEnabled()) ? ("012345678912345") : ("");
            SchleifeVO testSchleife = getDbResource().getObjectFactory()
                            .createSchleife();
            try
            {
                testSchleife.setName("0123456789012345");
                testSchleife.setKuerzel("01234");
            }
            catch (StdException e)
            {

            }

            AlarmVO alarm = getDbResource().getObjectFactory().createAlarm();
            alarm.setReihenfolge(100000);

            String msg = getSmsContent().resolveBestMatchingContentForAlarm(
                            alarm, testSchleife, " ", gps);

            int r = (160 - msg.length());
            
            return r;
        }

        return 50;
    }

    @Override
    public void setViewData(final RequestResources req)
    {
        req.setData(Parameters.MAX_LENGTH_NACHRICHT, getMaxLengthForNachricht());
        req.setData(Parameters.IS_GPS_ENABLED, isGpsEnabled());
    }

    /**
     * Erzeugt einen Alarm
     */
    protected void doAlarm(final RequestResources req)
    {
        final SchleifenDAO daoSchleife = req.getDbResource().getDaoFactory()
                        .getSchleifenDAO();
        final PersonDAO daoPerson = req.getDbResource().getDaoFactory()
                        .getPersonDAO();
        final AlarmDAO daoAlarm = req.getDbResource().getDaoFactory()
                        .getAlarmDAO();

        long arrZuAlarmierendeSchleifen[] = req
                        .getLongArrayForParam(Parameters.SELECT_SCHLEIFEN_SEND_TO);
        List<SchleifeVO> voAlarmierung = new ArrayList<SchleifeVO>();
        SchleifeVO schleifeVO = null;
        SchleifeVO[] schleifenVO = null;

        FormValidator formValidator = new FormValidator(
                        req.getServletRequest(), req.getErrorBean());
        FormObject foTextSize = new FormObject(Parameters.TEXT_NACHRICHT,
                        "Nachricht");
        foTextSize.setFlag(-FormObject.NOT_EMPTY);
        foTextSize.setMaxLength(50);

        // Reload-Sperre
        FormObject foFormularSubmit = new FormObject(Parameters.FRM_SUBMIT,
                        "Alarmierungs-Formular");
        foFormularSubmit.setFlag(FormObject.DISALLOW_RELOAD);
        foFormularSubmit.setFlag(-FormObject.NOT_EMPTY);
        formValidator.add(foFormularSubmit);

        formValidator.run();

        // Es sind Schleifen zum Alarmieren ausgewaehlt und bei der Validierung
        // der
        // Eingabe sind keine Fehler aufgetreten
        if ((arrZuAlarmierendeSchleifen != null)
                        && (formValidator.getTotalErrors() == 0))
        {
            // User ist auch wirklich eingeloggt
            if (req.getUserBean().getPerson() == null)
            {
                req.getErrorBean().addMessage("Sie sind nicht eingeloggt.");
            }
            else
            {
                // Alarme ueberpruefen
                for (int i = 0, m = arrZuAlarmierendeSchleifen.length; i < m; i++)
                {
                    try
                    {
                        schleifeVO = daoSchleife
                                        .findSchleifeById(new SchleifeId(
                                                        arrZuAlarmierendeSchleifen[i]));

                        // Schleife konnte gefunden werden
                        if (schleifeVO != null)
                        {
                            // Person hat das Recht dazu
                            if (daoPerson.hatPersonRechtInSchleife(req
                                            .getUserBean().getPerson()
                                            .getPersonId(),
                                            RechtId.ALARM_AUSLOESEN,
                                            schleifeVO.getSchleifeId()))
                            {
                                SchleifeId schleifeId = new SchleifeId(
                                                arrZuAlarmierendeSchleifen[i]);

                                // Schleife ist nicht aktiv
                                if (!daoAlarm.isSchleifeAktiv(schleifeId))
                                {
                                    voAlarmierung.add(schleifeVO);
                                }
                                else
                                { // Schleife aktiv
                                    req.getErrorBean()
                                                    .addMessage("Die Schleife "
                                                                    + schleifeVO.getKuerzel()
                                                                    + " ist bereits ausgelöst.");
                                } // if schleife.aktiv
                            }
                            else
                            { // Person hat kein Recht
                                req.getErrorBean()
                                                .addMessage("Sie haben nicht das Recht, in der Schleife "
                                                                + schleifeVO.getKuerzel()
                                                                + " einen Alarm auszulösen.");
                            } // if hatrechtinschleife
                        }
                        else
                        { // Schleife konnte nicht gefunden werden
                            req.getErrorBean()
                                            .addMessage("Die Schleife mit der Id "
                                                            + arrZuAlarmierendeSchleifen[i]
                                                            + " konnte nicht gefunden werden");
                        } // voSchleife != null
                    }
                    catch (StdException e)
                    { // Standard-Exception
                        req.getErrorBean().addMessage(e);
                    }
                }

                schleifenVO = new SchleifeVO[voAlarmierung.size()];

                for (int i = 0, m = voAlarmierung.size(); i < m; i++)
                {
                    schleifenVO[i] = (SchleifeVO) voAlarmierung.get(i);
                }

                if (schleifenVO != null)
                {
                    if (schleifenVO.length > 0)
                    {
                        getAlarmService()
                                        .alarmAusloesen(req
                                                        .getStringForParam(Parameters.TEXT_NACHRICHT),
                                                        schleifenVO,
                                                        AlarmQuelleId.ID_WEB,
                                                        req.getUserBean()
                                                                        .getPerson()
                                                                        .getPersonId(),
                                                        null,
                                                        req.getStringForParam(Parameters.TEXT_GPS_KOORDINATE));
                        req.setData(Parameters.IS_AUSGELOEST, "1");
                        req.setData(Parameters.ARR_SCHLEIFEN_AUSGELOEST,
                                        schleifenVO);
                    }
                    else
                    {
                        req.getErrorBean()
                                        .addMessage("Es konnten keine Schleifen für die Alarmierung gesetzt werden.");
                    }
                }
            }
        }
        else
        {
            req.getErrorBean()
                            .addMessage("Es wurde kein Array mit den zu alarmierenden Schleifen übergeben");
        }
    }

    /**
     * Setzt den {@link IAlarmService}
     * 
     * @param alarmService
     */
    public void setAlarmService(final IAlarmService alarmService)
    {
        this.alarmService = alarmService;
    }

    /**
     * Liefert den {@link IAlarmService}
     * 
     * @return
     */
    public IAlarmService getAlarmService()
    {
        return alarmService;
    }

    public boolean isGpsEnabled()
    {
        return isGpsEnabled;
    }

    public void setGpsEnabled(boolean isGpsEnabled)
    {
        this.isGpsEnabled = isGpsEnabled;
    }

    public SmsContent getSmsContent()
    {
        return smsContent;
    }

    public void setSmsContent(SmsContent smsContent)
    {
        this.smsContent = smsContent;
    }
}

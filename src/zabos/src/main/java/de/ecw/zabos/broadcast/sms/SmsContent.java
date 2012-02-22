package de.ecw.zabos.broadcast.sms;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import de.ecw.zabos.alarm.RueckmeldeStatistik;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.SchleifeVO;

/**
 * Liefert den vordefinierten Inhalt der SMSen zurück. Dieses wird über die
 * passende Sprachdatei (smscontent_messages.properties) und Spring Expression
 * Language aufgelöst, so dass der Inhalt frei definierbar ist.
 * 
 * @author ckl
 * 
 */
public class SmsContent
{
    private final static Logger log = Logger.getLogger(SmsContent.class);

    public final static String PARAM_SCHLEIFE = "schleife";

    public final static String PARAM_ALARM = "alarm";

    public final static String PARAM_HEADER = "header";

    public final static String PARAM_AUSGELOEST = "infoAusgeloest";

    public final static String PARAM_UNBEKANNT = "infoUnbekannt";

    public final static String PARAM_FUNKTIONSTRAEGER_DETAILS = "funktionstraegerDetails";

    public final static String PARAM_RUECKMELDESTATISTIK = "rueckmeldeStatistik";

    public final static String PARAM_FUNKTIONSTRAEGER = "funktionstraeger";

    public final static String PARAM_KEINE_BERECHTIGUNG = "infoKeineBerechtigung";

    public final static String PARAM_BEREITS_AUSGELOEST = "infoBereitsAusgeloest";

    public final static String ALARM_HEADER = "alarm.header";

    public final static String SYSTEM_AKTIVIERT = "zabos.aktiviert";

    public static final String SYSTEM_BEREITS_AKTIV = "zabos.bereits_aktiv";

    public static final String SYSTEM_BEREITS_INAKTIV = "zabos.bereits_inaktiv";

    public static final String SYSTEM_DEAKTIVIERT = "zabos.deaktiviert";

    public static final String SYSTEM_UNBEKANNT = "zabos.unbekannt";

    public static final String ABWESENHEITSZEIT_GESETZT = "abwesenheit.gesetzt";

    public static final String ABWESENHEIT_AUFGEHOBEN = "abwesenheit.aufgehoben";

    public static final String ALARM_ENTWARNUNG = "alarm.entwarnt";

    public static final String ALARM_AUSLOESUNG_REPORT = "alarm.ausloesung";

    public static final String ALARM_AUSLOESUNG_REPORT_AUSGELOEST = "alarm.ausloesung.ausgeloest";

    public static final String ALARM_AUSLOESUNG_REPORT_UNBEKANNT = "alarm.ausloesung.unbekannt";

    public static final String ALARM_AUSLOESUNG_REPORT_KEINE_BERECHTIGUNG = "alarm.ausloesung.keine_berechtigung";

    public static final String ALARM_AUSLOESUNG_REPORT_BEREITS_AUSGELOEST = "alarm.ausloesung.bereits_ausgeloest";

    public static final String RUECKMELDUNG_HEADER = "rueckmeldung.header";

    public static final String RUECKMELDUNG_REPORT = "rueckmeldung.standard";

    public static final String RUECKMELDUNG_FUNKTIONSTRAEGER_REPORT = "rueckmeldung.funktionstraeger";

    public static final String FUNKTIONSTRAEGER_STATISTIK = "rueckmeldung.funktionstraeger.detail";

    public static final String RUECKMELDUNG_REPORT_KLINIKUM = "rueckmeldung.ja_nein";

    public static final String ALARM_BENACHRICHTIGUNG_HEADER = "alarm.benachrichtigung.header";

    public static final String ALARM_BENACHRICHTIGUNG = "alarm.benachrichtigung";

    public static final String ALARM_BENACHRICHTIGUNG_MIT_GPS_KOORDINATE = "alarm.benachrichtigung.gps";

    public static final String ALARM_BENACHRICHTIGUNG_MIT_HINWEIS = "alarm.benachrichtigung.mit_hinweis";

    public static final String ALARM_BENACHRICHTIGUNG_MIT_HINWEIS_UND_GPS_KOORDINATE = "alarm.benachrichtigung.gps.mit_hinweis";

    public static final String FAIL = "failed";

    private MessageSource messages;

    public SmsContent(MessageSource _messageSource)
    {
        messages = _messageSource;
    }

    /**
     * Sucht die übergebene Nachricht und liefert diese zurück
     * 
     * @param _msgId
     * @param args
     * @return
     */
    public String resolve(String _msgId, Map<String, Object> args)
    {
        
        String msg = "";

        if (args == null)
        {
            args = new HashMap<String, Object>();
        }

        try
        {
            msg = messages.getMessage(_msgId, null, null);
            
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext sec = new StandardEvaluationContext();
            sec.setVariables(args);

            msg = parser.parseExpression(msg).getValue(sec, String.class);
        }
        catch (Exception e)
        {
            log.error("Failed to resolve message \"" + _msgId + "\": "
                            + e.getMessage());
            msg = _msgId + ": " + FAIL;
        }

        return msg;
    }

    /**
     * Sucht die Nachricht und liefert diese zurück.
     * 
     * @param _headerId
     *            Die ID des Headers. Dieser ist in der jeweiligen
     *            Hauptnachricht über #header (Spring EL) abrufbar
     * @param _bodyId
     * @param _schleifeVO
     *            Ist in der Hauptnachricht über #schleife (Spring EL) abrufbar
     * 
     * @param _alarmVO
     *            Ist in der Hauptnachricht über #alarm (Spring EL) abrufbar
     * @param args
     *            Map mit zusätzlichen Argumenten
     * @return
     */
    public String resolveWithHeader(String _headerId, String _bodyId,
                    SchleifeVO _schleifeVO, AlarmVO _alarmVO,
                    Map<String, Object> args)
    {
        if (args == null)
        {
            args = new HashMap<String, Object>();
        }

        args.put(PARAM_ALARM, _alarmVO);
        args.put(PARAM_SCHLEIFE, _schleifeVO);

        String header = resolve(_headerId, args);

        args.put(PARAM_HEADER, header);

        return resolve(_bodyId, args);
    }

    /**
     * Erstellt die SMS, die der Benutzer bekommt, der den Alarm ausgelöst hat
     * 
     * @param _alarmVO
     * @param _ausgeloest
     * @param _unbekannt
     * @param _keineBerechtigung
     * @param _bereitsAusgeloest
     * @param args
     * @return
     */
    public String resolveAusloesung(AlarmVO _alarmVO, String _ausgeloest,
                    String _unbekannt, String _keineBerechtigung,
                    String _bereitsAusgeloest, Map<String, Object> args)
    {
        if (args == null)
        {
            args = new HashMap<String, Object>();
        }

        args.put(PARAM_ALARM, _alarmVO);

        args.put(PARAM_AUSGELOEST,
                        resolveParameters(ALARM_AUSLOESUNG_REPORT_AUSGELOEST,
                                        _ausgeloest));
        args.put(PARAM_UNBEKANNT,
                        resolveParameters(ALARM_AUSLOESUNG_REPORT_UNBEKANNT,
                                        _unbekannt));
        args.put(PARAM_KEINE_BERECHTIGUNG,
                        resolveParameters(
                                        ALARM_AUSLOESUNG_REPORT_KEINE_BERECHTIGUNG,
                                        _keineBerechtigung));
        args.put(PARAM_BEREITS_AUSGELOEST,
                        resolveParameters(
                                        ALARM_AUSLOESUNG_REPORT_BEREITS_AUSGELOEST,
                                        _bereitsAusgeloest));

        return resolve(ALARM_AUSLOESUNG_REPORT, args);
    }

    /**
     * Löst die Nachricht anhand über {@link MessageSource} direkt auf
     * 
     * @param _msgId
     * @param args
     * @return
     */
    public String resolveParameters(String _msgId, String... args)
    {
        if (args != null && args.length > 0 && args[0] != null)
        {
            return messages.getMessage(_msgId, args, null);
        }
        return "";
    }

    /**
     * Liefert die SMS-Nachricht zurück, die am besten für den gegebenen Fall
     * zutrifft.
     * 
     * @param _alarmVO
     * @param _schleifeVO
     * @param _hinweis
     * @param _gps
     * @return
     */
    public String resolveBestMatchingContentForAlarm(AlarmVO _alarmVO,
                    SchleifeVO _schleifeVO, String _hinweis, String _gps)
    {
        String msg = ALARM_BENACHRICHTIGUNG;
        boolean bHinweis = (_hinweis != null && _hinweis.length() > 0);
        boolean bGps = (_gps != null && _gps.length() > 0);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("gps", _gps);
        map.put("hinweis", _hinweis);

        if (bHinweis)
        {
            msg = ALARM_BENACHRICHTIGUNG_MIT_HINWEIS;

            if (bGps)
            {
                msg = ALARM_BENACHRICHTIGUNG_MIT_HINWEIS_UND_GPS_KOORDINATE;
            }
        }
        else
        {
            if (bGps)
            {
                msg = ALARM_BENACHRICHTIGUNG_MIT_GPS_KOORDINATE;
            }
        }

        return resolveWithHeader(ALARM_BENACHRICHTIGUNG_HEADER, msg,
                        _schleifeVO, _alarmVO, map);
    }

    public String resolveRueckmeldeStatistik(String _msgId, AlarmVO _alarmVO,
                    SchleifeVO _schleifeVO,
                    RueckmeldeStatistik _rueckmeldeStatistik,
                    Map<String, Object> args)
    {
        if (args == null)
        {
            args = new HashMap<String, Object>();
        }

        args.put(PARAM_RUECKMELDESTATISTIK, _rueckmeldeStatistik);

        return resolveWithHeader(RUECKMELDUNG_HEADER, _msgId, _schleifeVO,
                        _alarmVO, args);

    }
}

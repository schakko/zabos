package de.ecw.zabos.service.alarm;

import de.ecw.zabos.alarm.ZwischenStatistik;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.AlarmQuelleId;
import de.ecw.zabos.types.id.PersonId;

/**
 * Interface zum Verarbeiten von Alarmen
 * 
 * @author ckl
 * 
 */
public interface IAlarmService
{

    /**
     * Ausloesung eines Alarms. - Anlegen eines Alarms - Ermittlung der
     * Schleifen-Personen - Ermittlung der Rufnummern der Schleifen-Personen -
     * Anlegen von schleife_in_alarm - Anlegen von smsout - Anlegen von
     * schleife_in_smsout - Report Nachricht fuer Person, welche den Alarm
     * ausgeloest hat genererieren -<br />
     * 
     * @param _zusatzText
     *            optionaler Zusatztext
     * @param _schleifen
     * @param _alarmQuelleId
     * @param _alarmPersonId
     *            null=Auslösung durch 5Ton
     * @param _rpUnbekannt
     *            null oder ',' separierte Liste von unbekannten Schleifen
     * @param _gpsKoordinate
     *            optionale GPS-Koordinaten des Alarms
     * @throws StdException
     */
    public AlarmVO alarmAusloesen(String _zusatzText, SchleifeVO[] _schleifen,
                    AlarmQuelleId _alarmQuelleId, PersonId _alarmPersonId,
                    String _rpUnbekannt, String _gpsKoordinate);

    /**
     * Aktive Alarme ermitteln und überprüfen
     * 
     * @throws StdException
     */
    public void processAktiveAlarme();

    /**
     * Vorläufige Rückmelde-Statistik für einen Alarm erfragen. Kann für
     * Alarm_Historie-View in der Web-GUI verwendet werden.
     * 
     * @param _alarmVO
     * @return
     */
    public ZwischenStatistik zwischenStatistik(AlarmVO _alarmVO);

    /**
     * Alarm Entwarnung; Entwarnt den Alarm mit der angegebenen Id
     * 
     * @param _alarmVO
     * @param _piaVOs
     * @since 2006-06-08
     * @author ckl
     */
    public void alarmEntwarnung(AlarmVO _alarmVO, PersonInAlarmVO[] _piaVOs) throws StdException;

}
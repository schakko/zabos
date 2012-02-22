package de.ecw.zabos.service.smsin.klinikum;

import org.apache.log4j.Logger;

import de.ecw.zabos.broadcast.sms.SmsContent;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.service.alarm.IAlarmService;
import de.ecw.zabos.service.smsin.SmsInServiceAdapter;
import de.ecw.zabos.sql.dao.AlarmDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RueckmeldungStatusVO;
import de.ecw.zabos.sql.vo.SystemKonfigurationVO;
import de.ecw.zabos.types.id.PersonId;

/**
 * Für das Klinikum ist es nötig, dass die SMSen, die nach Erreichen des
 * Bereichs-Timeouts eingehen, nicht mehr Auswirkungen auf den Rückmelde-Status
 * haben
 * 
 * @author ckl
 * 
 */
public class KlinikumSmsInService extends SmsInServiceAdapter
{
    private final static Logger log = Logger
                    .getLogger(KlinikumSmsInService.class);

    /**
     * Konstruktor
     * 
     * @param _dbresource
     *            Unbedingt eine neue Datenbankinstanz übergeben, da diese
     *            Klasse gethreadet wird.
     * 
     * @param _alarmService
     * @param _systemKonfiguration
     * @param _smsContent
     */
    public KlinikumSmsInService(final DBResource _dbResource,
                    final IAlarmService _alarmService,
                    SystemKonfigurationVO _systemKonfiguration, SmsContent _smsContent)
    {
        super(_dbResource, _alarmService, _systemKonfiguration, _smsContent);
    }

    /**
     * Der Rückmeldestatus für eine Person innerhalb eines Alarms kann geändert
     * werden, wenn
     * <ul>
     * <li>der Alarm noch aktiv ist</li>
     * <li>und der Bereich innerhalb des Alarms noch aktiv ist</li>
     * </ul>
     */
    public void processRueckmeldungStatus(PersonId _personId,
                    PersonVO _personVO, RueckmeldungStatusVO rmsVO) throws StdException

    {
        AlarmDAO daoAlarm = daoFactory.getAlarmDAO();

        // SMS ist Rueckmeldung auf ausgeloesten Alarm.
        // Ist die Person einem aktiven Alarm zugeordnet?
        PersonInAlarmVO[] personInAlarmVOs = personInAlarmDAO
                        .findPersonenInAktivemAlarmByPersonId(_personId);

        if (personInAlarmVOs.length == 0)
        {
            // Rueckmeldung bezieht sich auf nicht mehr aktiven Alarm ==>
            // verwerfen
            log.debug("SMS Rueckmeldung von Person name=\""
                            + _personVO.getName()
                            + "\" bezieht sich auf inaktive(m) Alarm(e)");
            return;
        }

        boolean bIsRueckmeldungGeupdatet = false;

        for (int i = 0, m = personInAlarmVOs.length; (i < m)
                        && (!bIsRueckmeldungGeupdatet); i++)
        {
            PersonInAlarmVO pia = personInAlarmVOs[i];

            // Ist der Bereich des Alarms noch aktiv, dem die Person
            // angehört?
            if (daoAlarm.isFunktionstraegerBereichInAlarmAktiv(
                            pia.getAlarmId(),
                            _personVO.getFunktionstraegerId(), _personVO
                                            .getBereichId()))
            {
                log
                                .debug("SMS Rueckmeldung mit der ID "
                                                + rmsVO
                                                                .getRueckmeldungStatusId()
                                                + " von Person name=\""
                                                + _personVO.getName()
                                                + "\" wurde akzeptiert. Die Bereichs-/Funktrionstraeger-Kombination der Person ist noch aktiv.");

                // Rueckmeldung-Status wird fuer alle aktiven Alarme gesetzt,
                // da aus der Rueck-SMS nicht hervorgehen kann, auf welchen
                // Alarm sich die Antwort bezieht
                personInAlarmDAO.updateRueckmeldungStatus(_personId, rmsVO
                                .getRueckmeldungStatusId());

                // Nach dem ersten Durchlauf kann die Schleife beendet werden,
                // da die Rueckmeldung fuer alle aktiven Alarme gilt
                bIsRueckmeldungGeupdatet = true;
            }
        }

        if (!bIsRueckmeldungGeupdatet)
        {
            log
                            .error("SMS Rueckmeldung von Person name=\""
                                            + _personVO
                                            + "\" wurde *nicht* akzeptiert, da die Bereichs-/Funktionstraeger-Kombination nicht mehr aktiv ist");
        }
    }

    public String toString()
    {
        return "SMS-Verarbeitung fuer Klinikum";
    }
}

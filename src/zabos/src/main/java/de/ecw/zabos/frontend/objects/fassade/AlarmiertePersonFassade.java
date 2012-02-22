package de.ecw.zabos.frontend.objects.fassade;

import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.types.id.RueckmeldungStatusId;

/**
 * Stellt eine Fassaden-Klasse für alarmierte Personen dar.
 * 
 * @author ckl
 */
public class AlarmiertePersonFassade
{
    private PersonVO voPerson = null;

    private PersonInAlarmVO voPersonInAlarm = null;

    /**
     * Liefert die alarmierte Person
     * 
     * @return PersonVO
     */
    public PersonVO getPerson()
    {
        return voPerson;
    }

    /**
     * Setzt die alarmierte Person
     * 
     * @param person
     *            Zu setzende Person
     */
    public void setPerson(PersonVO person)
    {
        this.voPerson = person;
    }

    /**
     * liefert die alarmierte Person mit den Rückmeldeinformationen
     * 
     * @return PersonInAlarmVO
     */
    public PersonInAlarmVO getPersonInAlarm()
    {
        return voPersonInAlarm;
    }

    /**
     * Setzt die Person mit den Rückmeldeinformationen
     * 
     * @param _personInAlarm
     *            Person
     */
    public void setPersonInAlarm(PersonInAlarmVO _personInAlarm)
    {
        this.voPersonInAlarm = _personInAlarm;
    }

    /**
     * Liefert zurück, ob der Status der Person gesetzt ist
     * 
     * @param _id
     * @return
     */
    private boolean isStatus(long _id)
    {
        if (voPersonInAlarm != null)
        {
            if (voPersonInAlarm.getRueckmeldungStatusId() != null)
            {
                if (voPersonInAlarm.getRueckmeldungStatusId().getLongValue() == _id)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Liefert true zurück, wenn die Person später kommt
     * 
     * @return true|false
     */
    public boolean isStatusJa()
    {
        return isStatus(RueckmeldungStatusId.STATUS_JA);
    }

    /**
     * Liefert true zurück, wenn die Person kommt
     * 
     * @return true|false
     */
    public boolean isStatusSpaeter()
    {
        return isStatus(RueckmeldungStatusId.STATUS_SPAETER);
    }

    /**
     * Liefert true zurück, wenn die Person nicht kommt
     * 
     * @return true|false
     */
    public boolean isStatusNein()
    {
        return isStatus(RueckmeldungStatusId.STATUS_NEIN);
    }
}

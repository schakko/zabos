package de.ecw.zabos.service.alarm.klinikum;

import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;

/**
 * Hilfsklasse zum Abbilden der Beziehung Person / PersonInAlarm
 * 
 * @author ckl
 * 
 */
public class PersonRueckmeldungCVO
{
    private PersonVO person;

    private PersonInAlarmVO personInAlarmVO;

    public PersonRueckmeldungCVO(PersonVO _person, PersonInAlarmVO _personInAlarm)
    {
        setPerson(_person);
        setPersonInAlarmVO(_personInAlarm);
    }

    /**
     * Setzt die Person, die zu einem Alarm gehört
     * 
     * @param personInAlarmVO
     */
    public void setPersonInAlarmVO(PersonInAlarmVO personInAlarmVO)
    {
        this.personInAlarmVO = personInAlarmVO;
    }

    /**
     * Liefert die Person, die zu dem Alarm gehört
     * 
     * @return
     */
    public PersonInAlarmVO getPersonInAlarmVO()
    {
        return personInAlarmVO;
    }

    /**
     * Setzt die Daten der Person
     * 
     * @param person
     */
    public void setPerson(PersonVO person)
    {
        this.person = person;
    }

    /**
     * Liefert die Daten der Person
     * 
     * @return
     */
    public PersonVO getPerson()
    {
        return person;
    }
}

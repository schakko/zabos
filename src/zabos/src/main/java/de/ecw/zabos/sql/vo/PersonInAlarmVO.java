package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;

/**
 * ValueObject fuer {@link Scheme#PERSON_IN_ALARM_TABLE}
 * 
 * @author bsp
 * 
 */
public class PersonInAlarmVO implements BaseIdVO
{
    PersonInAlarmVO()
    {
    }

    private BaseId id;

    private PersonId personId;

    private AlarmId alarmId;

    private RueckmeldungStatusId rueckmeldungStatusId;

    private String kommentar;

    private String kommentarLeitung;

    private boolean isEntwarnt;

    public BaseId getBaseId()
    {
        return id;
    }

    public void setBaseId(BaseId _id) throws StdException
    {
        if (_id == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }
        id = _id;
    }

    public PersonId getPersonId()
    {
        return personId;
    }

    public void setPersonId(PersonId _personId) throws StdException
    {
        if (_personId == null)
        {
            throw new StdException("person_id darf nicht null sein");
        }
        personId = _personId;
    }

    public AlarmId getAlarmId()
    {
        return alarmId;
    }

    public void setAlarmId(AlarmId _alarmId) throws StdException
    {
        if (_alarmId == null)
        {
            throw new StdException("alarm_id darf nicht null sein");
        }
        alarmId = _alarmId;
    }

    public RueckmeldungStatusId getRueckmeldungStatusId()
    {
        return rueckmeldungStatusId;
    }

    public void setRueckmeldungStatusId(RueckmeldungStatusId _id)
    {
        rueckmeldungStatusId = _id;
    }

    public void setKommentarLeitung(String kommentarLeitung)
    {
        this.kommentarLeitung = kommentarLeitung;
    }

    public String getKommentarLeitung()
    {
        return kommentarLeitung;
    }

    public void setKommentar(String kommentar)
    {
        this.kommentar = kommentar;
    }

    public String getKommentar()
    {
        return kommentar;
    }

    public void setEntwarnt(boolean isEntwarnt)
    {
        this.isEntwarnt = isEntwarnt;
    }

    public boolean isEntwarnt()
    {
        return isEntwarnt;
    }

}

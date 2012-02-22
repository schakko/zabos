package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.AlarmQuelleId;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.PersonId;

/**
 * ValueObject fuer {@link Scheme#ALARM_TABLE}
 * 
 * @author bsp
 * 
 */
public class AlarmVO extends DeletableBaseIdVO
{
    AlarmVO() {
        
    }
    
    private AlarmId id;

    private UnixTime alarmZeit;

    private UnixTime entwarnZeit;

    private PersonId alarmPersonId;

    private PersonId entwarnPersonId;

    private AlarmQuelleId alarmQuelleId;

    private String kommentar;

    private int reihenfolge;

    private boolean aktiv;
    
    private boolean nachalarmiert;

    private String gps_koordinate;

    public BaseId getBaseId()
    {
        return id;
    }

    public AlarmId getAlarmId()
    {
        return id;
    }

    public void setAlarmId(AlarmId _id) throws StdException
    {
        if (_id == null)
        {
            throw new StdException("alarm id darf nicht null sein");
        }
        id = _id;
    }

    public UnixTime getAlarmZeit()
    {
        return alarmZeit;
    }

    public void setAlarmZeit(UnixTime _alarmzeit)
    {
        alarmZeit = _alarmzeit;
    }

    public UnixTime getEntwarnZeit()
    {
        return entwarnZeit;
    }

    public void setEntwarnZeit(UnixTime _entwarnZeit)
    {
        entwarnZeit = _entwarnZeit;
    }

    public PersonId getAlarmPersonId()
    {
        return alarmPersonId;
    }

    public void setAlarmPersonId(PersonId _personId)
    {
        alarmPersonId = _personId;
    }

    public PersonId getEntwarnPersonId()
    {
        return entwarnPersonId;
    }

    public void setEntwarnPersonId(PersonId _entwarnPersonId)
    {
        entwarnPersonId = _entwarnPersonId;
    }

    public AlarmQuelleId getAlarmQuelleId()
    {
        return alarmQuelleId;
    }

    public void setAlarmQuelleId(AlarmQuelleId _alarmQuelleId) throws StdException
    {
        if (_alarmQuelleId == null)
        {
            throw new StdException("alarm quelle darf nicht null sein");
        }
        alarmQuelleId = _alarmQuelleId;
    }

    public String getKommentar()
    {
        return kommentar;
    }

    public void setKommentar(String _kommentar)
    {
        kommentar = _kommentar;
    }

    public boolean getAktiv()
    {
        return aktiv;
    }

    public void setAktiv(boolean _aktiv)
    {
        aktiv = _aktiv;
    }

    public boolean getNachalarmiert()
    {
        return nachalarmiert;
    }

    public void setNachalarmiert(boolean _nachalarmiert)
    {
        nachalarmiert = _nachalarmiert;
    }
    
    public int getReihenfolge()
    {
        return reihenfolge;
    }

    public void setReihenfolge(int _reihenfolge)
    {
        reihenfolge = _reihenfolge;
    }

    /**
     * GPS-Koordinate
     * 
     * @param _gps_koordinate
     * @author ckl
     * @since 2007-06-07
     */
    public void setGpsKoordinate(String _gps_koordinate)
    {
        gps_koordinate = _gps_koordinate;
    }

    /**
     * GPS-Koordinate
     * 
     * @return
     * @author ckl
     * @since 2007-07-06
     */
    public String getGpsKoordinate()
    {
        return gps_koordinate;
    }
}

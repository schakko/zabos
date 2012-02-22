package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.TelefonId;

/**
 * ValueObject fuer {@link Scheme#TELEFON_TABLE}
 * 
 * @author bsp
 * 
 */
public class TelefonVO extends DeletableBaseIdVO
{
    TelefonVO()
    {

    }

    private TelefonId id;

    private PersonId personId;

    private TelefonNummer nummer;

    private boolean aktiv;

    private UnixTime zeitfensterStart;

    private UnixTime zeitfensterEnde;

    private boolean flashSms;

    public BaseId getBaseId()
    {
        return id;
    }

    public TelefonId getTelefonId()
    {
        return id;
    }

    public void setTelefonId(TelefonId _telefonId) throws StdException
    {
        if (_telefonId == null)
        {
            throw new StdException("primary key id darf nicht null sein");
        }
        id = _telefonId;
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

    public TelefonNummer getNummer()
    {
        return nummer;
    }

    public void setNummer(TelefonNummer _nummer) throws StdException
    {
        if (_nummer == null)
        {
            throw new StdException("telefonnummer darf nicht null sein");
        }
        nummer = _nummer;
    }

    public boolean getAktiv()
    {
        return aktiv;
    }

    public void setAktiv(boolean _aktiv)
    {
        aktiv = _aktiv;
    }

    public UnixTime getZeitfensterStart()
    {
        return zeitfensterStart;
    }

    public void setZeitfensterStart(UnixTime _zeitfensterStart)
    {
        zeitfensterStart = _zeitfensterStart;
    }

    public UnixTime getZeitfensterEnde()
    {
        return zeitfensterEnde;
    }

    public void setZeitfensterEnde(UnixTime _zeitfensterEnde)
    {
        zeitfensterEnde = _zeitfensterEnde;
    }

    public boolean getFlashSms()
    {
        return flashSms;
    }

    public void setFlashSms(boolean _b)
    {
        flashSms = _b;
    }
}

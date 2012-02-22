package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.SmsInId;

/**
 * ValueObject für {@link Scheme#SMSIN_TABLE}
 * 
 * @author bsp
 * 
 */
public class SmsInVO implements BaseIdVO
{
    SmsInVO()
    {
    }

    private SmsInId id;

    private TelefonNummer rufnummer;

    private TelefonNummer modemRufnummer;

    private String nachricht;

    private UnixTime zeitpunkt;

    public BaseId getBaseId()
    {
        return id;
    }

    public SmsInId getSmsInId()
    {
        return id;
    }

    public void setSmsInId(SmsInId _smsInId) throws StdException
    {
        if (_smsInId == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }
        id = _smsInId;
    }

    public TelefonNummer getRufnummer()
    {
        return rufnummer;
    }

    public void setRufnummer(TelefonNummer _rufnummer) throws StdException
    {
        if (_rufnummer == null)
        {
            throw new StdException("rufnummer darf nicht null sein");
        }
        rufnummer = _rufnummer;
    }

    public void setModemRufnummer(TelefonNummer _modemRufnummer) throws StdException
    {
        if (_modemRufnummer == null)
        {
            throw new StdException("modem_rufnummer darf nicht null sein");
        }
        modemRufnummer = _modemRufnummer;
    }

    public TelefonNummer getModemRufnummer()
    {
        return modemRufnummer;
    }

    public String getNachricht()
    {
        return nachricht;
    }

    public void setNachricht(String _nachricht) throws StdException
    {
        if (_nachricht == null)
        {
            throw new StdException("nachricht darf nicht null sein");
        }
        nachricht = _nachricht;
    }

    public UnixTime getZeitpunkt()
    {
        return zeitpunkt;
    }

    public void setZeitpunkt(UnixTime _zeitpunkt) throws StdException
    {
        if (_zeitpunkt == null)
        {
            throw new StdException("zeitpunkt darf nicht null sein");
        }
        zeitpunkt = _zeitpunkt;
    }

}

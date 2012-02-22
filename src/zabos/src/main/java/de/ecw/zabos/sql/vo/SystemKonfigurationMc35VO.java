package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.util.StringUtils;

/**
 * ValueObject für {@link Scheme#SYSTEM_KONFIGURATION_MC35_TABLE}
 * 
 * @author bsp
 * 
 */
public class SystemKonfigurationMc35VO implements BaseIdVO
{
    SystemKonfigurationMc35VO()
    {
    }

    private BaseId id;

    private int comPort;

    private TelefonNummer rufnummer;

    private String pin1;

    private boolean alarmModem;

    private UnixTime zeitpunktLetzterSmsSelbsttest;

    public BaseId getBaseId()
    {
        return id;
    }

    public void setBaseId(BaseId _baseId) throws StdException
    {
        if (_baseId == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }
        id = _baseId;
    }

    public int getComPort()
    {
        return comPort;
    }

    public void setComPort(int _comPort)
    {
        comPort = _comPort;
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

    public String getPin1()
    {
        return pin1;
    }

    public void setPin1(String _pin1) throws StdException
    {
        if (StringUtils.isNullOrEmpty(_pin1))
        {
            throw new StdException("pin1 darf nicht null oder leer sein");
        }
        pin1 = _pin1;
    }

    public boolean getAlarmModem()
    {
        return alarmModem;
    }

    public void setAlarmModem(boolean _b)
    {
        alarmModem = _b;
    }

    public String toString()
    {
        return "" + rufnummer;
    }

    public void setZeitpunktLetzterSmsSelbsttest(UnixTime lastSelfSmsNotify)
    {
        this.zeitpunktLetzterSmsSelbsttest = lastSelfSmsNotify;
    }

    public UnixTime getZeitpunktLetzterSmsSelbsttest()
    {
        return zeitpunktLetzterSmsSelbsttest;
    }
}

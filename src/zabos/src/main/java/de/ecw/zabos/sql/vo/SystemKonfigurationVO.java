package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.types.id.BaseId;

/**
 * ValueObject für {@link Scheme#SYSTEM_KONFIGURATION_TABLE}
 * 
 * @author bsp
 * 
 */
public class SystemKonfigurationVO implements BaseIdVO
{
    SystemKonfigurationVO()
    {
    }

    private BaseId id; // Es gibt nur eine Zeile in der SysKonfig Tabelle

    /**
     * Timeout in "sec" (**nicht** ms!)
     */
    private long alarmTimeout;

    private Integer com5ton;

    private int reaktivierungTimeout;

    private int smsinTimeout;

    private int alarmhistorieLaenge;

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

    /**
     * Liefert den TimeOut für einen Alarm in Sekunden
     * 
     * @return
     */
    public long getAlarmTimeout()
    {
        return alarmTimeout;
    }

    /**
     * Setzt den Alarm-TimeOut in Sekunden
     * 
     * @param _alarmTimeOut
     */
    public void setAlarmTimeout(long _alarmTimeOut)
    {
        alarmTimeout = _alarmTimeOut;
    }

    public Integer getCom5Ton()
    {
        return com5ton;
    }

    public void setCom5Ton(Integer _portNumber) throws StdException
    {
        com5ton = _portNumber;
    }

    public int getReaktivierungTimeout()
    {
        return reaktivierungTimeout;
    }

    public void setReaktivierungTimeout(int _timeout)
    {
        reaktivierungTimeout = _timeout;
    }

    public int getSmsInTimeout()
    {
        return smsinTimeout;
    }

    public void setSmsInTimeout(int _timeout)
    {
        smsinTimeout = _timeout;
    }

    public int getAlarmHistorieLaenge()
    {
        return alarmhistorieLaenge;
    }

    public void setAlarmHistorieLaenge(int _n)
    {
        alarmhistorieLaenge = _n;
    }

}

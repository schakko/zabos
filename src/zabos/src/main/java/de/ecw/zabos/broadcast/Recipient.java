package de.ecw.zabos.broadcast;

import de.ecw.zabos.sql.vo.SmsOutVO;
import de.ecw.zabos.types.TelefonNummer;

/**
 * Interne Datenstruktur fuer SMS Broadcast
 * 
 * @author bsp
 * 
 */
public class Recipient
{

    private SmsOutVO smsOutVO;

    /**
     * Telefonnummer des Empfängers
     */
    private TelefonNummer handy_nr;

    /**
     * Status codes enspr. SQL zabos.smsout_status
     */
    private int lastStatusCode;

    /**
     * Absender dieser Nachricht (wird fürs "device-cycling" benötigt)
     */
    private TelefonNummer absenderRufnummer;

    public Recipient(SmsOutVO _smsOutVO, TelefonNummer _handynr,
                    TelefonNummer _absender)
    {
        smsOutVO = _smsOutVO;
        handy_nr = _handynr;
        absenderRufnummer = _absender;

        lastStatusCode = 0;
    }

    public SmsOutVO getSmsOutVO()
    {
        return smsOutVO;
    }

    public long getSmsOutId()
    {
        return smsOutVO.getSmsOutId().getLongValue();
    }

    public String getNachricht()
    {
        return smsOutVO.getNachricht();
    }

    public String getContext()
    {
        return smsOutVO.getContext();
    }

    public String getContextAlarm()
    {
        return smsOutVO.getContextAlarm();
    }

    public String getContextO()
    {
        return smsOutVO.getContextO();
    }

    public String getContextOE()
    {
        return smsOutVO.getContextOE();
    }

    public TelefonNummer getHandyNr()
    {
        return handy_nr;
    }

    public TelefonNummer getAbsenderRufnummer()
    {
        return absenderRufnummer;
    }

    public void setStatusCode(int _statusCode)
    {
        lastStatusCode = _statusCode;
    }

    public int getStatusCode()
    {
        return lastStatusCode;
    }

    public String toString()
    {
        if (getHandyNr() != null)
        {
            return getHandyNr().toString();
        }

        return super.toString();
    }
}

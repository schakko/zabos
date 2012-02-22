package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.SmsOutId;
import de.ecw.zabos.types.id.SmsOutStatusId;
import de.ecw.zabos.types.id.TelefonId;

/**
 * ValueObject für {@link Scheme#SMSOUT_TABLE}
 * 
 * @author bsp
 * 
 */
public class SmsOutVO implements BaseIdVO
{
    SmsOutVO()
    {
    }

    public static final SmsOutStatusId SMSOUT_STATUS_ID_UNSENT = new SmsOutStatusId(
                    0);

    private SmsOutId id;

    private TelefonId telefonId;

    private String nachricht;

    private UnixTime zeitpunkt;

    private SmsOutStatusId statusId = SmsOutStatusId.ID_UNSENT;

    private String context;

    private String contextAlarm;

    private String contextOrganisation;

    private String contextOrganisationseinheit;

    private boolean isFestnetzSms = false;

    public BaseId getBaseId()
    {
        return id;
    }

    public SmsOutId getSmsOutId()
    {
        return id;
    }

    public void setSmsOutId(SmsOutId _id) throws StdException
    {
        if (_id == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }
        id = _id;
    }

    public TelefonId getTelefonId()
    {
        return telefonId;
    }

    public void setTelefonId(TelefonId _telefonId) throws StdException
    {
        if (_telefonId == null)
        {
            throw new StdException("telefon_id darf nicht null sein");
        }
        telefonId = _telefonId;
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

    public SmsOutStatusId getStatusId()
    {
        return statusId;
    }

    public void setStatusId(SmsOutStatusId _statusId) throws StdException
    {
        if (_statusId == null)
        {
            throw new StdException("status_id darf nicht null sein");
        }
        statusId = _statusId;
    }

    public String getContext()
    {
        return context;
    }

    public void setContext(String _context)
    {
        context = _context;
    }

    public String getContextAlarm()
    {
        return contextAlarm;
    }

    public void setContextAlarm(String _context_alarm)
    {
        contextAlarm = _context_alarm;
    }

    public String getContextO()
    {
        return contextOrganisation;
    }

    public void setContextO(String _context_o)
    {
        contextOrganisation = _context_o;
    }

    public String getContextOE()
    {
        return contextOrganisationseinheit;
    }

    public void setContextOE(String _context_oe)
    {
        contextOrganisationseinheit = _context_oe;
    }

    public void setFestnetzSms(boolean isFestnetzSms)
    {
        this.isFestnetzSms = isFestnetzSms;
    }

    public boolean isFestnetzSms()
    {
        return isFestnetzSms;
    }

}

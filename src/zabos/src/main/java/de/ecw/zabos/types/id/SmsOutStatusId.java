package de.ecw.zabos.types.id;

/**
 * Primary Key der zabos.smsout_status Tabelle
 * 
 * @author bsp
 * 
 */
public class SmsOutStatusId extends BaseId
{

    public static final SmsOutStatusId ID_UNSENT = new SmsOutStatusId(0);

    public SmsOutStatusId(long _value)
    {
        super(_value);
    }

}

package de.ecw.zabos.types.id;

/**
 * Primary Key der zabos.alarm_quelle Tabelle
 * 
 * @author bsp
 * 
 */
public class AlarmQuelleId extends BaseId
{

    public static final AlarmQuelleId ID_SMS = new AlarmQuelleId(0);

    public static final AlarmQuelleId ID_5TON = new AlarmQuelleId(1);

    public static final AlarmQuelleId ID_WEB = new AlarmQuelleId(2);
    
    public static final AlarmQuelleId ID_POCSAG = new AlarmQuelleId(3);
    
    public static final AlarmQuelleId ID_FMS = new AlarmQuelleId(4);

    public AlarmQuelleId(long _value)
    {
        super(_value);
    }

}

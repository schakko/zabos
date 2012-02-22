package de.ecw.zabos.types.id;

/**
 * Primary Key der zabos.rueckmeldung_status Tabelle
 * 
 * @author bsp
 * 
 */
public class RueckmeldungStatusId extends BaseId
{

    /**
     * Stammdaten, siehe auch SQL zabos.rueckmeldung_status
     * 
     */

    public static final long STATUS_NEIN = 0;

    public static final long STATUS_JA = 1;

    public static final long STATUS_SPAETER = 2;

    public RueckmeldungStatusId(long _value)
    {
        super(_value);
    }

}

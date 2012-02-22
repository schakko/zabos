package de.ecw.zabos.frontend.types.id;

import java.util.HashMap;
import java.util.Map;

/**
 * Definiert, welche Schedule-Typen es gibt
 * 
 * @author ckl
 */
public class ScheduleId
{
    // Kein Typ
    public static final int NONE = 0x00;

    // Einmalig
    public static final int EINMALIG = 0x01;

    // Einmalig
    public static final int TAEGLICH = 0x02;

    // Wöchentlich
    public static final int WOECHENTLICH = 0x03;

    // Monatlich
    public static final int MONATLICH = 0x04;

    private static Map<String, String> mapIds = new HashMap<String, String>();

    static
    {
        mapIds.put(String.valueOf(ScheduleId.NONE), "Nicht definiert");
        mapIds.put(String.valueOf(ScheduleId.EINMALIG), "Einmalig");
        mapIds.put(String.valueOf(ScheduleId.TAEGLICH), "Täglich");
        mapIds.put(String.valueOf(ScheduleId.WOECHENTLICH), "Wöchentlich");
        mapIds.put(String.valueOf(ScheduleId.MONATLICH), "Monatlich");
    }

    /**
     * Liefert den Namen der Kontext-ID
     * 
     * @param _contextId
     * @return
     */
    public String getName(int _contextId)
    {
        if (mapIds.containsKey(String.valueOf(_contextId)))
        {
            return mapIds.get(String.valueOf(_contextId));
        }

        return "Unbekannt";
    }
}

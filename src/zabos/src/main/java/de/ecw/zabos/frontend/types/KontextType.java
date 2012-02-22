package de.ecw.zabos.frontend.types;

import java.util.HashMap;
import java.util.Map;

/**
 * Definiert, welche Kontext-Tyen es gibt
 * 
 * @author ckl
 */
public class KontextType
{
    // Id
    private int id = 0;

    // Kein Kontext
    public static final int NONE = 0x00;

    // Kontext System
    public static final int SYSTEM = 0x01;

    // Kontext Organisation
    public static final int ORGANISATION = 0x02;

    // Kontext Organisationseinheit
    public static final int ORGANISATIONSEINHEIT = 0x03;

    // Kontext Schleife
    public static final int SCHLEIFE = 0x04;

    // Kontext Person
    public static final int PERSON = 0x05;

    private static Map<String, String> mapIds = new HashMap<String, String>();

    static
    {
        mapIds.put(String.valueOf(KontextType.NONE), "Nicht definiert");
        mapIds.put(String.valueOf(KontextType.SYSTEM), "System");
        mapIds.put(String.valueOf(KontextType.ORGANISATION), "Organisation");
        mapIds.put(String.valueOf(KontextType.ORGANISATIONSEINHEIT),
                        "Organisationseinheit");
        mapIds.put(String.valueOf(KontextType.SCHLEIFE), "Schleife");
        mapIds.put(String.valueOf(KontextType.PERSON), "Person");
    }

    /**
     * Konstruktor
     * 
     * @param _kontextTypeId
     */
    public KontextType(int _kontextTypeId)
    {
        if (_kontextTypeId > 0x05)
        {
            _kontextTypeId = 0;
        }

        this.id = _kontextTypeId;
    }

    /**
     * Liefert die Id des Kontext zur�ck
     * 
     * @return
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Liefert den Namen der Kontext-ID
     * 
     * @param _contextId
     * @return
     */
    public String getName()
    {
        if (mapIds.containsKey(String.valueOf(id)))
        {
            return mapIds.get(String.valueOf(id));
        }

        return "Unbekannt";
    }

    /**
     * Liefert den Namen der Kontext-ID
     * 
     * @param _contextId
     * @return
     */
    public static String getName(int _id)
    {
        if (mapIds.containsKey(String.valueOf(_id)))
        {
            return mapIds.get(String.valueOf(_id));
        }

        return "Unbekannt";
    }
}

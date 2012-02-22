package de.ecw.zabos.frontend.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * Das DeltaBean setzt Werte und liefert diese zurück. Damit lassen sich einfach
 * Werte abfragen
 * 
 * @author ckl
 */
public class DeltaBean
{
    private Map<String, String> mapEntries = new HashMap<String, String>();

    private String testString = "";

    public DeltaBean()
    {
    }

    /**
     * Setzt einen Eintrag in der Hashmap
     * 
     * @param _entry
     */
    public void setEntry(String _entry)
    {
        if (null != _entry)
        {
            if (false == _entry.toString().equals(""))
            {
                this.mapEntries.put(_entry, _entry);
            }
        }
    }

    /**
     * Löscht einen Wert aus der Hashmap
     * 
     * @param _entry
     */
    public void setUnset(String _entry)
    {
        if (null != _entry)
        {
            if (!("".equals(_entry)))
            {
                this.mapEntries.remove(_entry);
            }
        }
    }

    /**
     * Setzt das Test-String
     * 
     * @param _data
     */
    public void setTest(String _data)
    {
        if (null != _data)
        {
            if (!("".equals(_data)))
            {
                this.testString = _data;
            }
        }
    }

    /**
     * Liefert den String zum Testen
     * 
     * @return String
     */
    public String getTest()
    {
        return this.testString;
    }

    /**
     * Liefert des Resultat des Tests
     * 
     * @return
     */
    public boolean getResult()
    {
        return this.mapEntries.containsKey(this.testString);
    }
}

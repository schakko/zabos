package de.ecw.zabos.types;

/**
 * Immutable TelefonNummer
 * 
 * @author bsp
 * 
 */
public class TelefonNummer
{

    private static final String DEFAULT_PREFIX = "0049";

    public static final TelefonNummer UNBEKANNT = new TelefonNummer("unbekannt");;

    private String nummer;

    public TelefonNummer(String _nummer)
    {
        _nummer = _nummer.replaceAll("[-]", "");
        _nummer = _nummer.replaceAll("[ ]", "");
        _nummer = _nummer.replaceAll("[\\/ ]", "");
        _nummer = _nummer.replaceAll("^\\+", "00");
        if (!_nummer.startsWith("00"))
        {
            if (_nummer.startsWith("0"))
            {
                nummer = DEFAULT_PREFIX + _nummer.substring(1);
            }
            else
            {
                // "unbekannt" bleibt "unbekannt"
                nummer = _nummer;
            }
        }
        else
        {
            // o2 korrigiert 00490171... automatisch nach 0049171...<br />
            // T-Mobile hingegen nicht!

            if (_nummer.startsWith(DEFAULT_PREFIX + "0"))
            {
                _nummer = DEFAULT_PREFIX
                                + _nummer
                                                .substring((DEFAULT_PREFIX
                                                                .length() + 1));
            }

            // Nummer faengt mit "00" an
            nummer = _nummer;
        }
    }

    /**
     * Liefert zurück, ob die übergebene Handynummer als Festnetznummer erkannt
     * wurde
     * 
     * @return
     */
    public boolean isFestnetzNummer()
    {
        // Handynummern in Deutschland
        // http://de.wikipedia.org/wiki/Vorwahl_01_%28Deutschland%29
        return (!(nummer.startsWith(DEFAULT_PREFIX + "15")
                        || nummer.startsWith(DEFAULT_PREFIX + "16") || nummer
                        .startsWith(DEFAULT_PREFIX + "17")));
    }

    /**
     * Liefert die Rufnummer als String
     * 
     * @return
     */
    public String getNummer()
    {
        return nummer;
    }

    /**
     * Liefert zurück, ob die beiden Telefonnummern identisch sind
     */
    public boolean equals(Object _o)
    {
        if (!(_o instanceof TelefonNummer))
            return false;

        return nummer.equals(((TelefonNummer) _o).getNummer());
    }

    /**
     * Überschriebene Methode aus java.Object um ein TelefonNummer Objekt z.B.
     * in print statements benutzen zu können
     * 
     */
    public String toString()
    {
        return nummer;
    }

}

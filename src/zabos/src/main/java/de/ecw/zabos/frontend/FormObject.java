package de.ecw.zabos.frontend;

/**
 * Stellt HTML-Input-Felder als Java-Objekte dar
 * 
 * @author ckl
 */
public class FormObject
{
    // Nur numerische Werte
    final public static int NUMERIC = 1;

    // Alpha-Numerische Werte erlaubt
    final public static int ALPHANUMERIC = 2;

    // Werte-Bereich, wenn IS_NUMERIC gesetzt ist
    final public static int IN_RANGE = 4;

    // Max. L�nge des Feldes
    final public static int HAS_MAXLENGTH = 8;

    // Min. L�nge des Feldes
    final public static int HAS_MINLENGTH = 16;

    // Identisch mit einem anderen Feld
    final public static int IDENTICAL = 32;

    // Darf nicht leer sein
    final public static int NOT_EMPTY = 64;

    // Wenn Validierung fehlerhaft => Weitere Validierung des Formulars beenden
    final public static int ON_ERROR_STOP_FORM_VALIDATION = 128;

    // Wenn Validierung fehlerhaft => Weitere Validierung des Objekts beenden;
    final public static int ON_ERROR_STOP_OBJECT_VALIDATION = 256;

    // Datum soll richtiges Format besitzen (dd.mm.yyyy)
    final public static int VALID_DATE_FORMAT = 512;

    // Datumsangabe muss stimmen
    final public static int VALID_DATE = 1024;

    // Zeit soll richtiges Format besitzen (HH:ii)
    final public static int VALID_TIME_FORMAT = 2048;

    // Zeitangabe muss stimmen
    final public static int VALID_TIME = 4096;

    // Reload-Sperre
    final public static int DISALLOW_RELOAD = 8192;

    final public static String ERROR_IS_EMPTY = "Das Feld ${fieldName} darf nicht leer sein.";

    final public static String ERROR_EXCEEDS_MAX_LENGTH = "Das Feld ${fieldName} darf nicht länger als ${maxLength} Zeichen sein.";

    final public static String ERROR_NOT_EXCEEDS_MIN_LENGTH = "Das Feld ${fieldName} muss mindestens ${minLength} Zeichen lang sein.";

    final public static String ERROR_NOT_A_NUMBER = "Das Feld ${fieldName} muss eine gültige Zahl sein.";

    final public static String ERROR_NOT_IDENTICAL = "Das Feld ${fieldName} muss mit dem Feld ${fieldName2} identisch sein.";

    final public static String ERROR_NOT_VALID_DATE_FORMAT = "Das Feld ${fieldName} muss ein Datumsformat der Form dd.mm.yyyy besitzen.";

    final public static String ERROR_NOT_VALID_DATE = "Das Feld ${fieldName} ist kein gültiges Datum.";

    final public static String ERROR_NOT_VALID_TIME_FORMAT = "Das Feld ${fieldName} muss ein Zeitformat der Form HH:ii besitzen.";

    final public static String ERROR_NOT_VALID_TIME = "Das Feld ${fieldName} ist keine gültige Zeitangabe.";

    final public static String ERROR_DISALLOW_RELOAD = "Das Formular ${fieldName} darf nicht neu geladen werden.";

    protected String inputFieldId = "";

    protected String inputFieldName = "";

    protected FormObject fCheckAgainst = null;

    protected long minRange = 0;

    protected long maxRange = 1000000000;

    protected int maxLength = 255;

    protected int minLength = 0;

    protected int flagsValidation = (NOT_EMPTY + ON_ERROR_STOP_OBJECT_VALIDATION);

    /**
     * Konstruktor
     * 
     * @param _inputFieldId
     *            Das HTML-"name"-Tag
     * @param _inputFieldName
     *            Beschreibung des Feldes
     */
    public FormObject(String _inputFieldId, String _inputFieldName)
    {
        setInputFieldId(_inputFieldId);
        setInputFieldName(_inputFieldName);
    }

    /**
     * Liefert die ID des Feldes, die ID ist gleichbedeutend mit dem
     * "name"-HTML-Tag <input name="test"... hätte die Id "test"
     * 
     * @return inputFieldId
     */
    public String getInputFieldId()
    {
        return inputFieldId;
    }

    /**
     * Setzt die ID des Feldes, die ID ist gleichbedeutend mit dem
     * "name"-HTML-Tag <input name="test"... hätte die Id "test"
     * 
     * @param _inputFieldId
     */
    public void setInputFieldId(String _inputFieldId)
    {
        this.inputFieldId = _inputFieldId;
    }

    /**
     * Liefert den Namen des Input-Feldes zurück, z.B. "Benutzername" o.ä.
     * 
     * @return inputFieldName
     */
    public String getInputFieldName()
    {
        return inputFieldName;
    }

    /**
     * Setzt den Namen des Feldes, z.B. "Benutzername" o.ä.
     * 
     * @param _inputFieldName
     */
    public void setInputFieldName(String _inputFieldName)
    {
        this.inputFieldName = _inputFieldName;
    }

    /**
     * Setzt das übergebene Flag
     * 
     * @param _flag
     */
    public void setFlag(int _flag)
    {
        flagsValidation += _flag;
    }

    /**
     * Liefert die Flags
     * 
     * @return
     */
    public int getFlag()
    {
        return flagsValidation;
    }

    /**
     * Liefert das Objekt zurück, welches gegengeprüft werden soll
     * 
     * @return fCheckAgainst Das Objekt, das überprüft werden soll
     */
    public FormObject getCheckAgainstFormObject()
    {
        return fCheckAgainst;
    }

    /**
     * Setzt das Objekt, was noch überprüft werden soll
     * 
     * @param _f
     */
    public void setCheckAgainstFormObject(FormObject _f)
    {
        this.fCheckAgainst = _f;
        setFlag(IDENTICAL);
    }

    /**
     * Testet, ob das Flag gesetzt ist
     * 
     * @param _flag
     * @return true|false
     */
    public boolean testFlag(int _flag)
    {
        int flagTest = (this.getFlag() & _flag);

        if (flagTest == _flag)
        {
            return true;
        }

        return false;
    }

    /**
     * Liefert die maximale Zeichenlänge des Feldes
     * 
     * @return maxLength
     */
    public int getMaxLength()
    {
        return maxLength;
    }

    /**
     * Liefert die minimale Länge des Feldes
     * 
     * @return minLength
     */
    public int getMinLength()
    {
        return minLength;
    }

    /**
     * Setzt die maximale Länge des Feldes
     * 
     * @param _maxLength
     */
    public void setMaxLength(int _maxLength)
    {
        this.maxLength = _maxLength;
        setFlag(HAS_MAXLENGTH);
    }

    /**
     * Setzt die minmale Länge der Zeichenfolge des Feldes
     * 
     * @param _minLength
     */
    public void setMinLength(int _minLength)
    {
        this.minLength = _minLength;
        setFlag(HAS_MINLENGTH);
    }
}

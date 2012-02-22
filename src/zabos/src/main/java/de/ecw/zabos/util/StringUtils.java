package de.ecw.zabos.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.ecw.zabos.types.Pin;

/**
 * Helper Methoden für Stringbearbeitung
 * 
 * 
 * @author bsp
 * 
 */
public class StringUtils
{

    private static final char[] hexChars =
    { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static ArrayList<String> alReplacements = new ArrayList<String>();

    static
    {
        alReplacements.add("?");
        alReplacements.add("+");
        alReplacements.add("[");
        alReplacements.add("]");
        alReplacements.add("(");
        alReplacements.add(")");
        alReplacements.add("{");
        alReplacements.add("}");
        alReplacements.add("\\");
        alReplacements.add("^");
        alReplacements.add("$");
    }

    /**
     * Wandelt das ggb. byte in einen String (mit führender '0') um.
     * 
     * @param _b
     * @return Hexadezimale Stringdarstellung von _b
     */
    public static String byteToHexString(byte _b)
    {
        if ((_b < 0) || (_b > 0xf))
        {
            return Integer.toHexString(_b & 0xFF);
        }
        else
        {
            StringBuffer sb = new StringBuffer();
            sb.append('0');
            sb.append(hexChars[_b]);
            return sb.toString();
        }
    }

    /**
     * Wandelt das ggb. ByteArray in einen HexString um
     * 
     * @param _bytes
     * @return
     */
    public static String bytesToHexString(byte[] _bytes)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < _bytes.length; i++)
        {
            byte b = _bytes[i];
            if ((b < 0) || (b > 0xf))
            {
                sb.append(Integer.toHexString(b & 0xFF));
            }
            else
            {
                sb.append('0');
                sb.append(hexChars[b]);
            }
        }
        return sb.toString();
    }

    /**
     * Vergleicht 2 Strings.
     * 
     * Die Strings werden als "gleich" betrachtet wenn entweder beide null sind
     * oder denselben Inhalt haben.
     * 
     * @param _a
     * @param _b
     * @return
     */
    public static boolean compare(String _a, String _b)
    {
        if (_a != null)
        {
            if (_b != null)
            {
                return (_a.compareTo(_b) == 0);
            }
            else
            {
                return false;
            }
        }
        else
        {
            return (_b == null);
        }
    }

    /**
     * Vergleicht 2 Pins.
     * 
     * Die Pins werden als "gleich" betrachtet wenn entweder beide null sind
     * oder denselben Inhalt haben.
     * 
     * @param _a
     * @param _b
     * @return
     */
    public static boolean compare(Pin _a, Pin _b)
    {
        if (_a != null)
        {
            if (_b != null)
            {
                boolean bIsOk = false;

                // 2007-12-28 CKL: Beide Pins ans sich duerfen nicht null sein
                // Fuehrte zum Fehler beim Verarbeiten einer Eingangs-SMS, die
                // keinen vernuenftigen Alias-Text enthielt
                if (_a.getPin() != null && _b.getPin() != null)
                {
                    // 2006-11-06 CKL: Vergleich der laenge geaendert.
                    if (_a.getPin().length() == _b.getPin().length())
                    {
                        try
                        {
                            bIsOk = (_a.getPin().compareTo(_b.getPin()) == 0);
                        }
                        catch (StringIndexOutOfBoundsException e)
                        {
                            bIsOk = false;
                        }
                    }
                }

                return bIsOk;

            }
            else
            {
                return false;
            }
        }
        else
        {
            return (_b == null);
        }
    }

    /**
     * Testet ob der ggb. String leer oder null ist.
     * 
     * @param _s
     * @return
     */
    public static boolean isNullOrEmpty(String _s)
    {
        if (_s == null)
        {
            return true;
        }
        if (_s.trim().compareTo("") == 0)
        {
            return true;
        }
        return false;
    }

    /**
     * Liefert ein Array mit Worten des String zurück
     * 
     * @param _wort
     * @return
     * @author ckl
     * @since 200623.06.2006_12:57:55
     */
    public static String[] findWords(String _wort)
    {
        String words[] = null;
        String ret[] = null;

        if (_wort != null)
        {
            words = _wort.trim().split("[\\n|\\s]");

            if (words != null)
            {
                List<String> listStrings = new ArrayList<String>();

                for (int i = 0, m = words.length; i < m; i++)
                {
                    words[i].trim();

                    if (words[i].length() > 0)
                    {
                        listStrings.add(words[i]);
                    }
                }

                ret = new String[listStrings.size()];
                listStrings.toArray(ret);
            }
        }

        return ret;
    }

    /**
     * Extrahiert das erste Wort aus dem ggb. String. Trennzeichen für Wörter
     * sind SPC und LF.
     * 
     * Das Index auf das erste Zeichen nach dem Wort wird in _idx_ret[0]
     * zurückgeliefert.
     * 
     * @param _text
     * @param _idx_ret
     * @return
     */
    public static String firstWord(String _text, int[] _idx_ret)
    {
        // Erstes Wort extrahieren
        _text = _text.trim();
        int idxSpc = _text.indexOf(' ');
        int idxLf = _text.indexOf('\n');
        int idx;

        if (idxSpc == -1)
            idxSpc = 9999999;

        if (idxLf == -1)
            idxLf = 9999999;

        if (idxSpc < idxLf)
            idx = idxSpc;
        else
            idx = idxLf;

        if (idx == 9999999)
            idx = _text.length();

        // 2006-06-23 CKL: Fix fuer leere Strings
        if (_text.length() > 0)
            _idx_ret[0] = idx + 1;
        else
            _idx_ret[0] = 0;

        // Erstes Wort extrahieren
        return _text.substring(0, idx).trim();
    }

    /**
     * Extrahiert die erste Zeile aus dem ggb. String. Trennzeichen für Zeilen
     * sind LF.
     * 
     * Das Index auf das erste Zeichen nach dem Wort wird in _idx_ret[0]
     * zurückgeliefert.
     * 
     * @param _text
     * @param _idx_ret
     * @return
     */
    public static String firstLine(String _text, int[] _idx_ret)
    {
        // Erste Zeile extrahieren
        _text = _text.trim();
        int idxLf = _text.indexOf('\n');
        int idx;
        if (idxLf == -1)
            idxLf = 9999999;
        idx = idxLf;
        if (idx == 9999999)
            idx = _text.length();

        _idx_ret[0] = idx + 1;

        // Erste Zeile extrahieren
        return _text.substring(0, idx).trim();
    }

    /**
     * Kommaseparierte Werteliste in String[] umwandeln.
     * 
     * Trennzeichen ist ','
     * 
     * @param _text
     * @param _idx_ret
     * @return
     */
    public static String[] csvList(String _text, Integer[] _idx_ret)
    {
        List<String> list = new ArrayList<String>();
        int idx = 0;
        boolean bCont = true;
        int idxr;
        do
        {
            idxr = _text.indexOf(',', idx);
            if (idxr == -1)
            {
                idxr = _text.length();
                bCont = false;
            }
            list.add(_text.substring(idx, idxr).trim());
            idx = idxr + 1;
        }
        while (bCont);

        _idx_ret[0] = (idxr + 1);

        String[] ret = new String[list.size()];
        list.toArray(ret);
        return ret;
    }

    /**
     * Space-separierte Werteliste in String[] umwandeln.
     * 
     * Trennzeichen sind ' ' und '\n'
     * 
     * @param _text
     * @param _idx_ret
     * @return
     */
    public static String[] ssvList(String _text)
    {
        List<String> list = new ArrayList<String>();
        int idx = 0;
        boolean bCont = true;
        int idxr;
        do
        {
            idxr = _text.indexOf(' ', idx);
            if (idxr == -1)
            {
                idxr = _text.indexOf('\n', idx);
                if (idxr == -1)
                {
                    idxr = _text.length();
                    bCont = false;
                }
            }
            list.add(_text.substring(idx, idxr).trim());
            idx = idxr + 1;
        }
        while (bCont);

        String[] ret = new String[list.size()];
        list.toArray(ret);
        return ret;
    }

    /**
     * Liefert einen substring der ggb. Zeichenkette.
     * 
     * Wenn idx ausserhalb des gültigen Bereichs liegt wird einfach ein leerer
     * String zurückgeliefert.
     * 
     * @param _text
     * @param _idx
     * @return
     */
    public static String substring(String _text, int _idx)
    {
        if (_idx >= 0)
        {
            if (_idx < _text.length())
            {
                return _text.substring(_idx);
            }
        }
        return "";
    }

    /**
     * Hängt ein Wort an eine ',' separierte Stringliste an
     * 
     * @param _csv
     *            (oder null)
     * @param _word
     * @return Neue CSV Stringliste
     */
    public static String addToCSV(String _csv, String _word)
    {
        if (_csv != null)
        {
            return _csv + "," + _word;
        }
        else
        {
            return _word;
        }
    }

    /**
     * Wandelt einen Integer in einen Hex-String um
     * 
     * @param _i
     * @return
     */
    public static String intToHexString(int _i)
    {
        return Integer.toHexString(_i).toUpperCase();
    }

    /**
     * Erstellt für den ggb. String einen MD5-Hash und liefert diesen als
     * Zeichenkette zurück.
     * 
     * @param _s
     * @return MD5-Hash-String
     */
    public static String md5(String _s)
    {
        byte[] dig;

        try
        {
            // MD5-Hash für String bilden
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(_s.getBytes());
            dig = md.digest();
            // Raw-Hash in String umwandeln
            return bytesToHexString(dig);
        }
        catch (NoSuchAlgorithmException e)
        {
            // An diser Stelle steht leider kein Log zur Verf�gung
            return null;
        }
    }

    /**
     * Entfernt aus dem ggb. String alle ?[](){}^$\ so dass es keine Fehler in
     * der SQL-Abfrage gibt bzw. SQL-Injection nicht möglich ist
     * 
     * @param _s
     * @return Bereinigter String
     */
    public static String removeSpecialCharsForSQLRegExp(String _s)
    {
        String _r = "";

        for (int i = 0, m = _s.length(); i < m; i++)
        {
            char c = _s.charAt(i);

            if (false == alReplacements.contains("" + c))
            {
                _r = _r.concat("" + c);
            }
        }

        return _r;
    }

    public final static String numberCharacters = "0123456789";

    public final static String lowerCharacters = "abcdefghijklmnopqrstuvwxyz";

    public final static String upperCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public final static String otherCharacters = "%_!?";

    /**
     * Erzeugt eine Zufallszahl
     * 
     * @param aStart
     * @param aEnd
     * @param aRandom
     * @return
     */
    public static int generateRandomInteger(int aStart, int aEnd, Random aRandom)
    {
        if (aStart > aEnd)
        {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        // get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        int randomNumber = (int) (fraction + aStart);

        return randomNumber;
    }

    /**
     * Von http://www.jswelt.de/showsource.php?id=1123706440
     * 
     * @param number
     * @param lower
     * @param upper
     * @param other
     * @param extra
     * @param _random
     * @return
     */
    public static char getRandomChar(boolean number, boolean lower,
                    boolean upper, boolean other, String extra, Random _random)
    {
        String charSet = extra;
        if (number == true)
            charSet += numberCharacters;
        if (lower == true)
            charSet += lowerCharacters;
        if (upper == true)
            charSet += upperCharacters;
        if (other == true)
            charSet += otherCharacters;
        return charSet.charAt(generateRandomInteger(0, (charSet.length() - 1),
                        _random));
    }

    /**
     * Von http://www.jswelt.de/showsource.php?id=1123706440
     * 
     * @param length
     * @param extraChars
     * @param firstNumber
     * @param firstLower
     * @param firstUpper
     * @param firstOther
     * @param latterNumber
     * @param latterLower
     * @param latterUpper
     * @param latterOther
     * @return
     */
    public static String randomString(int length, String extraChars,
                    boolean firstNumber, boolean firstLower,
                    boolean firstUpper, boolean firstOther,
                    boolean latterNumber, boolean latterLower,
                    boolean latterUpper, boolean latterOther)
    {
        Random r = new Random();

        String rc = "";
        if (length > 0)
            rc = rc
                            + getRandomChar(firstNumber, firstLower,
                                            firstUpper, firstOther, extraChars,
                                            r);
        for (int idx = 1; idx < length; ++idx)
        {
            rc = rc
                            + getRandomChar(latterNumber, latterLower,
                                            latterUpper, latterOther,
                                            extraChars, r);
        }
        return rc;
    }
}

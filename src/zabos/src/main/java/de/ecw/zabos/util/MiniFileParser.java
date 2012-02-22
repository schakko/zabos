package de.ecw.zabos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;

import de.ecw.zabos.exceptions.StdException;

/**
 * Parst eine Konfigurationsdatei
 * 
 * @author ckl
 * @deprecated
 */
public class MiniFileParser
{

    public static Hashtable<String, String> parseFile(String _filename) throws StdException
    {
        File f = new File(_filename);
        if (f.canRead())
        {
            StringBuffer sb = new StringBuffer();
            try
            {
                FileInputStream is = new FileInputStream(f);
                int avail;
                do
                {
                    avail = is.available();
                    if (avail > 0)
                    {
                        byte[] bytes = new byte[avail];
                        is.read(bytes);
                        for (int i = 0; i < bytes.length; i++)
                            sb.append((char) bytes[i]);
                    }
                }
                while (avail > 0);
                return parseString(sb.toString());
            }
            catch (IOException e)
            {
                throw new StdException(e);
            }
        }
        else
        {
            throw new StdException("Kann ini Datei \"" + _filename
                            + "\" nicht lesen!");
        }
    }

    public static Hashtable<String, String> parseString(String _buf) throws StdException
    {
        Hashtable<String, String> r = new Hashtable<String, String>();
        _buf = _buf.replace('\r', ' ');
        int bufLen = _buf.length();
        int idxStart = 0;
        int idxLf;
        do
        {
            // Zeilen iterieren
            idxLf = _buf.indexOf('\n', idxStart);
            String line;
            if (idxLf != -1)
                line = _buf.substring(idxStart, idxLf);
            else
                line = _buf.substring(idxStart);
            // Ist die Zeile ein Kommentar?
            if (!line.startsWith("#"))
            {
                // Zeile in key/value zerlegen
                int idxAsn = line.indexOf('=');
                if (idxAsn == -1)
                {
                    throw new StdException(
                                    "Fehler beim Parsen der ini-Zeile \""
                                                    + line + "\"");
                }
                else
                {
                    String key = line.substring(0, idxAsn).trim();
                    String value = line.substring(idxAsn + 1).trim();
                    if (r.containsKey(key))
                    {
                        throw new StdException("Ini-file Key \"" + key
                                        + "\" ist bereits definiert");
                    }
                    else
                    {
                        r.put(key, value);
                    }
                }
            }
            idxStart = idxLf + 1;
        }
        while ((idxLf != -1) && (idxStart < bufLen));
        return r;
    }
}

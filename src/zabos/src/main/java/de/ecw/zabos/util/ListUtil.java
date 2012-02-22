package de.ecw.zabos.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Hilfsklasse für Arrays
 * 
 * @author ckl
 * 
 */
public class ListUtil
{
    /**
     * Generische Methode zum Umwandeln von Array in Listen
     * 
     * @param <T>
     * @param _array
     * @return
     */
    public static <T extends List<T>> List<T> toList(T[] _array)
    {
        List<T> r = new ArrayList<T>();

        for (int i = 0, m = _array.length; i < m; i++)
        {
            r.add(_array[i]);
        }

        return r;
    }

    /**
     * Wandelt eine Liste in Liste von Strings um
     * 
     * @param <T>
     * @param _toString
     * @param _concator
     * @return
     */
    public static <T> String toString(List<T> _toString, String _concator)
    {
        StringBuffer sb = new StringBuffer();

        for (int i = 0, m = _toString.size(); i < m; i++)
        {
            sb.append(_toString.get(i));

            if ((i + 1) != m)
            {
                sb.append(_concator);
            }
        }

        return sb.toString();
    }
}

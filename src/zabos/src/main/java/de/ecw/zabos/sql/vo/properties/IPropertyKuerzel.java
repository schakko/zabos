package de.ecw.zabos.sql.vo.properties;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;

public interface IPropertyKuerzel
{
    /**
     * Unique, {@link Scheme#COLUMN_KUERZEL}
     * 
     * @param _kuerzel
     */
    public void setKuerzel(String _kuerzel) throws StdException;

    /**
     * Unique, {@link Scheme#COLUMN_KUERZEL}
     * 
     * @return
     */
    public String getKuerzel();
}

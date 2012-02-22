package de.ecw.zabos.sql.vo.properties;

import de.ecw.zabos.sql.Scheme;

public interface IPropertyGeloescht
{

    /**
     * {@link Scheme#COLUMN_GELOESCHT}
     * @return
     */
    public boolean getGeloescht();

    /**
     * {@link Scheme#COLUMN_GELOESCHT}
     * @param _geloescht
     */
    public void setGeloescht(boolean _geloescht);
}

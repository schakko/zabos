package de.ecw.zabos.sql.vo.properties;

import de.ecw.zabos.sql.Scheme;

public interface IPropertyBeschreibung
{
    /**
     * {@link Scheme#COLUMN_BESCHREIBUNG}
     * 
     * @param _beschreibung
     */
    public void setBeschreibung(String _beschreibung);

    /**
     * {@link Scheme#COLUMN_BESCHREIBUNG}
     * 
     * @return
     */
    public String getBeschreibung();
}

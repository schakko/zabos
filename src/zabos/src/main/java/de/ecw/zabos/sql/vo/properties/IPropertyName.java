package de.ecw.zabos.sql.vo.properties;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;

public interface IPropertyName
{
    /**
     * Name, ist immer unique; {@link Scheme#COLUMN_NAME}
     * 
     * @param _name
     */
    public void setName(String _name) throws StdException;

    /**
     * Name, ist immer unique; {@link Scheme#COLUMN_NAME};
     * 
     * @return
     */
    public String getName();
}

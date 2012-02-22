package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.vo.properties.IPropertyName;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.util.StringUtils;

/**
 * ValueObject fuer {@link Scheme#FUNKTIONSTRAEGER_TABLE}
 * 
 * @author ckl
 */
public class BereichVO extends DeletableBaseIdDescVO implements IPropertyName
{
    BereichVO()
    {

    }

    private BereichId id;

    private String name;

    public BaseId getBaseId()
    {
        return id;
    }

    public BereichId getBereichId()
    {
        return id;
    }

    /**
     * @param _bereichId
     * @throws StdException
     *             Wenn _bereichId null ist
     */
    public void setBereichId(BereichId _bereichId) throws StdException
    {
        if (_bereichId == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }
        id = _bereichId;
    }

    /**
     * 
     * @param _name
     * @throws StdException
     *             Wenn _name null oder leer ist
     */
    public void setName(String _name) throws StdException
    {
        if (StringUtils.isNullOrEmpty(_name))
        {
            throw new StdException("name darf nicht null oder leer sein");
        }
        name = _name;
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return getName() + " (" + getBeschreibung() + ")";
    }
}

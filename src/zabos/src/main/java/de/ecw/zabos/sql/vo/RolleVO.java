package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.vo.properties.IPropertyName;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.RolleId;
import de.ecw.zabos.util.StringUtils;

/**
 * ValueObject fuer {@link Scheme#ROLLE_TABLE}
 * 
 * @author bsp
 * 
 */
public class RolleVO extends DeletableBaseIdDescVO implements IPropertyName
{
    RolleVO()
    {
    }

    private RolleId id;

    private String name;

    public BaseId getBaseId()
    {
        return id;
    }

    public RolleId getRolleId()
    {
        return id;
    }

    public void setRolleId(RolleId _rolleId) throws StdException
    {
        if (_rolleId == null)
        {
            throw new StdException("primary key id darf nicht null sein");
        }
        id = _rolleId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String _name) throws StdException
    {
        if (StringUtils.isNullOrEmpty(_name))
        {
            throw new StdException("rollen name darf nicht null sein");
        }
        name = _name;
    }

    public String toString()
    {
        return name;
    }
}

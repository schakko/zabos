package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.vo.properties.IPropertyName;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.util.StringUtils;

/**
 * ValueObject fuer {@link Scheme#RECHT_TABLE}
 * 
 * @author bsp
 * 
 */
public class RechtVO extends BaseIdDescVO implements IPropertyName
{
    RechtVO()
    {

    }

    private RechtId id;

    private String name;

    public BaseId getBaseId()
    {
        return id;
    }

    public RechtId getRechtId()
    {
        return id;
    }

    public void setRechtId(RechtId _rechtId) throws StdException
    {
        if (_rechtId == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }
        id = _rechtId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String _name) throws StdException
    {
        if (StringUtils.isNullOrEmpty(_name))
        {
            throw new StdException("name darf nicht null oder leer sein");
        }
        name = _name;
    }

}

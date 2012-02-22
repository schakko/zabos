package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.vo.properties.IPropertyName;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;
import de.ecw.zabos.util.StringUtils;

/**
 * ValueObject für {@link Scheme#RUECKMELDUNG_STATUS_TABLE}
 * 
 * @author bsp
 * 
 */
public class RueckmeldungStatusVO implements BaseIdVO, IPropertyName
{
    RueckmeldungStatusVO()
    {
    }

    private RueckmeldungStatusId id;

    private String name;

    public BaseId getBaseId()
    {
        return id;
    }

    public RueckmeldungStatusId getRueckmeldungStatusId()
    {
        return id;
    }

    public void setRueckmeldungStatusId(RueckmeldungStatusId _id) throws StdException
    {
        if (_id == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }
        id = _id;
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

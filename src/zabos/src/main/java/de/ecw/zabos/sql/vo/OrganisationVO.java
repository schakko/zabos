package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.vo.properties.IPropertyName;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.util.StringUtils;

/**
 * ValueObject fuer {@link Scheme#ORGANISATION_TABLE}
 * 
 * @author bsp
 * 
 */
public class OrganisationVO extends DeletableBaseIdDescVO implements
                IPropertyName
{
    OrganisationVO()
    {
    }

    private OrganisationId id;

    private String name;

    public BaseId getBaseId()
    {
        return id;
    }

    public OrganisationId getOrganisationId()
    {
        return id;
    }

    public void setOrganisationId(OrganisationId _organisationId) throws StdException
    {
        if (_organisationId == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }
        id = _organisationId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String _name) throws StdException
    {
        if (StringUtils.isNullOrEmpty(_name))
        {
            throw new StdException(
                            "organisations name darf nicht null der leer sein");
        }
        name = _name;
    }

    public String toString()
    {
        return name;
    }
}

package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.vo.properties.IPropertyName;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.util.StringUtils;

/**
 * ValueObject fuer {@link Scheme#ORGANISATIONSEINHEIT_TABLE}
 * 
 * @author bsp
 * 
 */
public class OrganisationsEinheitVO extends DeletableBaseIdDescVO implements
                IPropertyName
{
    OrganisationsEinheitVO()
    {
    }

    private OrganisationsEinheitId id;

    private OrganisationId organisationId;

    private String name;

    public BaseId getBaseId()
    {
        return id;
    }

    public OrganisationsEinheitId getOrganisationsEinheitId()
    {
        return id;
    }

    public void setOrganisationsEinheitId(
                    OrganisationsEinheitId _organisationsEinheitId) throws StdException
    {
        if (_organisationsEinheitId == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }
        id = _organisationsEinheitId;
    }

    public OrganisationId getOrganisationId()
    {
        return organisationId;
    }

    public void setOrganisationId(OrganisationId _organisationId) throws StdException
    {
        if (_organisationId == null)
        {
            throw new StdException("organisation darf nicht null sein");
        }
        organisationId = _organisationId;
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
                            "organisations name darf nicht null oder leer sein");
        }
        name = _name;
    }

    public String toString()
    {
        return name;
    }
}

package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.RueckmeldungStatusAliasId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;
import de.ecw.zabos.util.StringUtils;

/**
 * VO für {@link Scheme#RUECKMELDUNG_STATUS_ALIAS_TABLE}
 * 
 * @author ckl
 * 
 */
public class RueckmeldungStatusAliasVO implements BaseIdVO
{
    RueckmeldungStatusAliasVO()
    {
    }

    private RueckmeldungStatusAliasId id;

    private RueckmeldungStatusId rueckmeldungStatusId;

    private String alias;

    public BaseId getBaseId()
    {
        return id;
    }

    public RueckmeldungStatusAliasId getRueckmeldungStatusAliasId()
    {
        return id;
    }

    public void setRueckmeldungStatusAliasId(RueckmeldungStatusAliasId _id) throws StdException
    {
        if (_id == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }
        id = _id;
    }

    public RueckmeldungStatusId getRueckmeldungStatusId()
    {
        return rueckmeldungStatusId;
    }

    public void setRueckmeldungStatusId(RueckmeldungStatusId _id) throws StdException
    {
        if (_id == null)
        {
            throw new StdException(
                            "rueckmeldung_status_id darf nicht null sein");
        }
        rueckmeldungStatusId = _id;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(String _alias) throws StdException
    {
        if (StringUtils.isNullOrEmpty(_alias))
        {
            throw new StdException("alias darf nicht null oder leer sein");
        }
        alias = _alias;
    }

}

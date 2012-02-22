package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.ProbeTerminId;

/**
 * VO für {@link Scheme#PROBE_TERMIN_TABLE}
 * 
 * @author ckl
 * 
 */
public class ProbeTerminVO implements BaseIdVO
{
    ProbeTerminVO()
    {
    }

    private ProbeTerminId id;

    private OrganisationsEinheitId organisationseinheitId;

    private UnixTime start;

    private UnixTime ende;

    public BaseId getBaseId()
    {
        return id;
    }

    public ProbeTerminId getProbeTerminId()
    {
        return id;
    }

    public void setProbeTerminId(ProbeTerminId _probeTerminId) throws StdException
    {
        if (_probeTerminId == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }
        id = _probeTerminId;
    }

    public OrganisationsEinheitId getOrganisationsEinheitId()
    {
        return organisationseinheitId;
    }

    public void setOrganisationsEinheitId(OrganisationsEinheitId _id) throws StdException
    {
        if (_id == null)
        {
            throw new StdException(
                            "organisationseinheit_id darf nicht null sein");
        }
        organisationseinheitId = _id;
    }

    public UnixTime getStart()
    {
        return start;
    }

    public void setStart(UnixTime _start) throws StdException
    {
        if (_start == null)
        {
            throw new StdException("start zeit darf nicht null sein");
        }
        start = _start;
    }

    public UnixTime getEnde()
    {
        return ende;
    }

    public void setEnde(UnixTime _ende) throws StdException
    {
        if (_ende == null)
        {
            throw new StdException("end zeit darf nicht null sein");
        }
        ende = _ende;
    }

}

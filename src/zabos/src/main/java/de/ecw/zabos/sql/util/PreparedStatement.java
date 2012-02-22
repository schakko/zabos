package de.ecw.zabos.sql.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Map;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.types.Pin;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.AlarmQuelleId;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.BereichInSchleifeId;
import de.ecw.zabos.types.id.FuenfTonId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.ProbeTerminId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;
import de.ecw.zabos.types.id.RueckmeldungStatusAliasId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;
import de.ecw.zabos.types.id.SchleifeId;
import de.ecw.zabos.types.id.SmsInId;
import de.ecw.zabos.types.id.SmsOutId;
import de.ecw.zabos.types.id.SmsOutStatusId;
import de.ecw.zabos.types.id.TelefonId;

/**
 * Ein vorkompiliertes, parametrisierbares SQL Statement
 * 
 * @author bsp
 * 
 */
public class PreparedStatement
{
    private java.sql.PreparedStatement pst;

    private String query;

    private DBConnection dbconnection;

    /**
     * Instanziert ein vorkompiliertes SQL Statement.
     * 
     * @param _dbconnection
     * @param _sql
     * @throws StdException
     */
    public PreparedStatement(DBConnection _dbconnection, String _sql)
                    throws StdException
    {
        try
        {
            dbconnection = _dbconnection;
            pst = dbconnection.getConnection().prepareStatement(_sql);
            query = _sql;
        }
        catch (SQLException e)
        {
            throw new StdException("error preparing statement \"" + _sql
                            + "\".", e);
        }
    }

    public void close() throws StdException
    {
        try
        {
            pst.close();
        }
        catch (SQLException e)
        {
            throw new StdException("unable to close PreparedStatement: "
                            + e.getMessage());
        }
    }

    /**
     * Führt eine Select-Query aus und liefert die Ergebnisse als ResultSet.
     * 
     * @return
     * @throws StdException
     */
    public ResultSet executeQuery() throws StdException
    {
        try
        {
            // log.debug("Execute query [.executeQuery] [" + query + "]");
            return new ResultSet(pst.executeQuery());
        }
        catch (SQLException e)
        {
            throw new StdException("failed to execute query \"" + query
                            + "\": " + e.getMessage(), e);
        }
    }

    /**
     * Führt eine Insert/Update/Delete Query aus.
     * 
     * @throws StdException
     */
    public void execute() throws StdException
    {
        try
        {
            // log.debug("Execute query [.execute] [" + query + "]");
            pst.execute();
        }
        catch (SQLException e)
        {
            throw new StdException("failed to execute \"" + query + "\": "
                            + e.getMessage());
        }
    }

    /**
     * Liefert die nächste Sequence ID
     * 
     * @return
     * @throws StdException
     */
    public long nextId() throws StdException
    {
        return dbconnection.nextId();
    }

    public void setBoolean(int _nr, boolean _b) throws StdException
    {
        try
        {
            pst.setBoolean(_nr, _b);
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set boolean parameter #" + _nr, e);
        }
    }

    public void setInteger(int _nr, Integer _i) throws StdException
    {
        try
        {
            if (_i == null)
            {
                pst.setNull(_nr, java.sql.Types.INTEGER);
            }
            else
            {
                pst.setInt(_nr, _i.intValue());
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set integer parameter #" + _nr, e);
        }
    }

    public void setIntegerNN(int _nr, int _i) throws StdException
    {
        try
        {
            pst.setInt(_nr, _i);
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set integer parameter #" + _nr, e);
        }
    }

    public void setLong(int _nr, long _l) throws StdException
    {
        try
        {
            pst.setLong(_nr, _l);
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set long parameter #" + _nr, e);
        }
    }

    public void setPin(int _nr, Pin _pin) throws StdException
    {
        try
        {
            if (_pin == null)
            {
                pst.setNull(_nr, java.sql.Types.VARCHAR);
            }
            else
            {
                pst.setString(_nr, _pin.getPin());
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set string parameter #" + _nr, e);
        }
    }

    public void setProbeTerminId(int _nr, ProbeTerminId _id) throws StdException
    {
        try
        {
            if (_id == null)
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
            else
            {
                pst.setLong(_nr, _id.getLongValue());
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set ProbeTerminId parameter #"
                            + _nr, e);
        }
    }

    public void setString(int _nr, String _s) throws StdException
    {
        try
        {
            if (_s == null)
            {
                pst.setNull(_nr, java.sql.Types.VARCHAR);
            }
            else
            {
                pst.setString(_nr, _s);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set string parameter #" + _nr, e);
        }
    }

    public void setAlarmId(int _nr, AlarmId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set alarmId parameter #" + _nr, e);
        }
    }

    public void setAlarmQuelleId(int _nr, AlarmQuelleId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set alarmQuelleId parameter #"
                            + _nr, e);
        }
    }

    public void setBaseId(int _nr, BaseId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set baseId parameter #" + _nr, e);
        }
    }

    public void setBereichId(int _nr, BereichId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set bereichId parameter #" + _nr,
                            e);
        }
    }

    public void setBereichInSchleifeId(int _nr, BereichInSchleifeId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set bereichId parameter #" + _nr,
                            e);
        }
    }

    public void setFuenfTonId(int _nr, FuenfTonId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException(
                            "failed to set fuenfTonId parameter #" + _nr, e);
        }
    }

    public void setOrganisationId(int _nr, OrganisationId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set organisationsId parameter #"
                            + _nr, e);
        }
    }

    public void setOrganisationsEinheitId(int _nr, OrganisationsEinheitId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException(
                            "failed to set organisationsEinheitId parameter #"
                                            + _nr, e);
        }
    }

    public void setPersonId(int _nr, PersonId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set personId parameter #" + _nr,
                            e);
        }
    }

    public void setRechtId(int _nr, RechtId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set rechtId parameter #" + _nr, e);
        }
    }

    public void setFunktionstraegerId(int _nr, FunktionstraegerId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException(
                            "failed to set funktionstraegerId parameter #"
                                            + _nr, e);
        }
    }

    public void setRolleId(int _nr, RolleId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set rolleId parameter #" + _nr, e);
        }
    }

    public void setRueckmeldungStatusId(int _nr, RueckmeldungStatusId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException(
                            "failed to set rueckmeldungStatusId parameter #"
                                            + _nr, e);
        }
    }

    public void setRueckmeldungStatusAlias(int _nr, String _alias) throws StdException
    {
        try
        {
            if (_alias != null)
            {
                pst.setString(_nr, _alias.toString());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.CHAR);
            }
        }
        catch (SQLException e)
        {
            throw new StdException(
                            "failed to set rueckmeldungStatusAlias parameter #"
                                            + _nr, e);
        }
    }

    public void setRueckmeldungStatusAliasId(int _nr,
                    RueckmeldungStatusAliasId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException(
                            "failed to set rueckmeldungStatusAliasId parameter #"
                                            + _nr, e);
        }
    }

    public void setSchleifeId(int _nr, SchleifeId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException(
                            "failed to set schleifeId parameter #" + _nr, e);
        }
    }

    public void setSmsInId(int _nr, SmsInId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set smsInId parameter #" + _nr, e);
        }
    }

    public void setSmsOutId(int _nr, SmsOutId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set smsOutId parameter #" + _nr,
                            e);
        }
    }

    public void setSmsOutStatusId(int _nr, SmsOutStatusId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set smsOutStatusId parameter #"
                            + _nr, e);
        }
    }

    public void setTelefonId(int _nr, TelefonId _id) throws StdException
    {
        try
        {
            if (_id != null)
            {
                pst.setLong(_nr, _id.getLongValue());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set telefonId parameter #" + _nr,
                            e);
        }
    }

    public void setTelefonNummer(int _nr, TelefonNummer _telefonNummer) throws StdException
    {
        try
        {
            pst.setString(_nr, _telefonNummer.getNummer());
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set TelefonNummer parameter #"
                            + _nr, e);
        }
    }

    public void setUnixTime(int _nr, UnixTime _ut) throws StdException
    {
        try
        {
            if (_ut != null)
            {
                pst.setLong(_nr, _ut.getTimeStamp());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BIGINT);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set unixtime parameter #" + _nr,
                            e);
        }
    }

    public void setMap(int _nr, Map<String, String> _hm) throws StdException
    {
        try
        {
            if (_hm != null)
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(_hm);
                oos.close();

                ByteArrayInputStream ois = new ByteArrayInputStream(
                                baos.toByteArray());
                pst.setBinaryStream(_nr, ois, baos.size());
            }
            else
            {
                pst.setNull(_nr, java.sql.Types.BINARY);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to set hashmap parameter #" + _nr, e);
        }
        catch (IOException e)
        {
            throw new StdException(
                            "failed to serialize hashmap for parameter #" + _nr,
                            e);
        }

    }

}

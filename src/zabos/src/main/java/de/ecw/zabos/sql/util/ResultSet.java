package de.ecw.zabos.sql.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.HashMap;
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
 * Hält die Ergebniszeile(n) einer SQL Abfrage
 * 
 * @author bsp
 * 
 */
public class ResultSet
{

    private java.sql.ResultSet result_set;

    public ResultSet(java.sql.ResultSet _resultSet)
    {
        result_set = _resultSet;
    }

    /**
     * Liefert die Anzahl der Ergebniszeilen zurück
     * 
     * @return
     * @throws StdException
     */
    public int size() throws StdException
    {
        try
        {
            return result_set.getFetchSize();
        }
        catch (SQLException e)
        {
            throw new StdException(e);
        }
    }

    /**
     * Schließt das ResultSet
     * 
     * @throws StdException
     */
    public void close() throws StdException
    {
        try
        {
            result_set.close();
        }
        catch (SQLException e)
        {
            throw new StdException("Unable to close result set: "
                            + e.getMessage());
        }
    }

    /**
     * Stellt den Cursor auf die nächste verfügbare Ergebniszeile. Wenn keine
     * Zeile mehr vorhanden ist liefert die Methode false zur�ck.
     * 
     * @return
     * @throws StdException
     */
    public boolean next() throws StdException
    {
        try
        {
            return result_set.next();
        }
        catch (SQLException e)
        {
            throw new StdException(e);
        }
    }

    public boolean getBooleanNN(String _col) throws StdException
    {
        try
        {
            boolean b = result_set.getBoolean(_col);
            if (result_set.wasNull())
            {
                throw new StdException("failed to get NULL boolean column \""
                                + _col + "\"");
            }
            else
            {
                return b;
            }
        }
        catch (SQLException e)
        {
            throw new StdException(
                            "failed to get long column \"" + _col + "\"", e);
        }
    }

    public Integer getInteger(String _col) throws StdException
    {
        try
        {
            int i = result_set.getInt(_col);
            if (result_set.wasNull())
            {
                return null;
            }
            else
            {
                return Integer.valueOf(i);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to get integer column \"" + _col
                            + "\"", e);
        }
    }

    public int getIntegerNN(String _col) throws StdException
    {
        try
        {
            int i = result_set.getInt(_col);
            if (result_set.wasNull())
            {
                return 0;
            }
            else
            {
                return i;
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to get integer column \"" + _col
                            + "\"", e);
        }
    }

    public Long getLong(String _col) throws StdException
    {
        try
        {
            long l = result_set.getLong(_col);
            if (result_set.wasNull())
            {
                return null;
            }
            else
            {
                return new Long(l);
            }
        }
        catch (SQLException e)
        {
            throw new StdException(
                            "failed to get long column \"" + _col + "\"", e);
        }
    }

    public Long getLong(int _i) throws StdException
    {
        try
        {
            long l = result_set.getLong(_i);
            if (result_set.wasNull())
            {
                return null;
            }
            else
            {
                return new Long(l);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to get long column \"" + _i + "\"",
                            e);
        }
    }

    public String getString(String _col) throws StdException
    {
        try
        {
            String s = result_set.getString(_col);
            if (result_set.wasNull())
            {
                return null;
            }
            else
            {
                return s;
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to get string column \"" + _col
                            + "\"", e);
        }
    }

    public String getString(int _i) throws StdException
    {
        try
        {
            String s = result_set.getString(_i);
            if (result_set.wasNull())
            {
                return null;
            }
            else
            {
                return s;
            }
        }
        catch (SQLException e)
        {
            throw new StdException(
                            "failed to get string column \"" + _i + "\"", e);
        }
    }

    public UnixTime getUnixTime(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new UnixTime(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public AlarmId getAlarmId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new AlarmId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public AlarmQuelleId getAlarmQuelleId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new AlarmQuelleId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public BaseId getBaseId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new BaseId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public FuenfTonId getFuenfTonId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new FuenfTonId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public OrganisationId getOrganisationId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new OrganisationId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public OrganisationId getOrganisationId(int _i) throws StdException
    {
        Long l = getLong(_i);
        if (l != null)
        {
            return new OrganisationId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public OrganisationsEinheitId getOrganisationsEinheitId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new OrganisationsEinheitId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public OrganisationsEinheitId getOrganisationsEinheitId(int _i) throws StdException
    {
        Long l = getLong(_i);
        if (l != null)
        {
            return new OrganisationsEinheitId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public PersonId getPersonId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new PersonId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public Pin getPin(String _col) throws StdException
    {
        String s = getString(_col);
        if (s != null)
        {
            return new Pin(s);
        }
        else
        {
            return null;
        }
    }

    public ProbeTerminId getProbeTerminId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new ProbeTerminId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public RechtId getRechtId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new RechtId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public RolleId getRolleId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new RolleId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public RueckmeldungStatusId getRueckmeldungStatusId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new RueckmeldungStatusId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public RueckmeldungStatusAliasId getRueckmeldungStatusAliasId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new RueckmeldungStatusAliasId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public SchleifeId getSchleifeId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new SchleifeId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public SchleifeId getSchleifeId(int _i) throws StdException
    {
        Long l = getLong(_i);
        if (l != null)
        {
            return new SchleifeId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public SmsOutId getSmsOutId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new SmsOutId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public SmsInId getSmsInId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new SmsInId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public BereichId getBereichId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new BereichId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public BereichInSchleifeId getBereichInSchleifeId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new BereichInSchleifeId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public TelefonId getTelefonId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new TelefonId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public FunktionstraegerId getFunktionstraegerId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new FunktionstraegerId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    public TelefonNummer getTelefonNummer(String _col) throws StdException
    {
        try
        {
            String s = result_set.getString(_col);
            if (result_set.wasNull())
            {
                return null;
            }
            else
            {
                return new TelefonNummer(s);
            }
        }
        catch (SQLException e)
        {
            throw new StdException("failed to get telefonnummer column \""
                            + _col + "\"", e);
        }
    }

    public SmsOutStatusId getSmsOutStatusId(String _col) throws StdException
    {
        Long l = getLong(_col);
        if (l != null)
        {
            return new SmsOutStatusId(l.longValue());
        }
        else
        {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getMap(String _col) throws StdException
    {
        Map<String, String> r = new HashMap<String, String>();

        try
        {
            InputStream is = result_set.getBinaryStream(_col);
            if (is != null)
            {
                ObjectInputStream ois = new ObjectInputStream(is);
                Object obj = ois.readObject();
                r = (HashMap<String, String>) obj;
            }

        }
        catch (SQLException e)
        {
            throw new StdException("failed to get serialized hashmap column \""
                            + _col + "\"", e);
        }
        catch (ClassNotFoundException e)
        {
            throw new StdException("failed to deserialize column \"" + _col
                            + "\"", e);
        }
        catch (IOException e)
        {
            throw new StdException("could not deserialize column \"" + _col
                            + "\"", e);
        }

        return r;
    }

}

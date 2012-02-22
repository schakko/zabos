package de.ecw.zabos.sql.dao;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.RueckmeldungStatusVO;

/**
 * DataAccessObject für {@link Scheme#RUECKMELDUNG_STATUS_TABLE}
 * 
 * @author bsp
 * 
 */
public class RueckmeldungStatusDAO extends AbstractBaseDAO
{

    public RueckmeldungStatusDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private RueckmeldungStatusVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        if (_rs.next())
        {
            return toVO(_rs, _keepResultSet);
        }
        else
        {
            return null;
        }
    }

    public RueckmeldungStatusVO toVO(ResultSet _rs,
                    boolean _keepResultSet) throws StdException
    {
        RueckmeldungStatusVO vo = getObjectFactory().createRueckmeldungStatus();

        vo.setRueckmeldungStatusId(_rs
                        .getRueckmeldungStatusId(Scheme.COLUMN_ID));
        vo.setName(_rs.getString(Scheme.RUECKMELDUNG_STATUS_COLUMN_NAME));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    /*
     * 
     * 
     * queries
     */

    /**
     * Liefert den Rückmeldungs-Status für einen Alias-String.
     * 
     */
    public RueckmeldungStatusVO findRueckmeldungStatusByAlias(String _alias) throws StdException
    {
        PreparedStatement pst = getPstFindRueckmeldungStatusByAlias();

        pst.setString(1, _alias);

        RueckmeldungStatusVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /*
     * 
     * 
     * prepared statements
     */

    private PreparedStatement getPstFindRueckmeldungStatusByAlias() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT s.* FROM "
                                        + Scheme.RUECKMELDUNG_STATUS_TABLE
                                        + " s,"
                                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_TABLE
                                        + " a WHERE a."
                                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_COLUMN_RUECKMELDUNG_STATUS_ID
                                        + "=s."
                                        + Scheme.COLUMN_ID
                                        + " AND "
                                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_COLUMN_ALIAS
                                        + "=?;");
    }
}

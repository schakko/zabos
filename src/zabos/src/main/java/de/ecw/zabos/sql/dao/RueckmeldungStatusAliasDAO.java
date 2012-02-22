package de.ecw.zabos.sql.dao;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.RueckmeldungStatusAliasVO;
import de.ecw.zabos.types.id.RueckmeldungStatusAliasId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;

/**
 * DataAccessObject für {@link Scheme#RUECKMELDUNG_STATUS_ALIAS_TABLE}
 * 
 * @author bsp
 * 
 */
public class RueckmeldungStatusAliasDAO extends AbstractBaseDAO
{
    public RueckmeldungStatusAliasDAO(DBConnection _dbconnection,
                    ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private RueckmeldungStatusAliasVO nextToVO(ResultSet _rs,
                    boolean _keepResultSet) throws StdException
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

    public RueckmeldungStatusAliasVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        RueckmeldungStatusAliasVO vo = getObjectFactory()
                        .createRueckmeldungStatusAlias();

        vo.setRueckmeldungStatusAliasId(_rs
                        .getRueckmeldungStatusAliasId(Scheme.COLUMN_ID));
        vo.setRueckmeldungStatusId(_rs
                        .getRueckmeldungStatusId(Scheme.RUECKMELDUNG_STATUS_ALIAS_COLUMN_RUECKMELDUNG_STATUS_ID));
        vo.setAlias(_rs.getString(Scheme.RUECKMELDUNG_STATUS_ALIAS_COLUMN_ALIAS));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    private RueckmeldungStatusAliasVO[] toVOs(ResultSet _rs,
                    boolean _keepResultSet) throws StdException
    {
        List<RueckmeldungStatusAliasVO> al = new ArrayList<RueckmeldungStatusAliasVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        RueckmeldungStatusAliasVO[] r = new RueckmeldungStatusAliasVO[al.size()];
        al.toArray(r);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return r;
    }

    /*
     * 
     * 
     * queries
     */

    /**
     * Legt einen neuen Rueckmeldungs-Alias an.
     * 
     */
    public RueckmeldungStatusAliasVO createRueckmeldungStatusAlias(
                    RueckmeldungStatusAliasVO _aliasVO) throws StdException
    {
        RueckmeldungStatusAliasId id = new RueckmeldungStatusAliasId(
                        dbconnection.nextId());

        PreparedStatement pst = getPstCreateRueckmeldungStatusAlias();
        pst.setRueckmeldungStatusAliasId(1, id);
        pst.setRueckmeldungStatusId(2, _aliasVO.getRueckmeldungStatusId());
        pst.setString(3, _aliasVO.getAlias());
        pst.execute();
        pst.close();

        return findByRueckmeldungStatusAliasId(id);
    }

    /**
     * Löscht einen Alias.
     * 
     */
    public void deleteRueckmeldungStatusAlias(RueckmeldungStatusAliasId _aliasId) throws StdException
    {
        PreparedStatement pst = getPstDeleteRueckmeldungStatusAlias();
        pst.setRueckmeldungStatusAliasId(1, _aliasId);
        pst.execute();
        pst.close();
    }

    /**
     * Liefert alle Aliase.
     * 
     */
    public RueckmeldungStatusAliasVO[] findAll() throws StdException
    {
        PreparedStatement pst = getPstFindAll();
        RueckmeldungStatusAliasVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert den Alias mit der ggb. Id
     * 
     */
    public RueckmeldungStatusAliasVO findByRueckmeldungStatusAliasId(
                    RueckmeldungStatusAliasId _aliasId) throws StdException
    {
        PreparedStatement pst = getPstFindByRueckmeldungStatusAliasId();
        pst.setRueckmeldungStatusAliasId(1, _aliasId);
        RueckmeldungStatusAliasVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert Aliase mit dem gegebenen Alias. Diese Methode ist
     * case-insensitive. Der gegebene Alias "Ja" liefert also "ja", "jA",
     * "JA"...
     * 
     * @param _alias
     * @return
     * @throws StdException
     * 
     */
    public RueckmeldungStatusAliasVO[] findByRueckmeldungStatusAlias(
                    String _alias) throws StdException
    {
        PreparedStatement pst = getPstFindByRueckmeldungStatusAlias();
        pst.setRueckmeldungStatusAlias(1, _alias);
        RueckmeldungStatusAliasVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Aliase fuer die ggb. StatusId.
     * 
     * @param _rueckmeldungStatusId
     * @return
     * @throws StdException
     */
    public RueckmeldungStatusAliasVO[] findByRueckmeldungStatusId(
                    RueckmeldungStatusId _rueckmeldungStatusId) throws StdException
    {
        PreparedStatement pst = getPstFindByRueckmeldungStatusId();
        pst.setRueckmeldungStatusId(1, _rueckmeldungStatusId);
        RueckmeldungStatusAliasVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Ändert einen Alias
     * 
     * @return
     * @throws StdException
     */
    public RueckmeldungStatusAliasVO updateRueckmeldungStatusAlias(
                    RueckmeldungStatusAliasVO _aliasVO) throws StdException
    {
        PreparedStatement pst = getPstUpdateRueckmeldungStatusAlias();
        pst.setRueckmeldungStatusId(1, _aliasVO.getRueckmeldungStatusId());
        pst.setString(2, _aliasVO.getAlias());
        pst.setRueckmeldungStatusAliasId(3,
                        _aliasVO.getRueckmeldungStatusAliasId());
        pst.execute();
        pst.close();

        return findByRueckmeldungStatusAliasId(_aliasVO
                        .getRueckmeldungStatusAliasId());
    }

    /*
     * 
     * 
     * prepared statements
     */

    private PreparedStatement getPstCreateRueckmeldungStatusAlias() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "INSERT INTO "
                                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_TABLE
                                        + " ("
                                        + Scheme.COLUMN_ID
                                        + ","
                                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_COLUMN_RUECKMELDUNG_STATUS_ID
                                        + ","
                                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_COLUMN_ALIAS
                                        + ") VALUES(?,?,?);");
    }

    private PreparedStatement getPstDeleteRueckmeldungStatusAlias() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_TABLE + " WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstFindAll() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_TABLE + ";");
    }

    private PreparedStatement getPstFindByRueckmeldungStatusAliasId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_TABLE + " WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstFindByRueckmeldungStatusId() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT * FROM "
                                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_TABLE
                                        + " WHERE "
                                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_COLUMN_RUECKMELDUNG_STATUS_ID
                                        + "=?;");
    }

    private PreparedStatement getPstUpdateRueckmeldungStatusAlias() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "UPDATE "
                                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_TABLE
                                        + " SET "
                                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_COLUMN_RUECKMELDUNG_STATUS_ID
                                        + "=?, "
                                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_COLUMN_ALIAS
                                        + "=? WHERE " + Scheme.COLUMN_ID
                                        + "=?;");
    }

    private PreparedStatement getPstFindByRueckmeldungStatusAlias() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_TABLE + " WHERE "
                        + Scheme.RUECKMELDUNG_STATUS_ALIAS_COLUMN_ALIAS
                        + " ~* ?;");
    }

}

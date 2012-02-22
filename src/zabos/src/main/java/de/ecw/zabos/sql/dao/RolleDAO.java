package de.ecw.zabos.sql.dao;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.dao.cache.CacheMultipleAdapter;
import de.ecw.zabos.sql.dao.cache.ICacheMultiple;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;

/**
 * DataAccessObject für {@link Scheme#ROLLE_TABLE}
 * 
 * @author bsp
 * 
 */
public class RolleDAO extends AbstractCreateUpdateDeleteDAO
{
    /**
     * Cache
     */
    public final ICacheMultiple<RolleVO> CACHE_FIND_ALL = new CacheMultipleAdapter<RolleVO>()
    {
        public void updateAfterElementReleasement() throws StdException
        {
            setElements(findAll());
        }
    };

    public RolleDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private RolleVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<RolleVO> al = new ArrayList<RolleVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        RolleVO[] r = new RolleVO[al.size()];
        al.toArray(r);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return r;
    }

    private RolleVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public RolleVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        RolleVO vo = getObjectFactory().createRolle();
        vo.setRolleId(_rs.getRolleId(Scheme.COLUMN_ID));
        vo.setBeschreibung(_rs.getString(Scheme.COLUMN_BESCHREIBUNG));
        vo.setName(_rs.getString(Scheme.ROLLE_COLUMN_NAME));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    public BaseIdVO create(BaseIdVO _vo) throws StdException
    {
        return createRolle((RolleVO) _vo);
    }

    public BaseIdVO update(BaseIdVO _vo) throws StdException
    {
        return updateRolle((RolleVO) _vo);
    }

    public void delete(BaseId _id) throws StdException
    {
        removeRolle((RolleId) _id);
    }

    /*
     * 
     * 
     * queries
     */

    /**
     * Liefert die Anzahl der Zuweisungen der ggb. Rolle zum System,
     * Organisationen, Organisationseinheiten und SChleifen.
     * 
     */
    public long countRolleById(RolleId _rolleId) throws StdException
    {
        PreparedStatement pst = getPstCountRolleById();
        pst.setRolleId(1, _rolleId);
        pst.setRolleId(2, _rolleId);
        pst.setRolleId(3, _rolleId);
        pst.setRolleId(4, _rolleId);
        ResultSet rs = pst.executeQuery();

        long r = 0;

        if (rs.next())
        {
            r = rs.getLong(1).longValue();
        }

        rs.close();
        pst.close();
        return r;
    }

    /**
     * Legt eine neue Rolle an.
     * 
     */
    public RolleVO createRolle(RolleVO _vo) throws StdException
    {
        RolleId id = new RolleId(dbconnection.nextId());
        PreparedStatement pst = getPstCreateRolle();
        pst.setRolleId(1, id);
        pst.setString(2, _vo.getBeschreibung());
        pst.setString(3, _vo.getName());
        pst.execute();
        pst.close();

        return findRolleById(id);
    }

    /**
     * Liefert alle Rollen.
     * 
     * @return
     * @throws StdException
     */
    public RolleVO[] findAll() throws StdException
    {
        if (CACHE_FIND_ALL.findMultiple() != null)
        {
            return CACHE_FIND_ALL.findMultiple();
        }

        PreparedStatement pst = getPstFindAll();
        RolleVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Rolle mit der ggb. RolleId. Wenn die Rolle nicht gefunden
     * wurde dann liefert diese Methode null.
     * 
     * @param _rolleId
     * @return
     * @throws StdException
     */
    public RolleVO findRolleById(RolleId _rolleId) throws StdException
    {
        if (_rolleId == null)
        {
            throw new StdException("cannot find rolle by null id");
        }
        PreparedStatement pst = getPstFindRolleById();
        pst.setRolleId(1, _rolleId);
        RolleVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Rolle mit dem ggb. Namen. Wenn die Rolle nicht gefunden wurde
     * dann liefert diese Methode null. <br />
     * Die Methode ist <strong>case-insensitive</strong>
     * 
     * @param _name
     * @return
     * @throws StdException
     */
    public RolleVO findRolleByName(String _name) throws StdException
    {
        PreparedStatement pst = getPstFindRolleByName();
        pst.setString(1, _name);
        RolleVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Weist einer Rolle ein Recht zu.
     * 
     * @param _rechtId
     * @param _rolleId
     * @throws StdException
     */
    public void addRechtToRolle(RechtId _rechtId, RolleId _rolleId) throws StdException
    {
        long id = dbconnection.nextId();
        PreparedStatement pst = getPstAddRechtToRolle();
        pst.setLong(1, id);
        pst.setRechtId(2, _rechtId);
        pst.setRolleId(3, _rolleId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht einer Rolle ein Recht.
     * 
     * @param _rechtId
     * @param _rolleId
     * @throws StdException
     */
    public void removeRechtFromRolle(RechtId _rechtId, RolleId _rolleId) throws StdException
    {
        PreparedStatement pst = getPstRemoveRechtFromRolle();
        pst.setRechtId(1, _rechtId);
        pst.setRolleId(2, _rolleId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht einer Rolle alle Rechte.
     * 
     * @param _rolleId
     * @throws StdException
     */
    public void removeRechteFromRolle(RolleId _rolleId) throws StdException
    {
        PreparedStatement pst = getPstRemoveRechteFromRolle();
        pst.setRolleId(1, _rolleId);
        pst.execute();
        pst.close();
    }

    /**
     * Löscht eine Rolle.
     * 
     * @param _rolleId
     * @throws StdException
     */
    public void removeRolle(RolleId _rolleId) throws StdException
    {
        PreparedStatement pst = getPstRemoveRolle();
        pst.setRolleId(1, _rolleId);
        pst.execute();
        pst.close();
    }

    /**
     * Ändert eine Rolle.
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public RolleVO updateRolle(RolleVO _vo) throws StdException
    {
        PreparedStatement pst = getPstUpdateRolle();
        pst.setString(1, _vo.getBeschreibung());
        pst.setString(2, _vo.getName());
        pst.setRolleId(3, _vo.getRolleId());
        pst.execute();
        pst.close();

        return findRolleById(_vo.getRolleId());
    }

    /*
     * 
     * 
     * prepared statements
     */

    private PreparedStatement getPstAddRechtToRolle() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.RECHT_IN_ROLLE_TABLE + " (" + Scheme.COLUMN_ID
                        + "," + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID + ","
                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                        + ") VALUES(?,?,?);");
    }

    private PreparedStatement getPstCountRolleById() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT (SELECT COUNT("
                                        + Scheme.COLUMN_ID
                                        + ") FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                                        + "=?) + (SELECT COUNT("
                                        + Scheme.COLUMN_ID
                                        + ") FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + "=?) + (SELECT COUNT("
                                        + Scheme.COLUMN_ID
                                        + ") FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID
                                        + "=?) + (SELECT COUNT("
                                        + Scheme.COLUMN_ID
                                        + ") FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_ROLLE_ID
                                        + "=?);");
    }

    private PreparedStatement getPstCreateRolle() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.ROLLE_TABLE + " (" + Scheme.COLUMN_ID + ","
                        + Scheme.COLUMN_BESCHREIBUNG + ","
                        + Scheme.ROLLE_COLUMN_NAME + ") values(?,?,?);");
    }

    private PreparedStatement getPstFindAll() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ROLLE_TABLE + " ORDER BY "
                        + Scheme.ROLLE_COLUMN_NAME + ";");
    }

    private PreparedStatement getPstFindRolleById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ROLLE_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + "=?;");
    }

    private PreparedStatement getPstFindRolleByName() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ROLLE_TABLE + " WHERE "
                        + Scheme.ROLLE_COLUMN_NAME + " ILIKE ?;");
    }

    private PreparedStatement getPstRemoveRechtFromRolle() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.RECHT_IN_ROLLE_TABLE + " WHERE "
                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID + "=? AND "
                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID + "=?;");
    }

    private PreparedStatement getPstRemoveRechteFromRolle() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.RECHT_IN_ROLLE_TABLE + " WHERE "
                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID + "=?;");
    }

    private PreparedStatement getPstRemoveRolle() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.ROLLE_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + "=?;");
    }

    private PreparedStatement getPstUpdateRolle() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.ROLLE_TABLE + " SET "
                        + Scheme.COLUMN_BESCHREIBUNG + "=?,"
                        + Scheme.ROLLE_COLUMN_NAME + "=? WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

}

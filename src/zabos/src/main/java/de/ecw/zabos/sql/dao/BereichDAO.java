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
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * DataAccessObject für {@link Scheme#BEREICH_TABLE}
 * 
 * @author ckl
 * 
 */
public class BereichDAO extends AbstractCreateUpdateDeleteDAO
{
    /**
     * Cache für VOs
     */
    public final ICacheMultiple<BereichVO> CACHE_FIND_ALL = new CacheMultipleAdapter<BereichVO>()
    {
        public void updateAfterElementReleasement() throws StdException
        {
            setElements(findAll());
        }
    };

    public BereichDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private BereichVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public BereichVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        BereichVO vo = getObjectFactory().createBereich();
        vo.setBereichId(_rs.getBereichId(Scheme.COLUMN_ID));
        vo.setBeschreibung(_rs.getString(Scheme.COLUMN_BESCHREIBUNG));
        vo.setName(_rs.getString(Scheme.COLUMN_NAME));
        vo.setGeloescht(_rs.getBooleanNN(Scheme.COLUMN_GELOESCHT));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    private BereichVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<BereichVO> al = new ArrayList<BereichVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        BereichVO[] vos = new BereichVO[al.size()];
        al.toArray(vos);

        if (!_keepResultSet)
        {
            _rs.close();
        }
        return vos;
    }

    public BaseIdVO create(BaseIdVO _vo) throws StdException
    {
        return createBereich((BereichVO) _vo);
    }

    public BaseIdVO update(BaseIdVO _vo) throws StdException
    {
        return updateBereich((BereichVO) _vo);
    }

    public void delete(BaseId _id) throws StdException
    {
        deleteBereich((BereichId) _id);
    }

    /*
     * 
     * 
     * queries
     */

    /**
     * Liefert den Bereich mit der angegebenen Id
     * 
     * @param _bereichId
     * @return
     * @throws StdException
     */
    public BereichVO findBereichById(BereichId _bereichId) throws StdException
    {
        PreparedStatement pst = getPstFindBereichById();

        pst.setBereichId(1, _bereichId);

        BereichVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert den Bereich mit dem angegebenen Namen <br />
     * Die Methode ist <strong>case-insensitive</strong>.
     * 
     * @param _bereichId
     * @return
     * @throws StdException
     */
    public BereichVO findBereichByName(String _name) throws StdException
    {
        PreparedStatement pst = getPstFindBereichByName();

        pst.setString(1, _name);

        BereichVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;

    }

    /**
     * Liefert die Bereiche zurück, die einer Schleife zugeordnet sind
     * 
     * @param _schleifeId
     * @return
     * @throws StdException
     */
    public BereichVO[] findBereicheBySchleifeIdAndFunktionstraegerId(
                    SchleifeId _schleifeId,
                    FunktionstraegerId _funktionstraegerId) throws StdException
    {
        PreparedStatement pst = getPstFindBereicheBySchleifeIdAndFunktionstraegerId();

        pst.setSchleifeId(1, _schleifeId);
        pst.setFunktionstraegerId(2, _funktionstraegerId);

        BereichVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;

    }

    /**
     * Liefert die Bereiche zurück, die innerhalb eines Funktionsträgers und
     * Schleife in einem Alarm ausgelöst worden sind. Das schließt auch
     * gelöschte Bereiche mit ein.
     * 
     * @param _alarmId
     * @param _funktionstraegerId
     * @return
     * @throws StdException
     */
    public BereichVO[] findBereicheInAlarmByFunktionstragerIdAndSchleifeId(
                    AlarmId _alarmId, FunktionstraegerId _funktionstraegerId,
                    SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstFindBereicheInAlarmByFunktionstragerIdAndSchleifeId();

        pst.setAlarmId(1, _alarmId);
        pst.setFunktionstraegerId(2, _funktionstraegerId);
        pst.setSchleifeId(3, _schleifeId);

        BereichVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Bereiche zurück, die einem Funktionsträger zugeordnet sind
     * 
     * @param _funktionstraegerId
     * @return
     * @throws StdException
     */
    public BereichVO[] findBereicheMitAktiverFunktionstraegerZuordnung(
                    FunktionstraegerId _funktionstraegerId) throws StdException
    {
        PreparedStatement pst = getPstFindBereicheMitAktiverFunktionstraegerZuordnung();

        pst.setFunktionstraegerId(1, _funktionstraegerId);

        BereichVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Bereiche
     * 
     * @return
     * @throws StdException
     */
    public BereichVO[] findAll() throws StdException
    {
        PreparedStatement pst = getPstFindAll();
        BereichVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Ändert einen Bereich.
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public BereichVO updateBereich(BereichVO _vo) throws StdException
    {
        PreparedStatement pst = getPstUpdateBereich();
        pst.setString(1, _vo.getName());
        pst.setString(2, _vo.getBeschreibung());
        pst.setBereichId(3, _vo.getBereichId());
        pst.execute();
        pst.close();

        return findBereichById(_vo.getBereichId());
    }

    /**
     * Legt einen neuen Bereich an
     */
    public BereichVO createBereich(BereichVO _vo) throws StdException
    {
        BereichId id = new BereichId(dbconnection.nextId());
        PreparedStatement pst = getPstCreateBereich();
        pst.setBereichId(1, id);
        pst.setString(2, _vo.getName());
        pst.setString(3, _vo.getBeschreibung());
        pst.execute();
        pst.close();

        return findBereichById(id);
    }

    /**
     * Löscht einen Bereich
     * 
     * @param _id
     * @throws StdException
     */
    public void deleteBereich(BereichId _id) throws StdException
    {
        PreparedStatement pst = getPstDeleteBereich();
        pst.setBereichId(1, _id);
        pst.execute();
        pst.close();
    }

    /*
     * 
     * 
     * 
     * prepared statements
     */
    private PreparedStatement getPstCreateBereich() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.BEREICH_TABLE + " (" + Scheme.COLUMN_ID + ","
                        + Scheme.COLUMN_NAME + "," + Scheme.COLUMN_BESCHREIBUNG
                        + ") VALUES(?,?,?);");
    }

    private PreparedStatement getPstDeleteBereich() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.BEREICH_TABLE + " SET "
                        + Scheme.COLUMN_GELOESCHT + "=true WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstUpdateBereich() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.BEREICH_TABLE + " SET " + Scheme.COLUMN_NAME
                        + "=?," + Scheme.COLUMN_BESCHREIBUNG + "=? WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstFindBereichById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + "=?");
    }

    private PreparedStatement getPstFindBereicheBySchleifeIdAndFunktionstraegerId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_TABLE + " WHERE "
                        + Scheme.COLUMN_GELOESCHT + "=false AND "
                        + Scheme.COLUMN_ID + " IN (SELECT "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID
                        + " FROM " + Scheme.BEREICH_IN_SCHLEIFE_TABLE
                        + " WHERE "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + " = ? AND "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + " = ? AND " + Scheme.COLUMN_GELOESCHT + "=false)");
    }

    private PreparedStatement getPstFindAll() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_TABLE + " WHERE "
                        + Scheme.COLUMN_GELOESCHT + "=false " + " ORDER BY "
                        + Scheme.COLUMN_NAME + ";");
    }

    private PreparedStatement getPstFindBereichByName() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_TABLE + " WHERE " + Scheme.COLUMN_NAME
                        + " ILIKE ?");
    }

    private PreparedStatement getPstFindBereicheInAlarmByFunktionstragerIdAndSchleifeId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + " IN (SELECT "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID
                        + " FROM " + Scheme.BEREICH_IN_SCHLEIFE_TABLE
                        + " WHERE " + Scheme.COLUMN_ID + " IN (" + " SELECT "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID
                        + " FROM " + Scheme.BEREICH_IN_ALARM_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID + "=?) AND "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + " =? AND "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + " = ?)");
    }

    private PreparedStatement getPstFindBereicheMitAktiverFunktionstraegerZuordnung() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + " IN (SELECT DISTINCT "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID
                        + " FROM " + Scheme.BEREICH_IN_SCHLEIFE_TABLE
                        + " WHERE " + Scheme.COLUMN_GELOESCHT + "=false AND "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + "=?)");
    }

}

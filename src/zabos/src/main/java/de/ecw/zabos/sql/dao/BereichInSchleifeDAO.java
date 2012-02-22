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
import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.BereichInSchleifeId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * DataAccessObject für {@link Scheme#BEREICH_IN_SCHLEIFE_TABLE}
 * 
 * @author ckl
 */
public class BereichInSchleifeDAO extends AbstractCreateUpdateDeleteDAO
{
    /**
     * Cache
     */
    public final ICacheMultiple<BereichInSchleifeVO> CACHE_FIND_ALL = new CacheMultipleAdapter<BereichInSchleifeVO>()
    {
        public void updateAfterElementReleasement() throws StdException
        {
            setElements(findAll());
        }
    };

    public BereichInSchleifeDAO(DBConnection _dbconnection,
                    ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private BereichInSchleifeVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    private BereichInSchleifeVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<BereichInSchleifeVO> al = new ArrayList<BereichInSchleifeVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        BereichInSchleifeVO[] vos = new BereichInSchleifeVO[al.size()];
        al.toArray(vos);

        if (!_keepResultSet)
        {
            _rs.close();
        }
        return vos;
    }

    public BereichInSchleifeVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        BereichInSchleifeVO vo = getObjectFactory().createBereichInSchleife();
        vo.setBereichInSchleifeId(_rs.getBereichInSchleifeId(Scheme.COLUMN_ID));
        vo.setSchleifeId(_rs
                        .getSchleifeId(Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID));
        vo.setFunktionstraegerId(_rs
                        .getFunktionstraegerId(Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID));
        vo.setBereichId(_rs
                        .getBereichId(Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID));
        vo.setSollstaerke(_rs
                        .getIntegerNN(Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SOLLSTAERKE));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    public BaseIdVO create(BaseIdVO _vo) throws StdException
    {
        return createBereichInSchleife((BereichInSchleifeVO) _vo);
    }

    public BaseIdVO update(BaseIdVO _vo) throws StdException
    {
        return updateBereichInSchleife((BereichInSchleifeVO) _vo);
    }

    public void delete(BaseId _id) throws StdException
    {
        deleteBereichInSchleife((BereichInSchleifeId) _id);
    }

    /*
     * queries
     */

    /**
     * Erstellt eine neue Zuweisung
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public BereichInSchleifeVO createBereichInSchleife(BereichInSchleifeVO _vo) throws StdException
    {
        BereichInSchleifeId id = new BereichInSchleifeId(dbconnection.nextId());
        PreparedStatement pst = getPstCreateBereichInSchleife();
        pst.setBereichInSchleifeId(1, id);
        pst.setBereichId(2, _vo.getBereichId());
        pst.setFunktionstraegerId(3, _vo.getFunktionstraegerId());
        pst.setSchleifeId(4, _vo.getSchleifeId());
        pst.setInteger(5, _vo.getSollstaerke());
        pst.execute();
        pst.close();

        return findBereichInSchleifeById(id);
    }

    /**
     * Löscht eine Zuweisung
     * 
     * @param _id
     * @throws StdException
     */
    public void deleteBereichInSchleife(BereichInSchleifeId _id) throws StdException
    {
        PreparedStatement pst = getPstDeleteBereichInSchleife();
        pst.setBereichInSchleifeId(1, _id);
        pst.execute();
        pst.close();
    }

    /**
     * Liefert alle Zuweisungen zurück
     * 
     * @return
     * @throws StdException
     */
    public BereichInSchleifeVO[] findAll() throws StdException
    {
        if (CACHE_FIND_ALL.findMultiple() != null)
        {
            return CACHE_FIND_ALL.findMultiple();
        }

        PreparedStatement pst = getPstFindAll();
        BereichInSchleifeVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Findet die Bereich-In-Schleife-Zuweisung anhand der eigenen Id
     * 
     * @param _bereichInSchleifeId
     * @return
     * @throws StdException
     */
    public BereichInSchleifeVO findBereichInSchleifeById(
                    BereichInSchleifeId _bereichInSchleifeId) throws StdException
    {
        PreparedStatement pst = getPstFindBereichInSchleifeById();
        pst.setBereichInSchleifeId(1, _bereichInSchleifeId);
        BereichInSchleifeVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Bereiche zurück, die in einem Alarm noch aktiv sind
     * 
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public BereichInSchleifeVO[] findAktiveBereicheInSchleifeInAlarmByAlarmId(
                    AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstFindAktiveBereicheInSchleifeInAlarmByAlarmId();
        pst.setAlarmId(1, _alarmId);
        BereichInSchleifeVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert den Bereich zurück, der als <strong>direkter</strong> Vorgänger
     * zu diesem definiert ist. Es wird davon ausgegangen, dass das übergebene
     * {@link BereichInSchleifeVO} einer Folgeschleife zugewiesen wurde. <br />
     * Wenn kein Vorgänger existiert, wird null zurückgeliefert.
     * 
     * @param _bereichInSchleifeVO
     * @return
     */
    public BereichInSchleifeVO findVorgaenger(
                    BereichInSchleifeVO _bereichInSchleifeVO) throws StdException
    {
        PreparedStatement pst = getPstFindVorgaenger();
        pst.setBereichId(1, _bereichInSchleifeVO.getBereichId());
        pst.setFunktionstraegerId(2,
                        _bereichInSchleifeVO.getFunktionstraegerId());
        pst.setSchleifeId(3, _bereichInSchleifeVO.getSchleifeId());
        BereichInSchleifeVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Bereiche zurück, die einem Alarm angehören
     * 
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public BereichInSchleifeVO[] findBereicheInSchleifeInAlarmByAlarmId(
                    AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstFindBereicheInSchleifeInAlarmByAlarmId();
        pst.setAlarmId(1, _alarmId);
        BereichInSchleifeVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Bereich-/Funktionsträger-Zuordnung anhand aller PKs zurück
     * 
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public BereichInSchleifeVO findBereichInSchleifeBySchleifeIdAndBereichIdAndFunktionstraegerIdAndAlarmId(
                    SchleifeId _schleifeId, BereichId _bereichId,
                    FunktionstraegerId _funktionstraegerId, AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstFindBereichInSchleifeBySchleifeIdAndBereichIdAndFunktionstraegerIdAndAlarmId();
        pst.setSchleifeId(1, _schleifeId);
        pst.setBereichId(2, _bereichId);
        pst.setFunktionstraegerId(3, _funktionstraegerId);
        pst.setAlarmId(4, _alarmId);

        BereichInSchleifeVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Bereiche einer Schleife zurück, die einem Funktionsträger
     * zugewiesen sind
     * 
     * @param _funktionstraegerId
     * @return
     * @throws StdException
     */
    public BereichInSchleifeVO[] findBereicheInSchleifeByFunktionstraegerId(
                    FunktionstraegerId _funktionstraegerId) throws StdException
    {
        PreparedStatement pst = getPstFindBereicheInSchleifeByFunktionstraegerId();
        pst.setFunktionstraegerId(1, _funktionstraegerId);
        BereichInSchleifeVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Bereiche einer Schleife zurück, die einem Bereich angehören
     * 
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public BereichInSchleifeVO[] findBereicheInSchleifeByBereichId(
                    BereichId _bereichId) throws StdException
    {
        PreparedStatement pst = getPstFindBereicheInSchleifeByBereichId();
        pst.setBereichId(1, _bereichId);
        BereichInSchleifeVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Findet die Bereich-In-Schleife-Zuweisung anhand der SchleifeId. Es wird
     * in der Datenbank nach dem Namen des des Bereichs
     * {@link BereichVO#getName()} und der Beschreibung des Funktionträgers (
     * {@link FunktionstraegerVO#getBeschreibung()}) sortiert.
     * 
     * @param _name
     * @return
     * @throws StdException
     */
    public BereichInSchleifeVO[] findBereicheInSchleifeBySchleifeId(
                    SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstFindBereicheInSchleifeBySchleifeId();
        pst.setSchleifeId(1, _schleifeId);
        BereichInSchleifeVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Aendert einen die Sollstaerke eines Bereichs innerhalb einer Schleife.
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public BereichInSchleifeVO updateBereichInSchleife(BereichInSchleifeVO _vo) throws StdException
    {
        PreparedStatement pst = getPstUpdateBereichInSchleife();
        pst.setInteger(1, _vo.getSollstaerke());
        pst.setBereichInSchleifeId(2, _vo.getBereichInSchleifeId());
        pst.execute();
        pst.close();

        return findBereichInSchleifeById(_vo.getBereichInSchleifeId());
    }

    /**
     * Liefert zurück, ob die Funktionsträger/Bereichs-Kombination im Alarm
     * nachalarmiert wurde
     * 
     * @param _alarmId
     * @param _funktionstraegerId
     * @param _bereichId
     * @return
     * @throws StdException
     */
    public boolean istBereichInSchleifeInAlarmNachalarmiert(AlarmId _alarmId,
                    FunktionstraegerId _funktionstraegerId, BereichId _bereichId) throws StdException
    {
        PreparedStatement pst = getPstIstBereichInSchleifeInAlarmNachalarmiert();
        pst.setAlarmId(1, _alarmId);
        pst.setFunktionstraegerId(2, _funktionstraegerId);
        pst.setBereichId(3, _bereichId);

        ResultSet rs = pst.executeQuery();

        long r = 0;

        if (rs.next())
        {
            r = rs.getLong(1).longValue();
        }

        rs.close();
        pst.close();

        return (r > 1);
    }

    /**
     * Liefert zurück, ob die Funktionsträger/Bereichs-Kombination existiert
     * 
     * @param _funktionstraegerId
     * @param _bereichId
     * @return
     * @throws StdException
     */
    public boolean istBereichFunktionstraegerZuordnungExistent(
                    FunktionstraegerId _funktionstraegerId, BereichId _bereichId) throws StdException
    {
        PreparedStatement pst = getPstIstBereichFunktionstraegerZuordnungExistent();
        pst.setFunktionstraegerId(1, _funktionstraegerId);
        pst.setBereichId(2, _bereichId);

        ResultSet rs = pst.executeQuery();

        long r = 0;

        if (rs.next())
        {
            r = rs.getLong(1).longValue();
        }

        rs.close();
        pst.close();

        return (r >= 1);
    }

    /**
     * Liefert den Bereich zurück, der zuerst ausgelöst wurde. Existiert keiner,
     * wird der übergebene Bereiche zurückgeliefert
     * <ul>
     * <li>Schleifen S1, S2 und S3 existieren.</li>
     * <li>S3 ist Folgeschleife von S2; S2 ist Folgeschleife von S1</li>
     * <li>BereichInSchleife BIS1 ist S1, BereichInSchleife BIS2 ist S2 und
     * BereichInSchleife BIS3 ist S3 zugewiesen</li>
     * <li>Die Funktionsträger/Bereichs-Kombination aller ausgelösten Schleifen
     * ist identisch</li>
     * </ul>
     * 
     * <ul>
     * <li>Wenn Alarm S2 ausgelöst wurde BIS3 übergeben wird, wird BIS2
     * zurückgegeben</li>
     * <li>Wenn Alarm S1 ausgelöst wurde und BIS3 übergeben wird, wird BIS1
     * zurückgegeben</li>
     * <li>Wenn Alarm S3 ausgelöst wurde und BIS3 übergeben wird BIS3
     * zurückgegeben</li>
     * </ul>
     * 
     * @param _bereichInSchleifeVO
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public BereichInSchleifeVO findReferenzBereichInSchleifeInAlarm(
                    BereichInSchleifeVO _bereichInSchleifeVO, AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstFindReferenzBereichInSchleifeInAlarm();
        pst.setBereichId(1, _bereichInSchleifeVO.getBereichId());
        pst.setFunktionstraegerId(2,
                        _bereichInSchleifeVO.getFunktionstraegerId());
        pst.setSchleifeId(3, _bereichInSchleifeVO.getSchleifeId());
        pst.setAlarmId(4, _alarmId);
        pst.setInteger(5, 5);

        BereichInSchleifeVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    private PreparedStatement getPstFindReferenzBereichInSchleifeInAlarm() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID
                        + " =? AND "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + " = ? AND "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID + " = "
                        + Scheme.FUNC_FIND_REFERENZ_SCHLEIFE_IN_ALARM
                        + "(?, ?, ?)");
    }

    private PreparedStatement getPstCreateBereichInSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " ("
                        + Scheme.COLUMN_ID + ","
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID + ","
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + "," + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + "," + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SOLLSTAERKE
                        + ") VALUES(?,?,?,?,?);");
    }

    private PreparedStatement getPstFindAll() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.COLUMN_GELOESCHT + "=false");
    }

    private PreparedStatement getPstFindBereichInSchleifeById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.COLUMN_ID + "=?");
    }

    private PreparedStatement getPstFindBereicheInSchleifeBySchleifeId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT bis.* FROM "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " AS bis, "
                        + Scheme.BEREICH_TABLE + " AS b, "
                        + Scheme.FUNKTIONSTRAEGER_TABLE + " AS f WHERE bis."
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID
                        + " = b." + Scheme.COLUMN_ID + " AND bis."
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + " = f." + Scheme.COLUMN_ID + " AND bis."
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + "=? AND bis." + Scheme.COLUMN_GELOESCHT
                        + "=false ORDER BY f." + Scheme.COLUMN_BESCHREIBUNG
                        + ", b." + Scheme.COLUMN_NAME);
    }

    private PreparedStatement getPstDeleteBereichInSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " SET "
                        + Scheme.COLUMN_GELOESCHT + "=true" + " WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstUpdateBereichInSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " SET "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SOLLSTAERKE + "=? "
                        + " WHERE " + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstFindAktiveBereicheInSchleifeInAlarmByAlarmId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.COLUMN_GELOESCHT + "=false AND "
                        + Scheme.COLUMN_ID + " IN (SELECT "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID
                        + " FROM " + Scheme.BEREICH_IN_ALARM_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_AKTIV + "=true AND "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID + "=?)");
    }

    private PreparedStatement getPstFindBereicheInSchleifeInAlarmByAlarmId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.COLUMN_GELOESCHT + "=false AND "
                        + Scheme.COLUMN_ID + " IN (SELECT "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID
                        + " FROM " + Scheme.BEREICH_IN_ALARM_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID + ")");
    }

    private PreparedStatement getPstFindBereicheInSchleifeByFunktionstraegerId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + "=? AND " + Scheme.COLUMN_GELOESCHT + "=false");
    }

    private PreparedStatement getPstFindBereicheInSchleifeByBereichId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID
                        + "=? AND " + Scheme.COLUMN_GELOESCHT + "=false");
    }

    private PreparedStatement getPstFindBereichInSchleifeBySchleifeIdAndBereichIdAndFunktionstraegerIdAndAlarmId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + "=? AND "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID
                        + "=? AND "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + "=? AND " + Scheme.COLUMN_ID + " IN (SELECT "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID
                        + " FROM " + Scheme.BEREICH_IN_ALARM_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID + " = ?)");
    }

    private PreparedStatement getPstIstBereichInSchleifeInAlarmNachalarmiert() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT COUNT(*) FROM "
                        + Scheme.BEREICH_IN_ALARM_TABLE + " AS bia, "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " AS bis "
                        + " WHERE bia."
                        + Scheme.BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID
                        + " = bis." + Scheme.COLUMN_ID + " AND bia."
                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID
                        + " = ? AND bis."
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + " = ? AND bis."
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID
                        + " = ? GROUP BY bis."
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + ", bis."
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID);
    }

    private PreparedStatement getPstIstBereichFunktionstraegerZuordnungExistent() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT COUNT(*) FROM "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + " = ? AND "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID + " = ?");
    }

    private PreparedStatement getPstFindVorgaenger() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID
                        + " = ? AND "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + " = ? AND "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + " = (SELECT " + Scheme.COLUMN_ID + " FROM "
                        + Scheme.SCHLEIFE_TABLE + " WHERE "
                        + Scheme.SCHLEIFE_COLUMN_FOLGESCHLEIFE_ID + " = ?)");
    }
}

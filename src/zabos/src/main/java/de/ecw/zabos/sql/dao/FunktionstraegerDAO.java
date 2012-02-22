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
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * DataAccessObject für {@link Scheme#FUNKTIONSTRAEGER_TABLE}
 * 
 * @since 2007-06-07
 * @author ckl
 */
public class FunktionstraegerDAO extends AbstractCreateUpdateDeleteDAO
{
    /**
     * Cache
     */
    public final ICacheMultiple<FunktionstraegerVO> CACHE_FIND_ALL = new CacheMultipleAdapter<FunktionstraegerVO>()
    {
        public void updateAfterElementReleasement() throws StdException
        {
            setElements(findAll());
        }
    };

    public FunktionstraegerDAO(DBConnection _dbconnection,
                    ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    public FunktionstraegerVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public FunktionstraegerVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<FunktionstraegerVO> al = new ArrayList<FunktionstraegerVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }

        FunktionstraegerVO[] vos = new FunktionstraegerVO[al.size()];
        al.toArray(vos);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vos;
    }

    public BaseIdVO create(BaseIdVO _vo) throws StdException
    {
        return createFunktionstraeger((FunktionstraegerVO) _vo);
    }

    public BaseIdVO update(BaseIdVO _vo) throws StdException
    {
        return updateFunktionstraeger((FunktionstraegerVO) _vo);
    }

    public void delete(BaseId _id) throws StdException
    {
        deleteFunktionstraeger((FunktionstraegerId) _id);
    }

    public FunktionstraegerVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        FunktionstraegerVO vo = getObjectFactory().createFunktionstraeger();
        vo.setFunktionstraegerId(_rs.getFunktionstraegerId(Scheme.COLUMN_ID));
        vo.setKuerzel(_rs.getString(Scheme.COLUMN_KUERZEL));
        vo.setBeschreibung(_rs
                        .getString(Scheme.FUNKTIONSTRAEGER_COLUMN_BESCHREIBUNG));
        vo.setGeloescht(_rs.getBooleanNN(Scheme.COLUMN_GELOESCHT));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    /*
     * queries
     */

    /**
     * Liefert alle verfügbaren Rechte.
     */
    public FunktionstraegerVO[] findAll() throws StdException
    {
        PreparedStatement pst = getPstFindAll();
        FunktionstraegerVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Testet ob ein ggb. Funktionsträger existiert. Wenn der Funktionsträger
     * nicht existiert dann liefert diese Methode null.
     * 
     * @param _funktionstragerId
     * @return
     * @throws StdException
     */
    public FunktionstraegerVO findFunktionstraegerById(
                    FunktionstraegerId _funktionstraegerId) throws StdException
    {
        PreparedStatement pst = getPstFindFunktionstraegerById();
        pst.setFunktionstraegerId(1, _funktionstraegerId);
        FunktionstraegerVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Funktionsträger zurück, die einer Schleife zugeordnet sind
     * 
     * @param _schleifeId
     * @return
     * @throws StdException
     */
    public FunktionstraegerVO[] findFunktionstraegerInSchleifeBySchleifeId(
                    SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstFindFunktionstraegerInSchleifeBySchleifeId();

        pst.setSchleifeId(1, _schleifeId);

        FunktionstraegerVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Findet die Funktionsträger, die einer Schleife innerhalb eines Alarm
     * zugeordnet sind. Das schließt auch die gelöschten Funktionsträger mit
     * ein.
     * 
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public FunktionstraegerVO[] findFunktionstraegerInAlarmAndSchleife(
                    AlarmId _alarmId, SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstFindFunktionstraegerInAlarmAndSchleife();

        pst.setAlarmId(1, _alarmId);
        pst.setSchleifeId(2, _schleifeId);

        FunktionstraegerVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert den Funktionsträger einer Person zurück. wurde kein
     * Funktionsträger eingetragen, wird null zurückgeliefert.
     * 
     * @param _personId
     * @return
     * @author ckl
     * @throws StdException
     */
    public FunktionstraegerVO findFunktionstraegerByPersonId(PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstFindFunktionstraegerByPerson();
        pst.setPersonId(1, _personId);
        FunktionstraegerVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert den Funktionsträger anhand des Kürzels zurück <br />
     * Die Methode ist <strong>case-insensitive</strong>.
     * 
     * @param _kuerzel
     * @return
     * @author ckl
     * @throws StdException
     */
    public FunktionstraegerVO findFunktionstraegerByKuerzel(String _kuerzel) throws StdException
    {
        PreparedStatement pst = getPstFindFunktionstraegerByKuerzel();
        pst.setString(1, _kuerzel);
        FunktionstraegerVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert den Funktionsträger anhand der Beschreibung zurück <br />
     * Die Methode ist <strong>case-insensitive</strong>.
     * 
     * @param _beschreibung
     * @return
     * @author ckl
     * @throws StdException
     */
    public FunktionstraegerVO findFunktionstraegerByBeschreibung(
                    String _beschreibung) throws StdException
    {
        PreparedStatement pst = getPstFindFunktionstraegerByBeschreibung();
        pst.setString(1, _beschreibung);
        FunktionstraegerVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Funktionsträger zurück, die einem Bereich zugeordnet sind <br />
     * 
     * @return
     * @author ckl
     * @throws StdException
     */
    public FunktionstraegerVO[] findFunktionstraegerMitAktiverBereichZuordnung() throws StdException
    {
        PreparedStatement pst = getPstFindFunktionstraegerMitAktiverBereichZuordnung();
        FunktionstraegerVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Ändert einen Funktionsträger.
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public FunktionstraegerVO updateFunktionstraeger(FunktionstraegerVO _vo) throws StdException
    {
        PreparedStatement pst = getPstUpdateFunktionstraeger();

        pst.setString(1, _vo.getKuerzel());
        pst.setString(2, _vo.getBeschreibung());
        pst.setFunktionstraegerId(3, _vo.getFunktionstraegerId());
        pst.execute();
        pst.close();

        return findFunktionstraegerById(_vo.getFunktionstraegerId());
    }

    /**
     * Legt einen neuen Funktionstraeger an
     */
    public FunktionstraegerVO createFunktionstraeger(FunktionstraegerVO _vo) throws StdException
    {
        FunktionstraegerId id = new FunktionstraegerId(dbconnection.nextId());
        PreparedStatement pst = getPstCreateFunktionstraeger();
        pst.setFunktionstraegerId(1, id);
        pst.setString(2, _vo.getKuerzel());
        pst.setString(3, _vo.getBeschreibung());
        pst.execute();
        pst.close();

        return findFunktionstraegerById(id);
    }

    /**
     * Löscht einen Funktionstraeger
     * 
     * @param _id
     * @throws StdException
     */
    public void deleteFunktionstraeger(FunktionstraegerId _id) throws StdException
    {
        PreparedStatement pst = getPstDeleteFunktionstraeger();
        pst.setFunktionstraegerId(1, _id);
        pst.execute();
        pst.close();
    }

    /*
     * prepared statements
     */
    private PreparedStatement getPstCreateFunktionstraeger() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.FUNKTIONSTRAEGER_TABLE + " ("
                        + Scheme.COLUMN_ID + "," + Scheme.COLUMN_KUERZEL + ","
                        + Scheme.FUNKTIONSTRAEGER_COLUMN_BESCHREIBUNG
                        + ") VALUES(?,?,?);");
    }

    private PreparedStatement getPstDeleteFunktionstraeger() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.FUNKTIONSTRAEGER_TABLE + " SET "
                        + Scheme.COLUMN_GELOESCHT + "=true WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstUpdateFunktionstraeger() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.FUNKTIONSTRAEGER_TABLE + " SET "
                        + Scheme.COLUMN_KUERZEL + "=?,"
                        + Scheme.FUNKTIONSTRAEGER_COLUMN_BESCHREIBUNG
                        + "=? WHERE " + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstFindAll() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.FUNKTIONSTRAEGER_TABLE + " WHERE "
                        + Scheme.COLUMN_GELOESCHT + "=false");
    }

    private PreparedStatement getPstFindFunktionstraegerByKuerzel() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.FUNKTIONSTRAEGER_TABLE + " WHERE "
                        + Scheme.COLUMN_KUERZEL + " ILIKE ?");
    }

    private PreparedStatement getPstFindFunktionstraegerByBeschreibung() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.FUNKTIONSTRAEGER_TABLE + " WHERE "
                        + Scheme.FUNKTIONSTRAEGER_COLUMN_BESCHREIBUNG
                        + " ILIKE ?");
    }

    private PreparedStatement getPstFindFunktionstraegerById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.FUNKTIONSTRAEGER_TABLE + " WHERE "
                        + Scheme.COLUMN_ID + "=?");
    }

    private PreparedStatement getPstFindFunktionstraegerByPerson() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.FUNKTIONSTRAEGER_TABLE + " WHERE "
                        + Scheme.COLUMN_GELOESCHT + "=false AND "
                        + Scheme.COLUMN_ID + " = (SELECT "
                        + Scheme.PERSON_COLUMN_FUNKTIONSTRAEGER_ID + " FROM "
                        + Scheme.PERSON_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + " = ?)");
    }

    private PreparedStatement getPstFindFunktionstraegerInSchleifeBySchleifeId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.FUNKTIONSTRAEGER_TABLE + " WHERE "
                        + Scheme.COLUMN_GELOESCHT + "=false AND "
                        + Scheme.COLUMN_ID + " IN (SELECT "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + " FROM " + Scheme.BEREICH_IN_SCHLEIFE_TABLE
                        + " WHERE "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + " = ?)");
    }

    private PreparedStatement getPstFindFunktionstraegerInAlarmAndSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.FUNKTIONSTRAEGER_TABLE + " WHERE "
                        + Scheme.COLUMN_ID + " IN (SELECT "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + " FROM " + Scheme.BEREICH_IN_SCHLEIFE_TABLE
                        + " WHERE " + Scheme.COLUMN_ID + " IN (" + " SELECT "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID
                        + " FROM " + Scheme.BEREICH_IN_ALARM_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID + "=?) AND "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + " = ?)");
    }

    private PreparedStatement getPstFindFunktionstraegerMitAktiverBereichZuordnung() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.FUNKTIONSTRAEGER_TABLE + " WHERE "
                        + Scheme.COLUMN_ID + " IN (SELECT DISTINCT "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + " FROM " + Scheme.BEREICH_IN_SCHLEIFE_TABLE
                        + " WHERE " + Scheme.COLUMN_GELOESCHT + "=false)");
    }

}
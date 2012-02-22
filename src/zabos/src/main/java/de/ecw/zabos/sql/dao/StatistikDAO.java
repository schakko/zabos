package de.ecw.zabos.sql.dao;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.BereichReportStatistikVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * DataAccessObject für {@link Scheme#VIEW_BEREICH_REPORT} und
 * {@link Scheme#VIEW_BEREICH_REPORT_DETAIL}
 * 
 * @author bsp
 * 
 */
public class StatistikDAO extends AbstractBaseDAO
{
    public StatistikDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private BereichReportStatistikVO nextToVO(ResultSet _rs,
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

    public BereichReportStatistikVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        BereichReportStatistikVO vo = getObjectFactory()
                        .createBereichReportStatistik();
        vo.setAlarmId(_rs
                        .getAlarmId(Scheme.VIEW_BEREICH_REPORT_COLUMN_ALARM_ID));
        vo.setAlarmReihenfolge(_rs
                        .getIntegerNN(Scheme.VIEW_BEREICH_REPORT_COLUMN_ALARM_REIHENFOLGE));
        vo.setAlarmZeit(_rs
                        .getUnixTime(Scheme.VIEW_BEREICH_REPORT_COLUMN_ALARM_ZEIT));
        vo.setEntwarnZeit(_rs
                        .getUnixTime(Scheme.VIEW_BEREICH_REPORT_COLUMN_ENTWARN_ZEIT));
        vo.setSchleifeId(_rs
                        .getSchleifeId(Scheme.VIEW_BEREICH_REPORT_COLUMN_SCHLEIFE_ID));
        vo.setSchleifeName(_rs
                        .getString(Scheme.VIEW_BEREICH_REPORT_COLUMN_SCHLEIFE_NAME));
        vo.setFunktionstraegerId(_rs
                        .getFunktionstraegerId(Scheme.VIEW_BEREICH_REPORT_COLUMN_FUNKTIONSTRAEGER_ID));
        vo.setFunktionstraegerKuerzel(_rs
                        .getString(Scheme.VIEW_BEREICH_REPORT_COLUMN_FUNKTIONSTRAEGER_KUERZEL));
        vo.setFunktionstraegerBeschreibung(_rs
                        .getString(Scheme.VIEW_BEREICH_REPORT_COLUMN_FUNKTIONSTRAEGER_BESCHREIBUNG));
        vo.setBereichId(_rs
                        .getBereichId(Scheme.VIEW_BEREICH_REPORT_COLUMN_BEREICH_ID));
        vo.setBereichName(_rs
                        .getString(Scheme.VIEW_BEREICH_REPORT_COLUMN_BEREICH_NAME));
        vo.setBereichSollstaerke(_rs
                        .getIntegerNN(Scheme.VIEW_BEREICH_REPORT_COLUMN_BEREICH_SOLLSTAERKE));
        vo.setRueckmeldungPositiv(_rs
                        .getIntegerNN(Scheme.VIEW_BEREICH_REPORT_COLUMN_POSITIVE_RUECKMELDUNG));
        vo.setRueckmeldungUnbekannt(_rs
                        .getIntegerNN(Scheme.VIEW_BEREICH_REPORT_DETAIL_UNBEKANNT_RUECKMELDUNG));
        vo.setPersonenInAlarmGesamt(_rs
                        .getIntegerNN(Scheme.VIEW_BEREICH_REPORT_DETAIL_PERSONEN_IN_ALARM_GESAMT));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    private BereichReportStatistikVO[] toVOs(ResultSet _rs,
                    boolean _keepResultSet) throws StdException
    {
        List<BereichReportStatistikVO> al = new ArrayList<BereichReportStatistikVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        BereichReportStatistikVO[] vos = new BereichReportStatistikVO[al.size()];
        al.toArray(vos);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vos;
    }

    /**
     * Liefert die Statistik für den kompletten Alarm zurück. Es werden immer
     * nur die zuerst ausgelösten FBKs zurückgegeben. Die nachfolgenden FBKs
     * sind zusammenaggregiert.
     * 
     * @return
     * @throws StdException
     */
    public BereichReportStatistikVO[] findByAlarmId(AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstFindByAlarmId();
        pst.setAlarmId(1, _alarmId);

        BereichReportStatistikVO[] r = toVOs(pst.executeQuery(), false);
        pst.close();

        return r;
    }

    /**
     * Liefert die Statistik für eine Bereich-/Funktionstraeger-Kombination
     * innerhalb eines Alarms zurück. Es können dabei mehrere Elemente
     * zurückgegeben werden, da eine Schleife nachalarmiert werden kann.
     * 
     * @param _alarmId
     * @param _funktionstraegerId
     * @param _bereichId
     * @return
     */
    public BereichReportStatistikVO[] findByAlarmIdAndFunktionstraegerIdAndBereichId(
                    AlarmId _alarmId, FunktionstraegerId _funktionstraegerId,
                    BereichId _bereichId) throws StdException
    {
        PreparedStatement pst = getPstFindByAlarmIdAndFunktionstraegerIdAndBereichId();
        pst.setAlarmId(1, _alarmId);
        pst.setFunktionstraegerId(2, _funktionstraegerId);
        pst.setBereichId(3, _bereichId);

        BereichReportStatistikVO[] r = toVOs(pst.executeQuery(), false);
        pst.close();

        return r;
    }

    /**
     * Findet einen einzelnen Bereichs-Statistik.
     * 
     * @param _alarmId
     * @param _funktionstraegerId
     * @param _bereichId
     * @param _schleifeId
     * @return
     */
    public BereichReportStatistikVO findByAlarmIdAndFunktionstraegerIdAndBereichIdAndSchleifeId(
                    AlarmId _alarmId, FunktionstraegerId _funktionstraegerId,
                    BereichId _bereichId, SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstFindByAlarmIdAndFunktionstraegerIdAndBereichIdAndSchleifeId();
        pst.setAlarmId(1, _alarmId);
        pst.setFunktionstraegerId(2, _funktionstraegerId);
        pst.setBereichId(3, _bereichId);
        pst.setSchleifeId(4, _schleifeId);

        BereichReportStatistikVO r = nextToVO(pst.executeQuery(), false);
        pst.close();

        return r;
    }

    /*
     * 
     * 
     * 
     * prepared statements
     */
    private PreparedStatement getPstFindByAlarmId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.VIEW_BEREICH_REPORT + " WHERE "
                        + Scheme.VIEW_BEREICH_REPORT_COLUMN_ALARM_ID + " = ?");
    }

    private PreparedStatement getPstFindByAlarmIdAndFunktionstraegerIdAndBereichId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.VIEW_BEREICH_REPORT + " WHERE "
                        + Scheme.VIEW_BEREICH_REPORT_COLUMN_ALARM_ID
                        + " = ? AND "
                        + Scheme.VIEW_BEREICH_REPORT_COLUMN_FUNKTIONSTRAEGER_ID
                        + " =? AND "
                        + Scheme.VIEW_BEREICH_REPORT_COLUMN_BEREICH_ID + "=?");
    }

    private PreparedStatement getPstFindByAlarmIdAndFunktionstraegerIdAndBereichIdAndSchleifeId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.VIEW_BEREICH_REPORT + " WHERE "
                        + Scheme.VIEW_BEREICH_REPORT_COLUMN_ALARM_ID
                        + " = ? AND "
                        + Scheme.VIEW_BEREICH_REPORT_COLUMN_FUNKTIONSTRAEGER_ID
                        + " =? AND "
                        + Scheme.VIEW_BEREICH_REPORT_COLUMN_BEREICH_ID
                        + "=? AND "
                        + Scheme.VIEW_BEREICH_REPORT_COLUMN_HAUPTSCHLEIFE_ID
                        + " =?");
    }
}

package de.ecw.zabos.sql.dao;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.BereichInSchleifeId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * DataAccessObject für {@link Scheme#ALARM_TABLE}
 * 
 * @author bsp, ckl
 * 
 */
public class AlarmDAO extends AbstractBaseDAO
{
    public AlarmDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private AlarmVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public AlarmVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        AlarmVO vo = getObjectFactory().createAlarm();

        vo.setAlarmId(_rs.getAlarmId(Scheme.COLUMN_ID));
        vo.setAlarmZeit(_rs.getUnixTime(Scheme.ALARM_COLUMN_ALARM_ZEIT));
        vo.setEntwarnZeit(_rs.getUnixTime(Scheme.ALARM_COLUMN_ENTWARN_ZEIT));
        vo.setAlarmPersonId(_rs
                        .getPersonId(Scheme.ALARM_COLUMN_ALARM_PERSON_ID));
        vo.setEntwarnPersonId(_rs
                        .getPersonId(Scheme.ALARM_COLUMN_ENTWARN_PERSON_ID));
        vo.setAlarmQuelleId(_rs
                        .getAlarmQuelleId(Scheme.ALARM_COLUMN_ALARM_QUELLE_ID));
        vo.setKommentar(_rs.getString(Scheme.ALARM_COLUMN_KOMMENTAR));
        vo.setAktiv(_rs.getBooleanNN(Scheme.ALARM_COLUMN_AKTIV));
        vo.setReihenfolge(_rs.getIntegerNN(Scheme.ALARM_COLUMN_REIHENFOLGE));
        vo.setGpsKoordinate(_rs.getString(Scheme.ALARM_COLUMN_GPS_KOORDINATE));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    private AlarmVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<AlarmVO> al = new ArrayList<AlarmVO>();

        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }

        AlarmVO[] vos = new AlarmVO[al.size()];
        al.toArray(vos);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vos;
    }

    /*
     * 
     * 
     * queries
     */

    /**
     * Weist eine Schleife einem Alarm zu.
     * 
     */
    public void addSchleifeToAlarm(SchleifeId _schleifeId, AlarmId _alarmId) throws StdException
    {
        long id = dbconnection.nextId();
        PreparedStatement pst = getPstAddSchleifeToAlarm();
        pst.setLong(1, id);
        pst.setSchleifeId(2, _schleifeId);
        pst.setAlarmId(3, _alarmId);
        pst.execute();
        pst.close();
    }

    /**
     * Weist eine Person einem Alarm zu.
     * 
     * @param _personId
     * @param _alarmId
     * @throws StdException
     */
    public void addPersonToAlarm(PersonId _personId, AlarmId _alarmId) throws StdException
    {
        long id = dbconnection.nextId();
        PreparedStatement pst = getPstAddPersonToAlarm();
        pst.setLong(1, id);
        pst.setPersonId(2, _personId);
        pst.setAlarmId(3, _alarmId);
        pst.execute();
        pst.close();
    }

    /**
     * Legt einen neuen Alarm an.
     * 
     * @param _alarmVO
     * @return
     * @throws StdException
     */
    public AlarmVO createAlarm(AlarmVO _alarmVO) throws StdException
    {
        AlarmId id = new AlarmId(dbconnection.nextId());
        PreparedStatement pst = getPstCreateAlarm();
        pst.setAlarmId(1, id);
        pst.setUnixTime(2, _alarmVO.getAlarmZeit());
        pst.setUnixTime(3, _alarmVO.getEntwarnZeit());
        pst.setPersonId(4, _alarmVO.getAlarmPersonId());
        pst.setPersonId(5, _alarmVO.getEntwarnPersonId());
        pst.setAlarmQuelleId(6, _alarmVO.getAlarmQuelleId());
        pst.setString(7, _alarmVO.getKommentar());
        pst.setString(8, _alarmVO.getGpsKoordinate());

        pst.execute();
        AlarmVO r = findAlarmById(id);

        pst.close();
        return r;
    }

    /**
     * Deaktiviert einen Alarm.
     * 
     * @param _alarmId
     * @throws StdException
     */
    public void deaktiviereAlarmById(AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstDeaktiviereAlarmById();
        pst.setAlarmId(1, _alarmId);
        pst.execute();
        pst.close();
    }

    /**
     * Sucht einen Alarm unter Angabe der AlarmId.
     * 
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public AlarmVO findAlarmById(AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstFindAlarmById();
        pst.setAlarmId(1, _alarmId);
        AlarmVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Sucht einen Alarm unter Angabe der Reihenfolge. Die Reihenfolge wird in
     * SMS Nachrichten ("AlarmNr") verschickt.
     * 
     * @param _reihenfolge
     * @return
     * @throws StdException
     */
    public AlarmVO findAlarmByReihenfolge(int _reihenfolge) throws StdException
    {
        PreparedStatement pst = getPstFindAlarmByReihenfolge();
        pst.setIntegerNN(1, _reihenfolge);
        AlarmVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die letzten _n Alarme.
     * 
     */
    public AlarmVO[] findAlarmeByLimit(int _n) throws StdException
    {
        PreparedStatement pst = getPstFindAlarmeByLimit();
        pst.setIntegerNN(1, _n);
        AlarmVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Fügt einen Bereich in einem Alarm hinzu, der gerade aktiv ist
     * 
     * @param _alarmId
     * @param _bereichInSchleifeId
     * @param _aktivierung
     * @param _isAktiv
     */
    public void addBereichInAlarm(AlarmId _alarmId,
                    BereichInSchleifeId _bereichInSchleifeId,
                    UnixTime _aktivierung) throws StdException
    {
        PreparedStatement pst = getPstAddBereichtoAlarm();
        pst.setBaseId(1, dbconnection.nextBaseId());
        pst.setAlarmId(2, _alarmId);
        pst.setBereichInSchleifeId(3, _bereichInSchleifeId);
        pst.setUnixTime(4, _aktivierung);

        pst.execute();
        pst.close();
    }

    /**
     * Liefert den Zeitpunkt zurück, an dem ein Bereich alarmiert wurde
     * 
     * @param _alarmId
     * @param _bereichInSchleifeId
     * @return
     * @throws StdException
     */
    public UnixTime findBereichsAlarmAktivierung(AlarmId _alarmId,
                    BereichInSchleifeId _bereichInSchleifeId) throws StdException
    {
        PreparedStatement pst = getPstFindBereichsAlarmierungAktivierung();
        pst.setAlarmId(1, _alarmId);
        pst.setBereichInSchleifeId(2, _bereichInSchleifeId);

        ResultSet rs = pst.executeQuery();

        UnixTime r = UnixTime.now();

        if (rs.next())
        {
            r = rs.getUnixTime(Scheme.BEREICH_IN_ALARM_COLUMN_AKTIVIERUNG);
        }

        rs.close();
        pst.close();

        return r;
    }

    /**
     * Liefert zurück, ob die Bereichs/Funktionsträger-Kombination innerhalb der
     * Schleife eines Alarms noch aktiv ist
     * 
     * @param _alarmId
     * @param _bereichInSchleifeId
     * @return
     * @throws StdException
     */
    public boolean isBereichInSchleifeInAlarmAktiv(AlarmId _alarmId,
                    BereichInSchleifeId _bereichInSchleifeId) throws StdException
    {
        PreparedStatement pst = getPstIsBereichInSchleifeInAlarmAktiv();
        pst.setAlarmId(1, _alarmId);
        pst.setBereichInSchleifeId(2, _bereichInSchleifeId);

        ResultSet rs = pst.executeQuery();

        boolean r = false;

        if (rs.next())
        {
            if (rs.getLong(1).longValue() > 0)
            {
                r = true;
            }
        }
        rs.close();
        pst.close();

        return r;
    }

    /**
     * Liefert zurück, ob der Bereich innerhalb des Alarms noch aktiv ist. Diese
     * Methode berücksichtigt ***nicht*** die zugehörige Schleife!
     * 
     * @param _alarmId
     * @param _funktionstraegerId
     * @param _bereichId
     * @return
     * @throws StdException
     */
    public boolean isFunktionstraegerBereichInAlarmAktiv(AlarmId _alarmId,
                    FunktionstraegerId _funktionstraegerId, BereichId _bereichId) throws StdException
    {
        PreparedStatement pst = getPstIsFunktionstraegerBereichInAlarmAktiv();
        pst.setAlarmId(1, _alarmId);
        pst.setFunktionstraegerId(2, _funktionstraegerId);
        pst.setBereichId(3, _bereichId);

        ResultSet rs = pst.executeQuery();

        boolean r = false;

        if (rs.next())
        {
            if (rs.getLong(1).longValue() > 0)
            {
                r = true;
            }
        }
        rs.close();
        pst.close();

        return r;

    }

    /**
     * Liefert zurück, ob sich die Schleife in dem Alarm befindet
     * 
     * @param _schleifeId
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public boolean isSchleifeInAlarm(SchleifeId _schleifeId, AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstIsSchleifeInAlarm();
        pst.setSchleifeId(1, _schleifeId);
        pst.setAlarmId(2, _alarmId);

        ResultSet rs = pst.executeQuery();

        boolean r = false;

        if (rs.next())
        {
            if (rs.getLong(1).longValue() > 0)
            {
                r = true;
            }
        }

        rs.close();
        pst.close();

        return r;
    }

    /**
     * Liefert zurück, ob sich die Person innerhalb eines Alarms befindet
     * 
     * @param _personId
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public boolean isPersonInAlarm(PersonId _personId, AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstIsPersonInAlarm();
        pst.setPersonId(1, _personId);
        pst.setAlarmId(2, _alarmId);

        ResultSet rs = pst.executeQuery();

        boolean r = false;

        if (rs.next())
        {
            if (rs.getLong(1).longValue() > 0)
            {
                r = true;
            }
        }

        rs.close();
        pst.close();

        return r;
    }

    /**
     * Deaktiviert einen Bereich innerhalb eines Alarms
     * 
     * @param _alarmId
     * @param _bereichInSchleifeId
     */
    public void deaktiviereBereichInAlarm(AlarmId _alarmId,
                    BereichInSchleifeId _bereichInSchleifeId) throws StdException

    {
        PreparedStatement pst = getPstDeaktiviereBereichInAlarm();
        pst.setAlarmId(1, _alarmId);
        pst.setBereichInSchleifeId(2, _bereichInSchleifeId);
        pst.execute();
        pst.close();
    }

    /**
     * Deaktiviert alle Bereiche innerhalb eines Alarms. Der Alarm bleibt
     * trotzdem weiterhin aktiv!
     * 
     * @param _alarmId
     * @throws StdException
     */
    public void deaktivereAlleBereicheInAlarm(AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstDeaktiveAlleBereicheInAlarm();
        pst.setAlarmId(1, _alarmId);
        pst.execute();
        pst.close();
    }

    /**
     * Liefert die letzten _alarmeProAbfrage Alarme ab Spalte _indexStart.
     * 
     * @param _alarmeProAbfrage
     * @param _indexStart
     * @return
     * @throws StdException
     * @author ckl
     * @since 200627.06.2006_14:07:57
     */
    public AlarmVO[] findAlarmeByOffset(int _alarmeProAbfrage, int _indexStart) throws StdException
    {
        PreparedStatement pst = getPstFindAlarmeByOffset();
        pst.setIntegerNN(1, _alarmeProAbfrage);
        pst.setIntegerNN(2, _indexStart);
        AlarmVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Suchte alle Alarme, die innerhalb des ggb. Zeitfensters ausgelöst wurden
     * 
     */
    public AlarmVO[] findAlarmeByZeitfenster(UnixTime _start, UnixTime _end) throws StdException
    {
        PreparedStatement pst = getPstFindAlarmeByZeitfenster();
        pst.setUnixTime(1, _start);
        pst.setUnixTime(2, _end);
        AlarmVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle aktiven Alarme.
     * 
     * @return
     * @throws StdException
     */
    public AlarmVO[] findAktiveAlarme() throws StdException
    {
        PreparedStatement pst = getPstFindAktiveAlarme();
        AlarmVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Testet ob eine bestimmte Schleife gerad in einem aktiven Alarm
     * referenziert wird.
     * 
     * @param _schleifeId
     * @return
     * @throws StdException
     */
    public boolean isSchleifeAktiv(SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstIsSchleifeAktiv();
        pst.setSchleifeId(1, _schleifeId);
        boolean r = pst.executeQuery().next();

        pst.close();
        return r;
    }

    /**
     * Liefert zurück, ob einer der Bereiche im Alarm nachalarmiert wurde
     * 
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public boolean istAlarmNachalarmiert(AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstIstAlarmNachalarmiert();
        pst.setAlarmId(1, _alarmId);

        ResultSet rs = pst.executeQuery();

        long r = 0;

        if (rs.next())
        {
            r = rs.getLong(1).longValue();
        }

        rs.close();
        pst.close();

        return (r > 1) ? (true) : (false);
    }

    /**
     * Liefert den Timestamp zurück, wann das nächste (späteste) Mal eine
     * Schleife eines Alarms nachalarmiert wird oder aber der Alarm deaktiviert
     * wird.
     * 
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public UnixTime findNachalarmierungsDeaktivierungsZeitpunkt(AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstFindNachalarmierungsDeaktivierungsZeitpunkt();
        pst.setAlarmId(1, _alarmId);

        ResultSet rs = pst.executeQuery();

        long r = 0;

        if (rs.next())
        {
            r = rs.getLong(1).longValue();
        }

        rs.close();
        pst.close();

        return new UnixTime(r);
    }

    /*
     * 
     * 
     * prepared statement getters
     */

    private PreparedStatement getPstAddSchleifeToAlarm() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.SCHLEIFE_IN_ALARM_TABLE + " ("
                        + Scheme.COLUMN_ID + ","
                        + Scheme.SCHLEIFE_IN_ALARM_COLUMN_SCHLEIFE_ID + ","
                        + Scheme.SCHLEIFE_IN_ALARM_COLUMN_ALARM_ID
                        + ") VALUES(?,?,?);");
    }

    private PreparedStatement getPstAddBereichtoAlarm() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.BEREICH_IN_ALARM_TABLE + " ("
                        + Scheme.COLUMN_ID + ","
                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID + ","
                        + Scheme.BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID
                        + "," + Scheme.BEREICH_IN_ALARM_COLUMN_AKTIVIERUNG
                        + ") VALUES(?,?,?,?);");
    }

    private PreparedStatement getPstDeaktiviereBereichInAlarm() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.BEREICH_IN_ALARM_TABLE + " SET "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_AKTIV
                        + "=false WHERE "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID + "=? AND "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID
                        + "= ?;");
    }

    private PreparedStatement getPstAddPersonToAlarm() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.PERSON_IN_ALARM_TABLE + " ("
                        + Scheme.COLUMN_ID + ","
                        + Scheme.PERSON_IN_ALARM_COLUMN_PERSON_ID + ","
                        + Scheme.PERSON_IN_ALARM_COLUMN_ALARM_ID + ","
                        + Scheme.PERSON_IN_ALARM_COLUMN_RUECKMELDUNG_STATUS_ID
                        + "," + Scheme.PERSON_IN_ALARM_COLUMN_IST_ENTWARNT
                        + ") VALUES(?,?,?,NULL,false);");
    }

    private PreparedStatement getPstCreateAlarm() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.ALARM_TABLE + " (" + Scheme.COLUMN_ID + ","
                        + Scheme.ALARM_COLUMN_ALARM_ZEIT + ","
                        + Scheme.ALARM_COLUMN_ENTWARN_ZEIT + ","
                        + Scheme.ALARM_COLUMN_ALARM_PERSON_ID + ","
                        + Scheme.ALARM_COLUMN_ENTWARN_PERSON_ID + ","
                        + Scheme.ALARM_COLUMN_ALARM_QUELLE_ID + ","
                        + Scheme.ALARM_COLUMN_KOMMENTAR + ","
                        + Scheme.ALARM_COLUMN_AKTIV + ","
                        + Scheme.ALARM_COLUMN_GPS_KOORDINATE
                        + ") VALUES(?,?,?,?,?,?,?,true,?);");
    }

    private PreparedStatement getPstDeaktiviereAlarmById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.ALARM_TABLE + " SET "
                        + Scheme.ALARM_COLUMN_AKTIV + "=false WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstDeaktiveAlleBereicheInAlarm() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.BEREICH_IN_ALARM_TABLE + " SET "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_AKTIV
                        + "=false WHERE "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID + "=?;");
    }

    private PreparedStatement getPstFindAlarmById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ALARM_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + "=?;");
    }

    private PreparedStatement getPstFindAlarmByReihenfolge() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ALARM_TABLE + " WHERE "
                        + Scheme.ALARM_COLUMN_REIHENFOLGE + "=?;");
    }

    private PreparedStatement getPstFindAlarmeByLimit() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ALARM_TABLE + " ORDER BY " + Scheme.COLUMN_ID
                        + " DESC LIMIT ?;");
    }

    private PreparedStatement getPstFindAlarmeByOffset() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ALARM_TABLE + " ORDER BY " + Scheme.COLUMN_ID
                        + " DESC LIMIT ? OFFSET ?;");
    }

    private PreparedStatement getPstFindAlarmeByZeitfenster() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ALARM_TABLE + " WHERE "
                        + Scheme.ALARM_COLUMN_ALARM_ZEIT + ">? AND "
                        + Scheme.ALARM_COLUMN_ALARM_ZEIT + "<? ORDER BY "
                        + Scheme.ALARM_COLUMN_REIHENFOLGE + " DESC;");
    }

    private PreparedStatement getPstFindAktiveAlarme() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ALARM_TABLE + " WHERE "
                        + Scheme.ALARM_COLUMN_AKTIV + "=true ORDER BY "
                        + Scheme.ALARM_COLUMN_REIHENFOLGE + " DESC;");
    }

    private PreparedStatement getPstIsSchleifeAktiv() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT sia."
                        + Scheme.COLUMN_ID + " FROM "
                        + Scheme.SCHLEIFE_IN_ALARM_TABLE + " sia, "
                        + Scheme.ALARM_TABLE + " a WHERE a."
                        + Scheme.ALARM_COLUMN_AKTIV + "=true AND a."
                        + Scheme.COLUMN_ID + "=sia."
                        + Scheme.SCHLEIFE_IN_ALARM_COLUMN_ALARM_ID
                        + " AND sia."
                        + Scheme.SCHLEIFE_IN_ALARM_COLUMN_SCHLEIFE_ID + "=?;");
    }

    private PreparedStatement getPstFindBereichsAlarmierungAktivierung() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_AKTIVIERUNG + " FROM "
                        + Scheme.BEREICH_IN_ALARM_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID + "=? AND "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID
                        + "=?");
    }

    private PreparedStatement getPstIsBereichInSchleifeInAlarmAktiv() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT COUNT(*) FROM "
                        + Scheme.BEREICH_IN_ALARM_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_AKTIV + " = true AND "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID + "=? AND "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID
                        + " = ?");
    }

    private PreparedStatement getPstIsSchleifeInAlarm() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT COUNT(*) FROM "
                        + Scheme.SCHLEIFE_IN_ALARM_TABLE + " WHERE "
                        + Scheme.SCHLEIFE_IN_ALARM_COLUMN_SCHLEIFE_ID
                        + "=? AND " + Scheme.SCHLEIFE_IN_ALARM_COLUMN_ALARM_ID
                        + "=?");
    }

    private PreparedStatement getPstIstAlarmNachalarmiert() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT MAX(alarmierte_bereiche) FROM (SELECT COUNT(*) AS alarmierte_bereiche FROM "
                                        + Scheme.BEREICH_IN_ALARM_TABLE
                                        + " AS bia, "
                                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE
                                        + " AS bis WHERE bia."
                                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID
                                        + "=? AND bia."
                                        + Scheme.BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID
                                        + " = bis."
                                        + Scheme.COLUMN_ID
                                        + " GROUP BY bis."
                                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                                        + ", bis."
                                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID
                                        + ") AS v");
    }

    private PreparedStatement getPstFindNachalarmierungsDeaktivierungsZeitpunkt() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT MAX((bia. "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_AKTIVIERUNG + " + (s."
                        + Scheme.SCHLEIFE_COLUMN_RUECKMELDE_INTERVALL
                        + " * 1000))) FROM " + Scheme.SCHLEIFE_TABLE + " s, "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " bis, "
                        + Scheme.BEREICH_IN_ALARM_TABLE + " bia WHERE bia."
                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID + " = ? AND "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_AKTIV
                        + "=true AND bis." + Scheme.COLUMN_ID + " = bia."
                        + Scheme.BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID
                        + " AND s." + Scheme.COLUMN_ID + "="
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + " GROUP BY bia."
                        + Scheme.BEREICH_IN_ALARM_COLUMN_AKTIVIERUNG);
    }

    private PreparedStatement getPstIsFunktionstraegerBereichInAlarmAktiv() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT COUNT(*) FROM "
                        + Scheme.BEREICH_IN_ALARM_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_AKTIV + "=true AND "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_ALARM_ID + "=? AND "
                        + Scheme.BEREICH_IN_ALARM_COLUMN_BEREICH_IN_SCHLEIFE_ID
                        + " IN (SELECT " + Scheme.COLUMN_ID + " FROM "
                        + Scheme.BEREICH_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_FUNKTIONSTRAEGER_ID
                        + "=? AND "
                        + Scheme.BEREICH_IN_SCHLEIFE_COLUMN_BEREICH_ID + " =?)");
    }

    private PreparedStatement getPstIsPersonInAlarm() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT COUNT(*) FROM "
                        + Scheme.PERSON_IN_ALARM_TABLE + " WHERE "
                        + Scheme.PERSON_IN_ALARM_COLUMN_PERSON_ID + "=? AND "
                        + Scheme.PERSON_IN_ALARM_COLUMN_ALARM_ID + "=?");
    }
}

package de.ecw.zabos.sql.dao;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * DataAccessObject für {@link Scheme#PERSON_IN_ALARM_TABLE}
 * 
 * @author bsp
 * 
 */
public class PersonInAlarmDAO extends AbstractBaseDAO
{
    public PersonInAlarmDAO(DBConnection _dbconnection,
                    ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    public PersonInAlarmVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public PersonInAlarmVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<PersonInAlarmVO> al = new ArrayList<PersonInAlarmVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        PersonInAlarmVO[] ret = new PersonInAlarmVO[al.size()];
        al.toArray(ret);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return ret;
    }

    public PersonInAlarmVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        PersonInAlarmVO vo = getObjectFactory().createPersonInAlarm();
        vo.setBaseId(_rs.getBaseId(Scheme.COLUMN_ID));
        vo.setPersonId(_rs.getPersonId(Scheme.PERSON_IN_ALARM_COLUMN_PERSON_ID));
        vo.setAlarmId(_rs.getAlarmId(Scheme.PERSON_IN_ALARM_COLUMN_ALARM_ID));
        vo.setRueckmeldungStatusId(_rs
                        .getRueckmeldungStatusId(Scheme.PERSON_IN_ALARM_COLUMN_RUECKMELDUNG_STATUS_ID));

        vo.setKommentar(_rs.getString(Scheme.PERSON_IN_ALARM_COLUMN_KOMMENTAR));
        vo.setKommentarLeitung(_rs
                        .getString(Scheme.PERSON_IN_ALARM_COLUMN_KOMMENTAR_LEITUNG));

        vo.setEntwarnt(_rs
                        .getBooleanNN(Scheme.PERSON_IN_ALARM_COLUMN_IST_ENTWARNT));
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
     * Liefert alle Personen, die im Rahmen des ggb. Alarms benachrichtigt
     * wurden.
     * 
     */
    public PersonInAlarmVO[] findPersonenInAlarmByAlarmId(AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenInAlarmByAlarmId();
        pst.setAlarmId(1, _alarmId);
        PersonInAlarmVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Findet die Personen innerhalb eines Alarms, die für eine Schleife
     * alarmiert worden. Es werden dabei *alle* Personen berücksichtigt, die
     * dieser Schleife direkt oder einer ihrer ausgelösten Folgeschleifen
     * angehören.
     * 
     * @param _schleifeId
     * @return
     * @throws StdException
     */
    public PersonInAlarmVO[] findPersonenInAlarmByAlarmIdAndSchleifeId(
                    AlarmId _alarmId, SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenInAlarmByAlarmIdAndSchleifeId();
        pst.setAlarmId(1, _alarmId);
        pst.setSchleifeId(2, _schleifeId);
        pst.setSchleifeId(3, _schleifeId);
        pst.setAlarmId(4, _alarmId);
        pst.setInteger(5, 5);
        PersonInAlarmVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Zuweisungen einer Person zu aktiven Alarmen unter Angabe der
     * PersonId.
     * 
     * @param _personId
     * @return
     * @throws StdException
     */
    public PersonInAlarmVO[] findPersonenInAktivemAlarmByPersonId(
                    PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenInAktivemAlarmByPersonId();
        pst.setPersonId(1, _personId);
        PersonInAlarmVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Ändert den Rückmeldungs-Status für eine bestimmte Person in Bezug auf
     * alle *aktiven* Alarme.
     * 
     * @param _personId
     * @param _rmsId
     * @throws StdException
     */
    public void updateRueckmeldungStatus(PersonId _personId,
                    RueckmeldungStatusId _rmsId) throws StdException
    {
        PreparedStatement pst = getPstUpdateRueckmeldeStatus();
        pst.setRueckmeldungStatusId(1, _rmsId);
        pst.setPersonId(2, _personId);
        pst.execute();
        pst.close();
    }

    /**
     * Ändert den Leitungs-Kommentar zu einer Person in Bezug auf alle *aktiven*
     * Alarme.
     * 
     * @param _alarmId
     * @param _personId
     * @param _kommentarLeitung
     */
    public void updateKommentarLeitung(AlarmId _alarmId, PersonId _personId,
                    String _kommentarLeitung) throws StdException
    {
        PreparedStatement pst = getPstUpdateKommentarLeitung();
        pst.setString(1, _kommentarLeitung);
        pst.setAlarmId(2, _alarmId);
        pst.setPersonId(3, _personId);
        pst.execute();
        pst.close();
    }

    /**
     * Markiert, dass eine Person als "Entwart" gekennzeichnet wurde
     * 
     * @param _alarmId
     * @param _personId
     * @throws StdException
     */
    public void markPersonAsEntwarnt(AlarmId _alarmId, PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstUpdatePersonAsEntwarnt();
        pst.setAlarmId(1, _alarmId);
        pst.setPersonId(2, _personId);
        pst.execute();
        pst.close();
    }

    /**
     * Liefert die Anzahl der alarmierten Personen eines Alarms zurück
     * 
     * @return
     */
    public long countAlarmiertePersonenInAlarm(AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstCountAlarmiertePersonenInAlarm();
        pst.setAlarmId(1, _alarmId);
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

    /*
     * 
     * 
     * prepared statements
     */
    private PreparedStatement getPstFindPersonenInAlarmByAlarmId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PERSON_IN_ALARM_TABLE + " WHERE "
                        + Scheme.PERSON_IN_ALARM_COLUMN_ALARM_ID + "=?;");
    }

    private PreparedStatement getPstFindPersonenInAktivemAlarmByPersonId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT pia.* FROM "
                        + Scheme.PERSON_IN_ALARM_TABLE + " pia,"
                        + Scheme.ALARM_TABLE + " a WHERE pia."
                        + Scheme.PERSON_IN_ALARM_COLUMN_PERSON_ID
                        + "=? AND pia."
                        + Scheme.PERSON_IN_ALARM_COLUMN_ALARM_ID + "=a."
                        + Scheme.COLUMN_ID + " AND a."
                        + Scheme.ALARM_COLUMN_AKTIV + "=true;");
    }

    private PreparedStatement getPstUpdateRueckmeldeStatus() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.PERSON_IN_ALARM_TABLE + " SET "
                        + Scheme.PERSON_IN_ALARM_COLUMN_RUECKMELDUNG_STATUS_ID
                        + "=? WHERE " + Scheme.PERSON_IN_ALARM_COLUMN_PERSON_ID
                        + "=? AND " + Scheme.PERSON_IN_ALARM_COLUMN_ALARM_ID
                        + " IN (SELECT a." + Scheme.COLUMN_ID + " FROM "
                        + Scheme.ALARM_TABLE + " a WHERE a."
                        + Scheme.ALARM_COLUMN_AKTIV + "=true);");
    }

    private PreparedStatement getPstFindPersonenInAlarmByAlarmIdAndSchleifeId() throws StdException
    {
        /**
         * DISTINCT um doppelte Einträge aus der
         * Scheme.VIEW_PERSONEN_IN_SCHLEIFEN zu filtern
         */
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT DISTINCT pia.* FROM "
                                        + Scheme.PERSON_IN_ALARM_TABLE
                                        + " pia, "
                                        + Scheme.VIEW_PERSONEN_IN_SCHLEIFEN
                                        + " vpis WHERE pia."
                                        + Scheme.PERSON_IN_ALARM_COLUMN_ALARM_ID
                                        + " = ? "
                                        + " AND "
                                        + Scheme.PERSON_IN_ALARM_COLUMN_PERSON_ID
                                        + " = vpis."
                                        + Scheme.COLUMN_ID
                                        + " AND (vpis."
                                        + Scheme.VIEW_PERSONEN_IN_SCHLEIFEN_COLUMN_SCHLEIFE_ID
                                        + "=? OR "
                                        + Scheme.VIEW_PERSONEN_IN_SCHLEIFEN_COLUMN_SCHLEIFE_ID
                                        + "= ANY("
                                        + Scheme.FUNC_FIND_NACHFOLGENDE_SCHLEIFEN
                                        + "(?,?,?)))");
    }

    private PreparedStatement getPstUpdateKommentarLeitung() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.PERSON_IN_ALARM_TABLE + " SET "
                        + Scheme.PERSON_IN_ALARM_COLUMN_KOMMENTAR_LEITUNG
                        + "=? WHERE " + Scheme.PERSON_IN_ALARM_COLUMN_ALARM_ID
                        + "=? AND " + Scheme.PERSON_IN_ALARM_COLUMN_PERSON_ID
                        + "=?");
    }

    private PreparedStatement getPstCountAlarmiertePersonenInAlarm() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT COUNT(*) FROM "
                        + Scheme.PERSON_IN_ALARM_TABLE + " WHERE "
                        + Scheme.PERSON_IN_ALARM_COLUMN_ALARM_ID + "=?");
    }

    private PreparedStatement getPstUpdatePersonAsEntwarnt() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.PERSON_IN_ALARM_TABLE + " SET "
                        + Scheme.PERSON_IN_ALARM_COLUMN_IST_ENTWARNT
                        + " = true WHERE "
                        + Scheme.PERSON_IN_ALARM_COLUMN_ALARM_ID + " = ? AND "
                        + Scheme.PERSON_IN_ALARM_COLUMN_PERSON_ID + " = ?");
    }
}

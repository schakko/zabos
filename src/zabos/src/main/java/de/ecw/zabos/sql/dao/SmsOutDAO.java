package de.ecw.zabos.sql.dao;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.alarm.daemon.AlarmDaemon;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.service.alarm.IAlarmService;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.SmsOutVO;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.SchleifeId;
import de.ecw.zabos.types.id.SmsOutId;
import de.ecw.zabos.types.id.SmsOutStatusId;
import de.ecw.zabos.types.id.TelefonId;

/**
 * DataAccessObject für {@link Scheme#SMSOUT_TABLE}
 * 
 * @author bsp
 * 
 */
public class SmsOutDAO extends AbstractBaseDAO
{
    public SmsOutDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private SmsOutVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    private SmsOutVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<SmsOutVO> al = new ArrayList<SmsOutVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        SmsOutVO[] vos = new SmsOutVO[al.size()];
        al.toArray(vos);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vos;
    }

    public SmsOutVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        SmsOutVO vo = getObjectFactory().createSmsOut();
        vo.setSmsOutId(_rs.getSmsOutId(Scheme.COLUMN_ID));
        vo.setTelefonId(_rs.getTelefonId(Scheme.SMSOUT_COLUMN_TELEFON_ID));
        vo.setNachricht(_rs.getString(Scheme.SMSOUT_COLUMN_NACHRICHT));
        vo.setZeitpunkt(_rs.getUnixTime(Scheme.SMSOUT_COLUMN_ZEITPUNKT));
        vo.setContext(_rs.getString(Scheme.SMSOUT_COLUMN_CONTEXT));
        vo.setContextAlarm(_rs.getString(Scheme.SMSOUT_COLUMN_CONTEXT_ALARM));
        vo.setContextO(_rs.getString(Scheme.SMSOUT_COLUMN_CONTEXT_O));
        vo.setContextOE(_rs.getString(Scheme.SMSOUT_COLUMN_CONTEXT_OE));
        vo.setFestnetzSms(_rs.getBooleanNN(Scheme.SMSOUT_COLUMN_IST_FESTNETZ));
        vo.setStatusId(_rs.getSmsOutStatusId(Scheme.SMSOUT_COLUMN_STATUS_ID));

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
     * Fügt eine SMS dem Nachrichtenausgang hinzu (queue). Der
     * Nachrichtenausgang wird in Intervallen vom {@link IAlarmService} bzw.
     * {@link AlarmDaemon} überprüft und noch nicht verschickte Nachrichten
     * werden via SMS-Gateway versendet.
     * 
     */
    public SmsOutVO createSmsOut(SmsOutVO _vo) throws StdException
    {
        SmsOutId id = new SmsOutId(dbconnection.nextId());
        PreparedStatement pst = getPstCreateSmsOut();
        pst.setSmsOutId(1, id);
        pst.setTelefonId(2, _vo.getTelefonId());
        pst.setString(3, _vo.getNachricht());
        pst.setUnixTime(4, _vo.getZeitpunkt());
        pst.setSmsOutStatusId(5, _vo.getStatusId());
        pst.setString(6, _vo.getContext());
        pst.setString(7, _vo.getContextAlarm());
        pst.setString(8, _vo.getContextO());
        pst.setString(9, _vo.getContextOE());
        pst.setBoolean(10, _vo.isFestnetzSms());
        pst.execute();
        pst.close();

        return findSmsOutById(id);
    }

    /**
     * Stellt die Relation zwischen einer versandten SMS und einer Alarmschleife
     * her.
     * 
     * @param _smsOutId
     * @param _schleifeId
     * @param _alarmId
     * @throws StdException
     */
    public void addSmsOutToSchleifeInAlarm(SmsOutId _smsOutId,
                    SchleifeId _schleifeId, AlarmId _alarmId) throws StdException
    {
        long id = dbconnection.nextId();
        PreparedStatement pst = getPstAddSmsOutToSchleifeInAlarm();
        pst.setLong(1, id);
        pst.setSchleifeId(2, _schleifeId);
        pst.setAlarmId(3, _alarmId);
        pst.setSmsOutId(4, _smsOutId);
        pst.execute();
        pst.close();
    }

    /**
     * Liefert eine versandte SMS unter Angabe der SmsOutId. Wenn die SMS nicht
     * gefunden wurde dann liefert diese Methode null.
     * 
     * @param _smsOutId
     * @return
     * @throws StdException
     */
    public SmsOutVO findSmsOutById(SmsOutId _smsOutId) throws StdException
    {
        PreparedStatement pst = getPstFindSmsOutById();
        pst.setSmsOutId(1, _smsOutId);
        SmsOutVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle SMS, die an das ggb. (Mobil-)Telefon verschickt wurden. Wenn
     * keine SMS gefunden wurden dann liefert diese Methode ein leeres Array.
     * 
     * @param _telefonId
     * @return
     * @throws StdException
     */
    public SmsOutVO[] findSmsOutByTelefonId(TelefonId _telefonId) throws StdException
    {
        PreparedStatement pst = getPstFindSmsOutByTelefonId();
        pst.setTelefonId(1, _telefonId);
        SmsOutVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle SMS, die für den ggb. Alarm verschickt wurden. Wenn keine
     * SMS gefunden wurden dann liefert diese Methode ein leeres Array.
     * 
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public SmsOutVO[] findSmsOutByAlarmId(AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstFindSmsOutByAlarmId();
        pst.setAlarmId(1, _alarmId);
        SmsOutVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle SMS, die für die ggb. Alarm-Schleife verschickt wurden. Wenn
     * keine SMS gefunden wurden dann liefert diese Methode ein leeres Array.
     * 
     * @param _schleifeId
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public SmsOutVO[] findSmsOutBySchleifeInAlarm(SchleifeId _schleifeId,
                    AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstFindSmsOutBySchleifeInAlarm();
        pst.setAlarmId(1, _alarmId);
        pst.setSchleifeId(2, _schleifeId);
        SmsOutVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle SMS, die bisher nur gequeued aber noch nicht verschickt
     * wurden. Wenn keine SMS gefunden wurden dann liefert diese Methode ein
     * leeres Array.
     * 
     * @return
     * @throws StdException
     */
    public SmsOutVO[] findSmsOutByStatusUnsent() throws StdException
    {
        PreparedStatement pst = getPstFindSmsOutByStatusUnsent();
        SmsOutVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Ändert den Versand-Status für die ggb. SMS.
     * 
     * @param _smsOutId
     * @param _smsOutStatusId
     * @return
     * @throws StdException
     */
    public SmsOutVO updateSmsOutStatus(SmsOutId _smsOutId,
                    SmsOutStatusId _smsOutStatusId) throws StdException
    {
        PreparedStatement pst = getPstUpdateSmsOutStatus();
        pst.setSmsOutStatusId(1, _smsOutStatusId);
        pst.setSmsOutId(2, _smsOutId);
        pst.execute();
        pst.close();

        return findSmsOutById(_smsOutId);
    }

    /*
     * 
     * 
     * prepared statements
     */

    private PreparedStatement getPstAddSmsOutToSchleifeInAlarm() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.SCHLEIFE_IN_SMSOUT_TABLE + " ("
                        + Scheme.COLUMN_ID + ","
                        + Scheme.SCHLEIFE_IN_SMSOUT_COLUMN_SCHLEIFE_IN_ALARM_ID
                        + "," + Scheme.SCHLEIFE_IN_SMSOUT_COLUMN_SMSOUT_ID
                        + ") VALUES(?,(SELECT " + Scheme.COLUMN_ID + " FROM "
                        + Scheme.SCHLEIFE_IN_ALARM_TABLE + " WHERE "
                        + Scheme.SCHLEIFE_IN_ALARM_COLUMN_SCHLEIFE_ID
                        + "=? AND " + Scheme.SCHLEIFE_IN_ALARM_COLUMN_ALARM_ID
                        + "=?),?);");
    }

    private PreparedStatement getPstCreateSmsOut() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.SMSOUT_TABLE + " (" + Scheme.COLUMN_ID + ","
                        + Scheme.SMSOUT_COLUMN_TELEFON_ID + ","
                        + Scheme.SMSOUT_COLUMN_NACHRICHT + ","
                        + Scheme.SMSOUT_COLUMN_ZEITPUNKT + ","
                        + Scheme.SMSOUT_COLUMN_STATUS_ID + ","
                        + Scheme.SMSOUT_COLUMN_CONTEXT + ","
                        + Scheme.SMSOUT_COLUMN_CONTEXT_ALARM + ","
                        + Scheme.SMSOUT_COLUMN_CONTEXT_O + ","
                        + Scheme.SMSOUT_COLUMN_CONTEXT_OE + ","
                        + Scheme.SMSOUT_COLUMN_IST_FESTNETZ
                        + ") VALUES(?,?,?,?,?,?,?,?,?,?);");
    }

    private PreparedStatement getPstFindSmsOutById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SMSOUT_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + "=?;");
    }

    private PreparedStatement getPstFindSmsOutByAlarmId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT sms.* FROM "
                        + Scheme.SMSOUT_TABLE + " sms,"
                        + Scheme.SCHLEIFE_IN_SMSOUT_TABLE + " sis,"
                        + Scheme.SCHLEIFE_IN_ALARM_TABLE + " sia WHERE sms."
                        + Scheme.COLUMN_ID + "=sis."
                        + Scheme.SCHLEIFE_IN_SMSOUT_COLUMN_SMSOUT_ID + " AND "
                        + Scheme.SCHLEIFE_IN_SMSOUT_COLUMN_SCHLEIFE_IN_ALARM_ID
                        + "=sia." + Scheme.COLUMN_ID + " AND sia."
                        + Scheme.SCHLEIFE_IN_ALARM_COLUMN_ALARM_ID + "=?;");
    }

    private PreparedStatement getPstFindSmsOutBySchleifeInAlarm() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT sms.* FROM "
                        + Scheme.SMSOUT_TABLE + " sms,"
                        + Scheme.SCHLEIFE_IN_SMSOUT_TABLE + " sis,"
                        + Scheme.SCHLEIFE_IN_ALARM_TABLE + " sia WHERE sms."
                        + Scheme.COLUMN_ID + "=sis."
                        + Scheme.SCHLEIFE_IN_SMSOUT_COLUMN_SMSOUT_ID + " AND "
                        + Scheme.SCHLEIFE_IN_SMSOUT_COLUMN_SCHLEIFE_IN_ALARM_ID
                        + "=sia." + Scheme.COLUMN_ID + " AND sia."
                        + Scheme.SCHLEIFE_IN_ALARM_COLUMN_ALARM_ID + "=?"
                        + " AND sia."
                        + Scheme.SCHLEIFE_IN_ALARM_COLUMN_SCHLEIFE_ID + "=?;");
    }

    private PreparedStatement getPstFindSmsOutByStatusUnsent() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SMSOUT_TABLE + " WHERE "
                        + Scheme.SMSOUT_COLUMN_STATUS_ID + "=0;");
    }

    private PreparedStatement getPstFindSmsOutByTelefonId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SMSOUT_TABLE + " WHERE "
                        + Scheme.SMSOUT_COLUMN_TELEFON_ID + "=?;");
    }

    private PreparedStatement getPstUpdateSmsOutStatus() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.SMSOUT_TABLE + " SET "
                        + Scheme.SMSOUT_COLUMN_STATUS_ID + "=? WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

}

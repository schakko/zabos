package de.ecw.zabos.sql.dao;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.SystemKonfigurationMc35VO;
import de.ecw.zabos.sql.vo.SystemKonfigurationVO;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.BaseId;

/**
 * DataAccessObject für {@link Scheme#SYSTEM_KONFIGURATION_TABLE} und
 * {@link Scheme#SYSTEM_KONFIGURATION_MC35_TABLE}
 * 
 * @author bsp
 * 
 */
public class SystemKonfigurationDAO extends AbstractBaseDAO
{
    public SystemKonfigurationDAO(DBConnection _dbconnection,
                    ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private SystemKonfigurationVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public SystemKonfigurationVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        SystemKonfigurationVO vo = getObjectFactory()
                        .createSystemKonfiguration();

        vo.setBaseId(new BaseId(_rs.getLong(Scheme.COLUMN_ID).longValue()));
        vo.setAlarmTimeout(_rs.getLong(
                        Scheme.SYSTEM_KONFIGURATION_COLUMN_ALARM_TIMEOUT)
                        .longValue());
        vo.setCom5Ton(_rs
                        .getInteger(Scheme.SYSTEM_KONFIGURATION_COLUMN_COM_5TON));
        vo.setReaktivierungTimeout(_rs
                        .getIntegerNN(Scheme.SYSTEM_KONFIGURATION_COLUMN_REAKTIVIERUNG_TIMEOUT));
        vo.setSmsInTimeout(_rs
                        .getIntegerNN(Scheme.SYSTEM_KONFIGURATION_COLUMN_SMSIN_TIMEOUT));
        vo.setAlarmHistorieLaenge(_rs
                        .getIntegerNN(Scheme.SYSTEM_KONFIGURATION_COLUMN_ALARMHISTORIE_LAENGE));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    private SystemKonfigurationMc35VO[] nextToMc35VOs(ResultSet _rs,
                    boolean _keepResultSet) throws StdException
    {
        List<SystemKonfigurationMc35VO> al = new ArrayList<SystemKonfigurationMc35VO>();
        while (_rs.next())
        {
            al.add(toMc35VO(_rs));
        }
        SystemKonfigurationMc35VO[] vos = new SystemKonfigurationMc35VO[al
                        .size()];
        al.toArray(vos);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vos;
    }

    private SystemKonfigurationMc35VO nextToMc35VO(ResultSet _rs) throws StdException
    {
        if (_rs.next())
        {
            return toMc35VO(_rs);
        }
        else
        {
            return null;
        }
    }

    private SystemKonfigurationMc35VO toMc35VO(ResultSet _rs) throws StdException
    {
        SystemKonfigurationMc35VO vo = getObjectFactory()
                        .createSystemKonfigurationMc35();

        vo.setBaseId(new BaseId(_rs.getLong(Scheme.COLUMN_ID).longValue()));
        vo.setComPort(_rs.getInteger(
                        Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_COM_PORT)
                        .intValue());
        vo.setPin1(_rs.getString(Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_PIN1));
        vo.setRufnummer(_rs
                        .getTelefonNummer(Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_RUFNUMMER));
        vo.setAlarmModem(_rs
                        .getBooleanNN(Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_ALARM_MODEM));
        vo.setZeitpunktLetzterSmsSelbsttest(_rs
                        .getUnixTime(Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_ZEITPUNKT_LETZTER_SMS_SELBSTTEST));

        return vo;
    }

    /*
     * 
     * 
     * queries
     */

    /**
     * Legt eine neue MC35 Konfiguration an.
     * 
     */
    public SystemKonfigurationMc35VO createSystemKonfigurationMc35(
                    SystemKonfigurationMc35VO _vo) throws StdException
    {
        BaseId id = dbconnection.nextBaseId();
        PreparedStatement pst = getPstCreateKonfigurationMc35();
        pst.setBaseId(1, id);
        pst.setBoolean(2, _vo.getAlarmModem());
        pst.setIntegerNN(3, _vo.getComPort());
        pst.setString(4, _vo.getPin1());
        pst.setTelefonNummer(5, _vo.getRufnummer());
        pst.setUnixTime(6, _vo.getZeitpunktLetzterSmsSelbsttest());
        pst.execute();

        pst.close();
        return findKonfigurationMc35ById(id);
    }

    /**
     * Ändert die Konfiguration eines MC35-Modems
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public SystemKonfigurationMc35VO updateKonfigurationMc35(
                    SystemKonfigurationMc35VO _vo) throws StdException
    {
        PreparedStatement pst = getPstUpdateKonfigurationMc35();
        pst.setBoolean(1, _vo.getAlarmModem());
        pst.setIntegerNN(2, _vo.getComPort());
        pst.setString(3, _vo.getPin1());
        pst.setTelefonNummer(4, _vo.getRufnummer());
        pst.setUnixTime(5, _vo.getZeitpunktLetzterSmsSelbsttest());
        pst.setBaseId(6, _vo.getBaseId());
        pst.execute();
        pst.close();

        return findKonfigurationMc35ById(_vo.getBaseId());
    }

    /**
     * Löscht eine MC35 Konfiguration.
     * 
     * @throws StdException
     */
    public void deleteKonfigurationMc35(SystemKonfigurationMc35VO _vo) throws StdException
    {
        PreparedStatement pst = getPstDeleteKonfigurationMc35();
        pst.setBaseId(1, _vo.getBaseId());
        pst.execute();
        pst.close();
    }

    /**
     * Liefert die MC35 Konfiguration mit der ggb. Id. Wenn keine Konfiguration
     * gefunden wurde liefert diese Methode null.
     * 
     * @param _id
     * @return
     * @throws StdException
     */
    public SystemKonfigurationMc35VO findKonfigurationMc35ById(BaseId _id) throws StdException
    {
        PreparedStatement pst = getPstFindKonfigurationMc35ById();
        pst.setBaseId(1, _id);
        SystemKonfigurationMc35VO r = nextToMc35VO(pst.executeQuery());

        pst.close();
        return r;
    }

    /**
     * Testet ob das System zur Zeit deaktiviert ist. Die Deaktivierung betrifft
     * *nur* die Auswertung von empfangenen 5Tonfolgen.
     * 
     * @return
     * @throws StdException
     */
    public boolean istSystemDeaktiviert() throws StdException
    {
        UnixTime t = getSystemReaktivierungsZeitpunkt();

        if (t != null)
        {
            UnixTime now = UnixTime.now();
            return t.isLaterThan(now);
        }

        return false;
    }

    /**
     * Liefert den Zeitpunkt zu dem das System automatisch wieder reaktiviert
     * wird. Null bedeutet dass das System zur Zeit aktiv ist.
     * 
     * @return
     * @throws StdException
     */
    public UnixTime getSystemReaktivierungsZeitpunkt() throws StdException
    {
        PreparedStatement pst = getPstGetSystemReaktivierungsZeitpunkt();
        ResultSet rs = pst.executeQuery();
        rs.next();
        UnixTime r = rs.getUnixTime(Scheme.SYSTEM_KONFIGURATION_COLUMN_REAKTIVIERUNG_ZEITPUNKT);

        rs.close();
        pst.close();
        return r;
    }

    /**
     * Setzt den Zeitpunkt zu dem das System automatisch wieder reaktiviert
     * wird. Null schaltet das System aktiv.
     * 
     * @param _t
     * @throws StdException
     */
    public void setSystemReaktivierungsZeitpunkt(UnixTime _t) throws StdException
    {
        PreparedStatement pst = getPstSetSystemReaktivierungsZeitpunkt();
        pst.setUnixTime(1, _t);
        pst.execute();
        pst.close();
    }

    /**
     * Liefert die Systemkonfiguration. Die Konfiguration wird beim Aufruf von
     * Globals.init() ausgelesen und in einer statischen Variable hinterlegt
     * (Global.getSystemKonfiguration()).
     * 
     * @return
     * @throws StdException
     */
    public SystemKonfigurationVO readKonfiguration() throws StdException
    {
        PreparedStatement pst = getPstReadKonfiguration();

        SystemKonfigurationVO vo = nextToVO(pst.executeQuery(), false);

        if (vo == null)
        {
            throw new StdException(
                            "Die System-Konfiguration konnte nicht geladen werden. Ist die Tabelle \""
                                            + Scheme.SYSTEM_KONFIGURATION_TABLE
                                            + "\" gefuellt?");
        }
        pst.close();

        return vo;
    }

    /**
     * Liefert alle MC35-Modems zurück
     * 
     * @return
     * @throws StdException
     */
    public SystemKonfigurationMc35VO[] findAllMC35() throws StdException
    {
        PreparedStatement pst = getPstReadKonfigurationMc35();

        SystemKonfigurationMc35VO[] r = nextToMc35VOs(pst.executeQuery(), false);
        pst.close();

        return r;
    }

    /**
     * Ändert die Systemkonfiguration.
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public SystemKonfigurationVO updateKonfiguration(SystemKonfigurationVO _vo) throws StdException
    {
        PreparedStatement pst = getPstUpdateKonfiguration();
        pst.setLong(1, _vo.getAlarmTimeout());
        pst.setInteger(2, _vo.getCom5Ton());
        pst.setIntegerNN(3, _vo.getReaktivierungTimeout());
        pst.setIntegerNN(4, _vo.getSmsInTimeout());
        pst.setIntegerNN(5, _vo.getAlarmHistorieLaenge());
        pst.execute();
        pst.close();

        return readKonfiguration();
    }

    /*
     * 
     * 
     * prepared statements
     */
    private PreparedStatement getPstCreateKonfigurationMc35() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "INSERT INTO "
                                        + Scheme.SYSTEM_KONFIGURATION_MC35_TABLE
                                        + " ("
                                        + Scheme.COLUMN_ID
                                        + ","
                                        + Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_ALARM_MODEM
                                        + ","
                                        + Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_COM_PORT
                                        + ","
                                        + Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_PIN1
                                        + ","
                                        + Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_RUFNUMMER
                                        + ","
                                        + Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_ZEITPUNKT_LETZTER_SMS_SELBSTTEST
                                        + ") VALUES(?,?,?,?,?,?);");
    }

    private PreparedStatement getPstUpdateKonfigurationMc35() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "UPDATE "
                                        + Scheme.SYSTEM_KONFIGURATION_MC35_TABLE
                                        + " SET "
                                        + Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_ALARM_MODEM
                                        + " = ?, "
                                        + Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_COM_PORT
                                        + " = ?, "
                                        + Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_PIN1
                                        + " = ?, "
                                        + Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_RUFNUMMER
                                        + " = ?, "
                                        + Scheme.SYSTEM_KONFIGURATION_MC35_COLUMN_ZEITPUNKT_LETZTER_SMS_SELBSTTEST
                                        + " = ? WHERE " + Scheme.COLUMN_ID
                                        + " = ?;");
    }

    private PreparedStatement getPstDeleteKonfigurationMc35() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.SYSTEM_KONFIGURATION_MC35_TABLE + " WHERE "
                        + Scheme.COLUMN_ID + " = ?;");
    }

    private PreparedStatement getPstGetSystemReaktivierungsZeitpunkt() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT "
                                        + Scheme.SYSTEM_KONFIGURATION_COLUMN_REAKTIVIERUNG_ZEITPUNKT
                                        + " FROM "
                                        + Scheme.SYSTEM_KONFIGURATION_TABLE
                                        + ";");
    }

    private PreparedStatement getPstSetSystemReaktivierungsZeitpunkt() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "UPDATE "
                                        + Scheme.SYSTEM_KONFIGURATION_TABLE
                                        + " SET "
                                        + Scheme.SYSTEM_KONFIGURATION_COLUMN_REAKTIVIERUNG_ZEITPUNKT
                                        + "=?;");
    }

    private PreparedStatement getPstReadKonfiguration() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SYSTEM_KONFIGURATION_TABLE + ";");
    }

    private PreparedStatement getPstFindKonfigurationMc35ById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SYSTEM_KONFIGURATION_MC35_TABLE + " WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstReadKonfigurationMc35() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SYSTEM_KONFIGURATION_MC35_TABLE + ";");
    }

    private PreparedStatement getPstUpdateKonfiguration() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "UPDATE "
                                        + Scheme.SYSTEM_KONFIGURATION_TABLE
                                        + " SET "
                                        + Scheme.SYSTEM_KONFIGURATION_COLUMN_ALARM_TIMEOUT
                                        + "=?,"
                                        + Scheme.SYSTEM_KONFIGURATION_COLUMN_COM_5TON
                                        + "=?,"
                                        + Scheme.SYSTEM_KONFIGURATION_COLUMN_REAKTIVIERUNG_TIMEOUT
                                        + "=?,"
                                        + Scheme.SYSTEM_KONFIGURATION_COLUMN_SMSIN_TIMEOUT
                                        + "=?,"
                                        + Scheme.SYSTEM_KONFIGURATION_COLUMN_ALARMHISTORIE_LAENGE
                                        + "=?;");
    }

}

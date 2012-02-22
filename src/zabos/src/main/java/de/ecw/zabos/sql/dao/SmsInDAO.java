package de.ecw.zabos.sql.dao;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.SmsInVO;
import de.ecw.zabos.types.id.SmsInId;

/**
 * DataAccessObject für {@link Scheme#SMSIN_TABLE}
 * 
 * @author bsp
 * 
 */
public class SmsInDAO extends AbstractBaseDAO
{
    public SmsInDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private SmsInVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<SmsInVO> al = new ArrayList<SmsInVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        SmsInVO[] vos = new SmsInVO[al.size()];
        al.toArray(vos);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vos;
    }

    private SmsInVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public SmsInVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        SmsInVO vo = getObjectFactory().createSmsIn();
        vo.setSmsInId(_rs.getSmsInId(Scheme.COLUMN_ID));
        vo.setRufnummer(_rs.getTelefonNummer(Scheme.SMSIN_COLUMN_RUFNUMMER));
        vo.setModemRufnummer(_rs
                        .getTelefonNummer(Scheme.SMSIN_COLUMN_MODEM_RUFNUMMER));
        vo.setNachricht(_rs.getString(Scheme.SMSIN_COLUMN_NACHRICHT));
        vo.setZeitpunkt(_rs.getUnixTime(Scheme.SMSIN_COLUMN_ZEITPUNKT));

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
     * Legt eine empfangene SMS in der Datenbank ab.
     * 
     */
    public SmsInVO createSmsIn(SmsInVO _vo) throws StdException
    {
        SmsInId id = new SmsInId(dbconnection.nextId());
        PreparedStatement pst = getPstCreateSmsIn();
        pst.setSmsInId(1, id);
        pst.setTelefonNummer(2, _vo.getRufnummer());
        pst.setTelefonNummer(3, _vo.getModemRufnummer());
        pst.setString(4, _vo.getNachricht());
        pst.setUnixTime(5, _vo.getZeitpunkt());
        pst.execute();
        pst.close();

        return findSmsInById(id);
    }

    /**
     * Liefert die SMS mit der ggb. Id. Wenn der Datensatz nicht gefunden wurde
     * liefert diese Methode null.
     * 
     * @param _smsInId
     * @return
     * @throws StdException
     */
    public SmsInVO findSmsInById(SmsInId _smsInId) throws StdException
    {
        PreparedStatement pst = getPstFindSmsInById();
        pst.setSmsInId(1, _smsInId);
        SmsInVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle ungelesenen SMS Nachrichten. Wenn keine ungelesen SMS
     * Nachrichten gefunden wurden dann liefert diese Methode ein leeres Array.
     * 
     * @return
     * @throws StdException
     */
    public SmsInVO[] findUngeleseneSmsIn() throws StdException
    {
        PreparedStatement pst = getPstFindUngeleseneSmsIn();
        SmsInVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Kennzeichnet eine SMS als gelesen.
     * 
     * @param _smsInId
     * @throws StdException
     */
    public void kennzeichneAlsGelesenBySmsInId(SmsInId _smsInId) throws StdException
    {
        PreparedStatement pst = getPstKennzeichneAlsGelesenBySmsInId();
        pst.setSmsInId(1, _smsInId);
        pst.execute();
        pst.close();
    }

    /*
     * 
     * 
     * prepared statements
     */
    private PreparedStatement getPstCreateSmsIn() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.SMSIN_TABLE + " (" + Scheme.COLUMN_ID + ","
                        + Scheme.SMSIN_COLUMN_RUFNUMMER + ","
                        + Scheme.SMSIN_COLUMN_MODEM_RUFNUMMER + ","
                        + Scheme.SMSIN_COLUMN_NACHRICHT + ","
                        + Scheme.SMSIN_COLUMN_ZEITPUNKT
                        + ") VALUES(?,?,?,?,?);");
    }

    private PreparedStatement getPstFindSmsInById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SMSIN_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + "=?;");
    }

    private PreparedStatement getPstFindUngeleseneSmsIn() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SMSIN_TABLE + " WHERE "
                        + Scheme.SMSIN_COLUMN_GELESEN + "=false;");
    }

    private PreparedStatement getPstKennzeichneAlsGelesenBySmsInId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.SMSIN_TABLE + " SET "
                        + Scheme.SMSIN_COLUMN_GELESEN + "=true WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }
}

package de.ecw.zabos.sql.dao;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.AlarmQuelleVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.types.id.AlarmQuelleId;

/**
 * DataAccessObject für {@link Scheme#ALARM_QUELLE_TABLE}
 * 
 * Hinweis: Die Alarmquellen sind Stammdaten und in der Klasse AlarmQuelleId als
 * Konstanten definiert (ID_SMS, ID_5TON, ID_WEB).
 * 
 * @author bsp,ckl
 * 
 */
public class AlarmQuelleDAO extends AbstractBaseDAO
{
    public AlarmQuelleDAO(DBConnection _dbconnection,
                    ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private AlarmQuelleVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public AlarmQuelleVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        AlarmQuelleVO vo = getObjectFactory().createAlarmQuelle();

        vo.setAlarmQuelleId(_rs.getAlarmQuelleId(Scheme.COLUMN_ID));
        vo.setName(_rs.getString(Scheme.ALARM_QUELLE_COLUMN_NAME));

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
     * Liefert eine AlarmQuelle unter Angabe der AlarmQuelleId.
     * 
     */
    public AlarmQuelleVO findAlarmQuelleById(AlarmQuelleId _alarmQuelleId) throws StdException
    {
        PreparedStatement pst = getPstFindAlarmQuelleById();
        pst.setAlarmQuelleId(1, _alarmQuelleId);
        AlarmQuelleVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /*
     * 
     * 
     * prepared statement getters
     */
    private PreparedStatement getPstFindAlarmQuelleById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ALARM_QUELLE_TABLE + " WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

}

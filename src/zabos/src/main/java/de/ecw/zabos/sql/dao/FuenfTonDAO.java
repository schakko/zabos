package de.ecw.zabos.sql.dao;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.FuenfTonVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.types.id.FuenfTonId;

/**
 * DataAccessObject für {@link Scheme#FUENFTON_TABLE}
 * 
 * @author bsp, ckl
 * 
 */
public class FuenfTonDAO extends AbstractBaseDAO
{
    public FuenfTonDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private FuenfTonVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public FuenfTonVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        FuenfTonVO vo = getObjectFactory().createFuenfTon();
        vo.setFuenfTonId(_rs.getFuenfTonId(Scheme.COLUMN_ID));
        vo.setFolge(_rs.getString(Scheme.FUENFTON_COLUMN_FOLGE));
        vo.setZeitpunkt(_rs.getUnixTime(Scheme.FUENFTON_COLUMN_ZEITPUNKT));

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
     * Legt einen empfangenen FuenfTon in der Datenbank ab.
     * 
     */
    public FuenfTonVO createFuenfTon(FuenfTonVO _fuenfTonVO) throws StdException
    {
        FuenfTonId id = new FuenfTonId(dbconnection.nextId());
        PreparedStatement pst = getPstCreateFuenfTon();
        pst.setFuenfTonId(1, id);
        pst.setString(2, _fuenfTonVO.getFolge());
        pst.setUnixTime(3, _fuenfTonVO.getZeitpunkt());
        pst.execute();
        pst.close();

        return findFuenfTonById(id);
    }

    /**
     * Sucht einen empfangenen FuenfTon unter Angabe der FuenfTonId.
     * 
     * @param _fuenfTonId
     * @return
     * @throws StdException
     */
    public FuenfTonVO findFuenfTonById(FuenfTonId _fuenfTonId) throws StdException
    {
        PreparedStatement pst = getPstFindFuenfTonById();
        pst.setFuenfTonId(1, _fuenfTonId);
        FuenfTonVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Findet den letzten Eintrag in der Datenbank, der zu dieser Fünfton-Folge
     * gehört
     * 
     * @param _fuenfton
     * @return
     * @throws StdException
     */
    public FuenfTonVO findLatestFuenfTon(String _fuenfton) throws StdException
    {
        PreparedStatement pst = getPstFindLatestFuenfTon();
        pst.setString(1, _fuenfton);
        FuenfTonVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /*
     * 
     * prepared statements
     */

    private PreparedStatement getPstCreateFuenfTon() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.FUENFTON_TABLE + " (" + Scheme.COLUMN_ID + ","
                        + Scheme.FUENFTON_COLUMN_FOLGE + ","
                        + Scheme.FUENFTON_COLUMN_ZEITPUNKT + ") VALUES(?,?,?);");
    }

    private PreparedStatement getPstFindFuenfTonById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.FUENFTON_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + "=?;");
    }

    private PreparedStatement getPstFindLatestFuenfTon() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.FUENFTON_TABLE + " WHERE "
                        + Scheme.FUENFTON_COLUMN_FOLGE + " = ? ORDER BY "
                        + Scheme.FUENFTON_COLUMN_ZEITPUNKT + " DESC LIMIT 1");
    }

}

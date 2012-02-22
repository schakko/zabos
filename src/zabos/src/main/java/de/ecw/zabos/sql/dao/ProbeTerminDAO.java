package de.ecw.zabos.sql.dao;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.ProbeTerminVO;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.ProbeTerminId;

/**
 * DAO für {@value Scheme#PROBE_TERMIN_TABLE}
 * 
 * @author ckl
 * 
 */
public class ProbeTerminDAO extends AbstractBaseDAO
{
    public ProbeTerminDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private ProbeTerminVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public ProbeTerminVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        ProbeTerminVO vo = getObjectFactory().createProbeTermin();
        vo.setProbeTerminId(_rs.getProbeTerminId(Scheme.COLUMN_ID));
        vo
                        .setOrganisationsEinheitId(_rs
                                        .getOrganisationsEinheitId(Scheme.PROBE_TERMIN_COLUMN_ORGANISATIONSEINHEIT_ID));
        vo.setStart(_rs.getUnixTime(Scheme.PROBE_TERMIN_COLUMN_START));
        vo.setEnde(_rs.getUnixTime(Scheme.PROBE_TERMIN_COLUMN_ENDE));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    private ProbeTerminVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<ProbeTerminVO> al = new ArrayList<ProbeTerminVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        ProbeTerminVO[] r = new ProbeTerminVO[al.size()];
        al.toArray(r);

        if (!_keepResultSet)
        {
            _rs.close();
        }
        return r;
    }

    /*
     * 
     * 
     * queries
     */

    /**
     * Legt einen neuen Probealarm-Termin an.
     * 
     */
    public ProbeTerminVO createProbeTermin(ProbeTerminVO _vo) throws StdException
    {
        ProbeTerminId id = new ProbeTerminId(getDBConnection().nextId());
        PreparedStatement pst = getPstCreateProbeTermin();
        pst.setProbeTerminId(1, id);
        pst.setUnixTime(2, _vo.getStart());
        pst.setUnixTime(3, _vo.getEnde());
        pst.setOrganisationsEinheitId(4, _vo.getOrganisationsEinheitId());
        pst.execute();
        pst.close();

        return findProbeTerminById(id);
    }

    /**
     * Ändert einen Probealarm-Termin.
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public ProbeTerminVO updateProbeTermin(ProbeTerminVO _vo) throws StdException
    {
        PreparedStatement pst = getPstUpdateProbeTermin();
        pst.setUnixTime(1, _vo.getStart());
        pst.setUnixTime(2, _vo.getEnde());
        pst.setOrganisationsEinheitId(3, _vo.getOrganisationsEinheitId());
        pst.setProbeTerminId(4, _vo.getProbeTerminId());
        pst.execute();
        pst.close();

        return findProbeTerminById(_vo.getProbeTerminId());
    }

    /**
     * Löscht einen Probealarm-Termin unter Angabe der ProbeTerminId.
     * 
     * @param _id
     * @throws StdException
     */
    public void deleteProbeTerminById(ProbeTerminId _id) throws StdException
    {
        PreparedStatement pst = getPstDeleteProbeTerminById();
        pst.setProbeTerminId(1, _id);
        pst.execute();
        pst.close();
    }

    /**
     * Liefert einen Probealarm-Termin unter Angabe der ProbeTerminId. Wenn der
     * Probealarm-Termin nicht gefunden wurde dann liefert diese Methode null.
     * 
     * @param _id
     * @return
     * @throws StdException
     */
    public ProbeTerminVO findProbeTerminById(ProbeTerminId _id) throws StdException
    {
        PreparedStatement pst = getPstFindProbeTerminById();
        pst.setProbeTerminId(1, _id);
        ProbeTerminVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Probealarm-Termine einer Organisationseinheit. Wenn keine
     * Probealarm-Termine gefunden wurden dann liefert diese Methode ein leeres
     * Array.
     * 
     * @param _id
     * @return
     * @throws StdException
     */
    public ProbeTerminVO[] findProbeTermineByOrganisationsEinheitId(
                    OrganisationsEinheitId _id) throws StdException
    {
        PreparedStatement pst = getPstFindProbeTermineByOrganisationsEinheitId();
        pst.setOrganisationsEinheitId(1, _id);
        ProbeTerminVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Probealarm-Termine *aller* Organisationseinheiten für ein
     * ggb. Zeitfenster. Wenn keine Probealarm-Termine gefunden wurden dann
     * liefert diese Methode ein leeres Array.
     * 
     * @param _start
     * @param _ende
     * @return
     * @throws StdException
     */
    public ProbeTerminVO[] findProbeTermineByZeitfenster(UnixTime _start,
                    UnixTime _ende) throws StdException
    {
        PreparedStatement pst = getPstFindProbeTermineByZeitfenster();
        pst.setUnixTime(1, _start);
        pst.setUnixTime(2, _ende);
        ProbeTerminVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;

    }

    /**
     * Liefert alle Probealarm-Termine einer Organisationseinheit für ein ggb.
     * Zeitfenster. Wenn keine Probealarm-Termine gefunden wurden dann liefert
     * diese Methode ein leeres Array.
     * 
     */
    public ProbeTerminVO[] findProbeTermineByZeitfensterAndOrganisationsEinheitId(
                    UnixTime _start, UnixTime _ende,
                    OrganisationsEinheitId _orgEinheitId) throws StdException
    {
        PreparedStatement pst = getPstFindProbeTermineByZeitfensterAndOrganisationsEinheitId();
        pst.setUnixTime(1, _start);
        pst.setUnixTime(2, _ende);
        pst.setOrganisationsEinheitId(3, _orgEinheitId);
        ProbeTerminVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /*
     * 
     * 
     * prepared statements
     */
    private PreparedStatement getPstCreateProbeTermin() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.PROBE_TERMIN_TABLE + " (" + Scheme.COLUMN_ID
                        + "," + Scheme.PROBE_TERMIN_COLUMN_START + ","
                        + Scheme.PROBE_TERMIN_COLUMN_ENDE + ","
                        + Scheme.PROBE_TERMIN_COLUMN_ORGANISATIONSEINHEIT_ID
                        + ") VALUES(?,?,?,?);");
    }

    private PreparedStatement getPstUpdateProbeTermin() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.PROBE_TERMIN_TABLE + " SET "
                        + Scheme.PROBE_TERMIN_COLUMN_START + "=?,"
                        + Scheme.PROBE_TERMIN_COLUMN_ENDE + "=?,"
                        + Scheme.PROBE_TERMIN_COLUMN_ORGANISATIONSEINHEIT_ID
                        + "=? WHERE " + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstDeleteProbeTerminById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.PROBE_TERMIN_TABLE + " WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstFindProbeTerminById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PROBE_TERMIN_TABLE + " WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstFindProbeTermineByOrganisationsEinheitId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PROBE_TERMIN_TABLE + " WHERE "
                        + Scheme.PROBE_TERMIN_COLUMN_ORGANISATIONSEINHEIT_ID
                        + "=? " + "ORDER BY "
                        + Scheme.PROBE_TERMIN_COLUMN_START);
    }

    private PreparedStatement getPstFindProbeTermineByZeitfenster() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PROBE_TERMIN_TABLE + " WHERE "
                        + Scheme.PROBE_TERMIN_COLUMN_START + ">=? AND "
                        + Scheme.PROBE_TERMIN_COLUMN_ENDE + "<=? "
                        + " ORDER BY " + Scheme.PROBE_TERMIN_COLUMN_START + ";");
    }

    private PreparedStatement getPstFindProbeTermineByZeitfensterAndOrganisationsEinheitId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PROBE_TERMIN_TABLE + " WHERE ("
                        + Scheme.PROBE_TERMIN_COLUMN_START + ">=? AND "
                        + Scheme.PROBE_TERMIN_COLUMN_ENDE + "<=?) AND "
                        + Scheme.PROBE_TERMIN_COLUMN_ORGANISATIONSEINHEIT_ID
                        + "=?" + " ORDER BY "
                        + Scheme.PROBE_TERMIN_COLUMN_START + ";");
    }

}

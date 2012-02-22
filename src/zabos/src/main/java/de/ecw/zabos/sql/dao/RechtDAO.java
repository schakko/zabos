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
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.RechtVO;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * DataAccessObject für {@link Scheme#RECHT_TABLE}
 * 
 * Hinweis: Die Rechte sind Stammdaten und als Konstanten in der Klasse RechtId
 * definiert.
 * 
 * 2006-06-01 CST: Sortierungen in den Prepared Statements eingeführt
 * 
 * @author bsp
 * 
 */
public class RechtDAO extends AbstractBaseDAO
{
    /**
     * Cache
     */
    public final ICacheMultiple<RechtVO> CACHE_FIND_ALL = new CacheMultipleAdapter<RechtVO>()
    {
        public void updateAfterElementReleasement() throws StdException
        {
            setElements(findAll());
        }
    };

    public RechtDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    public RechtVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public RechtVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<RechtVO> al = new ArrayList<RechtVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        RechtVO[] vos = new RechtVO[al.size()];
        al.toArray(vos);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vos;
    }

    public RechtVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        RechtVO vo = getObjectFactory().createRecht();
        vo.setRechtId(_rs.getRechtId(Scheme.COLUMN_ID));
        vo.setName(_rs.getString(Scheme.RECHT_COLUMN_NAME));
        vo.setBeschreibung(_rs.getString(Scheme.COLUMN_BESCHREIBUNG));

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
     * Liefert alle verfügbaren Rechte.
     * 
     */
    public RechtVO[] findAll() throws StdException
    {
        if (CACHE_FIND_ALL.findMultiple() != null)
        {
            return CACHE_FIND_ALL.findMultiple();
        }

        PreparedStatement pst = getPstFindAll();
        RechtVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Testet ob ein ggb. Recht existiert. Wenn das Recht nicht existiert dann
     * liefert diese Methode null.
     * 
     * @param _rechtId
     * @return
     * @throws StdException
     */
    public RechtVO findRechtById(RechtId _rechtId) throws StdException
    {
        PreparedStatement pst = getPstFindRechtById();
        pst.setRechtId(1, _rechtId);
        RechtVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Rechte, die der ggb. Rolle zugewiesen sind. Wenn der Rolle
     * keine Rechte zugewiesen sind dann liefert diese Methode ein leeres Array.
     * 
     * @param _rolleId
     * @return
     * @throws StdException
     */
    public RechtVO[] findRechteByRolleId(RolleId _rolleId) throws StdException
    {
        PreparedStatement pst = getPstFindRechteByRolleId();
        pst.setRolleId(1, _rolleId);
        RechtVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Rechte zurück, die eine Person in einer nicht-gelöschten
     * Organisation, nicht-gelöschten Organisationseinheit oder nicht-gelöschten
     * Schleife besitzt
     * 
     * @param _personId
     * @param _rechtId
     * @return
     * @throws StdException
     */
    public RechtVO[] findRechteByPerson(PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstFindRechteByPerson();
        pst.setPersonId(1, _personId);
        pst.setPersonId(2, _personId);
        pst.setPersonId(3, _personId);
        pst.setPersonId(4, _personId);

        RechtVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Rechte, die eine Person in Bezug auf das System hat. Wenn
     * die Person keine Rechte hat dann liefert diese Methode ein leeres Array.
     * 
     * @param _personId
     * @return
     * @throws StdException
     */
    public RechtVO[] findRechteByPersonInSystem(PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstFindRechteByPersonInSystem();
        pst.setPersonId(1, _personId);
        RechtVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Rechte, die eine Person in Bezug auf eine Organisation hat.
     * Wenn die Person keine Rechte hat dann liefert diese Methode ein leeres
     * Array.
     * 
     * @param _personId
     * @param _organisationId
     * @return
     * @throws StdException
     */
    public RechtVO[] findRechteByPersonInOrganisation(PersonId _personId,
                    OrganisationId _organisationId) throws StdException
    {
        PreparedStatement pst = getPstFindRechteByPersonInOrganisation();
        pst.setPersonId(1, _personId);
        pst.setOrganisationId(2, _organisationId);
        pst.setPersonId(3, _personId);
        RechtVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Rechte, die eine Person in Bezug auf eine
     * Organisationseinheit hat. Wenn die Person keine Rechte hat dann liefert
     * diese Methode ein leeres Array.
     * 
     * @param _personId
     * @param _organisationsEinheitId
     * @return
     * @throws StdException
     */
    public RechtVO[] findRechteByPersonInOrganisationsEinheit(
                    PersonId _personId,
                    OrganisationsEinheitId _organisationsEinheitId) throws StdException
    {
        PreparedStatement pst = getPstFindRechteByPersonInOrganisationsEinheit();
        pst.setPersonId(1, _personId);
        pst.setOrganisationsEinheitId(2, _organisationsEinheitId);
        pst.setPersonId(3, _personId);
        pst.setOrganisationsEinheitId(4, _organisationsEinheitId);
        pst.setPersonId(5, _personId);
        RechtVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Rechte, die eine Person in Bezug auf eine Schleife hat. Wenn
     * die Person keine Recht hat dann liefert diese Methode ein leeres Array.
     * 
     * @param _personId
     * @param _schleifeId
     * @return
     * @throws StdException
     */
    public RechtVO[] findRechteByPersonInSchleife(PersonId _personId,
                    SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstFindRechteByPersonInSchleife();
        pst.setPersonId(1, _personId);
        pst.setSchleifeId(2, _schleifeId);
        pst.setSchleifeId(3, _schleifeId);
        pst.setPersonId(4, _personId);
        pst.setSchleifeId(5, _schleifeId);
        pst.setPersonId(6, _personId);
        pst.setPersonId(7, _personId);
        RechtVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /*
     * 
     * 
     * prepared statements
     */
    private PreparedStatement getPstFindAll() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.RECHT_TABLE + ";");
    }

    private PreparedStatement getPstFindRechtById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.RECHT_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + "=?;");
    }

    private PreparedStatement getPstFindRechteByPerson() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT DISTINCT * FROM "
                                        + Scheme.RECHT_TABLE
                                        + " WHERE "
                                        + Scheme.COLUMN_ID
                                        + " IN (SELECT rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + " FROM "
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir WHERE rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " IN ("
                                        + " SELECT piris."
                                        + " "
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                                        + " FROM "
                                        + " "
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                                        + " piris "
                                        + " WHERE "
                                        + " "
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                                        + " = ?"
                                        + " UNION"
                                        + " SELECT DISTINCT "
                                        + " "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + " FROM "
                                        + " "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " pirio "
                                        + " LEFT JOIN "
                                        + " "
                                        + Scheme.ORGANISATION_TABLE
                                        + " o ON o."
                                        + Scheme.COLUMN_ID
                                        + " = pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + " WHERE pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + " = ?"
                                        + " AND o."
                                        + Scheme.COLUMN_GELOESCHT
                                        + " = false"
                                        + " UNION "
                                        + " SELECT DISTINCT pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID
                                        + " FROM "
                                        + " "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " pirioe"
                                        + " LEFT JOIN "
                                        + Scheme.ORGANISATIONSEINHEIT_TABLE
                                        + " oe ON oe."
                                        + Scheme.COLUMN_ID
                                        + "  = pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + " WHERE pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + " = ?"
                                        + " AND oe."
                                        + Scheme.COLUMN_GELOESCHT
                                        + " = false"
                                        + " UNION"
                                        + " SELECT DISTINCT "
                                        + " "
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                                        + " FROM "
                                        + " "
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE
                                        + " piris"
                                        + " LEFT JOIN "
                                        + Scheme.SCHLEIFE_TABLE
                                        + " s ON s."
                                        + Scheme.COLUMN_ID
                                        + " = piris."
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                                        + " WHERE piris."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                                        + " = ?" + " AND s."
                                        + Scheme.COLUMN_GELOESCHT + " = false"
                                        + ")" + ")");
    }

    private PreparedStatement getPstFindRechteByPersonInSchleife() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        // Rechte aus Schleifenzuordnung
                        "SELECT DISTINCT re.* FROM "
                                        + Scheme.RECHT_TABLE
                                        + " re,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE
                                        + " piris WHERE piris."
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_PERSON_ID
                                        + "=? AND piris."
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                                        + "=? AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + "=piris."
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=re."
                                        + Scheme.COLUMN_ID

                                        // Rechte aus
                                        // Organisationseinheitzuordnung
                                        + " UNION SELECT DISTINCT re.* FROM "
                                        + Scheme.RECHT_TABLE
                                        + " re,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " pirioe,"
                                        + Scheme.SCHLEIFE_TABLE
                                        + " s WHERE s."
                                        + Scheme.COLUMN_ID
                                        + "=? AND pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + "=? AND pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=s."
                                        + Scheme.SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + "=pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=re."
                                        + Scheme.COLUMN_ID

                                        // Rechte aus Organisationszuordnung
                                        + " UNION SELECT DISTINCT re.* FROM "
                                        + Scheme.RECHT_TABLE
                                        + " re,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " pirio,"
                                        + Scheme.ORGANISATIONSEINHEIT_TABLE
                                        + " oe,"
                                        + Scheme.SCHLEIFE_TABLE
                                        + " s WHERE s."
                                        + Scheme.COLUMN_ID
                                        + "=? AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + "=? AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + "=oe."
                                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_ORGANISATION_ID
                                        + " AND oe."
                                        + Scheme.COLUMN_ID
                                        + "=s."
                                        + Scheme.SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + "=pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=re."
                                        + Scheme.COLUMN_ID

                                        // Rechte aus Systemzuordnung
                                        + " UNION SELECT DISTINCT re.* FROM "
                                        + Scheme.RECHT_TABLE
                                        + " re,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                                        + " pirisys WHERE pirisys."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                                        + "=? AND pirisys."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=re." + Scheme.COLUMN_ID + ";");
        // 2006-06-01 CKL Funktioniert so nicht...
        // + " ORDER BY re." + Scheme.RECHT_COLUMN_NAME + ";");
    }

    private PreparedStatement getPstFindRechteByPersonInOrganisationsEinheit() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        // Rechte aus Organisationseinheitzuordnung
                        "SELECT DISTINCT re.* FROM "
                                        + Scheme.RECHT_TABLE
                                        + " re,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " pirioe WHERE pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + "=? AND pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=? AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + "=pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=re."
                                        + Scheme.COLUMN_ID

                                        // Rechte aus Organisationszuordnung
                                        + " UNION SELECT DISTINCT re.* FROM "
                                        + Scheme.RECHT_TABLE
                                        + " re,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " pirio,"
                                        + Scheme.ORGANISATIONSEINHEIT_TABLE
                                        + " oe WHERE pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + "=? AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + "=oe."
                                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_ORGANISATION_ID
                                        + " AND oe."
                                        + Scheme.COLUMN_ID
                                        + "=? AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + "=pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=re."
                                        + Scheme.COLUMN_ID

                                        // Rechte aus Systemzuordnung
                                        + " UNION SELECT DISTINCT re.* FROM "
                                        + Scheme.RECHT_TABLE
                                        + " re,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                                        + " pirisys WHERE pirisys."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                                        + "=? AND pirisys."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=re." + Scheme.COLUMN_ID + ";");
        // 2006-06-01 CKL Funktioniert so nicht...
        // + " ORDER BY re." + Scheme.RECHT_COLUMN_NAME + ";");
    }

    private PreparedStatement getPstFindRechteByPersonInOrganisation() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        // Rechte aus Organisationszuordnung
                        "SELECT DISTINCT re.* FROM "
                                        + Scheme.RECHT_TABLE
                                        + " re,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " pirio WHERE pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + "=? AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + "=? AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + "=pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=re."
                                        + Scheme.COLUMN_ID

                                        // Rechte aus Systemzuordnung
                                        + " UNION SELECT DISTINCT re.* FROM "
                                        + Scheme.RECHT_TABLE
                                        + " re,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                                        + " pirisys WHERE pirisys."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                                        + "=? AND pirisys."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=re." + Scheme.COLUMN_ID + ";");
        // 2006-06-01 CKL Funktioniert so nicht...
        // + " ORDER BY re." + Scheme.RECHT_COLUMN_NAME + ";");
    }

    private PreparedStatement getPstFindRechteByPersonInSystem() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT DISTINCT re.* FROM "
                                        + Scheme.RECHT_TABLE
                                        + " re, "
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                                        + " piris WHERE piris."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                                        + "=? AND piris."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND "
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=re." + Scheme.COLUMN_ID
                                        + " ORDER BY re."
                                        + Scheme.RECHT_COLUMN_NAME + ";");
    }

    private PreparedStatement getPstFindRechteByRolleId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT r.* FROM "
                        + Scheme.RECHT_TABLE + " r,"
                        + Scheme.RECHT_IN_ROLLE_TABLE + " rir WHERE rir."
                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID + "=r."
                        + Scheme.COLUMN_ID + " AND rir."
                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID + "=?"
                        + " ORDER BY r." + Scheme.RECHT_COLUMN_NAME + ";");
    }

}

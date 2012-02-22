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
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RolleId;

/**
 * DataAccessObject für {@link Scheme#ORGANISATION_TABLE}
 * 
 * 2006-06-01 CST: Sortierung in Prepared Statements eingebaut
 * 
 * @author bsp
 * 
 */
public class OrganisationDAO extends AbstractCreateUpdateDeleteDAO
{
    /**
     * Cache
     */
    public ICacheMultiple<OrganisationVO> CACHE_FIND_ALL = new CacheMultipleAdapter<OrganisationVO>()
    {
        public void updateAfterElementReleasement() throws StdException
        {
            setElements(findAll());
        }
    };

    public OrganisationDAO(DBConnection _dbconnection,
                    ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private OrganisationVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<OrganisationVO> al = new ArrayList<OrganisationVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        OrganisationVO[] r = new OrganisationVO[al.size()];
        al.toArray(r);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return r;
    }

    private OrganisationVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public OrganisationVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        OrganisationVO vo = getObjectFactory().createOrganisation();

        vo.setOrganisationId(_rs.getOrganisationId(Scheme.COLUMN_ID));
        vo.setName(_rs.getString(Scheme.ORGANISATION_COLUMN_NAME));
        vo.setBeschreibung(_rs.getString(Scheme.COLUMN_BESCHREIBUNG));
        vo.setGeloescht(_rs.getBooleanNN(Scheme.COLUMN_GELOESCHT));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    public BaseIdVO create(BaseIdVO _vo) throws StdException
    {
        return createOrganisation((OrganisationVO) _vo);
    }

    public BaseIdVO update(BaseIdVO _vo) throws StdException
    {
        return updateOrganisation((OrganisationVO) _vo);
    }

    public void delete(BaseId _id) throws StdException
    {
        deleteOrganisation((OrganisationId) _id);
    }

    /*
     * 
     * 
     * queries
     */

    /**
     * Weist einer Person in einer Rolle Berechtigungen in Bezug auf eine
     * Organisation zu.
     * 
     */
    public void addPersonInRolleToOrganisation(PersonId _personId,
                    RolleId _rolleId, OrganisationId _organisationId) throws StdException
    {
        PreparedStatement pst = getPstAddPersonInRolleToOrganisation();
        BaseId id = getDBConnection().nextBaseId();
        pst.setBaseId(1, id);
        pst.setPersonId(2, _personId);
        pst.setRolleId(3, _rolleId);
        pst.setOrganisationId(4, _organisationId);
        pst.execute();
        pst.close();
    }

    /**
     * Legt eine neue Organisation an. Wenn die Organisation mit dem ggb. Namen
     * bereits als "gelöscht" in der Datenbank steht dann wird der alte
     * Datensatz reaktiviert und das "gelöscht" Flag zurückgesetzt.
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public OrganisationVO createOrganisation(OrganisationVO _vo) throws StdException
    {
        OrganisationVO vo = findOrganisationByName(_vo.getName());
        if (vo != null)
        {
            if (vo.getGeloescht())
            {
                undeleteOrganisation(vo.getOrganisationId());
                _vo.setOrganisationId(vo.getOrganisationId());
                return updateOrganisation(_vo);
            }
            else
            {
                throw new StdException("organisation id="
                                + vo.getOrganisationId() + " name="
                                + vo.getName() + " existiert bereits");
            }
        }
        else
        {
            OrganisationId id = new OrganisationId(dbconnection.nextId());
            PreparedStatement pst = getPstCreateOrganisation();
            pst.setOrganisationId(1, id);
            pst.setString(2, _vo.getBeschreibung());
            pst.setString(3, _vo.getName());
            pst.execute();
            pst.close();

            return findOrganisationById(id);
        }
    }

    /**
     * Liefert alle Organisationen, die *nicht* als gelöscht markiert sind. Wenn
     * keine Organisationen existieren dann liefert diese Methode ein leeres
     * Array.
     * 
     * @return
     * @throws StdException
     */
    public OrganisationVO[] findAll() throws StdException
    {
        if (CACHE_FIND_ALL.findMultiple() != null)
        {
            return CACHE_FIND_ALL.findMultiple();
        }

        PreparedStatement pst = getPstFindAll();
        OrganisationVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Organisationen zurück, in denen die Person eingetragen
     * wurde. Die Organisationen, die als gelöscht gekennzeichnet sind, werden
     * dabei nicht berücksichtigt. Wenn der Benutzer keiner Organisation
     * zugewiesen ist, liefert die Methode ein leeres Array zurück.
     * 
     * @author ckl
     * @param _id
     * @return
     * @throws StdException
     */
    public OrganisationVO[] findMitgliedschaftInOrganisationVonPerson(
                    PersonId _id) throws StdException
    {
        PreparedStatement pst = getPstFindMitgliedschaftInOrganisationenVonPerson();
        pst.setPersonId(1, _id);

        OrganisationVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert eine Organisation unter Angabe der OrganisationId. Wenn die
     * Organisation nicht gefunden wurde dann liefert diese Methode null. Diese
     * Methode liefert auch als gelöscht markierte Datensätze.
     * 
     * @param _id
     * @return
     * @throws StdException
     */
    public OrganisationVO findOrganisationById(OrganisationId _id) throws StdException
    {
        PreparedStatement pst = getPstFindOrganisationById();
        pst.setOrganisationId(1, _id);
        OrganisationVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert eine Organisation unter Angabe des Namens. Wenn die Organisation
     * nicht gefunden wurde dann liefert diese Methode null. <br />
     * Die Methode ist <strong>case-insensitive</strong>
     * 
     * @param _name
     * @return
     * @throws StdException
     */
    public OrganisationVO findOrganisationByName(String _name) throws StdException
    {
        PreparedStatement pst = getPstFindOrganisationByName();
        pst.setString(1, _name);
        OrganisationVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Löscht eine Organisation. Der Datensatz wird *nicht* aus der Datenbank
     * gelöscht sondern nur als "gelöscht" markiert.
     * 
     * @param _organisationId
     * @throws StdException
     */
    public void deleteOrganisation(OrganisationId _organisationId) throws StdException
    {
        PreparedStatement pst = getPstDeleteOrganisation();
        pst.setOrganisationId(1, _organisationId);
        pst.execute();
        pst.close();
    }

    /**
     * Setzt das "gelöscht" Flag für eine Organisation zurück.
     * 
     * @param _id
     * @return
     * @throws StdException
     */
    public OrganisationVO undeleteOrganisation(OrganisationId _id) throws StdException
    {
        PreparedStatement pst = getPstUndeleteOrganisation();
        pst.setOrganisationId(1, _id);
        pst.execute();
        pst.close();
        return findOrganisationById(_id);
    }

    /**
     * Ändert eine Organisation.
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public OrganisationVO updateOrganisation(OrganisationVO _vo) throws StdException
    {
        PreparedStatement pst = getPstUpdateOrganisation();
        pst.setString(1, _vo.getBeschreibung());
        pst.setString(2, _vo.getName());
        pst.setOrganisationId(3, _vo.getOrganisationId());
        pst.execute();
        pst.close();
        return findOrganisationById(_vo.getOrganisationId());
    }

    /**
     * Entzieht allen Personen die Berechtigungen auf die ggb. Organisation
     * 
     */
    public void removeAllPersonenFromOrganisation(OrganisationId _oId) throws StdException
    {
        PreparedStatement pst = getPstRemoveAllPersonenFromOrganisation();
        pst.setOrganisationId(1, _oId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht einer Person alle Berechtigungen/Rollen in Bezug auf eine
     * Organisation.
     * 
     * @param _personId
     * @param _organisationId
     * @throws StdException
     */
    public void removePersonFromOrganisation(PersonId _personId,
                    OrganisationId _organisationId) throws StdException
    {
        PreparedStatement pst = getPstRemovePersonFromOrganisation();
        pst.setPersonId(1, _personId);
        pst.setOrganisationId(2, _organisationId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht einer Person alle Berechtigungen/Rollen in Bezug auf *alle*
     * Organisation.
     * 
     * @param _personId
     * @throws StdException
     */
    public void removePersonFromAllOrganisationen(PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstRemovePersonFromAllOrganisationen();
        pst.setPersonId(1, _personId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht einer Person eine bestimmte Rolle in Bezug auf eine
     * Organisation.
     * 
     * @param _personId
     * @param _rolleId
     * @param _organisationId
     * @throws StdException
     */
    public void removePersonInRolleFromOrganisation(PersonId _personId,
                    RolleId _rolleId, OrganisationId _organisationId) throws StdException
    {
        PreparedStatement pst = getPstRemovePersonInRolleFromOrganisation();
        pst.setPersonId(1, _personId);
        pst.setRolleId(2, _rolleId);
        pst.setOrganisationId(3, _organisationId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht allen Personen die Berechtigungen in der ggb. Rolle auf eine
     * Organisation.
     * 
     * @param _rolleId
     * @param _organisationId
     * @throws StdException
     */
    public void removeRolleFromOrganisation(RolleId _rolleId,
                    OrganisationId _organisationId) throws StdException
    {
        PreparedStatement pst = getPstRemoveRolleFromOrganisation();
        pst.setRolleId(1, _rolleId);
        pst.setOrganisationId(2, _organisationId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht allen Personen die Berechtigungen in der ggb. Rolle auf *alle*
     * Organisationen.
     * 
     * @param _rolleId
     * @throws StdException
     */
    public void removeRolleFromAllOrganisationen(RolleId _rolleId) throws StdException
    {
        PreparedStatement pst = getPstRemoveRolleFromAllOrganisationen();
        pst.setRolleId(1, _rolleId);
        pst.execute();
        pst.close();
    }

    /*
     * 
     * 
     * Prepared statements
     */
    private PreparedStatement getPstCreateOrganisation() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.ORGANISATION_TABLE + " (" + Scheme.COLUMN_ID
                        + "," + Scheme.COLUMN_BESCHREIBUNG + ","
                        + Scheme.ORGANISATION_COLUMN_NAME + ") VALUES(?,?,?);");
    }

    private PreparedStatement getPstFindAll() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ORGANISATION_TABLE + " WHERE "
                        + Scheme.COLUMN_GELOESCHT + "=false ORDER BY "
                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_NAME + ";");
    }

    private PreparedStatement getPstFindOrganisationById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ORGANISATION_TABLE + " WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstFindOrganisationByName() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ORGANISATION_TABLE + " WHERE "
                        + Scheme.ORGANISATION_COLUMN_NAME + " ILIKE ?;");
    }

    private PreparedStatement getPstAddPersonInRolleToOrganisation() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "INSERT INTO "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " ("
                                        + Scheme.COLUMN_ID
                                        + ","
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + ","
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + ","
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + ") VALUES(?,?,?,?);");
    }

    private PreparedStatement getPstDeleteOrganisation() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.ORGANISATION_TABLE + " SET "
                        + Scheme.COLUMN_GELOESCHT + "=true WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstUndeleteOrganisation() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.ORGANISATION_TABLE + " SET "
                        + Scheme.COLUMN_GELOESCHT + "=false WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstUpdateOrganisation() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.ORGANISATION_TABLE + " SET "
                        + Scheme.COLUMN_BESCHREIBUNG + "=?,"
                        + Scheme.ORGANISATION_COLUMN_NAME + "=? WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstRemoveAllPersonenFromOrganisation() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "DELETE FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + "=?;");
    }

    private PreparedStatement getPstRemovePersonInRolleFromOrganisation() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "DELETE FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + "=? AND "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + "=? AND "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + "=?;");
    }

    private PreparedStatement getPstRemovePersonFromAllOrganisationen() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "DELETE FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + "=?;");
    }

    private PreparedStatement getPstRemovePersonFromOrganisation() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "DELETE FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + "=? AND "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + "=?;");
    }

    private PreparedStatement getPstRemoveRolleFromOrganisation() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "DELETE FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + "=? AND "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + "=?;");
    }

    private PreparedStatement getPstRemoveRolleFromAllOrganisationen() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "DELETE FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + "=?;");
    }

    private PreparedStatement getPstFindMitgliedschaftInOrganisationenVonPerson() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT * FROM "
                                        + Scheme.ORGANISATION_TABLE
                                        + " WHERE "
                                        + Scheme.COLUMN_ID
                                        + " IN ("
                                        + "SELECT "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + " FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + " = ?) AND "
                                        + Scheme.COLUMN_GELOESCHT + " = false "
                                        + "ORDER BY "
                                        + Scheme.ORGANISATION_COLUMN_NAME);
    }
}

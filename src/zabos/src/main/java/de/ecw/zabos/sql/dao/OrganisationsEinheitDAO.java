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
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RolleId;

/**
 * DataAccessObject für {@link Scheme#ORGANISATIONSEINHEIT_TABLE}
 * 
 * 2006-06-01 CST: Sortierung in Prepared Statements eingebaut
 * 
 * @author bsp
 * 
 */
public class OrganisationsEinheitDAO extends AbstractCreateUpdateDeleteDAO
{
    /**
     * Cache
     */
    public ICacheMultiple<OrganisationsEinheitVO> CACHE_FIND_ALL = new CacheMultipleAdapter<OrganisationsEinheitVO>()
    {
        public void updateAfterElementReleasement() throws StdException
        {
            setElements(findAll());
        }
    };

    public OrganisationsEinheitDAO(DBConnection _dbconnection,
                    ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private OrganisationsEinheitVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<OrganisationsEinheitVO> al = new ArrayList<OrganisationsEinheitVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        OrganisationsEinheitVO[] r = new OrganisationsEinheitVO[al.size()];
        al.toArray(r);

        if (!_keepResultSet)
        {
            _rs.close();
        }
        return r;
    }

    private OrganisationsEinheitVO nextToVO(ResultSet _rs,
                    boolean _keepResultSet) throws StdException
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

    public OrganisationsEinheitVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        OrganisationsEinheitVO vo = getObjectFactory()
                        .createOrganisationsEinheit();
        vo.setOrganisationsEinheitId(_rs
                        .getOrganisationsEinheitId(Scheme.COLUMN_ID));
        vo.setName(_rs.getString(Scheme.ORGANISATIONSEINHEIT_COLUMN_NAME));
        vo.setBeschreibung(_rs.getString(Scheme.COLUMN_BESCHREIBUNG));
        vo.setOrganisationId(_rs
                        .getOrganisationId(Scheme.ORGANISATIONSEINHEIT_COLUMN_ORGANISATION_ID));
        vo.setGeloescht(_rs.getBooleanNN(Scheme.COLUMN_GELOESCHT));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    public BaseIdVO create(BaseIdVO _vo) throws StdException
    {
        return createOrganisationsEinheit((OrganisationsEinheitVO) _vo);
    }

    public BaseIdVO update(BaseIdVO _vo) throws StdException
    {
        return updateOrganisationsEinheit((OrganisationsEinheitVO) _vo);
    }

    public void delete(BaseId _id) throws StdException
    {
        deleteOrganisationsEinheit((OrganisationsEinheitId) _id);
    }

    /*
     * 
     * 
     * queries
     */

    /**
     * Liefert alle Organisationseinheiten zurück, in denen die Person
     * eingetragen wurde. Die Organisationseinheiten, die als gelöscht
     * gekennzeichnet sind, werden dabei nicht berücksichtigt. Wenn der Benutzer
     * keiner Organisationseinheit direkt zugewiesen ist, liefert die Methode
     * ein leeres Array zurück.
     * 
     * @param _id
     * @return
     * @throws StdException
     */
    public OrganisationsEinheitVO[] findMitgliedschaftInOrganisationseinheitenVonPerson(
                    PersonId _id) throws StdException
    {
        PreparedStatement pst = getPstFindMitgliedschaftInOrganisationseinheitenVonPerson();
        pst.setPersonId(1, _id);

        OrganisationsEinheitVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Weist einer Person in einer Rolle Berechtigungen in Bezug auf eine
     * Organisationseinheit zu.
     * 
     */
    public void addPersonInRolleToOrganisationseinheit(PersonId _personId,
                    RolleId _rolleId,
                    OrganisationsEinheitId _organisationsEinheitId) throws StdException
    {
        BaseId id = dbconnection.nextBaseId();
        PreparedStatement pst = getPstAddPersonInRolleToOrganisationsEinheit();
        pst.setBaseId(1, id);
        pst.setPersonId(2, _personId);
        pst.setRolleId(3, _rolleId);
        pst.setOrganisationsEinheitId(4, _organisationsEinheitId);
        pst.execute();
        pst.close();
    }

    /**
     * Legt eine neue Organisationseinheit an. Wenn in der Datenbank bereits ein
     * Datensatz mit dem ggb. Namen als "gelöscht" markiert existiert dann wird
     * dieser Datensatz reaktiviert und das "gelöscht" Flag zurückgesetzt.
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public OrganisationsEinheitVO createOrganisationsEinheit(
                    OrganisationsEinheitVO _vo) throws StdException
    {
        OrganisationsEinheitVO vo = findOrganisationsEinheitByName(_vo
                        .getName());
        if (vo != null)
        {
            if (vo.getGeloescht())
            {
                PreparedStatement pst = getPstUndeleteOrganisationsEinheit();
                pst.setOrganisationsEinheitId(1, vo.getOrganisationsEinheitId());
                pst.execute();
                pst.close();

                _vo.setOrganisationsEinheitId(vo.getOrganisationsEinheitId());
                return updateOrganisationsEinheit(_vo);
            }
            else
            {
                throw new StdException("organisationseinheit id="
                                + vo.getOrganisationsEinheitId() + " name="
                                + vo.getName() + " existiert bereits");
            }
        }
        else
        {
            OrganisationsEinheitId id = new OrganisationsEinheitId(
                            dbconnection.nextId());
            PreparedStatement pst = getPstCreateOrganisationsEinheit();
            pst.setOrganisationsEinheitId(1, id);
            pst.setString(2, _vo.getBeschreibung());
            pst.setString(3, _vo.getName());
            pst.setOrganisationId(4, _vo.getOrganisationId());
            pst.execute();
            pst.close();

            return findOrganisationsEinheitById(id);
        }
    }

    /**
     * Liefert alle nicht als gelöscht markierten Organisationseinheiten. Wenn
     * keine Organisationseinheiten existieren dann liefert diese Methode ein
     * leeres Array.
     * 
     * @return
     * @throws StdException
     */
    public OrganisationsEinheitVO[] findAll() throws StdException
    {
        if (CACHE_FIND_ALL.findMultiple() != null)
        {
            return CACHE_FIND_ALL.findMultiple();
        }

        PreparedStatement pst = getPstFindAll();
        OrganisationsEinheitVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert eine Organisationseinheit unter Angabe der
     * OrganisationsEinheitId. Wenn die Organisationseinheit nicht existiert
     * dann liefert diese Methode null.
     * 
     * @param _id
     * @return
     * @throws StdException
     */
    public OrganisationsEinheitVO findOrganisationsEinheitById(
                    OrganisationsEinheitId _id) throws StdException
    {
        PreparedStatement pst = getPstFindOrganisationsEinheitById();
        pst.setOrganisationsEinheitId(1, _id);
        OrganisationsEinheitVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert eine Organisationseinheit unter Angabe des Namens. Wenn die
     * Organisationseinheit nicht existiert dann liefert diese Methode null.
     * Diese Methode findet auch als gelöscht markierte Datensätze. <br />
     * Die Methode ist <strong>case-insensitive</strong>
     * 
     * @param _name
     * @return
     * @throws StdException
     */
    public OrganisationsEinheitVO findOrganisationsEinheitByName(String _name) throws StdException
    {
        PreparedStatement pst = getPstFindOrganisationsEinheitByName();
        pst.setString(1, _name);
        OrganisationsEinheitVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle nicht als gelöscht markierten Organisationseinheiten
     * unterhalb einer Organisation.
     * 
     * @param _id
     * @return
     * @throws StdException
     */
    public OrganisationsEinheitVO[] findOrganisationsEinheitenByOrganisationId(
                    OrganisationId _id) throws StdException
    {
        PreparedStatement pst = getPstFindOrganisationsEinheitenByOrganisationId();
        pst.setOrganisationId(1, _id);
        OrganisationsEinheitVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Löscht eine Organisationseinheit. Der Datensatz wird nicht aus der
     * Datenbank entfernt sondern nur als "gelöscht" markiert.
     * 
     * @param _id
     * @throws StdException
     */
    public void deleteOrganisationsEinheit(OrganisationsEinheitId _id) throws StdException
    {
        PreparedStatement pst = getPstDeleteOrganisationsEinheit();
        pst.setOrganisationsEinheitId(1, _id);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht allen Personen die Berechtigungen auf die ggb.
     * Organisationseinheit
     * 
     */
    public void removeAllPersonenFromOrganisationseinheit(
                    OrganisationsEinheitId _oeId) throws StdException
    {
        PreparedStatement pst = getPstRemoveAllPersonenFromOrganisationseinheit();
        pst.setOrganisationsEinheitId(1, _oeId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht einer Person die Berechtigungen der ggb. Rolle in Bezug auf eine
     * Organisationseinheit.
     * 
     * @param _personId
     * @param _rolleId
     * @param _organisationsEinheitId
     * @throws StdException
     */
    public void removePersonInRolleFromOrganisationseinheit(PersonId _personId,
                    RolleId _rolleId,
                    OrganisationsEinheitId _organisationsEinheitId) throws StdException
    {
        PreparedStatement pst = getPstRemovePersonInRolleFromOrganisationsEinheit();
        pst.setPersonId(1, _personId);
        pst.setRolleId(2, _rolleId);
        pst.setOrganisationsEinheitId(3, _organisationsEinheitId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht einer Person alle Berechtigungen in Bezug auf eine
     * Organisationseinheit.
     * 
     * @param _personId
     * @param _organisationsEinheitId
     * @throws StdException
     */
    public void removePersonFromOrganisationseinheit(PersonId _personId,
                    OrganisationsEinheitId _organisationsEinheitId) throws StdException
    {
        PreparedStatement pst = getPstRemovePersonFromOrganisationsEinheit();
        pst.setPersonId(1, _personId);
        pst.setOrganisationsEinheitId(2, _organisationsEinheitId);
        pst.execute();
        pst.close();
    }

    /**
     * Enzieht einer Person alle Berechtigungen in Bezug auf *alle*
     * Organisationseinheiten.
     * 
     * @param _personId
     * @throws StdException
     */
    public void removePersonFromAllOrganisationseinheiten(PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstRemovePersonFromAllOrganisationsEinheiten();
        pst.setPersonId(1, _personId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht allen Personen die Berechtigungen der ggb. Rolle in Bezug auf
     * eine Organisationseinheit.
     * 
     * @param _rolleId
     * @param _organisationsEinheitId
     * @throws StdException
     */
    public void removeRolleFromOrganisationseinheit(RolleId _rolleId,
                    OrganisationsEinheitId _organisationsEinheitId) throws StdException
    {
        PreparedStatement pst = getPstRemoveRolleFromOrganisationsEinheit();
        pst.setRolleId(1, _rolleId);
        pst.setOrganisationsEinheitId(2, _organisationsEinheitId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht allen Personen die Berechtigungen der ggb. Rolle in Bezug auf
     * *alle* Organisationseinheiten.
     * 
     * @param _rolleId
     * @throws StdException
     */
    public void removeRolleFromAllOrganisationseinheiten(RolleId _rolleId) throws StdException
    {
        PreparedStatement pst = getPstRemoveRolleFromAllOrganisationsEinheiten();
        pst.setRolleId(1, _rolleId);
        pst.execute();
        pst.close();
    }

    /**
     * Setzt das "gelöscht" Flag für eine Organisationseinheit zurück.
     * 
     * @param _organisationsEinheitId
     * @throws StdException
     */
    public void undeleteOrganisationseinheit(
                    OrganisationsEinheitId _organisationsEinheitId) throws StdException
    {
        PreparedStatement pst = getPstUndeleteOrganisationsEinheit();
        pst.setOrganisationsEinheitId(1, _organisationsEinheitId);
        pst.execute();
        pst.close();
    }

    /**
     * Ändert eine Organisationseinheit.
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public OrganisationsEinheitVO updateOrganisationsEinheit(
                    OrganisationsEinheitVO _vo) throws StdException
    {
        PreparedStatement pst = getPstUpdateOrganisationsEinheit();
        pst.setString(1, _vo.getBeschreibung());
        pst.setString(2, _vo.getName());
        pst.setOrganisationId(3, _vo.getOrganisationId());
        pst.setOrganisationsEinheitId(4, _vo.getOrganisationsEinheitId());
        pst.execute();
        pst.close();
        return findOrganisationsEinheitById(_vo.getOrganisationsEinheitId());
    }

    /*
     * 
     * 
     * Prepared statement getters
     */
    private PreparedStatement getPstCreateOrganisationsEinheit() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.ORGANISATIONSEINHEIT_TABLE + " ("
                        + Scheme.COLUMN_ID + "," + Scheme.COLUMN_BESCHREIBUNG
                        + "," + Scheme.ORGANISATIONSEINHEIT_COLUMN_NAME + ","
                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_ORGANISATION_ID
                        + ") VALUES(?,?,?,?);");
    }

    private PreparedStatement getPstFindAll() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ORGANISATIONSEINHEIT_TABLE + " WHERE "
                        + Scheme.COLUMN_GELOESCHT + "=false ORDER BY "
                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_NAME + ";");
    }

    private PreparedStatement getPstFindOrganisationsEinheitById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ORGANISATIONSEINHEIT_TABLE + " WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstFindOrganisationsEinheitByName() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ORGANISATIONSEINHEIT_TABLE + " WHERE "
                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_NAME + " ILIKE ?;");
    }

    private PreparedStatement getPstFindOrganisationsEinheitenByOrganisationId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.ORGANISATIONSEINHEIT_TABLE + " WHERE "
                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_ORGANISATION_ID
                        + "=? AND " + Scheme.COLUMN_GELOESCHT
                        + "=false ORDER BY "
                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_NAME + ";");
    }

    private PreparedStatement getPstAddPersonInRolleToOrganisationsEinheit() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "INSERT INTO "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " ("
                                        + Scheme.COLUMN_ID
                                        + ","
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + ","
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID
                                        + ","
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + ") VALUES(?,?,?,?);");
    }

    private PreparedStatement getPstDeleteOrganisationsEinheit() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.ORGANISATIONSEINHEIT_TABLE + " SET "
                        + Scheme.COLUMN_GELOESCHT + "=true WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstUndeleteOrganisationsEinheit() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.ORGANISATIONSEINHEIT_TABLE + " SET "
                        + Scheme.COLUMN_GELOESCHT + "=false WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstUpdateOrganisationsEinheit() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.ORGANISATIONSEINHEIT_TABLE + " SET "
                        + Scheme.COLUMN_BESCHREIBUNG + "=?,"
                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_NAME + "=?,"
                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_ORGANISATION_ID
                        + "=? WHERE " + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstRemoveAllPersonenFromOrganisationseinheit() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "DELETE FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=?;");
    }

    private PreparedStatement getPstRemovePersonFromOrganisationsEinheit() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "DELETE FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + "=? AND "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=?;");
    }

    private PreparedStatement getPstRemovePersonFromAllOrganisationsEinheiten() throws StdException
    {

        return new PreparedStatement(
                        getDBConnection(),
                        "DELETE FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + "=?;");
    }

    private PreparedStatement getPstRemovePersonInRolleFromOrganisationsEinheit() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "DELETE FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + "=? AND "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID
                                        + "=? AND "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=?;");
    }

    private PreparedStatement getPstRemoveRolleFromOrganisationsEinheit() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "DELETE FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID
                                        + "=? AND "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=?;");
    }

    private PreparedStatement getPstRemoveRolleFromAllOrganisationsEinheiten() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "DELETE FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID
                                        + "=?;");
    }

    private PreparedStatement getPstFindMitgliedschaftInOrganisationseinheitenVonPerson() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT * FROM "
                                        + Scheme.ORGANISATIONSEINHEIT_TABLE
                                        + " WHERE "
                                        + Scheme.COLUMN_ID
                                        + " IN ("
                                        + "SELECT "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + " FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " WHERE "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + " = ?) "
                                        + " AND "
                                        + Scheme.COLUMN_GELOESCHT
                                        + " = false "
                                        + "ORDER BY "
                                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_NAME
                                        + ";");
    }
}

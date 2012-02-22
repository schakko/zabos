package de.ecw.zabos.sql.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.cvo.PersonMitRollenCVO;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * DAO für {@link Scheme#PERSON_IN_ROLLE_IN_ORGANISATION_TABLE},
 * {@link Scheme#PERSON_IN_ROLLE_IN_SYSTEM_TABLE},
 * {@link Scheme#PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE} und
 * {@link Scheme#PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE}
 * 
 * @author ckl
 * 
 */
public class PersonMitRollenDAO extends AbstractBaseDAO
{
    private PersonDAO personDao;

    public PersonMitRollenDAO(DBConnection _dbconnection,
                    ObjectFactory _objectFactory, PersonDAO _personDao)
    {
        super(_dbconnection, _objectFactory);
        setPersonDao(_personDao);
    }

    private PersonMitRollenCVO[] toVOs(ResultSet _rs) throws StdException
    {
        // Hashmap mit den einzelnen CVOs
        Map<PersonVO, PersonMitRollenCVO> hmPersonen = new HashMap<PersonVO, PersonMitRollenCVO>();

        while (_rs.next())
        {
            PersonVO person = null;
            RolleVO rolle = getObjectFactory().createRolle();
            person = toVO(_rs, rolle);

            if (!hmPersonen.containsKey(person))
            {
                PersonMitRollenCVO personMitRolle = new PersonMitRollenCVO();
                personMitRolle.setPerson(person);
                hmPersonen.put(person,
                                personMitRolle);
            }

            List<RolleVO> alZugewieseneRollen = hmPersonen.get(person)
                            .getRollen();

            // Jede Rolle soll nur einmal vorkommen
            if (!alZugewieseneRollen.contains(rolle))
            {
                alZugewieseneRollen.add(rolle);
            }
        }

        PersonMitRollenCVO[] r = new PersonMitRollenCVO[hmPersonen.size()];

        hmPersonen.values().toArray(r);

        _rs.close();

        return r;
    }

    /**
     * Im Gegensatz zu den anderen toVOs wird hier mit call-by-reference
     * gearbeitet
     * 
     * @param _rs
     * @param _person
     * @param _rolle
     * @throws StdException
     */
    public PersonVO toVO(ResultSet _rs, RolleVO _rolle) throws StdException
    {

        _rolle.setRolleId(_rs
                        .getRolleId(Scheme.VERERBTE_ROLLEN_COLUMN_ROLLE_ID));
        _rolle.setName(_rs.getString(Scheme.VERERBTE_ROLLEN_COLUMN_ROLLE_NAME));
        _rolle.setBeschreibung(_rs
                        .getString(Scheme.VERERBTE_ROLLEN_COLUMN_ROLLE_BESCHREIBUNG));
        return getPersonDao().toVO(_rs, true);
    }

    /**
     * Liefert die Personen zurück, die in einer Schleife Rollen vererbt
     * bekommen haben. Die Rollen der Person werden gemergt, d.h. pro Person
     * existiert immer nur eine Rolle. Wenn eine Person eine Rolle z.B. im
     * System und in der Organisation zugewiesen bekommen hat, wird dies nicht
     * berücksichtigt.
     * 
     * @param _o
     * @param _oe
     * @param _s
     * @return
     */
    public PersonMitRollenCVO[] findPersonenMitVererbtenRollenInSchleife(
                    OrganisationId _o, OrganisationsEinheitId _oe, SchleifeId _s) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenMitVererbtenRollenInSchleife();
        pst.setOrganisationId(1, _o);
        pst.setOrganisationsEinheitId(2, _oe);
        pst.setSchleifeId(3, _s);

        PersonMitRollenCVO[] r = toVOs(pst.executeQuery());

        pst.close();
        return r;
    }

    /**
     * Liefert die Personen zurück, die der Schleife direkt mit einer Rolle
     * zugeordnet sind
     * 
     * @param _s
     * @return
     * @throws StdException
     */
    public PersonMitRollenCVO[] findPersonenMitRollenInSchleife(SchleifeId _s) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenMitRollenIn();
        pst.setInteger(1, Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.SCHLEIFE
                        .ordinal());
        pst.setSchleifeId(2, _s);

        PersonMitRollenCVO[] r = toVOs(pst.executeQuery());

        pst.close();
        return r;
    }

    /**
     * Liefert die Personen zurück, die in einer Organisationseinheit Rollen
     * vererbt bekommen haben. Die Rollen der Person werden gemergt, d.h. pro
     * Person existiert immer nur eine Rolle. Wenn eine Person eine Rolle z.B.
     * im System und in der Organisation zugewiesen bekommen hat, wird dies
     * nicht berücksichtigt.
     * 
     * @param _o
     * @param _oe
     * @return
     */
    public PersonMitRollenCVO[] findPersonenMitVererbtenRollenInOrganisationsenheit(
                    OrganisationId _o, OrganisationsEinheitId _oe) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenMitVererbtenRollenInOrganisationsenheit();
        pst.setOrganisationId(1, _o);
        pst.setOrganisationsEinheitId(2, _oe);

        PersonMitRollenCVO[] r = toVOs(pst.executeQuery());

        pst.close();
        return r;
    }

    /**
     * Liefert die Personen zurück, die der Organisationseinheit direkt mit
     * einer Rolle zugeordnet sind
     * 
     * @param _oe
     * @return
     * @throws StdException
     */
    public PersonMitRollenCVO[] findPersonenMitRollenInOrganisationseinheit(
                    OrganisationsEinheitId _oe) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenMitRollenIn();
        pst.setInteger(1,
                        Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.ORGANISATIONSEINHEIT
                                        .ordinal());
        pst.setOrganisationsEinheitId(2, _oe);

        PersonMitRollenCVO[] r = toVOs(pst.executeQuery());

        pst.close();
        return r;
    }

    /**
     * Liefert die Personen zurück, die in einer Organsiation Rollen vererbt
     * bekommen haben. Die Rollen der Person werden gemergt, d.h. pro Person
     * existiert immer nur eine Rolle. Wenn eine Person eine Rolle z.B. im
     * System und in der Organisation zugewiesen bekommen hat, wird dies nicht
     * berücksichtigt.
     * 
     * @param _o
     * @return
     */
    public PersonMitRollenCVO[] findPersonenMitVererbtenRollenInOrganisation(
                    OrganisationId _o) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenMitVererbtenRollenInOrganisation();
        pst.setOrganisationId(1, _o);

        PersonMitRollenCVO[] r = toVOs(pst.executeQuery());

        pst.close();
        return r;
    }

    /**
     * Liefert die Personen zurück, die der Organisation direkt mit einer Rolle
     * zugeordnet sind
     * 
     * @param _o
     * @return
     * @throws StdException
     */
    public PersonMitRollenCVO[] findPersonenMitRollenInOrganisation(
                    OrganisationId _o) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenMitRollenIn();
        pst.setInteger(1,
                        Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.ORGANISATION
                                        .ordinal());
        pst.setOrganisationId(2, _o);

        PersonMitRollenCVO[] r = toVOs(pst.executeQuery());

        pst.close();
        return r;
    }

    /**
     * Liefert die Personen zurück, die dem System direkt mit einer Rolle
     * zugeordnet sind
     * 
     * @return
     * @throws StdException
     */
    public PersonMitRollenCVO[] findPersonenMitRollenInSystem() throws StdException
    {
        PreparedStatement pst = getPstFindPersonenMitRollenIn();
        pst.setInteger(1, Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.SYSTEM
                        .ordinal());
        pst.setInteger(2, 0);

        PersonMitRollenCVO[] r = toVOs(pst.executeQuery());

        pst.close();
        return r;
    }

    /**
     * Liefert die Personen zurück, die im System Rollen zugewiesen bekommen
     * haben. Die Rollen der Person werden gemergt, d.h. pro Person existiert
     * immer nur eine Rolle.
     * 
     * @return
     */
    public PersonMitRollenCVO[] findPersonenMitVererbtenRollenInSystem() throws StdException
    {
        PreparedStatement pst = getPstFindPersonenMitVererbtenRollenInSystem();

        PersonMitRollenCVO[] r = toVOs(pst.executeQuery());

        pst.close();
        return r;
    }

    private PreparedStatement getPstFindPersonenMitVererbtenRollenInOrganisationsenheit() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT * FROM "
                                        + Scheme.VERERBTE_ROLLEN_VIEW
                                        + " WHERE "
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false AND ("
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT
                                        + "="
                                        + Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.SYSTEM
                                                        .ordinal()
                                        + " OR ("
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT
                                        + "="
                                        + Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.ORGANISATION
                                                        .ordinal()
                                        + " AND "
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT_ID
                                        + " =?)"
                                        + " OR ("
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT
                                        + "="
                                        + Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.ORGANISATIONSEINHEIT
                                                        .ordinal()
                                        + " AND "
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT_ID
                                        + "=?)) ORDER BY "
                                        + Scheme.PERSON_COLUMN_NACHNAME + ","
                                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenMitVererbtenRollenInSchleife() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT * FROM "
                                        + Scheme.VERERBTE_ROLLEN_VIEW
                                        + " WHERE "
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false AND ("
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT
                                        + "="
                                        + Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.SYSTEM
                                                        .ordinal()
                                        + " OR ("
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT
                                        + "="
                                        + Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.ORGANISATION
                                                        .ordinal()
                                        + " AND "
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT_ID
                                        + " =?)"
                                        + " OR ("
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT
                                        + "="
                                        + Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.ORGANISATIONSEINHEIT
                                                        .ordinal()
                                        + " AND "
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT_ID
                                        + "=?) OR ("
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT
                                        + "="
                                        + Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.SCHLEIFE
                                                        .ordinal()
                                        + " AND "
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT_ID
                                        + "=?)) ORDER BY "
                                        + Scheme.PERSON_COLUMN_NACHNAME + ","
                                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenMitVererbtenRollenInOrganisation() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT * FROM "
                                        + Scheme.VERERBTE_ROLLEN_VIEW
                                        + " WHERE "
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false AND ("
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT
                                        + "="
                                        + Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.SYSTEM
                                                        .ordinal()
                                        + " OR ("
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT
                                        + "="
                                        + Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.ORGANISATION
                                                        .ordinal()
                                        + " AND "
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT_ID
                                        + " =?)) ORDER BY "
                                        + Scheme.PERSON_COLUMN_NACHNAME + ","
                                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenMitVererbtenRollenInSystem() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT * FROM "
                                        + Scheme.VERERBTE_ROLLEN_VIEW
                                        + " WHERE "
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false AND ("
                                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT
                                        + "="
                                        + Scheme.VERERBTE_ROLLEN_KONTEXT_IDENTIFIER.SYSTEM
                                                        .ordinal()
                                        + ") ORDER BY "
                                        + Scheme.PERSON_COLUMN_NACHNAME + ","
                                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenMitRollenIn() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.VERERBTE_ROLLEN_VIEW + " WHERE "
                        + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT + "= ? "
                        + " AND " + Scheme.VERERBTE_ROLLEN_COLUMN_KONTEXT_ID
                        + " = ?" + " ORDER BY " + Scheme.PERSON_COLUMN_NACHNAME
                        + ", " + Scheme.PERSON_COLUMN_VORNAME);
    }

    public void setPersonDao(PersonDAO personDao)
    {
        this.personDao = personDao;
    }

    public PersonDAO getPersonDao()
    {
        return personDao;
    }
}

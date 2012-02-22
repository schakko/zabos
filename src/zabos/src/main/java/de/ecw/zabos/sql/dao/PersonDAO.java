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
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.types.Pin;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;
import de.ecw.zabos.types.id.SchleifeId;
import de.ecw.zabos.types.id.TelefonId;
import de.ecw.zabos.util.StringUtils;

/**
 * DataAccessObject für {@link Scheme#PERSON_TABLE}
 * 
 * 2006-06-01 CST: Sortierungen in den Prepared Statements eingeführt
 * 
 * @author bsp
 * 
 */
public class PersonDAO extends AbstractCreateUpdateDeleteDAO
{

    /**
     * Cache
     */
    public final ICacheMultiple<PersonVO> CACHE_FIND_ALL = new CacheMultipleAdapter<PersonVO>()
    {
        public void updateAfterElementReleasement() throws StdException
        {
            setElements(findAll());
        }
    };

    public PersonDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    public PersonVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public PersonVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<PersonVO> al = new ArrayList<PersonVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        PersonVO[] ret = new PersonVO[al.size()];
        al.toArray(ret);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return ret;
    }

    public PersonVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        PersonVO vo = getObjectFactory().createPerson();
        vo.setPersonId(_rs.getPersonId(Scheme.COLUMN_ID));
        vo.setName(_rs.getString(Scheme.PERSON_COLUMN_NAME));
        vo.setBeschreibung(_rs.getString(Scheme.COLUMN_BESCHREIBUNG));
        vo.setGeloescht(_rs.getBooleanNN(Scheme.COLUMN_GELOESCHT));
        vo.setVorname(_rs.getString(Scheme.PERSON_COLUMN_VORNAME));
        vo.setNachname(_rs.getString(Scheme.PERSON_COLUMN_NACHNAME));
        vo.setPin(new Pin(_rs.getString(Scheme.PERSON_COLUMN_PIN)));
        vo.setPassword(_rs.getString(Scheme.PERSON_COLUMN_PASSWD));
        vo.setAbwesendBis(_rs.getUnixTime(Scheme.PERSON_COLUMN_ABWESEND_BIS));
        vo.setEmail(_rs.getString(Scheme.PERSON_COLUMN_EMAIL));
        vo.setBereichId(_rs.getBereichId(Scheme.PERSON_COLUMN_BEREICH_ID));
        vo
                        .setOEKostenstelle(_rs
                                        .getOrganisationsEinheitId(Scheme.PERSON_COLUMN_OE_KOSTENSTELLE));
        // 2006-07-12 CKL: Abwesend von
        vo.setAbwesendVon(_rs.getUnixTime(Scheme.PERSON_COLUMN_ABWESEND_VON));

        vo
                        .setFunktionstraegerId(_rs
                                        .getFunktionstraegerId(Scheme.PERSON_COLUMN_FUNKTIONSTRAEGER_ID));

        vo.setReportOptionen(_rs.getMap(Scheme.PERSON_COLUMN_REPORT_OPTIONEN));
        vo
                        .setInFolgeschleife(_rs
                                        .getBooleanNN(Scheme.PERSON_COLUMN_IST_IN_FOLGESCHLEIFE));

        PersonId erstelltVonPersonId = _rs
                        .getPersonId(Scheme.PERSON_COLUMN_ERSTELLT_VON);

        if (erstelltVonPersonId != null)
        {
            vo.setErstelltVon(_rs
                            .getPersonId(Scheme.PERSON_COLUMN_ERSTELLT_VON));
        }

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    public BaseIdVO create(BaseIdVO _vo) throws StdException
    {
        return createPerson((PersonVO) _vo);
    }

    public BaseIdVO update(BaseIdVO _vo) throws StdException
    {
        return updatePerson((PersonVO) _vo);
    }

    public void delete(BaseId _id) throws StdException
    {
        deletePerson((PersonId) _id);
    }

    /*
     * 
     * 
     * queries
     */

    /**
     * F?r eine Person in einer Rolle Berechtigungen auf das System hinzuf?gen
     * 
     */
    public void addPersonInRolleToSystem(PersonId _personId, RolleId _rolleId) throws StdException
    {
        PreparedStatement pst = getPstAddPersonInRolleToSystem();
        BaseId id = getDBConnection().nextBaseId();
        pst.setBaseId(1, id);
        pst.setPersonId(2, _personId);
        pst.setRolleId(3, _rolleId);
        pst.execute();
        pst.close();
    }

    /**
     * Anzahl der (nicht geloeschten) Personen zaehlen
     */
    public long countPersonen() throws StdException
    {
        PreparedStatement pst = getPstCountPersonen();
        ResultSet rs = pst.executeQuery();

        long r = 0;

        if (rs.next())
        {
            r = rs.getLong(1).longValue();
        }

        rs.close();
        pst.close();
        return r;
    }

    /**
     * Eine Person anlegen. Wenn die Person bereits als gelöscht existiert wird
     * der alte Datensatz reaktiviert (undelete) und erneut verwendet.
     * 
     * @param _personVO
     * @return
     * @throws StdException
     */
    public PersonVO createPerson(PersonVO _personVO) throws StdException
    {
        PersonVO vo = findPersonByName(_personVO.getName());

        if (vo != null)
        {
            if (vo.getGeloescht())
            {
                undeletePerson(vo.getPersonId());
                _personVO.setPersonId(vo.getPersonId());
                return updatePerson(_personVO);
            }
            else
            {
                throw new StdException("Person id=" + vo.getPersonId()
                                + " existiert bereits.");
            }
        }
        else
        {
            PersonId id = new PersonId(dbconnection.nextId());
            PreparedStatement pst = getPstCreatePerson();
            pst.setPersonId(1, id);
            pst.setString(2, _personVO.getName());
            pst.setString(3, _personVO.getVorname());
            pst.setString(4, _personVO.getNachname());
            pst.setString(5, _personVO.getBeschreibung());
            pst.setString(6, _personVO.getEmail());
            pst.setPin(7, _personVO.getPin());
            pst.setString(8, _personVO.getPassword());
            pst.setUnixTime(9, _personVO.getAbwesendBis());
            pst.setOrganisationsEinheitId(10, _personVO.getOEKostenstelle());
            // 2006-07-12 CKL: Abwesend von
            pst.setUnixTime(11, _personVO.getAbwesendVon());
            // 2007-06-07 CKL: Funktionstraeger
            pst.setFunktionstraegerId(12, _personVO.getFunktionstraegerId());
            // 2009-11-23 CKL: Bereich
            pst.setBereichId(13, _personVO.getBereichId());
            pst.setMap(14, _personVO.getReportOptionen());
            pst.setBoolean(15, _personVO.isInFolgeschleife());
            pst.setPersonId(16, _personVO.getErstelltVon());

            pst.execute();
            pst.close();
            return findPersonById(id);
        }
    }

    /**
     * Liefert alle Personen.
     * 
     * @return
     * @throws StdException
     */
    public PersonVO[] findAll() throws StdException
    {
        if (CACHE_FIND_ALL.findMultiple() != null)
        {
            return CACHE_FIND_ALL.findMultiple();
        }

        PreparedStatement pst = getPstFindAll();
        PersonVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Eine Person unter Angabe einer Id suchen. Wenn die Person nicht gefunden
     * wurde dann liefert diese Methode null.
     * 
     * @param _personId
     * @return
     * @throws StdException
     */
    public PersonVO findPersonById(PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonById();
        pst.setPersonId(1, _personId);
        PersonVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Eine Person unter Angabe des Login-Namens suchen. Wenn die Person nicht
     * gefunden wurde dann liefert diese Methode null. <br />
     * Die Methode ist <strong>case-insensitive</strong>
     * 
     * @param _name
     * @return
     * @throws StdException
     */
    public PersonVO findPersonByName(String _name) throws StdException
    {
        PreparedStatement pst = getPstFindPersonByName();
        pst.setString(1, _name);
        PersonVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Eine Person unter Angabe der Email-Adresse suchen. Wenn die Person nicht
     * gefunden wurde dann liefert diese Methode null.
     * 
     * @param _name
     * @return
     * @throws StdException
     */
    public PersonVO findPersonByEmail(String _email) throws StdException
    {
        PreparedStatement pst = getPstFindPersonByEmail();
        pst.setString(1, _email);
        PersonVO r = nextToVO(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Eine Person unter Angabe des Login-Namens und der Pin suchen. Wenn die
     * Person nicht gefunden wurde dann liefert diese Methode null. Diese
     * Methode kann zur SMS-Authentifizierung verwendet werden.
     * 
     * @param _name
     * @param _pin
     * @return
     * @throws StdException
     */
    public PersonVO findPersonByNameAndPin(String _name, Pin _pin) throws StdException
    {
        PreparedStatement pst = getPstFindPersonByNameAndPin();
        pst.setString(1, _name);
        pst.setPin(2, _pin);
        PersonVO r = nextToVO(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Eine Person unter Angabe des Login-Namens und des Passworts suchen. Wenn
     * die Person nicht gefunden wurde dann liefert diese Methode null. Diese
     * Methode kann zur Web-Authentifizierung verwendet werden.
     * 
     * @param _name
     * @param _pin
     * @return
     * @throws StdException
     */
    public PersonVO findPersonByNameAndPasswd(String _name, String _passwd) throws StdException
    {
        PreparedStatement pst = getPstFindPersonByNameAndPasswd();
        pst.setString(1, _name);
        pst.setString(2, _passwd);
        PersonVO r = nextToVO(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Eine Person unter Angabe der (Handy-)Nummer suchen. Wenn die Person nicht
     * gefunden wurde dann liefert diese Methode null. Es werden nur alle
     * aktiven Telefonnummern in Betracht gezogen.
     * 
     * @param _telefonNummer
     * @return
     * @throws StdException
     */
    public PersonVO findPersonByAktiverTelefonNummer(
                    TelefonNummer _telefonNummer) throws StdException
    {
        PreparedStatement pst = getPstFindPersonByAktiverTelefonNummer();
        pst.setTelefonNummer(1, _telefonNummer);
        PersonVO r = nextToVO(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Eine Person unter Angabe der (Handy-)Nummer suchen. Wenn die Person nicht
     * gefunden wurde dann liefert diese Methode null. Diese Methode durchsucht
     * auch alle inaktiven Rufnummern.
     * 
     * @param _telefonNummer
     * @return
     * @throws StdException
     */
    public PersonVO findPersonByTelefonNummer(TelefonNummer _telefonNummer) throws StdException
    {
        PreparedStatement pst = getPstFindPersonByTelefonNummer();
        pst.setTelefonNummer(1, _telefonNummer);
        PersonVO r = nextToVO(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Eine Person unter Angabe einer TelefonId suchen. Wenn die Person nicht
     * gefunden wurde dann liefert diese Methode null.
     * 
     * @param _telefonId
     * @return
     * @throws StdException
     */
    public PersonVO findPersonByTelefonId(TelefonId _telefonId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonByTelefonId();
        pst.setTelefonId(1, _telefonId);
        PersonVO r = nextToVO(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Sucht alle Personen, die auf die ggb. Patterns passen (wildcards sind %
     * und _)
     * 
     * @param _name
     * @param _vorname
     * @param _nachname
     * @param _beschreibung
     * @return
     * @throws StdException
     */
    public PersonVO[] findPersonenByPattern(String _name, String _vorname,
                    String _nachname, String _beschreibung) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenByPattern();
        // 2006-05-24 CKL: Spezielle Zeichen aus Suchanfrage entfernen
        pst.setString(1, StringUtils.removeSpecialCharsForSQLRegExp(_name));
        pst.setString(2, StringUtils.removeSpecialCharsForSQLRegExp(_vorname));
        pst.setString(3, StringUtils.removeSpecialCharsForSQLRegExp(_nachname));
        PersonVO[] r = toVOs(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Sucht alle Personen, die die ggb. Berechtigung in Bezug auf die ggb.
     * Schleife haben. Wenn keine Personen gefunden wurden dann wird ein leeres
     * Array zurückgegeben.
     * 
     * Diese Methode berücksichtigt auch vererbte Rechte aus ?bergeordneten
     * Objekten (Organisationseinheit,Organisation,System)
     * 
     * @param _rechtId
     * @param _schleifeId
     * @return
     * @throws StdException
     */
    public PersonVO[] findPersonenByRechtInSchleife(RechtId _rechtId,
                    SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenByRechtInSchleife();
        pst.setSchleifeId(1, _schleifeId);
        pst.setRechtId(2, _rechtId);
        pst.setSchleifeId(3, _schleifeId);
        pst.setRechtId(4, _rechtId);
        pst.setSchleifeId(5, _schleifeId);
        pst.setRechtId(6, _rechtId);
        pst.setRechtId(7, _rechtId);
        PersonVO[] r = toVOs(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Sucht alle Personen, die die ggb. Berechtigung in Bezug auf die ggb.
     * Organisationseinheit haben. Wenn keine Personen gefunden wurden dann wird
     * ein leeres Array zurückgegeben.
     * 
     * Diese Methode berücksichtigt auch vererbte Rechte aus ?bergeordneten
     * Objekten (Organisation,System)
     * 
     * @param _rechtId
     * @param _organisationsEinheitId
     * @return
     * @throws StdException
     */
    public PersonVO[] findPersonenByRechtInOrganisationseinheit(
                    RechtId _rechtId,
                    OrganisationsEinheitId _organisationsEinheitId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenByRechtInOrganisationseinheit();
        pst.setOrganisationsEinheitId(1, _organisationsEinheitId);
        pst.setRechtId(2, _rechtId);
        pst.setOrganisationsEinheitId(3, _organisationsEinheitId);
        pst.setRechtId(4, _rechtId);
        pst.setRechtId(5, _rechtId);
        PersonVO[] r = toVOs(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Sucht alle Personen, die die ggb. Berechtigung in Bezug auf die ggb.
     * Organisation haben. Wenn keine Personen gefunden wurden dann wird ein
     * leeres Array zurückgegeben.
     * 
     * Diese Methode berücksichtigt auch vererbte Rechte aus übergeordneten
     * Objekten (System)
     * 
     * @param _rechtId
     * @param _organisationId
     * @return
     * @throws StdException
     */
    public PersonVO[] findPersonenByRechtInOrganisation(RechtId _rechtId,
                    OrganisationId _organisationId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenByRechtInOrganisation();
        pst.setOrganisationId(1, _organisationId);
        pst.setRechtId(2, _rechtId);
        pst.setRechtId(3, _rechtId);
        PersonVO[] r = toVOs(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Sucht alle Personen, die die ggb. Berechtigung in Bezug auf das System
     * haben. Wenn keine Personen gefunden wurden dann wird ein leeres Array
     * zurückgegeben.
     * 
     * @param _rechtId
     * @return
     * @throws StdException
     */
    public PersonVO[] findPersonenByRechtInSystem(RechtId _rechtId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenByRechtInSystem();
        pst.setRechtId(1, _rechtId);
        PersonVO[] r = toVOs(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Liefert alle Personen, denen eine Rolle in Bezug auf das System
     * zugewiesen ist.
     * 
     * Diese Methode berücksichtigt *keine* vererbten Rechte!
     * 
     */
    public PersonVO[] findPersonenInSystem() throws StdException
    {
        PreparedStatement pst = getPstFindPersonenInSystem();
        PersonVO[] r = toVOs(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Liefert alle Personen, denen eine Rolle in Bezug auf eine Organisation
     * zugewiesen ist.
     * 
     * Diese Methode berücksichtigt *keine* vererbten Rechte!
     * 
     */
    public PersonVO[] findPersonenInOrganisation(OrganisationId _orgId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenInOrganisation();
        pst.setOrganisationId(1, _orgId);
        PersonVO[] r = toVOs(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Liefert alle Personen, denen eine Rolle in Bezug auf eine
     * Organisationseinheit zugewiesen ist.
     * 
     * Diese Methode berücksichtigt *keine* vererbten Rechte!
     * 
     */
    public PersonVO[] findPersonenInOrganisationseinheit(
                    OrganisationsEinheitId _orgEinheitId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenInOrganisationseinheit();
        pst.setOrganisationsEinheitId(1, _orgEinheitId);
        PersonVO[] r = toVOs(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Liefert alle Personen, denen eine Rolle in Bezug auf eine Schleife
     * zugewiesen ist.
     * 
     * Diese Methode berücksichtigt *keine* vererbten Rechte!
     * 
     */
    public PersonVO[] findPersonenInSchleife(SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenInSchleife();
        pst.setSchleifeId(1, _schleifeId);
        PersonVO[] r = toVOs(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Liefert alle Personen, denen die ggb. Rolle in Bezug auf eine Schleife
     * zugewiesen ist.
     * 
     * Diese Methode berücksichtigt *keine* vererbten Rechte!
     * 
     */
    public PersonVO[] findPersonenByRolleInSchleife(RolleId _rolleId,
                    SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenByRolleInSchleife();
        pst.setRolleId(1, _rolleId);
        pst.setSchleifeId(2, _schleifeId);
        PersonVO[] r = toVOs(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Liefert alle Personen, denen die ggb. Rolle in Bezug auf eine
     * Organisationseinheit zugewiesen ist
     * 
     * Diese Methode berücksichtigt *keine* vererbten Rechte!
     * 
     */
    public PersonVO[] findPersonenByRolleInOrganisationseinheit(
                    RolleId _rolleId, OrganisationsEinheitId _oeId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenByRolleInOrganisationseinheit();
        pst.setRolleId(1, _rolleId);
        pst.setOrganisationsEinheitId(2, _oeId);
        PersonVO[] r = toVOs(pst.executeQuery(), false);
        pst.close();
        return r;
    }

    /**
     * Liefert alle Personen, denen die ggb. Rolle in Bezug auf eine
     * Organisation zugewiesen ist
     * 
     * Diese Methode berücksichtigt *keine* vererbten Rechte!
     * 
     */
    public PersonVO[] findPersonenByRolleInOrganisation(RolleId _rolleId,
                    OrganisationId _oeId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenByRolleInOrganisation();
        pst.setRolleId(1, _rolleId);
        pst.setOrganisationId(2, _oeId);
        PersonVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Personen, denen die ggb. Rolle in Bezug auf das System
     * zugewiesen ist
     * 
     */
    public PersonVO[] findPersonenByRolleInSystem(RolleId _rolleId) throws StdException
    {
        PreparedStatement pst = getPstFindPersonenByRolleInSystem();
        pst.setRolleId(1, _rolleId);
        PersonVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Personen zurück, die **keine** Rolle im System, einer
     * Organisation, einer Organisationseinheit oder einer Schleife besitzen.
     * Damit lässt sich heraus finden, welche Personen frei im System
     * herumirren.
     * 
     * @return
     * @throws StdException
     */
    public PersonVO[] findPersonenOhneRolleInSystem() throws StdException
    {
        PreparedStatement pst = getPstFindPersonenOhneRolleInSystem();
        PersonVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Personen zurück, die **keine** aktive Handynummer besitzen.
     * Als Handynummer wird erkannt, wenn die Nummer mit 004915, 004916 oder
     * 004917 beginnt.
     * 
     * @return
     * @throws StdException
     */
    public PersonVO[] findPersonenOhneHandyNummer() throws StdException
    {
        PreparedStatement pst = getPstFindPersonenOhneHandyNummer();
        PersonVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * HelperMethode, welche testet ob in einem Array von Personen eine
     * bestimmte Person enthalten ist.
     * 
     * @param _personId
     * @param _personVOs
     * @return
     */
    private boolean contains(PersonId _personId, PersonVO[] _personVOs)
    {
        for (int i = 0; i < _personVOs.length; i++)
        {
            PersonVO personVO = _personVOs[i];
            if (_personId.equals(personVO.getPersonId()))
                return true;
        }
        return false;
    }

    /**
     * Testet ob eine Person ein bestimmtes Recht in Bezug auf eine Schleife
     * hat.
     * 
     * @param _personId
     * @param _rechtId
     * @param _schleifeId
     * @return
     * @throws StdException
     */
    public boolean hatPersonRechtInSchleife(PersonId _personId,
                    RechtId _rechtId, SchleifeId _schleifeId) throws StdException
    {
        return contains(_personId, findPersonenByRechtInSchleife(_rechtId,
                        _schleifeId));
    }

    /**
     * Testet ob eine Person ein bestimmtes Recht in Bezug auf eine
     * Organisationseinheit hat.
     * 
     * @param _personId
     * @param _rechtId
     * @param _organisationsEinheitId
     * @return
     * @throws StdException
     */
    public boolean hatPersonRechtInOrganisationseinheit(PersonId _personId,
                    RechtId _rechtId,
                    OrganisationsEinheitId _organisationsEinheitId) throws StdException
    {
        return contains(_personId, findPersonenByRechtInOrganisationseinheit(
                        _rechtId, _organisationsEinheitId));
    }

    /**
     * Testet ob eine Person ein bestimmtes Recht in Bezug auf eine Organisation
     * hat.
     * 
     * @param _personId
     * @param _rechtId
     * @param _organisationId
     * @return
     * @throws StdException
     */
    public boolean hatPersonRechtInOrganisation(PersonId _personId,
                    RechtId _rechtId, OrganisationId _organisationId) throws StdException
    {
        return contains(_personId, findPersonenByRechtInOrganisation(_rechtId,
                        _organisationId));
    }

    /**
     * Testet ob eine Person ein bestimmtes Recht in Bezug auf das System hat.
     * 
     * @param _personId
     * @param _rechtId
     * @return
     * @throws StdException
     */
    public boolean hatPersonRechtInSystem(PersonId _personId, RechtId _rechtId) throws StdException
    {
        return contains(_personId, findPersonenByRechtInSystem(_rechtId));
    }

    /**
     * Testet ob die Person die ggb. Rolle in Bezug auf das System hat.
     * 
     */
    public boolean hatPersonRolleInSystem(PersonId _personId, RolleId _rolleId) throws StdException
    {
        PreparedStatement pst = getPstHatPersonRolleInSystem();
        pst.setPersonId(1, _personId);
        pst.setRolleId(2, _rolleId);
        boolean r = pst.executeQuery().next();

        pst.close();
        return r;
    }

    /**
     * Testet ob die Person die ggb. Rolle in Bezug auf eine Organisation hat.
     * Vererbte Rollen werden *nicht* ber?cksichtigt.
     * 
     */
    public boolean hatPersonRolleInOrganisationNichtVererbt(PersonId _personId,
                    RolleId _rolleId, OrganisationId _organisationId) throws StdException
    {
        PreparedStatement pst = getPstHatPersonRolleInOrganisationNichtVererbt();
        pst.setPersonId(1, _personId);
        pst.setRolleId(2, _rolleId);
        pst.setOrganisationId(3, _organisationId);
        boolean r = pst.executeQuery().next();

        pst.close();
        return r;
    }

    /**
     * Testet ob die Person die ggb. Rolle in Bezug auf eine
     * Organisationseinheit hat. Vererbte Rollen werden *nicht* berücksichtigt.
     * 
     */
    public boolean hatPersonRolleInOrganisationseinheitNichtVererbt(
                    PersonId _personId, RolleId _rolleId,
                    OrganisationsEinheitId _organisationsEinheitId) throws StdException
    {
        PreparedStatement pst = getPstHatPersonRolleInOrganisationseinheitNichtVererbt();
        pst.setPersonId(1, _personId);
        pst.setRolleId(2, _rolleId);
        pst.setOrganisationsEinheitId(3, _organisationsEinheitId);
        boolean r = pst.executeQuery().next();

        pst.close();
        return r;
    }

    /**
     * Testet ob die Person die ggb. Rolle in Bezug auf eine Schleife hat.
     * Vererbte Rollen werden *nicht* berücksichtigt.
     * 
     */
    public boolean hatPersonRolleInSchleifeNichtVererbt(PersonId _personId,
                    RolleId _rolleId, SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstHatPersonRolleInSchleifeNichtVererbt();
        pst.setPersonId(1, _personId);
        pst.setRolleId(2, _rolleId);
        pst.setSchleifeId(3, _schleifeId);
        boolean r = pst.executeQuery().next();

        pst.close();
        return r;

    }

    /**
     * Löscht eine Person. Der Datenbankeintrag wird *nicht* entfernt sondern
     * nur als geloescht markiert.
     * 
     * @param _personId
     * @throws StdException
     */
    public void deletePerson(PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstDeletePerson();
        pst.setPersonId(1, _personId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht einer Person alle Berechtigungen (d.h. alle Rollen) auf das
     * System.
     * 
     * @param _personId
     * @throws StdException
     */
    public void removePersonFromSystem(PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstRemovePersonFromSystem();
        pst.setPersonId(1, _personId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht einer Person eine bestimmte Rolle in Bezug auf das System.
     * 
     * @param _personId
     * @param _rolleId
     * @throws StdException
     */
    public void removePersonInRolleFromSystem(PersonId _personId,
                    RolleId _rolleId) throws StdException
    {
        PreparedStatement pst = getPstRemovePersonInRolleFromSystem();
        pst.setPersonId(1, _personId);
        pst.setRolleId(2, _rolleId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht allen Personen in der ggb. Rolle die Berechtigungen auf das
     * System.
     * 
     * @param _rolleId
     * @throws StdException
     */
    public void removeRolleFromSystem(RolleId _rolleId) throws StdException
    {
        PreparedStatement pst = getPstRemoveRolleFromSystem();
        pst.setRolleId(1, _rolleId);
        pst.execute();
        pst.close();
    }

    /**
     * Setzt die Zuweisung des Bereiches neu
     * 
     * @param _bereichZuweisungAlt
     * @param _bereichZuweisungNeu
     */
    public void removeBereichZuweisung(BereichId _bereichZuweisungAlt,
                    BereichId _bereichZuweisungNeu) throws StdException
    {
        PreparedStatement pst = getPstRemoveBereichZuweisung();
        pst.setBereichId(1, _bereichZuweisungNeu);
        pst.setBereichId(2, _bereichZuweisungAlt);
        pst.execute();
        pst.close();
    }

    /**
     * Setzt die Zuweisung eines Funktionstraegers neu
     * 
     * @param _funktionstraegerZuweisungAlt
     * @param _funktionstraegerZuweisungNeu
     */
    public void removeFunktionstraegerZuweisung(
                    FunktionstraegerId _funktionstraegerZuweisungAlt,
                    FunktionstraegerId _funktionstraegerZuweisungNeu) throws StdException
    {
        PreparedStatement pst = getPstRemoveFunktionstraegerZuweisung();
        pst.setFunktionstraegerId(1, _funktionstraegerZuweisungNeu);
        pst.setFunktionstraegerId(2, _funktionstraegerZuweisungAlt);
        pst.execute();
        pst.close();
    }

    /**
     * Hebt die "gelöscht" Markierung wieder auf.
     * 
     * @param _personId
     * @return
     * @throws StdException
     */
    public PersonVO undeletePerson(PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstUndeletePerson();
        pst.setPersonId(1, _personId);
        pst.execute();
        pst.close();

        return findPersonById(_personId);
    }

    /**
     * Ändert eine Person.
     * 
     * @param _personVO
     * @return
     * @throws StdException
     */
    public PersonVO updatePerson(PersonVO _personVO) throws StdException
    {
        PreparedStatement pst = getPstUpdatePerson();
        pst.setString(1, _personVO.getName());
        pst.setString(2, _personVO.getVorname());
        pst.setString(3, _personVO.getNachname());
        pst.setString(4, _personVO.getBeschreibung());
        pst.setString(5, _personVO.getEmail());
        pst.setPin(6, _personVO.getPin());
        pst.setString(7, _personVO.getPassword());
        pst.setUnixTime(8, _personVO.getAbwesendBis());
        pst.setOrganisationsEinheitId(9, _personVO.getOEKostenstelle());
        pst.setUnixTime(10, _personVO.getAbwesendVon());
        pst.setFunktionstraegerId(11, _personVO.getFunktionstraegerId());
        pst.setBereichId(12, _personVO.getBereichId());
        pst.setMap(13, _personVO.getReportOptionen());
        pst.setBoolean(14, _personVO.isInFolgeschleife());
        pst.setPersonId(15, _personVO.getErstelltVon());
        pst.setPersonId(16, _personVO.getPersonId());
        pst.execute();
        pst.close();

        return findPersonById(_personVO.getPersonId());
    }

    /*
     * 
     * 
     * Prepared statements
     */
    private PreparedStatement getPstAddPersonInRolleToSystem() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE + " ("
                        + Scheme.COLUMN_ID + ","
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                        + ","
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                        + ") VALUES(?,?,?)");
    }

    private PreparedStatement getPstCountPersonen() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT count("
                        + Scheme.COLUMN_ID + ") FROM " + Scheme.PERSON_TABLE
                        + " WHERE " + Scheme.COLUMN_GELOESCHT + "=false");
    }

    private PreparedStatement getPstCreatePerson() throws StdException
    {
        // 2006-07-12 CKL: Abwesend von
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.PERSON_TABLE + " (" + Scheme.COLUMN_ID + ","
                        + Scheme.PERSON_COLUMN_NAME + ","
                        + Scheme.PERSON_COLUMN_VORNAME + ","
                        + Scheme.PERSON_COLUMN_NACHNAME + ","
                        + Scheme.COLUMN_BESCHREIBUNG + ","
                        + Scheme.PERSON_COLUMN_EMAIL + ","
                        + Scheme.PERSON_COLUMN_PIN + ","
                        + Scheme.PERSON_COLUMN_PASSWD + ","
                        + Scheme.PERSON_COLUMN_ABWESEND_BIS + ","
                        + Scheme.PERSON_COLUMN_OE_KOSTENSTELLE + ","
                        + Scheme.PERSON_COLUMN_ABWESEND_VON + ","
                        + Scheme.PERSON_COLUMN_FUNKTIONSTRAEGER_ID + ","
                        + Scheme.PERSON_COLUMN_BEREICH_ID + ","
                        + Scheme.PERSON_COLUMN_REPORT_OPTIONEN + ", "
                        + Scheme.PERSON_COLUMN_IST_IN_FOLGESCHLEIFE + ", "
                        + Scheme.PERSON_COLUMN_ERSTELLT_VON
                        + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    }

    private PreparedStatement getPstFindAll() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PERSON_TABLE + " WHERE "
                        + Scheme.COLUMN_GELOESCHT + "=false ORDER BY "
                        + Scheme.PERSON_COLUMN_NACHNAME + ", "
                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PERSON_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + "=?");
    }

    private PreparedStatement getPstFindPersonByEmail() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PERSON_TABLE + " WHERE "
                        + Scheme.PERSON_COLUMN_EMAIL + "=?");
    }

    private PreparedStatement getPstFindPersonByName() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PERSON_TABLE + " WHERE "
                        + Scheme.PERSON_COLUMN_NAME + " ILIKE ?");
    }

    private PreparedStatement getPstFindPersonByNameAndPin() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PERSON_TABLE + " WHERE "
                        + Scheme.PERSON_COLUMN_NAME + "=? AND "
                        + Scheme.PERSON_COLUMN_PIN + "=?");
    }

    private PreparedStatement getPstFindPersonByNameAndPasswd() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PERSON_TABLE + " WHERE "
                        + Scheme.PERSON_COLUMN_NAME + "=? AND "
                        + Scheme.PERSON_COLUMN_PASSWD + "=? AND "
                        + Scheme.COLUMN_GELOESCHT + " = false");
    }

    private PreparedStatement getPstFindPersonenByPattern() throws StdException
    {
        /**
         * Diese Suche ist case-insensitive und NUR!!! auf PostgreSQL angepasst!
         */
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PERSON_TABLE + " WHERE ("
                        + Scheme.PERSON_COLUMN_NAME + " ~* ? OR "
                        + Scheme.PERSON_COLUMN_VORNAME + " ~* ? OR "
                        + Scheme.PERSON_COLUMN_NACHNAME + " ~* ? ) AND "
                        + Scheme.COLUMN_GELOESCHT + "=false " + "ORDER BY "
                        + Scheme.PERSON_COLUMN_NACHNAME + ", "
                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenInSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT p.* FROM "
                        + Scheme.PERSON_TABLE + " p,"
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE
                        + " piris WHERE piris."
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_PERSON_ID
                        + "=p." + Scheme.COLUMN_ID + " AND piris."
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + "=? AND p." + Scheme.COLUMN_GELOESCHT + "=false "
                        + "ORDER BY " + Scheme.PERSON_COLUMN_NACHNAME + ", "
                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenInOrganisationseinheit() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT p.* FROM "
                                        + Scheme.PERSON_TABLE
                                        + " p,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " pirioe WHERE pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=? AND p." + Scheme.COLUMN_GELOESCHT
                                        + "=false " + "ORDER BY "
                                        + Scheme.PERSON_COLUMN_NACHNAME + ", "
                                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenInOrganisation() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT p.* FROM "
                                        + Scheme.PERSON_TABLE
                                        + " p,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " pirio WHERE pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + "=? AND p." + Scheme.COLUMN_GELOESCHT
                                        + "=false " + "ORDER BY "
                                        + Scheme.PERSON_COLUMN_NACHNAME + ", "
                                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenInSystem() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT p.* FROM "
                        + Scheme.PERSON_TABLE + " p,"
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                        + " pirisys WHERE pirisys."
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                        + "=p." + Scheme.COLUMN_ID + " AND p."
                        + Scheme.COLUMN_GELOESCHT + "=false " + "ORDER BY "
                        + Scheme.PERSON_COLUMN_NACHNAME + ", "
                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenByRolleInSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT p.* FROM "
                        + Scheme.PERSON_TABLE + " p,"
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE
                        + " piris WHERE piris."
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_ROLLE_ID
                        + "=? AND piris."
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_PERSON_ID
                        + "=p." + Scheme.COLUMN_ID + " AND piris."
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + "=? AND p." + Scheme.COLUMN_GELOESCHT + "=false "
                        + "ORDER BY " + Scheme.PERSON_COLUMN_NACHNAME + ", "
                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenByRolleInOrganisationseinheit() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT p.* FROM "
                                        + Scheme.PERSON_TABLE
                                        + " p,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " pirioe WHERE pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID
                                        + "=? AND pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=? AND p." + Scheme.COLUMN_GELOESCHT
                                        + "=false " + "ORDER BY "
                                        + Scheme.PERSON_COLUMN_NACHNAME + ", "
                                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenByRolleInOrganisation() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT p.* FROM "
                                        + Scheme.PERSON_TABLE
                                        + " p,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " pirio WHERE pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + "=? AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + "=? AND p." + Scheme.COLUMN_GELOESCHT
                                        + "=false " + "ORDER BY "
                                        + Scheme.PERSON_COLUMN_NACHNAME + ", "
                                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenByRolleInSystem() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT p.* FROM "
                        + Scheme.PERSON_TABLE + " p,"
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                        + " pirisys WHERE pirisys."
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                        + "=? AND pirisys."
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                        + "=p." + Scheme.COLUMN_ID + " AND p."
                        + Scheme.COLUMN_GELOESCHT + "=false " + "ORDER BY "
                        + Scheme.PERSON_COLUMN_NACHNAME + ", "
                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenByRechtInSchleife() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT p.* FROM "
                                        // Rechte aus Schleife
                                        + Scheme.PERSON_TABLE
                                        + " p, "
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE
                                        + " piris WHERE piris."
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                                        + "=? AND piris."
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND piris."
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND p."
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false"
                                        // Rechte aus Organisationseinheit
                                        + " UNION SELECT p.* FROM "
                                        + Scheme.PERSON_TABLE
                                        + " p,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.SCHLEIFE_TABLE
                                        + " s,"
                                        + Scheme.ORGANISATIONSEINHEIT_TABLE
                                        + " oe,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " pirioe WHERE s."
                                        + Scheme.COLUMN_ID
                                        + "=? AND pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=s."
                                        + Scheme.SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + " AND pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND p."
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false"
                                        // Rechte aus Organisation
                                        + " UNION SELECT p.* FROM "
                                        + Scheme.PERSON_TABLE
                                        + " p,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.SCHLEIFE_TABLE
                                        + " s,"
                                        + Scheme.ORGANISATION_TABLE
                                        + " o,"
                                        + Scheme.ORGANISATIONSEINHEIT_TABLE
                                        + " oe,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " pirio WHERE s."
                                        + Scheme.COLUMN_ID
                                        + "=? AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + "=oe."
                                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_ORGANISATION_ID
                                        + " AND oe."
                                        + Scheme.COLUMN_ID
                                        + "=s."
                                        + Scheme.SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + " AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND p."
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false"
                                        // Rechte aus System
                                        + " UNION SELECT p.* FROM "
                                        + Scheme.PERSON_TABLE
                                        + " p,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                                        + " pirisy WHERE pirisy."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND pirisy."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND p." + Scheme.COLUMN_GELOESCHT
                                        + "=false  " + "ORDER BY "
                                        + Scheme.PERSON_COLUMN_NACHNAME + ", "
                                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenByRechtInOrganisationseinheit() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        // Rechte aus Organisationseinheit
                        "SELECT p.* FROM "
                                        + Scheme.PERSON_TABLE
                                        + " p,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.ORGANISATIONSEINHEIT_TABLE
                                        + " oe,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " pirioe WHERE pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=? AND pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND p."
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false"
                                        // Rechte aus Organisation
                                        + " UNION SELECT p.* FROM "
                                        + Scheme.PERSON_TABLE
                                        + " p,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.ORGANISATION_TABLE
                                        + " o,"
                                        + Scheme.ORGANISATIONSEINHEIT_TABLE
                                        + " oe,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " pirio WHERE pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + "=oe."
                                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_ORGANISATION_ID
                                        + " AND oe."
                                        + Scheme.COLUMN_ID
                                        + "=? AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND p."
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false"
                                        // Rechte aus System
                                        + " UNION SELECT p.* FROM "
                                        + Scheme.PERSON_TABLE
                                        + " p,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                                        + " pirisy WHERE pirisy."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND pirisy."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND p." + Scheme.COLUMN_GELOESCHT
                                        + "=false " + "ORDER BY "
                                        + Scheme.PERSON_COLUMN_NACHNAME + ", "
                                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenByRechtInOrganisation() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        // Rechte aus Organisation
                        "SELECT p.* FROM "
                                        + Scheme.PERSON_TABLE
                                        + " p,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.ORGANISATION_TABLE
                                        + " o,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " pirio WHERE pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + "=? AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND p."
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false"
                                        // Rechte aus System
                                        + " UNION SELECT p.* FROM "
                                        + Scheme.PERSON_TABLE
                                        + " p,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                                        + " pirisy WHERE pirisy."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND pirisy."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND p." + Scheme.COLUMN_GELOESCHT
                                        + "=false " + "ORDER BY "
                                        + Scheme.PERSON_COLUMN_NACHNAME + ", "
                                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonenByRechtInSystem() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        // Rechte aus System
                        "SELECT p.* FROM "
                                        + Scheme.PERSON_TABLE
                                        + " p,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir,"
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                                        + " pirisy WHERE pirisy."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                                        + "=p."
                                        + Scheme.COLUMN_ID
                                        + " AND pirisy."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                                        + "=rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + " AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND p." + Scheme.COLUMN_GELOESCHT
                                        + "=false " + "ORDER BY "
                                        + Scheme.PERSON_COLUMN_NACHNAME + ", "
                                        + Scheme.PERSON_COLUMN_VORNAME);
    }

    private PreparedStatement getPstFindPersonByTelefonId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT p.* FROM "
                        + Scheme.PERSON_TABLE + " p," + Scheme.TELEFON_TABLE
                        + " t WHERE p." + Scheme.COLUMN_ID + "=t."
                        + Scheme.TELEFON_COLUMN_PERSON_ID + " AND t."
                        + Scheme.COLUMN_ID + "=?");
    }

    private PreparedStatement getPstFindPersonByAktiverTelefonNummer() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT p.* FROM "
                        + Scheme.PERSON_TABLE + " p, " + Scheme.TELEFON_TABLE
                        + " t WHERE p." + Scheme.COLUMN_ID + " t."
                        + Scheme.TELEFON_COLUMN_PERSON_ID + " AND t."
                        + Scheme.TELEFON_COLUMN_NUMMER + "=? AND t."
                        + Scheme.TELEFON_COLUMN_AKTIV + "=true");
    }

    private PreparedStatement getPstFindPersonByTelefonNummer() throws StdException
    {
        // 2006-11-23 CKL: Durch Zufall habe ich einen Fehler in der Abfrage
        // gefunden. AND hinzugefuegt.
        return new PreparedStatement(getDBConnection(), "SELECT p.* FROM "
                        + Scheme.PERSON_TABLE + " p, " + Scheme.TELEFON_TABLE
                        + " t WHERE p." + Scheme.COLUMN_ID + " = t."
                        + Scheme.TELEFON_COLUMN_PERSON_ID + " AND t."
                        + Scheme.TELEFON_COLUMN_NUMMER + "=?");
    }

    private PreparedStatement getPstDeletePerson() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.PERSON_TABLE + " SET "
                        + Scheme.COLUMN_GELOESCHT + "=true WHERE "
                        + Scheme.COLUMN_ID + "=?");
    }

    private PreparedStatement getPstHatPersonRolleInSystem() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT pirisys."
                        + Scheme.COLUMN_ID + " FROM "
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                        + " pirisys WHERE pirisys."
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                        + "=? AND "
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                        + "=?");
    }

    private PreparedStatement getPstHatPersonRolleInOrganisationNichtVererbt() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT pirio."
                                        + Scheme.COLUMN_ID
                                        + " FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " pirio WHERE pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + "=? AND "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + "=? AND "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + "=?");
    }

    private PreparedStatement getPstHatPersonRolleInOrganisationseinheitNichtVererbt() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT pirioe."
                                        + Scheme.COLUMN_ID
                                        + " FROM "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " pirioe WHERE pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + "=? AND "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID
                                        + "=? AND "
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=?");
    }

    private PreparedStatement getPstHatPersonRolleInSchleifeNichtVererbt() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT pirios."
                        + Scheme.COLUMN_ID + " FROM "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE
                        + " pirios WHERE pirios."
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_PERSON_ID
                        + "=? AND "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_ROLLE_ID
                        + "=? AND "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + "=?");
    }

    private PreparedStatement getPstRemovePersonFromSystem() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE + " WHERE "
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                        + "=?");
    }

    private PreparedStatement getPstRemovePersonInRolleFromSystem() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE + " WHERE "
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                        + "=? AND "
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                        + "=?");
    }

    private PreparedStatement getPstRemoveRolleFromSystem() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE + " WHERE "
                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                        + "=?;");
    }

    private PreparedStatement getPstUndeletePerson() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.PERSON_TABLE + " SET "
                        + Scheme.COLUMN_GELOESCHT + "=false WHERE "
                        + Scheme.COLUMN_ID + "=?");
    }

    private PreparedStatement getPstRemoveBereichZuweisung() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.PERSON_TABLE + " SET "
                        + Scheme.PERSON_COLUMN_BEREICH_ID + "=? WHERE "
                        + Scheme.PERSON_COLUMN_BEREICH_ID + "=?");
    }

    private PreparedStatement getPstRemoveFunktionstraegerZuweisung() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.PERSON_TABLE + " SET "
                        + Scheme.PERSON_COLUMN_FUNKTIONSTRAEGER_ID
                        + "=? WHERE "
                        + Scheme.PERSON_COLUMN_FUNKTIONSTRAEGER_ID + "=?");
    }

    private PreparedStatement getPstUpdatePerson() throws StdException
    {
        // 2006-07-12 CKL: Abwesend von
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.PERSON_TABLE + " SET "
                        + Scheme.PERSON_COLUMN_NAME + "=?,"
                        + Scheme.PERSON_COLUMN_VORNAME + "=?,"
                        + Scheme.PERSON_COLUMN_NACHNAME + "=?,"
                        + Scheme.COLUMN_BESCHREIBUNG + "=?,"
                        + Scheme.PERSON_COLUMN_EMAIL + "=?,"
                        + Scheme.PERSON_COLUMN_PIN + "=?,"
                        + Scheme.PERSON_COLUMN_PASSWD + "=?,"
                        + Scheme.PERSON_COLUMN_ABWESEND_BIS + "=?, "
                        + Scheme.PERSON_COLUMN_OE_KOSTENSTELLE + "=?, "
                        + Scheme.PERSON_COLUMN_ABWESEND_VON + "=?, "
                        + Scheme.PERSON_COLUMN_FUNKTIONSTRAEGER_ID + "=?, "
                        + Scheme.PERSON_COLUMN_BEREICH_ID + "=?, "
                        + Scheme.PERSON_COLUMN_REPORT_OPTIONEN + "=?, "
                        + Scheme.PERSON_COLUMN_IST_IN_FOLGESCHLEIFE + "=?, "
                        + Scheme.PERSON_COLUMN_ERSTELLT_VON + "=?" + " WHERE "
                        + Scheme.COLUMN_ID + "=?");
    }

    private PreparedStatement getPstFindPersonenOhneRolleInSystem() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PERSON_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + " NOT IN (SELECT DISTINCT " + Scheme.COLUMN_ID
                        + " FROM " + Scheme.VERERBTE_ROLLEN_VIEW + ") AND "
                        + Scheme.COLUMN_GELOESCHT + "=false");
    }

    private PreparedStatement getPstFindPersonenOhneHandyNummer() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.PERSON_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + " IN (SELECT " + Scheme.COLUMN_ID + " FROM (SELECT "
                        + Scheme.COLUMN_ID + ",  (SELECT COUNT(*) FROM "
                        + Scheme.TELEFON_TABLE + " WHERE "
                        + Scheme.TELEFON_COLUMN_PERSON_ID + " = p."
                        + Scheme.COLUMN_ID + " AND ("
                        + Scheme.TELEFON_COLUMN_NUMMER + " LIKE '004915%'  OR "
                        + Scheme.TELEFON_COLUMN_NUMMER + " LIKE '004916%' OR "
                        + Scheme.TELEFON_COLUMN_NUMMER
                        + " LIKE '004917%') AND " + Scheme.COLUMN_GELOESCHT
                        + " = false) AS anzahl_handy, (SELECT COUNT(*) FROM "
                        + Scheme.TELEFON_TABLE + " WHERE "
                        + Scheme.TELEFON_COLUMN_PERSON_ID + "= p."
                        + Scheme.COLUMN_ID + " AND ("
                        + Scheme.TELEFON_COLUMN_NUMMER
                        + " NOT LIKE '004915%' AND "
                        + Scheme.TELEFON_COLUMN_NUMMER
                        + " NOT LIKE '004916%' AND "
                        + Scheme.TELEFON_COLUMN_NUMMER
                        + " NOT LIKE '004917%') AND " + Scheme.COLUMN_GELOESCHT
                        + "= false) AS anzahl_festnetz FROM "
                        + Scheme.PERSON_TABLE + " p WHERE "
                        + Scheme.COLUMN_GELOESCHT
                        + "= false) AS sq_view WHERE anzahl_handy = 0)");
    }
}

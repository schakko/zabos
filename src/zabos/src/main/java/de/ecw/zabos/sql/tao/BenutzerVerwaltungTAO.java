package de.ecw.zabos.sql.tao;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.AbstractCreateUpdateDeleteDAO;
import de.ecw.zabos.sql.dao.BereichDAO;
import de.ecw.zabos.sql.dao.BereichInSchleifeDAO;
import de.ecw.zabos.sql.dao.FunktionstraegerDAO;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.BereichInSchleifeId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RolleId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;
import de.ecw.zabos.types.id.SchleifeId;
import de.ecw.zabos.types.id.TelefonId;

/**
 * Wrappt alle schreibenden (create/update/delete) DAO Aufrufe, die für die
 * Benutzerverwaltung relevant sind.
 * 
 * 
 * @author bsp
 * 
 */
public class BenutzerVerwaltungTAO extends BaseTAO
{

    private final static Logger log = Logger
                    .getLogger(BenutzerVerwaltungTAO.class);

    public BenutzerVerwaltungTAO(DBResource _dbresource)
    {
        super(_dbresource);
    }

    /**
     * Wrappt einen create() Aufruf in eine Transaktion
     * 
     * @param _dao
     * @param _vo
     * @return
     */
    private BaseIdVO create(AbstractCreateUpdateDeleteDAO _dao, BaseIdVO _vo)
    {
        try
        {
            begin();
            BaseIdVO r = _dao.create(_vo);
            commit();
            return r;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return null;
        }
    }

    /**
     * Wrappt einen delete() Aufruf in eine Transaktion
     * 
     * @param _dao
     * @param _vo
     * @return
     */
    private BaseIdVO update(AbstractCreateUpdateDeleteDAO _dao, BaseIdVO _vo)
    {
        try
        {
            begin();
            BaseIdVO r = _dao.update(_vo);
            commit();
            return r;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return null;
        }
    }

    /**
     * Wrappt einen delete() Aufruf in eine Transaktion
     * 
     * @param _dao
     * @param _id
     * @return
     */
    private boolean delete(AbstractCreateUpdateDeleteDAO _dao, BaseId _id)
    {
        try
        {
            begin();
            _dao.delete(_id);
            commit();
            return true;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return false;
        }
    }

    /**
     * Legt eine Organisation an
     * 
     * @param _organisationVO
     * @return
     */
    public synchronized OrganisationVO createOrganisation(
                    OrganisationVO _organisationVO)
    {
        OrganisationVO r = (OrganisationVO) create(
                        daoFactory.getOrganisationDAO(), _organisationVO);

        try
        {
            daoFactory.getOrganisationDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches nach dem Erstellen einer Organisation fehlgeschlagen",
                            e);
        }

        return r;

    }

    /**
     * Löscht eine Organisation und alle untergeordneten Objekte
     * (Organisationseinheiten, Schleifen, Zuordnungen von Personen)
     * 
     * Der Datensatz wird hierbei nicht aus der Datenbank gelöscht sondern nur
     * als gelöscht gekennzeichnet.
     * 
     * @param _organisationId
     * @return
     */
    public synchronized boolean deleteOrganisation(
                    OrganisationId _organisationId)
    {
        try
        {
            begin();

            OrganisationDAO oDAO = daoFactory.getOrganisationDAO();
            OrganisationsEinheitDAO oeDAO = daoFactory
                            .getOrganisationsEinheitDAO();
            OrganisationsEinheitVO[] oeVOs = oeDAO
                            .findOrganisationsEinheitenByOrganisationId(_organisationId);

            SchleifenDAO schleifenDAO = daoFactory.getSchleifenDAO();

            for (int i = 0; i < oeVOs.length; i++)
            {
                OrganisationsEinheitId oeId = oeVOs[i]
                                .getOrganisationsEinheitId();
                SchleifeVO[] schleifeVOs = schleifenDAO
                                .findSchleifenByOrganisationsEinheitId(oeId);
                // Zuordnungen von Personen zu Schleifen und Schleifen selber
                // löschen
                for (int j = 0; j < schleifeVOs.length; j++)
                {
                    SchleifeId schleifeId = schleifeVOs[j].getSchleifeId();
                    schleifenDAO.removeAllPersonenFromSchleife(schleifeId);
                    schleifenDAO.deleteSchleife(schleifeId);
                }

                // Zuordnungen von Personen zu OE und OE l�schen
                oeDAO.removeAllPersonenFromOrganisationseinheit(oeId);
                oeDAO.deleteOrganisationsEinheit(oeId);
            }

            // Zuordnungen von Personen zur Organisation und O selber löschen
            oDAO.removeAllPersonenFromOrganisation(_organisationId);

            oDAO.deleteOrganisation(_organisationId);

            commit();

            schleifenDAO.CACHE_FIND_ALL.update();
            oeDAO.CACHE_FIND_ALL.update();
            oDAO.CACHE_FIND_ALL.update();

            return true;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return false;
        }
    }

    /**
     * Verändert eine Organisation
     * 
     * @param _organisationVO
     * @return
     */
    public synchronized OrganisationVO updateOrganisation(
                    OrganisationVO _organisationVO)
    {
        OrganisationVO r = (OrganisationVO) update(
                        daoFactory.getOrganisationDAO(), _organisationVO);
        try
        {
            daoFactory.getOrganisationDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches nach dem Aendern einer Organisation fehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Legt eine neue Organisationseinheit an
     * 
     * @param _organisationseinheitVO
     * @return
     */
    public synchronized OrganisationsEinheitVO createOrganisationseinheit(
                    OrganisationsEinheitVO _organisationseinheitVO)
    {
        OrganisationsEinheitVO r = (OrganisationsEinheitVO) create(
                        daoFactory.getOrganisationsEinheitDAO(),
                        _organisationseinheitVO);

        try
        {
            daoFactory.getOrganisationsEinheitDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches nach dem Erstellen einer neuen OE fehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Löscht eine Organisationseinheit und alle untergeordneten Objekte
     * (Schleifen und Zuordnungen von Personen zu Schleifen)
     * 
     * Der Datensatz wird hierbei nicht aus der Datenbank gelöscht sondern nur
     * als gelöscht gekennzeichnet.
     * 
     * @param _organisationseinheitId
     * @return
     */
    public synchronized boolean deleteOrganisationseinheit(
                    OrganisationsEinheitId _organisationseinheitId)
    {
        try
        {
            begin();

            SchleifenDAO schleifenDAO = daoFactory.getSchleifenDAO();
            OrganisationsEinheitDAO oeDAO = daoFactory
                            .getOrganisationsEinheitDAO();

            // Alle Schleifen zu der ggb. OE finden
            SchleifeVO[] schleifeVOs = schleifenDAO
                            .findSchleifenByOrganisationsEinheitId(_organisationseinheitId);

            // Die Zuordnungen von Personen und die Schleifen l�schen
            for (int i = 0; i < schleifeVOs.length; i++)
            {
                SchleifeId schleifeId = schleifeVOs[i].getSchleifeId();
                schleifenDAO.removeAllPersonenFromSchleife(schleifeId);
                schleifenDAO.deleteSchleife(schleifeId);
            }

            oeDAO.deleteOrganisationsEinheit(_organisationseinheitId);

            commit();

            schleifenDAO.CACHE_FIND_ALL.update();
            oeDAO.CACHE_FIND_ALL.update();

            return true;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return false;
        }
    }

    /**
     * Verändert eine Organisationseinheit
     * 
     * @param _orgEinheitVO
     * @return
     */
    public synchronized OrganisationsEinheitVO updateOrganisationsEinheit(
                    OrganisationsEinheitVO _orgEinheitVO)
    {
        OrganisationsEinheitVO r = (OrganisationsEinheitVO) update(
                        daoFactory.getOrganisationsEinheitDAO(), _orgEinheitVO);

        try
        {
            daoFactory.getOrganisationsEinheitDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches fuer alle Organisationseinheiten fehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Verändert den Leitungskommentar einer Person in einem Alarm
     * 
     * @param _alarmId
     * @param _personId
     * @param _kommentarLeitung
     */
    public synchronized void updateKommentarLeitung(AlarmId _alarmId,
                    PersonId _personId, String _kommentarLeitung)
    {
        try
        {
            begin();
            daoFactory.getPersonInAlarmDAO().updateKommentarLeitung(_alarmId,
                            _personId, _kommentarLeitung);

            commit();
        }
        catch (StdException e)
        {
            rollback();
            log.error(e);
        }
    }

    /**
     * Legt einen neuen Bereich an
     * 
     * @param _bereichVO
     * @return
     */
    public synchronized BereichVO createBereich(BereichVO _bereichVO)
    {
        BereichVO r = (BereichVO) create(daoFactory.getBereichDAO(), _bereichVO);

        try
        {
            daoFactory.getBereichDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches fuer alle Bereiche fehlgeschlagen", e);
        }

        return r;
    }

    /**
     * Legt einen neuen Bereich innerhalb einer Schleife an
     * 
     * @param _personVO
     * @return
     */
    public synchronized BereichInSchleifeVO createBereichInSchleife(
                    BereichInSchleifeVO _bereichInSchleifeVO)
    {
        BereichInSchleifeVO r = (BereichInSchleifeVO) create(
                        daoFactory.getBereichInSchleifeDAO(),
                        _bereichInSchleifeVO);

        try
        {
            daoFactory.getBereichInSchleifeDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches fuer Bereiche innerhalb der Schleife fehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Legt einen neuen Funktionsträger an
     * 
     * @param _funktionstraegerVO
     * @return
     */
    public synchronized FunktionstraegerVO createFunktionstraeger(
                    FunktionstraegerVO _funktionstraegerVO)
    {
        FunktionstraegerVO r = (FunktionstraegerVO) create(
                        daoFactory.getFunktionstraegerDAO(),
                        _funktionstraegerVO);

        try
        {
            daoFactory.getFunktionstraegerDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches fuer alle Funktionstraeger fehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Legt eine neue Person an
     * 
     * @param _personVO
     * @return
     */
    public synchronized PersonVO createPerson(PersonVO _personVO)
    {
        PersonVO r = (PersonVO) create(daoFactory.getPersonDAO(), _personVO);

        try
        {
            daoFactory.getPersonDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches fuer alle Personen fehlgeschlagen", e);
        }

        return r;
    }

    /**
     * Kennzeichnet einen Bereich innerhalb der Datenbank als gelöscht. Alle
     * Zuweisungen von Personen zu diesem Bereich werden aufgehoben, alle
     * Bereiche in einer Schleife mit dieser Bereichs-ID werden als gelöscht
     * gekennzeichnet.
     * 
     * 
     * @param _bereichId
     * @return
     */
    public synchronized boolean deleteBereich(BereichId _bereichId)
    {
        try
        {
            begin();
            BereichInSchleifeDAO bereichInSchleifeDAO = daoFactory
                            .getBereichInSchleifeDAO();
            PersonDAO personDAO = daoFactory.getPersonDAO();

            BereichDAO bereichDAO = daoFactory.getBereichDAO();

            personDAO.removeBereichZuweisung(_bereichId, null);

            BereichInSchleifeVO[] bereicheInSchleife = bereichInSchleifeDAO
                            .findBereicheInSchleifeByBereichId(_bereichId);

            if ((bereicheInSchleife != null) && (bereicheInSchleife.length > 0))
            {
                for (int i = 0, m = bereicheInSchleife.length; i < m; i++)
                {
                    bereichInSchleifeDAO
                                    .deleteBereichInSchleife(bereicheInSchleife[i]
                                                    .getBereichInSchleifeId());
                }
            }

            bereichDAO.deleteBereich(_bereichId);

            commit();

            bereichDAO.CACHE_FIND_ALL.update();

            bereichInSchleifeDAO.CACHE_FIND_ALL.update();

            return true;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return false;
        }
    }

    /**
     * Kennzeichnet einen Bereich einer Schleife als gelöscht.
     * 
     * @param _bereichInSchleifeId
     * @return
     */
    public synchronized boolean deleteBereichInSchleife(
                    BereichInSchleifeId _bereichInSchleifeId)
    {
        try
        {
            begin();
            BereichInSchleifeDAO bereichInSchleifeDAO = daoFactory
                            .getBereichInSchleifeDAO();
            bereichInSchleifeDAO.deleteBereichInSchleife(_bereichInSchleifeId);

            commit();

            bereichInSchleifeDAO.CACHE_FIND_ALL.update();

            return true;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return false;
        }
    }

    /**
     * Markiert den Eintrag als gelöscht. Alle Zuweisungen der Personen auf
     * diesen Funktionsträger werden aufgehoben. Die Bereiche einer Schleife,
     * die auf diesen Funktionsträger verweisen, werden als gelöscht
     * gekennzeichnet.
     * 
     * @param _funktionstraegerId
     * @return
     */
    public synchronized boolean deleteFunktionstraeger(
                    FunktionstraegerId _funktionstraegerId)
    {
        try
        {
            begin();
            FunktionstraegerDAO funktionstraegerDAO = daoFactory
                            .getFunktionstraegerDAO();
            PersonDAO personDAO = daoFactory.getPersonDAO();
            BereichInSchleifeDAO bereichInSchleifeDAO = daoFactory
                            .getBereichInSchleifeDAO();

            personDAO.removeFunktionstraegerZuweisung(_funktionstraegerId, null);

            BereichInSchleifeVO[] bereicheInSchleife = bereichInSchleifeDAO
                            .findBereicheInSchleifeByFunktionstraegerId(_funktionstraegerId);

            if ((bereicheInSchleife != null) && (bereicheInSchleife.length > 0))
            {
                for (int i = 0, m = bereicheInSchleife.length; i < m; i++)
                {
                    bereichInSchleifeDAO
                                    .deleteBereichInSchleife(bereicheInSchleife[i]
                                                    .getBereichInSchleifeId());
                }
            }

            funktionstraegerDAO.deleteFunktionstraeger(_funktionstraegerId);

            commit();

            funktionstraegerDAO.CACHE_FIND_ALL.update();

            bereichInSchleifeDAO.CACHE_FIND_ALL.update();

            return true;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return false;
        }
    }

    /**
     * Löscht eine Person und alle "person_in_role_in_..." Zuordnungen zu
     * Schleifen/O/OE/System.
     * 
     * Der Datensatz wird hierbei nicht aus der Datenbank gelöscht sondern nur
     * als gelöscht gekennzeichnet.
     * 
     * 
     * 
     * @param _personId
     * @return
     */
    public synchronized boolean deletePerson(PersonId _personId)
    {
        try
        {
            begin();
            PersonDAO personDAO = daoFactory.getPersonDAO();
            personDAO.removePersonFromSystem(_personId);

            OrganisationDAO organisationDAO = daoFactory.getOrganisationDAO();
            organisationDAO.removePersonFromAllOrganisationen(_personId);

            OrganisationsEinheitDAO organisationsEinheitDAO = daoFactory
                            .getOrganisationsEinheitDAO();
            organisationsEinheitDAO
                            .removePersonFromAllOrganisationseinheiten(_personId);

            SchleifenDAO schleifenDAO = daoFactory.getSchleifenDAO();
            schleifenDAO.removePersonFromAllSchleifen(_personId);

            personDAO.deletePerson(_personId);
            commit();

            personDAO.CACHE_FIND_ALL.update();

            return true;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return false;
        }
    }

    /**
     * Verändert eine Person
     * 
     * @param _personVO
     * @return
     */
    public synchronized PersonVO updatePerson(PersonVO _personVO)
    {
        PersonVO r = (PersonVO) update(daoFactory.getPersonDAO(), _personVO);

        try
        {
            daoFactory.getPersonDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches nach dem Aendern einer Person fehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Verändert einen Bereich
     * 
     * @param _bereichVO
     * @return
     */
    public synchronized BereichVO updateBereich(BereichVO _bereichVO)
    {
        BereichVO r = (BereichVO) update(daoFactory.getBereichDAO(), _bereichVO);

        try
        {
            daoFactory.getBereichDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches nach dem �ndern eines Bereichs fehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Verändert einen Bereich innerhalb einer Schleife
     * 
     * @param _bereichInSchleifeVO
     * @return
     */
    public synchronized BereichInSchleifeVO updateBereichInSchleife(
                    BereichInSchleifeVO _bereichInSchleifeVO)
    {
        BereichInSchleifeVO r = (BereichInSchleifeVO) update(
                        daoFactory.getBereichInSchleifeDAO(),
                        _bereichInSchleifeVO);

        try
        {
            daoFactory.getBereichInSchleifeDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches nach dem Aendern eines Bereichs in einer Schleife fehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Verändert einen Funktionsträger
     * 
     * @param _funktionstraegerVO
     * @return
     */
    public synchronized FunktionstraegerVO updateFunktionstraeger(
                    FunktionstraegerVO _funktionstraegerVO)
    {
        FunktionstraegerVO r = (FunktionstraegerVO) update(
                        daoFactory.getFunktionstraegerDAO(),
                        _funktionstraegerVO);

        try
        {
            daoFactory.getFunktionstraegerDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches nach dem Aendern eines Funktionstraegers fehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Updatet den Rückmeldungsstatus einer Person
     * 
     * @param _personId
     * @param _rsId
     */
    public synchronized void updateRueckmeldungStatus(PersonId _personId,
                    RueckmeldungStatusId _rsId)
    {
        try
        {
            begin();
            daoFactory.getPersonInAlarmDAO().updateRueckmeldungStatus(
                            _personId, _rsId);
            commit();
        }
        catch (StdException e)
        {
            log.error("Rueckmeldung-Status konnte nicht gesetzt werden: "
                            + e.getMessage());
            rollback();
        }

    }

    /**
     * Legt eine neue Rolle an
     * 
     * @param _rolleVO
     * @return
     */
    public synchronized RolleVO createRolle(RolleVO _rolleVO)
    {
        RolleVO r = (RolleVO) create(daoFactory.getRolleDAO(), _rolleVO);

        try
        {
            daoFactory.getRolleDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches nach dem Erstellen einer Rolle fehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Löscht eine Rolle und alle Zuordnungen von Personen in Rollen zu
     * Organisationen, Organisationseinheiten, Schleifen und System
     * 
     * @param _rolleId
     * @return
     */
    public synchronized boolean deleteRolle(RolleId _rolleId)
    {
        try
        {
            begin();

            OrganisationDAO organisationDAO = daoFactory.getOrganisationDAO();
            organisationDAO.removeRolleFromAllOrganisationen(_rolleId);

            OrganisationsEinheitDAO organisationsEinheitDAO = daoFactory
                            .getOrganisationsEinheitDAO();
            organisationsEinheitDAO
                            .removeRolleFromAllOrganisationseinheiten(_rolleId);
            SchleifenDAO schleifenDAO = daoFactory.getSchleifenDAO();
            schleifenDAO.removeRolleFromAllSchleifen(_rolleId);

            PersonDAO personDAO = daoFactory.getPersonDAO();
            personDAO.removeRolleFromSystem(_rolleId);

            RolleDAO rolleDAO = daoFactory.getRolleDAO();

            rolleDAO.removeRolle(_rolleId);

            commit();

            rolleDAO.CACHE_FIND_ALL.update();

            return true;
        }
        catch (StdException e)
        {
            log.error(e);
            rollback();
            return false;
        }
    }

    /**
     * Verändert eine Rolle
     * 
     * @param _rolleVO
     * @return
     */
    public synchronized RolleVO updateRolle(RolleVO _rolleVO)
    {
        RolleVO r = (RolleVO) update(daoFactory.getRolleDAO(), _rolleVO);

        try
        {
            daoFactory.getRolleDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches nach dem Aendern einer Rolle fehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Legt eine neue Schleife an
     * 
     * @param _schleifeVO
     * @return
     */
    public synchronized SchleifeVO createSchleife(SchleifeVO _schleifeVO)
    {
        SchleifeVO r = (SchleifeVO) create(daoFactory.getSchleifenDAO(),
                        _schleifeVO);

        try
        {
            daoFactory.getSchleifenDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches nach dem Erstellen einer Schleife fehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Löscht eine Schleife
     * 
     * Der Datensatz wird hierbei nicht aus der Datenbank gelöscht sondern nur
     * als gelöscht gekennzeichnet.
     * 
     * @param _schleifeId
     * @return
     */
    public synchronized boolean deleteSchleife(SchleifeId _schleifeId)
    {
        boolean r = delete(daoFactory.getSchleifenDAO(), _schleifeId);

        try
        {
            daoFactory.getSchleifenDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches nach dem Loeschen einer Schleifefehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Verändert eine Schleife
     * 
     * @param _schleifeVO
     * @return
     */
    public synchronized SchleifeVO updateSchleife(SchleifeVO _schleifeVO)
    {
        SchleifeVO r = (SchleifeVO) update(daoFactory.getSchleifenDAO(),
                        _schleifeVO);

        try
        {
            daoFactory.getSchleifenDAO().CACHE_FIND_ALL.update();
        }
        catch (StdException e)
        {
            log.error("Update des Caches nach dem Aendern einer Schleife fehlgeschlagen",
                            e);
        }

        return r;
    }

    /**
     * Legt ein neues (Mobil-)Telefon an
     * 
     * @param _telefonVO
     * @return
     */
    public synchronized TelefonVO createTelefon(TelefonVO _telefonVO)
    {
        return (TelefonVO) create(daoFactory.getTelefonDAO(), _telefonVO);
    }

    /**
     * Verändert ein (Mobil-)Telefon
     * 
     * @param _telefonVO
     * @return
     */
    public synchronized TelefonVO updateTelefon(TelefonVO _telefonVO)
    {
        return (TelefonVO) update(daoFactory.getTelefonDAO(), _telefonVO);
    }

    /**
     * Löscht ein (Mobil-)Telefon
     * 
     * Der Datensatz wird hierbei nicht aus der Datenbank gelöscht sondern nur
     * als gelöscht gekennzeichnet.
     * 
     * @param _telefonId
     * @return
     */
    public synchronized boolean deleteTelefon(TelefonId _telefonId)
    {
        return delete(daoFactory.getTelefonDAO(), _telefonId);
    }

}

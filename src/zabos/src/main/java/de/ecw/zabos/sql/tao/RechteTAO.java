package de.ecw.zabos.sql.tao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.RechtDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.RechtVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * Transaktionssichere Methoden für {@link RolleDAO}, {@link SchleifenDAO},
 * {@link OrganisationsEinheitDAO} und {@link OrganisationDAO}
 * 
 * @author ckl
 * 
 */

public class RechteTAO extends BaseTAO
{

    private final static Logger log = Logger.getLogger(RechteTAO.class);

    public RechteTAO(DBResource _dbresource)
    {
        super(_dbresource);
    }

    /**
     * Weist dem System eine Person in einer Rolle zu
     * 
     * @param _personId
     * @param _organisationId
     * @return
     */
    public synchronized boolean addPersonInRolleToSystem(PersonId _personId, RolleId _rolleId)
    {
        try
        {
            begin();
            PersonDAO personDAO = daoFactory.getPersonDAO();
            personDAO.addPersonInRolleToSystem(_personId, _rolleId);
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
     * Weist einer Organisation eine Person in einer Rolle zu
     * 
     * @param _personId
     * @param _organisationId
     * @return
     */
    public synchronized boolean addPersonInRolleToOrganisation(PersonId _personId,
                    RolleId _rolleId, OrganisationId _organisationId)
    {
        try
        {
            begin();
            OrganisationDAO organisationDAO = daoFactory.getOrganisationDAO();
            organisationDAO.addPersonInRolleToOrganisation(_personId, _rolleId,
                            _organisationId);
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
     * Weist einer Organisationseinheit eine Person in einer Rolle zu
     * 
     * @param _personId
     * @param _organisationsEinheitId
     * @return
     */
    public synchronized boolean addPersonInRolleToOrganisationseinheit(PersonId _personId,
                    RolleId _rolleId,
                    OrganisationsEinheitId _organisationsEinheitId)
    {
        try
        {
            begin();
            OrganisationsEinheitDAO organisationsEinheitDAO = daoFactory
                            .getOrganisationsEinheitDAO();
            organisationsEinheitDAO.addPersonInRolleToOrganisationseinheit(
                            _personId, _rolleId, _organisationsEinheitId);
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
     * Weist einer Schleife eine Person in einer Rolle zu
     * 
     * @param _personId
     * @param _schleifeId
     * @return
     */
    public synchronized boolean addPersonInRolleToSchleife(PersonId _personId,
                    RolleId _rolleId, SchleifeId _schleifeId)
    {
        try
        {
            begin();
            SchleifenDAO schleifeDAO = daoFactory.getSchleifenDAO();
            schleifeDAO.addPersonInRolleToSchleife(_personId, _rolleId,
                            _schleifeId);
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
     * Entfernt eine Person in einer Rolle aus dem System
     * 
     * @param _personId
     * @param _rolleId
     * @return
     */
    public synchronized boolean removePersonInRolleFromSystem(PersonId _personId,
                    RolleId _rolleId)
    {
        try
        {
            begin();
            PersonDAO personDAO = daoFactory.getPersonDAO();
            personDAO.removePersonInRolleFromSystem(_personId, _rolleId);
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
     * Entfernt eine Person in allen Rollen aus dem System
     * 
     * @param _personId
     * @return
     */
    public synchronized boolean removePersonFromSystem(PersonId _personId)
    {
        try
        {
            begin();
            PersonDAO personDAO = daoFactory.getPersonDAO();
            personDAO.removePersonFromSystem(_personId);
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
     * Entfernt eine Person in einer Rolle aus einer Organisation
     * 
     * @param _personId
     * @param _rolleId
     * @param _organisationId
     * @return
     */
    public synchronized boolean removePersonInRolleFromOrganisation(PersonId _personId,
                    RolleId _rolleId, OrganisationId _organisationId)
    {
        try
        {
            begin();
            OrganisationDAO organisationDAO = daoFactory.getOrganisationDAO();
            organisationDAO.removePersonInRolleFromOrganisation(_personId,
                            _rolleId, _organisationId);
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
     * Entfernt eine Person in allen Rollen aus einer Organisation
     * 
     * @param _personId
     * @param _organisationId
     * @return
     */
    public synchronized boolean removePersonFromOrganisation(PersonId _personId,
                    OrganisationId _organisationId)
    {
        try
        {
            begin();
            OrganisationDAO organisationDAO = daoFactory.getOrganisationDAO();
            organisationDAO.removePersonFromOrganisation(_personId,
                            _organisationId);
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
     * Entfernt eine Person in einer Rolle aus einer Organisationseinheit
     * 
     * @param _personId
     * @param _rolleId
     * @param _organisationsEinheitId
     * @return
     */
    public synchronized boolean removePersonInRolleFromOrganisationseinheit(
                    PersonId _personId, RolleId _rolleId,
                    OrganisationsEinheitId _organisationsEinheitId)
    {
        try
        {
            begin();
            OrganisationsEinheitDAO organisationsEinheitDAO = daoFactory
                            .getOrganisationsEinheitDAO();
            organisationsEinheitDAO
                            .removePersonInRolleFromOrganisationseinheit(
                                            _personId, _rolleId,
                                            _organisationsEinheitId);
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
     * Entfernt eine Person in allen Rollen aus einer Organisationseinheit
     * 
     * @param _personId
     * @param _organisationsEinheitId
     * @return
     */
    public synchronized boolean removePersonFromOrganisationseinheit(PersonId _personId,
                    OrganisationsEinheitId _organisationsEinheitId)
    {
        try
        {
            begin();
            OrganisationsEinheitDAO organisationsEinheitDAO = daoFactory
                            .getOrganisationsEinheitDAO();
            organisationsEinheitDAO.removePersonFromOrganisationseinheit(
                            _personId, _organisationsEinheitId);
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
     * Entfernt eine Person in einer Rolle aus einer Schleife
     * 
     * @param _personId
     * @param _rolleId
     * @param _schleifeId
     * @return
     */
    public synchronized boolean removePersonInRolleFromSchleife(PersonId _personId,
                    RolleId _rolleId, SchleifeId _schleifeId)
    {
        try
        {
            begin();
            SchleifenDAO schleifeDAO = daoFactory.getSchleifenDAO();
            schleifeDAO.removePersonInRolleFromSchleife(_personId, _rolleId,
                            _schleifeId);
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
     * Entfernt eine Person in allen Rollen aus einer Schleife
     * 
     * @param _personId
     * @param _schleifeId
     * @return
     */
    public synchronized boolean removePersonFromSchleife(PersonId _personId,
                    SchleifeId _schleifeId)
    {
        try
        {
            begin();
            SchleifenDAO schleifeDAO = daoFactory.getSchleifenDAO();
            schleifeDAO.removePersonFromSchleife(_personId, _schleifeId);
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
     * Liefert alle Rollen, welche die Person in Hinsicht auf die Schleife und
     * ihren eigenen Berechtigungen zuweisen darf
     * 
     * @param _personId
     * @param _schleifeId
     * @return
     */
    public RolleVO[] findKompatibleRollenByPersonInSchleife(PersonId _personId,
                    SchleifeId _schleifeId)
    {
        try
        {
            RechtDAO rechtDAO = daoFactory.getRechtDAO();

            RechtVO[] rechtVOs = rechtDAO.findRechteByPersonInSchleife(
                            _personId, _schleifeId);

            return findKompatibleRollen(rechtVOs);

        }
        catch (StdException e)
        {
            log.error(e);
            return new RolleVO[0];
        }
    }

    /**
     * Liefert alle Rollen, welche die Person in Hinsicht auf die
     * Organisationseinheit und ihren eigenen Berechtigungen zuweisen darf
     * 
     * @param _personId
     * @param _organisationsEinheitId
     * @return
     */
    public RolleVO[] findKompatibleRollenByPersonInOrganisationsEinheit(
                    PersonId _personId,
                    OrganisationsEinheitId _organisationsEinheitId)
    {
        try
        {
            RechtDAO rechtDAO = daoFactory.getRechtDAO();

            RechtVO[] rechtVOs = rechtDAO
                            .findRechteByPersonInOrganisationsEinheit(
                                            _personId, _organisationsEinheitId);

            return findKompatibleRollen(rechtVOs);

        }
        catch (StdException e)
        {
            log.error(e);
            return new RolleVO[0];
        }
    }

    /**
     * Liefert alle Rollen, welche die Person in Hinsicht auf die Organisation
     * und ihren eigenen Berechtigungen zuweisen darf
     * 
     * @param _personId
     * @param _organisationId
     * @return
     */
    public RolleVO[] findKompatibleRollenByPersonInOrganisation(
                    PersonId _personId, OrganisationId _organisationId)
    {
        try
        {
            RechtDAO rechtDAO = daoFactory.getRechtDAO();

            RechtVO[] rechtVOs = rechtDAO.findRechteByPersonInOrganisation(
                            _personId, _organisationId);

            return findKompatibleRollen(rechtVOs);

        }
        catch (StdException e)
        {
            log.error(e);
            return new RolleVO[0];
        }
    }

    /**
     * Liefert alle Rollen, welche die Person in Hinsicht auf das System und
     * ihren eigenen Berechtigungen zuweisen darf
     * 
     * @param _personId
     * @return
     */
    public RolleVO[] findKompatibleRollenByPersonInSystem(PersonId _personId)
    {
        try
        {
            RechtDAO rechtDAO = daoFactory.getRechtDAO();

            RechtVO[] rechtVOs = rechtDAO.findRechteByPersonInSystem(_personId);

            return findKompatibleRollen(rechtVOs);

        }
        catch (StdException e)
        {
            log.error(e);
            return new RolleVO[0];
        }
    }

    private RolleVO[] findKompatibleRollen(RechtVO[] rechtVOs) throws StdException
    {
        RechtDAO rechtDAO = daoFactory.getRechtDAO();
        RolleDAO rolleDAO = daoFactory.getRolleDAO();

        List<RolleVO> al = new ArrayList<RolleVO>();

        // Alle Rollen iterieren
        RolleVO[] rolleVOs = rolleDAO.findAll();

        for (int i = 0; i < rolleVOs.length; i++)
        {
            RolleVO rolleVO = rolleVOs[i];

            RechtVO[] rolleRechteVOs = rechtDAO.findRechteByRolleId(rolleVO
                            .getRolleId());

            // Sind alle Rechte der aktuellen Rolle in rechtVOs vorhanden?

            boolean bOk = true;
            for (int j = 0; bOk && (j < rolleRechteVOs.length); j++)
            {
                RechtVO rolleRechtVO = rolleRechteVOs[j];
                RechtId rolleRechtId = rolleRechtVO.getRechtId();
                boolean bNotFound = true;
                // Testen ob das jeweilige Recht der akt. Rolle in der Menge
                // der Benutzerberechtigungen vorhanden ist
                for (int k = 0; bNotFound && (k < rechtVOs.length); k++)
                {
                    RechtVO rechtVO = rechtVOs[k];
                    RechtId rechtId = rechtVO.getRechtId();
                    bNotFound = !rechtId.equals(rolleRechtId);
                }
                bOk = !bNotFound;
            } // for rolleRechteVOs
            if (bOk)
            {
                al.add(rolleVO);
            }
        } // for rolleVOs
        RolleVO[] r = new RolleVO[al.size()];
        al.toArray(r);
        return r;
    }

}

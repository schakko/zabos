package de.ecw.zabos.util;

import org.apache.log4j.Logger;

import de.ecw.daemon.IDaemon;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.RechtDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.tao.RechteTAO;
import de.ecw.zabos.sql.tao.RolleTAO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RechtVO;
import de.ecw.zabos.sql.vo.RolleVO;

/**
 * Diese Hilfsklasse installiert den Administratoraccount und die
 * Systemadministrator-Rolle, so dass der erste Zugriff auf die
 * Systemverwaltungsoberfläche funktioniert
 * 
 * @author ckl
 * 
 */
public class FirstInstallService implements IDaemon
{

    private final static Logger log = Logger
                    .getLogger(FirstInstallService.class);

    /**
     * Datenbank
     */
    private DBResource dbResource;

    /**
     * Admin-Account
     */
    private PersonVO adminAccount;

    public FirstInstallService(DBResource _dbResource)
    {
        dbResource = _dbResource;
    }

    public void free()
    {
    }

    public void init() throws StdException
    {
        log.info("Installiere Systemumgebung...");

        RechtDAO rechtDAO = dbResource.getDaoFactory().getRechtDAO();
        RolleDAO rolleDAO = dbResource.getDaoFactory().getRolleDAO();
        RolleTAO rolleTAO = dbResource.getTaoFactory().getRolleTAO();
        RechteTAO rechteTAO = dbResource.getTaoFactory().getRechteTAO();
        BenutzerVerwaltungTAO benutzerVerwaltungTAO = dbResource
                        .getTaoFactory().getBenutzerVerwaltungTAO();
        PersonDAO personDAO = dbResource.getDaoFactory().getPersonDAO();

        // Es existieren noch keine Accounts in der Datenbank
        if ((personDAO.countPersonen() == 0) && (adminAccount != null))
        {
            log.info("Erstelle Administrator-Account ["
                            + adminAccount.getName() + "]");
            adminAccount.setPassword(StringUtils.md5(adminAccount.getPassword()));
            adminAccount = benutzerVerwaltungTAO.createPerson(adminAccount);
        }
        else
        {
            log.debug("Admin-Account wird nicht eingerichtet, da bereits Personen im System existieren");
        }

        RolleVO[] alleRollen = rolleDAO.findAll();
        RolleVO rolleAdmin = rolleDAO.getObjectFactory().createRolle();

        // Es existieren noch keine Rollen in der Datenbank
        if ((alleRollen != null) && (alleRollen.length == 0))
        {
            log.info("Erstelle neue Administrator-Rolle");
            rolleAdmin.setName("Systemadministrator");
            rolleAdmin.setBeschreibung("Systemadministrator");
            rolleAdmin = benutzerVerwaltungTAO.createRolle(rolleAdmin);

            if (rolleAdmin.getRolleId() != null)
            {
                log.info("Weise der Administrator-Rolle alle Rechte hinzu: ");
                RechtVO[] alleRechte = rechtDAO.findAll();

                if (alleRechte != null)
                {
                    for (int i = 0, m = alleRechte.length; i < m; i++)
                    {
                        RechtVO recht = alleRechte[i];
                        rolleTAO.addRechtToRolle(recht.getRechtId(),
                                        rolleAdmin.getRolleId());

                        log.info("  Recht \"" + recht.getName()
                                        + "\" hinzugefuegt");
                    }
                }
            }
        }
        else
        {
            log.debug("Systemadminstrator-Rolle wird nicht erstellt, da bereits Rollen im System existieren");
        }

        // Administrator-Account und Systemadministrator-Rolle wurden erzeugt
        if ((rolleAdmin != null) && (rolleAdmin.getRolleId() != null)
                        && (rolleAdmin.getRolleId().getLongValue() > 0)
                        && (adminAccount.getPersonId() != null)
                        && (adminAccount.getPersonId().getLongValue() > 0))
        {
            log.info("Weise dem Administrator-Account die Systemadministrator-Rolle hinzu");
            rechteTAO.addPersonInRolleToSystem(adminAccount.getPersonId(),
                            rolleAdmin.getRolleId());
        }
    }

    public DAEMON_STATUS getDaemonStatus()
    {
        return DAEMON_STATUS.ONLINE;
    }

    /**
     * Setzt den Administrator-Account.<br />
     * Das Passwort von {@link PersonVO#setPassword(String)} muss
     * unverschlüsselt eingetragen werden. Es wird automatisch verschlüsselt.
     * 
     * @param adminAccount
     */
    public void setAdminAccount(PersonVO adminAccount)
    {
        this.adminAccount = adminAccount;
    }

    /**
     * Liefert das {@link PersonVO}
     * 
     * @return
     */
    public PersonVO getAdminAccount()
    {
        return adminAccount;
    }

}
